package br.com.gigatron.futbusiness.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.Group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.gigatron.futbusiness.asynctask.AlteraSaldoTask;
import br.com.gigatron.futbusiness.asynctask.paginafut.AlteraSaldoNovoJogadorTask;
import br.com.gigatron.futbusiness.asynctask.paginafut.AtualizaJogadorEventoTask;
import br.com.gigatron.futbusiness.asynctask.paginafut.JogadorJaExisteTask;
import br.com.gigatron.futbusiness.asynctask.paginafut.PreencheEstatisticasJogadorTask;
import br.com.gigatron.futbusiness.asynctask.paginafut.SalvaJogadorFutTask;
import br.com.gigatron.futbusiness.database.FutBusinessDatabase;
import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.JogadorFut;
import br.com.gigatron.futbusiness.model.Usuario;

public class FormularioJogadorView {

    private final Context context;
    private final Fut fut;
    private final JogadorFut jogadorFut;
    private final JogadorDao jogadorDao;
    private final EventoDao eventoDao;
    private final EditText nome;
    private final EditText valorAvulso;
    private final EditText valorMensal;
    private final CheckBox jogadorMensalista;
    private final CheckBox padraoAvulso;
    private final CheckBox padraoMensal;
    private final Group informacoesJogador;
    private final TextView presenteAvulso;
    private final TextView presenteMensal;
    private final TextView tituloPresenteMensal;
    private final TextView calotesDados;
    private final TextView vezesQueFurou;

    public FormularioJogadorView(
            Context context,
            EditText nome,
            EditText valorAvulso,
            EditText valorMensal,
            CheckBox jogadorMensalista,
            CheckBox padraoAvulso,
            CheckBox padraoMensal,
            Group informacoesJogador,
            TextView presenteAvulso,
            TextView presenteMensal,
            TextView tituloPresenteMensal,
            TextView calotesDados,
            TextView vezesQueFurou,
            Fut fut,
            JogadorFut jogadorFut
    ) {

        this.context = context;
        this.nome = nome;
        this.valorAvulso = valorAvulso;
        this.valorMensal = valorMensal;
        this.jogadorMensalista = jogadorMensalista;
        this.padraoAvulso = padraoAvulso;
        this.padraoMensal = padraoMensal;
        this.informacoesJogador = informacoesJogador;
        this.presenteAvulso = presenteAvulso;
        this.presenteMensal = presenteMensal;
        this.tituloPresenteMensal = tituloPresenteMensal;
        this.calotesDados = calotesDados;
        this.vezesQueFurou = vezesQueFurou;
        this.fut = fut;
        this.jogadorFut = jogadorFut;
        FutBusinessDatabase database = FutBusinessDatabase.getInstance(this.context);
        jogadorDao = database.getJogadorDao();
        eventoDao = database.getEventoDao();
    }

//Public methods -----------------------------------------------------------------------------------
//Level 2 ------------------------------------------------------------------------------------------
    public void setCheckedEmValoresPadrao() {
        valorAvulso.setText(String.valueOf(fut.getValorAvulso()));
        valorMensal.setText(String.valueOf(fut.getValorMensal()));
        valorAvulso.setVisibility(View.INVISIBLE);
        valorMensal.setVisibility(View.INVISIBLE);
        padraoAvulso.setChecked(true);
        padraoMensal.setChecked(true);
    }

    public void escondeInformacoesJogador() {
        informacoesJogador.setVisibility(View.GONE);
    }

    public void preencheCamposComDadosDoJogador() {
        nome.setText(jogadorFut.getNome());
        valorAvulso.setText(String.valueOf(jogadorFut.getValorAvulso()));
        valorMensal.setText(String.valueOf(jogadorFut.getValorMensal()));
        jogadorMensalista.setChecked(jogadorFut.isMensalista());

        new PreencheEstatisticasJogadorTask(jogadorFut, eventoDao, jogadorDao, fut,
                () -> preencheCamposDaBarraDeInformacoesDoJogador(presenteAvulso,
                        String.valueOf(jogadorFut.getPresencasAvulso()),
                        presenteMensal,
                        String.valueOf(jogadorFut.getPresencasMensalista()),
                        calotesDados,
                        String.valueOf(jogadorFut.getCalotesDados()),
                        vezesQueFurou,
                        String.valueOf(jogadorFut.getVezesQueFurou()))).execute();
    }

    private void preencheCamposDaBarraDeInformacoesDoJogador(TextView presenteAvulso,
                                                             String s,
                                                             TextView presenteMensal,
                                                             String s2,
                                                             TextView calotesDados,
                                                             String s3,
                                                             TextView vezesQueFurou,
                                                             String s4) {
        presenteAvulso.setText(s);
        presenteMensal.setText(s2);
        calotesDados.setText(s3);
        vezesQueFurou.setText(s4);
    }

//Level 3 ------------------------------------------------------------------------------------------
    public void escondeCamposParaMensalistas(TextView valorMensalistas) {
        valorMensalistas.setVisibility(View.GONE);
        valorMensal.setVisibility(View.GONE);
        padraoMensal.setVisibility(View.GONE);
        jogadorMensalista.setVisibility(View.GONE);

        tituloPresenteMensal.setVisibility(View.GONE);
        presenteMensal.setVisibility(View.GONE);
    }

//Level 6 ------------------------------------------------------------------------------------------
    public boolean camposEstaoPreenchidos() {
        if (nome.getText().toString().isEmpty()) {
            alertaErro("Campo NOME precisa ser preenchido!");
            return false;

        } else if (!padraoAvulso.isChecked() && valorAvulso.getText().toString().isEmpty()) {
            alertaErro("Campo VALOR AVULSO precisa ser preenchido!");
            return false;

        } else if (!padraoMensal.isChecked() && valorMensal.getText().toString().isEmpty()) {
            alertaErro("Campo VALOR MENSAL precisa ser preenchido!");
            return false;
        }
        return true;
    }

