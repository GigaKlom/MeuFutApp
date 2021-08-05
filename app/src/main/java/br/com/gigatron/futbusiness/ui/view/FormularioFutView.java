package br.com.gigatron.futbusiness.ui.view;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import br.com.gigatron.futbusiness.R;
import br.com.gigatron.futbusiness.asynctask.fut.AlteraSaldoNovoFutTask;
import br.com.gigatron.futbusiness.asynctask.fut.AlteraValoresPadraoDosJogadoresTask;
import br.com.gigatron.futbusiness.asynctask.fut.FutJaExisteTask;
import br.com.gigatron.futbusiness.asynctask.fut.SalvaFutTask;
import br.com.gigatron.futbusiness.database.FutBusinessDatabase;
import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.FutDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.Usuario;

public class FormularioFutView {

    private final Context context;
    private final Fut fut;
    private final FutDao futDao;

    private final EditText local;
    private final EditText valorAvulso;
    private final EditText valorMensal;
    private final EditText aluguelDaQuadra;
    private final CheckBox mensal;
    private final LinearLayout diaDaSemanaLayout;
    private final RadioGroup diaDaSemanaRadioGroup;
    private final RadioButton domingo;
    private final RadioButton segunda;
    private final RadioButton terca;
    private final RadioButton quarta;
    private final RadioButton quinta;
    private final RadioButton sexta;
    private final RadioButton sabado;
    private final TextView horarioTextView;
    private final ImageButton horarioButton;

    public FormularioFutView(
            Context context,
            Fut fut,
            EditText local,
            EditText valorAvulso,
            EditText valorMensal,
            EditText aluguelDaQuadra,
            CheckBox mensal,
            LinearLayout diaDaSemanaLayout,
            RadioGroup diaDaSemanaRadioGroup,
            RadioButton domingo,
            RadioButton segunda,
            RadioButton terca,
            RadioButton quarta,
            RadioButton quinta,
            RadioButton sexta,
            RadioButton sabado,
            TextView horarioTextView,
            ImageButton horarioButton
    ) {

        this.context = context;
        this.fut = fut;
        this.local = local;
        this.valorAvulso = valorAvulso;
        this.valorMensal = valorMensal;
        this.aluguelDaQuadra = aluguelDaQuadra;
        this.mensal = mensal;
        this.diaDaSemanaLayout = diaDaSemanaLayout;
        this.diaDaSemanaRadioGroup = diaDaSemanaRadioGroup;
        this.domingo = domingo;
        this.segunda = segunda;
        this.terca = terca;
        this.quarta = quarta;
        this.quinta = quinta;
        this.sexta = sexta;
        this.sabado = sabado;
        this.horarioTextView = horarioTextView;
        this.horarioButton = horarioButton;
        FutBusinessDatabase database = FutBusinessDatabase.getInstance(this.context);
        futDao = database.getFutDao();
    }

//Public methods -----------------------------------------------------------------------------------
//Level 1 ------------------------------------------------------------------------------------------
    public void configuraTimePicker() {
        horarioButton.setOnClickListener(new View.OnClickListener() {
            private int hora;
            private int minuto;

            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener =
                        (view, selectedHour, selectedMinute) -> {

                    hora = selectedHour;
                    minuto = selectedMinute;
                    horarioTextView.setText(
                            String.format(Locale.getDefault(), "%02d:%02d",hora, minuto)
                    );
                };

                int style = AlertDialog.THEME_HOLO_DARK;
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        context, style, onTimeSetListener, hora, minuto, true
                );

                timePickerDialog.setTitle("Insira o horário");
                timePickerDialog.show();
            }
        });
    }

