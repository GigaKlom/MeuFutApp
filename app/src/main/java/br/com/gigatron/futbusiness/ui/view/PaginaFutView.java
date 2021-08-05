package br.com.gigatron.futbusiness.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.Group;

import java.util.Collections;
import java.util.List;

import br.com.gigatron.futbusiness.asynctask.AlteraSaldoTask;
import br.com.gigatron.futbusiness.asynctask.DefineUsuarioTask;
import br.com.gigatron.futbusiness.asynctask.fut.RemoveFutTask;
import br.com.gigatron.futbusiness.asynctask.paginafut.AlteraSaldoNovoJogadorTask;
import br.com.gigatron.futbusiness.asynctask.paginafut.AlteraStatusJogadorTask;
import br.com.gigatron.futbusiness.asynctask.paginafut.AtualizaDadosFutTask;
import br.com.gigatron.futbusiness.asynctask.paginafut.AtualizaListaJogadorFutTask;
import br.com.gigatron.futbusiness.asynctask.paginafut.CriaJogadoresEventoTask;
import br.com.gigatron.futbusiness.asynctask.paginafut.RemoveJogadorFutTask;
import br.com.gigatron.futbusiness.database.FutBusinessDatabase;
import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.FutDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.JogadorFut;
import br.com.gigatron.futbusiness.model.Usuario;
import br.com.gigatron.futbusiness.ui.adapter.PaginaFutAdapter;

import static android.view.View.GONE;

public class PaginaFutView {

    private final Context context;
    private final PaginaFutAdapter adapter;
    private final JogadorDao jogadorDao;
    private final EventoDao eventoDao;
    private final Fut fut;
    private final TextView local;
    private final TextView tipo;
    private final TextView diaSemana;
    private final TextView numeroJogadores;
    private final TextView numeroMensalistas;
    private final TextView valorQuadra;
    private final TextView ganhoEsperadoMensalistas;
    private final TextView ganhoEsperadoAvulsos;
    private final TextView lucroPrejuizo;
    private final TextView padraoAvulso;
    private final TextView padraoMensalista;
    private final TextView textoPadraoMensalista;
    private final TextView textoMensalistas;
    private final Group groupGanhoMensalistas;
    private final Group groupGanhoAvulsos;
    private final Group groupLucroPrejuizo;
    private final SwitchCompat switchMostrarDados;
    private final Group groupPadraoAvulso;
    private final Group groupPadraoMensalista;
    private final Group groupNumeroJogadores;
    private final Group groupNumeroMensalistas;
    private final Group groupPrecoQuadra;
    private final UsuarioDao usuarioDao;
    private Usuario usuario;
    private final TextView novosJogadoresDisponiveis;

    public PaginaFutView(
            Context context,
            PaginaFutAdapter adapter,
            JogadorDao jogadorDao,
            Fut fut,
            TextView local,
            TextView tipo,
            TextView diaSemana,
            TextView numeroJogadores,
            TextView numeroMensalistas,
            TextView valorQuadra,
            TextView ganhoEsperadoMensalistas,
            TextView ganhoEsperadoAvulsos,
            TextView lucroPrejuizo,
            TextView padraoAvulso,
            TextView padraoMensalista,
            TextView textoPadraoMensalista,
            TextView textoMensalistas,
            Group groupGanhoMensalistas,
            Group groupGanhoAvulsos,
            Group groupLucroPrejuizo,
            SwitchCompat switchMostrarDados,
            Group groupPadraoAvulso,
            Group groupPadraoMensalista,
            Group groupNumeroJogadores,
            Group groupNumeroMensalistas,
            Group groupPrecoQuadra,
            UsuarioDao usuarioDao,
            TextView novosJogadoresDisponiveis
    ) {
        this.eventoDao = FutBusinessDatabase.getInstance(context).getEventoDao();
        this.context = context;
        this.adapter = adapter;
        this.jogadorDao = jogadorDao;
        this.fut = fut;
        this.local = local;
        this.tipo = tipo;
        this.diaSemana = diaSemana;
        this.numeroJogadores = numeroJogadores;
        this.numeroMensalistas = numeroMensalistas;
        this.valorQuadra = valorQuadra;
        this.ganhoEsperadoMensalistas = ganhoEsperadoMensalistas;
        this.ganhoEsperadoAvulsos = ganhoEsperadoAvulsos;
        this.lucroPrejuizo = lucroPrejuizo;
        this.padraoAvulso = padraoAvulso;
        this.padraoMensalista = padraoMensalista;
        this.textoPadraoMensalista = textoPadraoMensalista;
        this.textoMensalistas = textoMensalistas;
        this.groupGanhoMensalistas = groupGanhoMensalistas;
        this.groupGanhoAvulsos = groupGanhoAvulsos;
        this.groupLucroPrejuizo = groupLucroPrejuizo;
        this.switchMostrarDados = switchMostrarDados;
        this.groupPadraoAvulso = groupPadraoAvulso;
        this.groupPadraoMensalista = groupPadraoMensalista;
        this.groupNumeroJogadores = groupNumeroJogadores;
        this.groupNumeroMensalistas = groupNumeroMensalistas;
        this.groupPrecoQuadra = groupPrecoQuadra;
        this.usuarioDao = usuarioDao;
        this.novosJogadoresDisponiveis = novosJogadoresDisponiveis;
    }

//Public methods -----------------------------------------------------------------------------------
//Level 1 ------------------------------------------------------------------------------------------
    public void atualiza() {
        new AtualizaListaJogadorFutTask(jogadorDao, fut, adapter,
                this::atualizaBarraDeInformacoes).execute();
        new DefineUsuarioTask(usuarioDao, usuario -> {
            this.usuario = usuario;
            atualizaNovosJogadoresDisponiveis();
        }).execute();
    }

//Level 3 ------------------------------------------------------------------------------------------
    public void confirmaRemoveJogador(ConfirmaRemoveJogadorFutListener listener) {
        StringBuilder nomesDosJogadores = new StringBuilder();
        for (JogadorFut jogadorFut : adapter.getJogadoresFutSelecionados()) {
            nomesDosJogadores.append("\n-").append(jogadorFut.getNome());
        }
        new AlertDialog.Builder(context)
                .setTitle("Remover jogador")
                .setMessage("Deseja remover o jogador(es):" +
                        nomesDosJogadores + "\npermanentemente?")
                .setNegativeButton("Não", null)
                .setPositiveButton("Sim", (dialog, which) -> {
                    for (JogadorFut jogadorFut : adapter.getJogadoresFutSelecionados()) {
                        remove(jogadorFut);
                    }
                    listener.aposRemover();
                })
                .show();
    }