    public void capitalizaPrimeiraLetraDoNome() {
        List<String> palavras = new ArrayList<>(Arrays.asList(nome.getText().toString().
                split(" ")));
        StringBuilder join = new StringBuilder();
        for (String s: palavras) {
            s = s.substring(0, 1).toUpperCase() + s.substring(1);
            join.append(" ").append(s);
        }
        String localFormatado = join.toString().trim();
        nome.setText(localFormatado);
    }

    public void verificaSeJogadorJaExiste(VerificaSeJogadorJaExisteListener listener) {
        String textNome = nome.getText().toString();

        new JogadorJaExisteTask(jogadorDao, fut, textNome, jogadorFut,
                jogadorJaExiste -> {
                    listener.aposVerificar(jogadorJaExiste);
                    if (jogadorJaExiste) {
                        alertaErro("Ja existe um jogador com este nome!");
                    }
                }).execute();
    }

//Level 7 ------------------------------------------------------------------------------------------
    public void alertaErro (String message) {
        new AlertDialog.Builder(context)
                .setTitle("Erro")
                .setMessage(message)
                .setNeutralButton("Ok", null)
                .show();
    }

    public void diminuiSaldoNovoJogador(Usuario usuario) {
        UsuarioDao usuarioDao = FutBusinessDatabase.getInstance(context).getUsuarioDao();
        new AlteraSaldoNovoJogadorTask(usuario, usuarioDao, AlteraSaldoTask.Acao.DIMINUI).execute();
    }


//Level 8 ------------------------------------------------------------------------------------------
    public void preencheDadosDoJogadorComValoresNosCampos(boolean modoEditaJogador,
                                      PreencheDadosDoJogadorComValoresNosCamposListener listener) {
        if (modoEditaJogador) {
            atualizaJogadorEvento(
                    () -> atualizaJogadorFut(
                            () -> salvaJogadorFut(modoEditaJogador,
                                    listener::aposPreencherDadosDoJogadorComValoresNosCampos)));

        } else {
            atualizaJogadorFut(
                    () -> salvaJogadorFut(modoEditaJogador,
                            listener::aposPreencherDadosDoJogadorComValoresNosCampos));
        }
    }

    public void salvaJogadorFut(boolean modoEditaJogador, SalvaJogadorFutListener listener) {
        new SalvaJogadorFutTask(modoEditaJogador, jogadorDao, jogadorFut,
                isCriaJogador -> {
                    if (isCriaJogador) {
                            feedBackAoUser("Jogador salvo com sucesso!");
                            listener.aposSalvarJogadorFut();
                    } else {
                            feedBackAoUser("Jogador editado com sucesso!");
                            listener.aposSalvarJogadorFut();
                    }
                }).execute();
    }

    public void feedBackAoUser(String text) {
        new Toast(context);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

//Private methods ----------------------------------------------------------------------------------
//Level 8 ------------------------------------------------------------------------------------------

    private void atualizaJogadorEvento(AtualizaJogadorEventoListener listener) {
        new AtualizaJogadorEventoTask(
                eventoDao,
                fut,
                jogadorDao,
                jogadorFut,
                nome.getText().toString(),
                jogadorMensalista.isChecked(),
                padraoAvulso.isChecked(),
                valorAvulso.getText().toString(),
                padraoMensal.isChecked(),
                valorMensal.getText().toString(),
                listener::aposAtualizarJogadorEvento).execute();
    }

    private void atualizaJogadorFut(AtualizaJogadorFutListener listener) {
        jogadorFut.setNome(nome.getText().toString());
        verificaCheckedPadrao();
        jogadorFut.setMensalista(jogadorMensalista.isChecked());
        jogadorFut.setFutId(fut.getFutId());
        listener.aposAtualizarJogadorFut();
    }

    private void verificaCheckedPadrao() {
        if (padraoAvulso.isChecked()) {
            jogadorFut.setValorAvulso(fut.getValorAvulso());
        } else {
            jogadorFut.setValorAvulso(Double.parseDouble(valorAvulso.getText().toString()));
        }

        if (padraoMensal.isChecked()) {
            jogadorFut.setValorMensal(fut.getValorMensal());
        } else {
            jogadorFut.setValorMensal(Double.parseDouble(valorMensal.getText().toString()));
        }
    }

//Interfaces ---------------------------------------------------------------------------------------
    public interface PreencheDadosDoJogadorComValoresNosCamposListener {
        void aposPreencherDadosDoJogadorComValoresNosCampos();
    }

    public interface AtualizaJogadorEventoListener {
        void aposAtualizarJogadorEvento();
    }

    public interface AtualizaJogadorFutListener {
        void aposAtualizarJogadorFut();
    }

    public interface VerificaSeJogadorJaExisteListener {
        void aposVerificar(boolean existe);
    }

    public interface SalvaJogadorFutListener {
        void aposSalvarJogadorFut();
    }
}