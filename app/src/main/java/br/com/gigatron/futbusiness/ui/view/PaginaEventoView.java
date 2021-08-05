package br.com.gigatron.futbusiness.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.Group;

import java.util.ArrayList;
import java.util.List;

import br.com.gigatron.futbusiness.asynctask.evento.AtualizaDadosEventoTask;
import br.com.gigatron.futbusiness.asynctask.evento.FinalizaEventoTask;
import br.com.gigatron.futbusiness.asynctask.evento.RemoveEventoTask;
import br.com.gigatron.futbusiness.asynctask.paginaevento.AlteraStatusDoPagamentoTask;
import br.com.gigatron.futbusiness.asynctask.paginaevento.AtualizaListaJogadorEventoTask;
import br.com.gigatron.futbusiness.asynctask.paginaevento.BuscaTotalObtidoELucroPrejuizoTask;
import br.com.gigatron.futbusiness.asynctask.paginaevento.ContagemDeJogadoresECalotesNoEventoTask;
import br.com.gigatron.futbusiness.asynctask.paginaevento.MarcaJogadorComoFaltanteTask;
import br.com.gigatron.futbusiness.asynctask.paginaevento.RemoveJogadorEventoTask;
import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.JogadorEvento;
import br.com.gigatron.futbusiness.ui.adapter.PaginaEventoAdapter;

public class PaginaEventoView {

    private final Context context;
    private final PaginaEventoAdapter adapter;
    private final JogadorDao jogadorDao;
    private final EventoDao eventoDao;
    private final Evento evento;
    private final TextView tituloPrecoAvulsoQuadra;
    private final TextView precoAvulsoQuadra;
    private final TextView txtGanhoPrevisto;
    private final TextView txtGanhoObtido;
    private final TextView txtNumeroJogadores;
    private final TextView txtJogadoresPagos;
    private final TextView txtStatusEvento;
    private final Group groupReferenciaMensal;
    private final TextView referenciaMensal;
    private final Group groupDadosMensal;
    private final Group groupDados;
    private final SwitchCompat switchMostraDadosMensal;
    private final SwitchCompat switchMostraDados;
    private final TextView precoMensalQuadra;
    private final TextView totalObtidoAvulsos;
    private final TextView totalObtidoMensalistas;
    private final TextView lucroPrejuizoMensal;
    private final TextView nJogadoresDiferentes;
    private final TextView calotesRecebidos;
    private final TextView vezesQueFuraram;

    public PaginaEventoView(
            Context context,
            PaginaEventoAdapter adapter,
            Evento evento,
            JogadorDao jogadorDao,
            EventoDao eventoDao,
            TextView tituloPrecoAvulsoQuadra,
            TextView precoAvulsoQuadra,
            TextView txtGanhoPrevisto,
            TextView txtGanhoObtido,
            TextView txtNumeroJogadores,
            TextView txtJogadoresPagos,
            TextView txtStatusEvento,
            Group groupReferenciaMensal,
            TextView referenciaMensal,
            Group groupDadosMensal,
            Group groupDados,
            SwitchCompat switchMostraDadosMensal,
            SwitchCompat switchMostraDados,
            TextView precoMensalQuadra,
            TextView totalObtidoAvulsos,
            TextView totalObtidoMensalistas,
            TextView lucroPrejuizoMensal,
            TextView nJogadoresDiferentes,
            TextView calotesRecebidos,
            TextView vezesQueFuraram
    ) {
        this.context = context;
        this.adapter = adapter;
        this.evento = evento;
        this.jogadorDao = jogadorDao;
        this.eventoDao = eventoDao;
        this.tituloPrecoAvulsoQuadra = tituloPrecoAvulsoQuadra;
        this.precoAvulsoQuadra = precoAvulsoQuadra;
        this.txtGanhoPrevisto = txtGanhoPrevisto;
        this.txtGanhoObtido = txtGanhoObtido;
        this.txtNumeroJogadores = txtNumeroJogadores;
        this.txtJogadoresPagos = txtJogadoresPagos;
        this.txtStatusEvento = txtStatusEvento;
        this.groupReferenciaMensal = groupReferenciaMensal;
        this.referenciaMensal = referenciaMensal;
        this.groupDadosMensal = groupDadosMensal;
        this.groupDados = groupDados;
        this.switchMostraDadosMensal = switchMostraDadosMensal;
        this.switchMostraDados = switchMostraDados;
        this.precoMensalQuadra = precoMensalQuadra;
        this.totalObtidoAvulsos = totalObtidoAvulsos;
        this.totalObtidoMensalistas = totalObtidoMensalistas;
        this.lucroPrejuizoMensal = lucroPrejuizoMensal;
        this.nJogadoresDiferentes = nJogadoresDiferentes;
        this.calotesRecebidos = calotesRecebidos;
        this.vezesQueFuraram = vezesQueFuraram;
    }

//Public methods -----------------------------------------------------------------------------------
//Level 1 ------------------------------------------------------------------------------------------
    public void atualizaLista() {
        new AtualizaListaJogadorEventoTask(jogadorDao, evento, adapter,
                this::atualizaBarraDeInformacoes).execute();
    }

//Level 2 ------------------------------------------------------------------------------------------
    public void alteraStatusDoPagamento(JogadorEvento jogadorEvento) {
        new AlteraStatusDoPagamentoTask(jogadorEvento, jogadorDao, this::atualizaLista).execute();
    }

