package br.com.gigatron.futbusiness.ui.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import br.com.gigatron.futbusiness.R;
import br.com.gigatron.futbusiness.asynctask.AlteraSaldoTask;
import br.com.gigatron.futbusiness.asynctask.evento.AlteraSaldoNovoEventoTask;
import br.com.gigatron.futbusiness.asynctask.evento.CriaEventoTask;
import br.com.gigatron.futbusiness.asynctask.evento.EditaEventoTask;
import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.Usuario;
import br.com.gigatron.futbusiness.ui.view.MeusEventosView;

public class CriaEventoAvulsoDialog extends AppCompatDialogFragment {

    private TextView textViewData;
    private TextView textViewHorario;
    private ImageButton imageButtonData;
    private ImageButton imageButtonHorario;
    private int hora, minuto;
    private final Context context;
    private final EventoDao eventoDao;
    private final Fut fut;
    private final MeusEventosView meusEventosView;
    private final boolean modoEdicao;
    private Evento evento;
    private String title;
    private final boolean mostrarArquivados;
    private final Usuario usuario;
    private final UsuarioDao usuarioDao;

    public CriaEventoAvulsoDialog(Context context,
                                  EventoDao eventoDao,
                                  Fut fut,
                                  MeusEventosView meusEventosView,
                                  boolean modoEdicao,
                                  Evento evento,
                                  boolean mostrarArquivados,
                                  Usuario usuario,
                                  UsuarioDao usuarioDao
    ) {
        this.context = context;
        this.eventoDao = eventoDao;
        this.fut = fut;
        this.meusEventosView = meusEventosView;
        this.modoEdicao = modoEdicao;
        this.evento = evento;
        this.mostrarArquivados = mostrarArquivados;
        this.usuario = usuario;
        this.usuarioDao = usuarioDao;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_cria_evento_avulso_dialog, null);
        configuraViewDialog(builder, view);
        inicializaCampos(view);
        verificaModo();
        setClickImageButtonData();
        setClickImageButtonHorario();
        return builder.create();
    }

//Level 1 ------------------------------------------------------------------------------------------
    private void configuraViewDialog(AlertDialog.Builder builder, View view) {
        builder.setView(view)
                .setTitle(title)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Ok", (dialog, which) -> {

                    preencheDadosDoEvento(evento);
                    if (modoEdicao) {
                        new EditaEventoTask(evento, eventoDao).execute();
                    } else {
                        new CriaEventoTask(evento, fut, eventoDao).execute();
                        diminuiSaldoNovoEvento();
                    }
                    meusEventosView.atualiza(mostrarArquivados);
                });
    }

    private void inicializaCampos(View view) {
        textViewData = view.findViewById(R.id.form_evento_textview_data);
        textViewHorario = view.findViewById(R.id.form_evento_textview_horario);
        imageButtonData = view.findViewById(R.id.form_evento_imagebutton_data);
        imageButtonHorario = view.findViewById(R.id.form_evento_imagebutton_horario);
    }

    private void verificaModo() {
        if (modoEdicao) {
            preencheTextViewsComDadosDoEvento();
            title = "Editar evento";
            Log.i("TAG", "preencheDadosDoEvento: " + evento.getMensalId());
            Log.i("TAG", "preencheDadosDoEvento: " + evento.getData());
        } else {
            evento = new Evento();
            preencheTextViewsComValoresAtuais();
            title = "Novo evento";
        }
    }

    private void setClickImageButtonData() {
        imageButtonData.setOnClickListener(v -> inicializaDatePicker());
    }

    private void setClickImageButtonHorario() {
        imageButtonHorario.setOnClickListener(v -> inicializaTimePicker());
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void preencheDadosDoEvento(Evento evento) {
        evento.setAtivo(true);
        evento.setData(textViewData.getText().toString());
        evento.setHorario(textViewHorario.getText().toString());
        evento.setGanhoPrevisto(0);
        evento.setAtivo(true);
        evento.setArquivado(false);
        evento.setPrecoMensalQuadra(fut.getAluguelDaQuadra());
        if (!evento.isMensal()) {
            evento.setMensal(false);
            evento.setMensalId(0);
        }
    }

    private void preencheTextViewsComDadosDoEvento() {
        textViewData.setText(evento.getData());
        textViewHorario.setText(evento.getHorario());
    }
    private void preencheTextViewsComValoresAtuais() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = simpleDateFormat.format(calendar.getTime());
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("kk:mm");
        String horarioFormatado = simpleTimeFormat.format(calendar.getTime());
        textViewData.setText(dataFormatada);
        textViewHorario.setText(horarioFormatado);
    }

    private void inicializaDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            month += 1;
            String data = dataToString(dayOfMonth, month, year);
            textViewData.setText(data);
        };
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int style = AlertDialog.THEME_HOLO_LIGHT;
        DatePickerDialog datePickerDialog =
                new DatePickerDialog(context, style, dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private void inicializaTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener =
                (view, selectedHour, selectedMinute) -> {
            hora = selectedHour;
            minuto = selectedMinute;
            textViewHorario.setText(
                    String.format(Locale.getDefault(), "%02d:%02d",hora, minuto)
            );
        };
        int style = AlertDialog.THEME_HOLO_DARK;
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                style,
                onTimeSetListener,
                hora,
                minuto,
                true
        );

        timePickerDialog.setTitle("Insira o horário");
        timePickerDialog.show();
    }

    private void diminuiSaldoNovoEvento() {
        new AlteraSaldoNovoEventoTask(
                usuario,
                usuarioDao,
                AlteraSaldoTask.Acao.DIMINUI
        ).execute();
    }

//Level 3 ------------------------------------------------------------------------------------------
    private String dataToString(int dayOfMonth, int month, int year) {
        return dayOfMonth + "/" + month + "/" + year;
    }
}