//Level 2 ------------------------------------------------------------------------------------------
    public void preencheCamposComDadosDoFut() {
        local.setText(fut.getLocal());
        valorAvulso.setText(String.valueOf(fut.getValorAvulso()));
        valorMensal.setText(String.valueOf(fut.getValorMensal()));
        aluguelDaQuadra.setText(String.valueOf(fut.getAluguelDaQuadra()));
        mensal.setChecked(fut.isMensal());
        preencheCamposDeMensal();
    }

    public boolean camposEstaoPreenchidos() {
        if (local.getText().toString().isEmpty()) {
            alertaErro("Campo LOCAL precisa ser preenchido!");
            return false;

        } else if (valorAvulso.getText().toString().isEmpty()) {
            alertaErro("Campo VALOR AVULSO precisa ser preenchido!");
            return false;

        } else if (mensal.isChecked() && valorMensal.getText().toString().isEmpty()) {
            alertaErro("Campo VALOR MENSAL precisa ser preenchido!");
            return false;

        } else if (aluguelDaQuadra.getText().toString().isEmpty()) {
            alertaErro("Campo ALUGUEL DA QUADRA precisa ser preenchido!");
            return false;

        } else if (mensal.isChecked() && diaDaSemanaRadioGroup.getCheckedRadioButtonId() == -1) {
            alertaErro("Campo DIA DA SEMANA precisa ser preenchido!");
            return false;
        }
        return true;
    }

    public void capitalizarPrimeiraLetraDeCadaPalavraNoCampoLocal() {
        List<String> palavras =
                new ArrayList<>(Arrays.asList(local.getText().toString().split(" ")));

        StringBuilder join = new StringBuilder();
        for (String s: palavras) {
            s = s.substring(0, 1).toUpperCase() + s.substring(1);
            join.append(" ").append(s);
        }
        String localFormatado = join.toString().trim();
        local.setText(localFormatado);
    }

    public void finalizaFormulario(boolean modoEditaFut, FinalizaFormularioListener listener) {
        String textLocal = local.getText().toString();
        boolean mensal = this.mensal.isChecked();
        new FutJaExisteTask(futDao, textLocal, mensal, fut, futExiste -> {
            if (!futExiste) {
                alteraValoresPadraoDosJogadoresSeNecessario(modoEditaFut, textLocal,
                        listener::aposSalvar);
            } else {
                alertaErro("Já existe um Fut com esse nome!");
            }
        }).execute();
    }

    public void diminuiSaldoNovoFut(Usuario usuario) {
        UsuarioDao usuarioDao = FutBusinessDatabase.getInstance(context).getUsuarioDao();
        new AlteraSaldoNovoFutTask(usuario, usuarioDao, AlteraSaldoNovoFutTask.Acao.DIMINUI)
                .execute();
    }

//Level 3 ------------------------------------------------------------------------------------------
    public void alteraValoresPadraoDosJogadoresSeNecessario(boolean modoEditaFut, String textLocal,
    AlteraValoresPadraoDosJogadoresSeNecessarioListener listener) {
        String textValorAvulso = valorAvulso.getText().toString();
        String textValorMensal = valorMensal.getText().toString();
        String textAluguelDaQuadra = aluguelDaQuadra.getText().toString();
        boolean checkedMensal = mensal.isChecked();
        int intDiaDaSemana = atribuiDiaDaSemana();
        String textHorario = horarioTextView.getText().toString();

        if (modoEditaFut) {
            FutBusinessDatabase database = FutBusinessDatabase.getInstance(context);
            JogadorDao jogadorDao = database.getJogadorDao();
            EventoDao eventoDao = database.getEventoDao();

            new AlteraValoresPadraoDosJogadoresTask(
                    jogadorDao,
                    eventoDao,
                    fut,
                    textValorAvulso,
                    textValorMensal,
                    () -> preencheDadosDoFutComValoresDosCampos(
                            modoEditaFut,
                            textLocal,
                            textValorAvulso,
                            textValorMensal,
                            textAluguelDaQuadra,
                            checkedMensal,
                            intDiaDaSemana,textHorario,
                            listener::aposAlterarValoresPadraoDosJogadoresSeNecessario
                    )).execute();

        } else {
            preencheDadosDoFutComValoresDosCampos(
                    modoEditaFut, textLocal, textValorAvulso, textValorMensal,
                    textAluguelDaQuadra, checkedMensal, intDiaDaSemana, textHorario,
                    listener::aposAlterarValoresPadraoDosJogadoresSeNecessario
            );
        }
    }