    public void confirmaRemoveJogadores(ConfirmaListener listener) {
        StringBuilder nomesDosJogadores = new StringBuilder();
        for (JogadorEvento jogadorEvento: adapter.getJogadoresEventoSelecionados()) {
            nomesDosJogadores.append("\n-").append(jogadorEvento.getNome());
        }
        new AlertDialog.Builder(context)
                .setTitle("Remover jogador")
                .setMessage("Deseja remover o(s) jogador(es):" +
                        nomesDosJogadores + "\ndesse evento?")
                .setNegativeButton("Não", null)

                .setPositiveButton("Sim", (dialog, which) -> {
                    for (JogadorEvento jogadorEvento: adapter.getJogadoresEventoSelecionados()) {
                        removeJogador(jogadorEvento);
                    }
                    listener.aposConfirmar();
                    feedbackAoUser("Jogador(es) removido(s)!");
                })

                .show();
    }

    public void alteraStatusDeFaltanteJogador(ConfirmaListener listener) {
        for (JogadorEvento jogadorEvento: adapter.getJogadoresEventoSelecionados()) {
            new MarcaJogadorComoFaltanteTask(jogadorEvento, jogadorDao, this::atualizaLista).execute();
        }
        listener.aposConfirmar();
        feedbackAoUser("Status alterado(s)!");
    }

    public void atribuiInformacoesDoEventoAoTitulo(TextView txtData, TextView txtHorario) {
        txtData.setText(evento.getData());
        txtHorario.setText(evento.getHorario());
    }

//Level 3 ------------------------------------------------------------------------------------------
    public void confirmaFinalizaEvento(ConfirmaFinalizaEventoListener listener) {
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Finalizar evento")
                .setMessage("Finalizar o evento significa que não haverá mais nenhuma alteração" +
                        " no evento, seja ela qual for.\n\nConfirme apenas se tiver certeza, pois" +
                        " esta ação não poderá ser desfeita!")
                .setNegativeButton("Cancelar", null)

                .setPositiveButton("Ok", (dialog, which) -> {
                    evento.setAtivo(false);
                    List<Evento> eventosSelecionados = new ArrayList<>();
                    eventosSelecionados.add(evento);
                    new FinalizaEventoTask(
                            eventosSelecionados,
                            jogadorDao,
                            eventoDao,
                            () -> {
                                feedbackAoUser("Evento finalizado!");
                                listener.aposConfirmar(true);
                            }
                    ).execute();
                })

                .show();
    }