    public void mudaStatus(List<JogadorFut> jogadoresSelecionados) {
        for (JogadorFut jogadorFut : jogadoresSelecionados) {
            jogadorFut.setMensalista(!jogadorFut.isMensalista());
            new AlteraStatusJogadorTask(jogadorFut, jogadorDao, eventoDao, fut).execute();
        }
        atualiza();
        feedbackAoUser("Status alterado(s) com sucesso!");
    }

//Level 4 ------------------------------------------------------------------------------------------
    public void confirmaRemoveFut(FutDao futDao, ConfirmaRemoveFutListener listener) {
        new AlertDialog.Builder(context)
                .setTitle("Deletar este fut")
                .setMessage("Deseja deletar este fut permanentemente?")
                .setNegativeButton("Não", null)
                .setPositiveButton("Sim", (DialogInterface dialog, int which) -> {
                        removeFut(futDao);
                        listener.aposRemover();
                })
                .show();
    }

//Private methods ----------------------------------------------------------------------------------
//Level 1 ------------------------------------------------------------------------------------------
    private void atualizaBarraDeInformacoes() {
        new AtualizaDadosFutTask(
                FutBusinessDatabase.getInstance(context).getFutDao(),
                jogadorDao,
                fut,
                this::atualizaTextViews
        ).execute();
    }

    private void atualizaNovosJogadoresDisponiveis() {
        String novosJogadoresDisponiveisText = usuario.getSaldoNovoJogador() + " disponíveis";
        novosJogadoresDisponiveis.setText(novosJogadoresDisponiveisText);
    }