//Level 4 ------------------------------------------------------------------------------------------
    private void preencheDadosDoFutComValoresDosCampos(boolean modoEditaFut,
                                                       String textLocal,
                                                       String textValorAvulso,
                                                       String textValorMensal,
                                                       String textAluguelDaQuadra,
                                                       boolean checkedMensal,
                                                       int intDiaDaSemana,
                                                       String horario,
                                                       PreencheDadosDoFutComValoresDosCamposListener
                                                               listener) {

        new SalvaFutTask(modoEditaFut, futDao, fut, textLocal, textValorAvulso, textValorMensal,
                textAluguelDaQuadra, checkedMensal, intDiaDaSemana, horario,
                isCriaFut -> {
            if (isCriaFut) {
                feedBackAoUser("Fut criado com sucesso!");
                listener.aposPreencherDadosDoFutComValoresDosCampos();
            } else {
                feedBackAoUser("Fut editado com sucesso!");
                listener.aposPreencherDadosDoFutComValoresDosCampos();
            }
        }).execute();
    }

//Private methods ----------------------------------------------------------------------------------
    private void alertaErro (String message) {
        new AlertDialog.Builder(context)
                .setTitle("Erro")
                .setMessage(message)
                .setNeutralButton("Ok", null)
                .show();
    }
    private void feedBackAoUser(String text) {
        new Toast(context);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void preencheCamposDeMensal() {
        if (fut.isMensal()) {
            valorMensal.setVisibility(View.VISIBLE);
            diaDaSemanaLayout.setVisibility(View.VISIBLE);
            horarioTextView.setText(fut.getHorario());
            preencheDiaDaSemana();
        }
    }

//Level 3 ------------------------------------------------------------------------------------------
    private void preencheDiaDaSemana() {
        if (fut.getDiaDaSemana() == 1) {
            domingo.setChecked(true);

        } else if (fut.getDiaDaSemana() == 2) {
            segunda.setChecked(true);

        } else if (fut.getDiaDaSemana() == 3) {
            terca.setChecked(true);

        } else if (fut.getDiaDaSemana() == 4) {
            quarta.setChecked(true);

        } else if (fut.getDiaDaSemana() == 5) {
            quinta.setChecked(true);

        } else if (fut.getDiaDaSemana() == 6) {
            sexta.setChecked(true);

        } else if (fut.getDiaDaSemana() == 7) {
            sabado.setChecked(true);
        }
    }

    private int atribuiDiaDaSemana() {
        int intDiaDaSemana;
        if (diaDaSemanaRadioGroup.
                getCheckedRadioButtonId() == R.id.activity_formulario_fut_radio_button_domingo) {
            intDiaDaSemana = 1;

        } else if (diaDaSemanaRadioGroup
                .getCheckedRadioButtonId() == R.id.activity_formulario_fut_radio_button_segunda) {
            intDiaDaSemana = 2;

        } else if (diaDaSemanaRadioGroup
                .getCheckedRadioButtonId() == R.id.activity_formulario_fut_radio_button_terca) {
            intDiaDaSemana = 3;

        } else if (diaDaSemanaRadioGroup
                .getCheckedRadioButtonId() == R.id.activity_formulario_fut_radio_button_quarta) {
            intDiaDaSemana = 4;

        } else if (diaDaSemanaRadioGroup
                .getCheckedRadioButtonId() == R.id.activity_formulario_fut_radio_button_quinta) {
            intDiaDaSemana = 5;

        } else if (diaDaSemanaRadioGroup
                .getCheckedRadioButtonId() == R.id.activity_formulario_fut_radio_button_sexta) {
            intDiaDaSemana = 6;

        } else if (diaDaSemanaRadioGroup
                .getCheckedRadioButtonId() == R.id.activity_formulario_fut_radio_button_sabado) {
            intDiaDaSemana = 7;

        } else {
            intDiaDaSemana = 0;
        }
        return intDiaDaSemana;
    }

//Interfaces ---------------------------------------------------------------------------------------
    public interface FinalizaFormularioListener {
        void aposSalvar();
    }

    public interface AlteraValoresPadraoDosJogadoresSeNecessarioListener {
        void aposAlterarValoresPadraoDosJogadoresSeNecessario();
    }

    public interface PreencheDadosDoFutComValoresDosCamposListener {
        void aposPreencherDadosDoFutComValoresDosCampos();
    }
}