    public void confirmaRemoveEvento(ConfirmaListener listener) {
        new AlertDialog.Builder(context)
                .setTitle("Remover evento")
                .setMessage("Deseja remover esse evento permanentemente?\n\n" +
                        "Todos os dados relacionados a este evento serão excluídos! " +
                        "Inclusive informações de jogadores como calotes e presenças.")
                .setNegativeButton("Não", null)

                .setPositiveButton("Sim", (dialog, which) -> {
                    removeEvento();
                    feedbackAoUser("Evento removido!");
                    listener.aposConfirmar();
                })

                .show();
    }

//Private methods ----------------------------------------------------------------------------------
//Level 1 ------------------------------------------------------------------------------------------
    private void atualizaBarraDeInformacoes() {
        new AtualizaDadosEventoTask(evento, jogadorDao, eventoDao, this::atualizaTextViews)
        .execute();
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void removeJogador(JogadorEvento jogadorEvento) {
        new RemoveJogadorEventoTask(jogadorEvento, jogadorDao, this::atualizaLista).execute();
    }

    private void atualizaTextViews() {
        configuraTextViewsMonetarias();
        configuraTextViewsDeContagem();
        if (evento.isMensal()) {
            configuraReferenciaMensal();
        }
        configuraCheckedChangeSwitch();
        configuraJogadoresDiferentesCalotesVezesQueFuraram();
    }

//Level 3 ------------------------------------------------------------------------------------------
    private void feedbackAoUser(String text) {
        new Toast(context);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private void configuraTextViewsMonetarias() {
        txtGanhoPrevisto.setText(textMonetariaPadrao(evento.getGanhoPrevisto()));
        txtGanhoObtido.setText(textMonetariaPadrao(evento.getGanhoObtido()));
        precoAvulsoQuadra.setText(textMonetariaPadrao(evento.getPrecoMensalQuadra()));
        configuraTextViewsMonetariasDoMensal();
    }

    private void configuraTextViewsDeContagem() {
        String numeroJogadoresString = evento.getNumeroDeJogadoresNoEvento() + " jogadores";
        String jogadoresPagosString;
        String statusEventoString;

        if (evento.isAtivo()) {
            jogadoresPagosString = evento.getNumeroDeJogadoresComPagamentoConfirmado() +
                    " jogadores já pagaram";
            statusEventoString = "Ativo";
            txtStatusEvento.setTextColor(Color.GREEN);
        } else {
            jogadoresPagosString = evento.getNumeroDeJogadoresComPagamentoConfirmado() +
                    " jogadores pagaram";
            statusEventoString = "Finalizado";
            txtStatusEvento.setTextColor(Color.RED);

        }
        txtNumeroJogadores.setText(numeroJogadoresString);
        txtJogadoresPagos.setText(jogadoresPagosString);
        txtStatusEvento.setText(statusEventoString);

        configuraTextColorGanhoObtido();
    }

    private void configuraReferenciaMensal() {
        String referenciaFormatada = String.valueOf(evento.getMensalId());
        String[] array = referenciaFormatada.split("");
        referenciaFormatada = array[0] + "/" + array[1] + array[2] + array[3] + array[4];
        referenciaMensal.setText(referenciaFormatada);

        if (evento.isMensal()) {
            groupReferenciaMensal.setVisibility(View.VISIBLE);
            tituloPrecoAvulsoQuadra.setVisibility(View.GONE);
            precoAvulsoQuadra.setVisibility(View.GONE);
        }
    }

    private void configuraCheckedChangeSwitch() {
        switchMostraDados.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                groupDados.setVisibility(View.VISIBLE);
            } else {
                groupDados.setVisibility(View.GONE);
            }
        });
        switchMostraDadosMensal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                groupDadosMensal.setVisibility(View.VISIBLE);
            } else {
                groupDadosMensal.setVisibility(View.GONE);
            }
        });
    }

    private void configuraJogadoresDiferentesCalotesVezesQueFuraram() {
        new ContagemDeJogadoresECalotesNoEventoTask(eventoDao, jogadorDao, evento,
                (numeroJogadores, numeroCalotes, nVezesQueFuraram) -> {

                    String nJogadoresDiferentesString = numeroJogadores + " jogadores";
                    nJogadoresDiferentes.setText(nJogadoresDiferentesString);
                    String calotesRecebidosString = numeroCalotes + " calotes recebidos";
                    calotesRecebidos.setText(calotesRecebidosString);
                    String vezesQueFuraramString = nVezesQueFuraram + " vezes";
                    vezesQueFuraram.setText(vezesQueFuraramString);
                }).execute();
    }

    private void removeEvento() {
        new RemoveEventoTask(eventoDao, evento, jogadorDao).execute();
    }

//Level 4 ------------------------------------------------------------------------------------------
    private String textMonetariaPadrao(double valor) {
        return "R$ " + String.valueOf(valor).replace(".", ",") + "0";
    }

    private void configuraTextViewsMonetariasDoMensal() {
        this.precoMensalQuadra.setText(textMonetariaPadrao(evento.getPrecoMensalQuadra()));
        new BuscaTotalObtidoELucroPrejuizoTask(eventoDao, jogadorDao, evento,
                this::configuraCalculoLucroPrejuizo).execute();
    }

    private void configuraTextColorGanhoObtido() {
        if (evento.getGanhoObtido() < evento.getGanhoPrevisto()) {
            txtGanhoObtido.setTextColor(Color.RED);
        } else {
            txtGanhoObtido.setTextColor(Color.GREEN);
        }
    }

//Level 5 ------------------------------------------------------------------------------------------
    private void configuraCalculoLucroPrejuizo(double totalObtidoAvulsos,
                                               double totalObtidoMensalistas) {
        this.totalObtidoAvulsos.setText(textMonetariaPadrao(totalObtidoAvulsos));
        this.totalObtidoMensalistas.setText(textMonetariaPadrao(totalObtidoMensalistas));
        double lucroPrejuizo =
                totalObtidoAvulsos + totalObtidoMensalistas - evento.getPrecoMensalQuadra();
        if (lucroPrejuizo < 0) {
            lucroPrejuizoMensal.setTextColor(Color.RED);
        } else {
            lucroPrejuizoMensal.setTextColor(Color.GREEN);
        }
        lucroPrejuizoMensal.setText(textMonetariaPadrao(Math.abs(lucroPrejuizo)));
    }

//Interfaces ---------------------------------------------------------------------------------------
    public interface ConfirmaListener {
        void aposConfirmar();
    }

    public interface ConfirmaFinalizaEventoListener {
        void aposConfirmar(boolean isConfirmado);
    }
}