    public void aumentaSaldoNovoJogador(UsuarioDao usuarioDao) {
        new AlteraSaldoNovoJogadorTask(usuario, usuarioDao, AlteraSaldoTask.Acao.AUMENTA).execute();
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void atualizaTextViews() {
        configuraMudancaDeCorLucroPrejuizo();
        preencheCabecalho();
        configuraContadorDeJogadores();
        configuraTextViewsMonetarias();
        if (!fut.isMensal()) {
            escondeTextViewsDeFutMensal();
        }
        configuraSwitchMostrarDados();
    }

//Level 3 ------------------------------------------------------------------------------------------
    private void remove(JogadorFut jogadorFut) {
        new RemoveJogadorFutTask(jogadorFut, jogadorDao, eventoDao, fut).execute();
        atualiza();
        feedbackAoUser("Jogador(es) removido(s)!");
    }

    private void removeFut(FutDao futDao) {
        new RemoveFutTask(futDao, fut).execute();
        feedbackAoUser(fut.getLocal() + " deletado!");
    }

    private void feedbackAoUser(String text) {
        new Toast(context);
        Toast.makeText(context, text, Toast.LENGTH_SHORT)
                .show();
    }

    private void configuraMudancaDeCorLucroPrejuizo() {
        if (fut.getLucroPrejuizo() < 0) {
            lucroPrejuizo.setTextColor(Color.RED);
        } else {
            lucroPrejuizo.setTextColor(Color.GREEN);
        }
    }

    private void preencheCabecalho() {
        local.setText(fut.getLocal());
        String textTipo = "AVULSO";
        String[] diasDaSemana = {
                " ", "DOMINGO", "SEGUNDA", "TERÇA", "QUARTA", "QUINTA", "SEXTA", "SÁBADO"
        };
        if (fut.isMensal()) {
            textTipo = "MENSAL";
            diaSemana.setText(diasDaSemana[fut.getDiaDaSemana()]);
        }
        tipo.setText(textTipo);
    }

    private void configuraContadorDeJogadores() {
        String textNumeroJogadores = fut.getNumeroJogadores() + " jogador(es)";
        numeroJogadores.setText(textNumeroJogadores);
        String textNumeroMensalistas = fut.getNumeroMensalistas() + " mensalista(s)";
        numeroMensalistas.setText(textNumeroMensalistas);
    }

    private void configuraTextViewsMonetarias() {
        padraoAvulso.setText(textMonetariaPadrao(fut.getValorAvulso()));
        padraoMensalista.setText(textMonetariaPadrao(fut.getValorMensal()));
        valorQuadra.setText(textMonetariaPadrao(fut.getAluguelDaQuadra()));
        ganhoEsperadoMensalistas.setText(textMonetariaPadrao(fut.getGanhoEsperadoMensalistas()));
        ganhoEsperadoAvulsos.setText(textMonetariaPadrao(fut.getGanhoEsperadoAvulsos()));
        lucroPrejuizo.setText(textMonetariaPadrao(Math.abs(fut.getLucroPrejuizo())));
    }

    private void escondeTextViewsDeFutMensal() {
        textoPadraoMensalista.setVisibility(GONE);
        padraoMensalista.setVisibility(GONE);
        textoMensalistas.setVisibility(GONE);
        numeroMensalistas.setVisibility(GONE);
        groupGanhoMensalistas.setVisibility(GONE);
        groupLucroPrejuizo.setVisibility(GONE);
        groupGanhoAvulsos.setVisibility(View.VISIBLE);
        ganhoEsperadoAvulsos.setVisibility(View.VISIBLE);
    }

    private void configuraSwitchMostrarDados() {
        if (fut.isMensal()) {
            switchMostrarDados.setOnCheckedChangeListener((
                    CompoundButton buttonView, boolean isChecked) -> {
                    if (isChecked) {
                        groupPadraoAvulso.setVisibility(View.VISIBLE);
                        groupPadraoMensalista.setVisibility(View.VISIBLE);
                        groupNumeroJogadores.setVisibility(View.VISIBLE);
                        numeroMensalistas.setVisibility(View.VISIBLE);
                        groupPrecoQuadra.setVisibility(View.VISIBLE);
                        groupGanhoMensalistas.setVisibility(View.VISIBLE);
                        groupGanhoAvulsos.setVisibility(View.VISIBLE);
                        groupLucroPrejuizo.setVisibility(View.VISIBLE);
                    } else {
                        groupPadraoAvulso.setVisibility(GONE);
                        groupPadraoMensalista.setVisibility(GONE);
                        groupNumeroJogadores.setVisibility(GONE);
                        groupPrecoQuadra.setVisibility(GONE);
                        groupGanhoMensalistas.setVisibility(GONE);
                        groupGanhoAvulsos.setVisibility(GONE);
                    }
            });
        } else {
            switchMostrarDados.setVisibility(View.INVISIBLE);
            groupNumeroJogadores.setVisibility(View.VISIBLE);
            groupPrecoQuadra.setVisibility(View.VISIBLE);
            groupGanhoAvulsos.setVisibility(View.VISIBLE);
        }
    }

//Level 4 ------------------------------------------------------------------------------------------
    private String textMonetariaPadrao(double valor) {
        return "R$ " + String.valueOf(valor).replace(".", ",") + "0";
    }

//Interfaces ---------------------------------------------------------------------------------------
//Level 3 ------------------------------------------------------------------------------------------
    public interface ConfirmaRemoveJogadorFutListener {
        void aposRemover();
    }

    public interface ConfirmaRemoveFutListener {
        void aposRemover();
    }

//Métodos para intent secundária -------------------------------------------------------------------
    public void adicionaJogadoresNoEvento(Evento evento, List<JogadorFut> jogadoresSelecionados,
                                          MeusEventosView.AdicionaJogadoresNoEventoListener
                                                  listener) {

        JogadorDao jogadorDao = FutBusinessDatabase.getInstance(context).getJogadorDao();
        new CriaJogadoresEventoTask(
                jogadorDao,
                jogadoresSelecionados,
                Collections.singletonList(evento),
                () -> {
                    feedbackAoUser("Jogadore(s) adicionado(s)!");
                    listener.aposAdicionarJogadoresNoEvento();
                }).execute();
    }
}