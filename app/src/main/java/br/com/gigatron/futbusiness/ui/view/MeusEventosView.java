package br.com.gigatron.futbusiness.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.gigatron.futbusiness.asynctask.AlteraSaldoTask;
import br.com.gigatron.futbusiness.asynctask.DefineUsuarioTask;
import br.com.gigatron.futbusiness.asynctask.evento.AlteraSaldoArquivaEventoTask;
import br.com.gigatron.futbusiness.asynctask.evento.AlteraSaldoNovoEventoTask;
import br.com.gigatron.futbusiness.asynctask.evento.AtualizaListaEventoTask;
import br.com.gigatron.futbusiness.asynctask.evento.EditaEventoTask;
import br.com.gigatron.futbusiness.asynctask.evento.ExistemEventosArquivadosTask;
import br.com.gigatron.futbusiness.asynctask.evento.FinalizaEventoTask;
import br.com.gigatron.futbusiness.asynctask.evento.RemoveEventoTask;
import br.com.gigatron.futbusiness.asynctask.paginafut.CriaJogadoresEventoTask;
import br.com.gigatron.futbusiness.database.FutBusinessDatabase;
import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.JogadorFut;
import br.com.gigatron.futbusiness.model.Usuario;
import br.com.gigatron.futbusiness.ui.adapter.MeusEventosAdapter;

public class MeusEventosView {

    private final Context context;
    private final MeusEventosAdapter adapter;
    private final EventoDao eventoDao;
    private final Fut fut;
    private boolean mostrarArquivados;
    private final TextView novosEventosDisponiveis;
    private final TextView novosArquivamentosDisponiveis;
    private final UsuarioDao usuarioDao;
    private Usuario usuario;

    public MeusEventosView(
            Context context,
            MeusEventosAdapter adapter,
            EventoDao eventoDao,
            Fut fut,
            TextView novosEventosDisponiveis,
            TextView novosArquivamentosDisponiveis,
            UsuarioDao usuarioDao
    ) {
        this.context = context;
        this.adapter = adapter;
        this.eventoDao = eventoDao;
        this.fut = fut;
        this.novosEventosDisponiveis = novosEventosDisponiveis;
        this.novosArquivamentosDisponiveis = novosArquivamentosDisponiveis;
        this.usuarioDao = usuarioDao;
    }

//Public methods  ----------------------------------------------------------------------------------
//Level 1 ------------------------------------------------------------------------------------------
    public void atualiza(boolean mostraArquivados) {
        new AtualizaListaEventoTask(eventoDao, fut, adapter, mostraArquivados).execute();
        new DefineUsuarioTask(usuarioDao, usuario -> {
            this.usuario = usuario;
            atualizaNovosEventosArquivamentosDisponiveis();
        }).execute();
    }

    public void mostrarArquivadosChecked(boolean isChecked) {
        this.mostrarArquivados = isChecked;
    }

    public void defineUsuario(DefineUsuarioListener listener) {
        new DefineUsuarioTask(usuarioDao, listener::aposDefinirUsuario).execute();
    }

//Level 2 ------------------------------------------------------------------------------------------
    public void aumentaSaldoNovoEvento(UsuarioDao usuarioDao) {
        new AlteraSaldoNovoEventoTask(usuario, usuarioDao, AlteraSaldoTask.Acao.AUMENTA).execute();
    }

    public void aumentaSaldoNovoArquivamento(UsuarioDao usuarioDao) {
        new AlteraSaldoArquivaEventoTask(usuario, usuarioDao, AlteraSaldoTask.Acao.AUMENTA).execute();
    }

    public void adicionaJogadoresNoEvento(List<Evento> eventosSelecionados,
                                          List<JogadorFut> jogadoresFutSelecionados,
                                          AdicionaJogadoresNoEventoListener listener) {

       JogadorDao jogadorDao = FutBusinessDatabase.getInstance(context).getJogadorDao();
       new CriaJogadoresEventoTask(
               jogadorDao,
               jogadoresFutSelecionados,
               eventosSelecionados,
               () -> {
                   feedbackAoUser("Jogador(es) adicionado(s)!");
                   listener.aposAdicionarJogadoresNoEvento();
               }).execute();
    }

    public void confirmaRemove(ConfirmaRemoveEventoListener listener) {
        new AlertDialog.Builder(context)
                .setTitle("Remover evento")
                .setMessage("Deseja remover esse(s) " + adapter.getEventosSelecionados().size() +
                        " evento(s) permanentemente?\n\nTodos os dados relacionados a " +
                        "este evento serão excluídos! Inclusive informações de jogadores " +
                        "como calotes e presenças.")
                .setNegativeButton("Não", null)

                .setPositiveButton("Sim", (dialog, which) -> {
                    for (Evento e: adapter.getEventosSelecionados()) {
                        remove(e);
                    }
                    feedbackAoUser("Evento(s) removido(s)!");
                    listener.aposRemover();
                })

                .show();
    }

    public void confirmaFinalizaEvento(
            PaginaEventoView.ConfirmaFinalizaEventoListener listener) {
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Finalizar evento(s)")
                .setMessage("Finalizar o evento significa que não haverá mais nenhuma alteração" +
                        " no evento, seja ela qual for.\n\nConfirme apenas se tiver certeza, " +
                        "pois esta ação não poderá ser desfeita!")
                .setNegativeButton("Cancelar", null)

                .setPositiveButton("Ok", (dialog, which) -> {

                    JogadorDao jogadorDao = FutBusinessDatabase.getInstance(context).getJogadorDao();
                    List<Evento> eventosSelecionados = adapter.getEventosSelecionados();
                    new FinalizaEventoTask(eventosSelecionados, jogadorDao, eventoDao, () -> {
                                feedbackAoUser("Evento(s) finalizado(s)!");
                                listener.aposConfirmar(true);
                            }).execute();

                })
                .show();
    }

    public void arquivaDesarquivaEvento(boolean arquiva, ConfirmaArquivaEventoListener listener) {
        for (Evento e: adapter.getEventosSelecionados()) {
            e.setArquivado(arquiva);
            if (arquiva) {
                new AlteraSaldoArquivaEventoTask(usuario, usuarioDao, AlteraSaldoTask.Acao.DIMINUI)
                        .execute();
            }
            edita(e);
        }
        feedbackAoUser("O(s) evento(s) foram arquivado/desarquivado(s)!");
        listener.aposConfirmar(true);
    }

    public void existemEventosArquivados(ExistemEventosArquivadosListener listener) {
        new ExistemEventosArquivadosTask(eventoDao, fut, listener::aposConferir).execute();
    }

//Private methods ----------------------------------------------------------------------------------
//Level 1 ------------------------------------------------------------------------------------------
    private void atualizaNovosEventosArquivamentosDisponiveis() {
        String novosEventosDisponiveisText = usuario.getSaldoNovoEvento() + " disponíveis";
        String novosArquivamentosDisponiveisText = usuario.getSaldoArquivaEvento() + " disponíveis";
        novosEventosDisponiveis.setText(novosEventosDisponiveisText);
        novosArquivamentosDisponiveis.setText(novosArquivamentosDisponiveisText);
    }

//Level 2 ------------------------------------------------------------------------------------------
    private void feedbackAoUser(String text) {
        new Toast(context);
        Toast.makeText(context, text, Toast.LENGTH_SHORT)
                .show();
    }

    private void remove(Evento evento) {
        JogadorDao jogadorDao = FutBusinessDatabase.getInstance(context).getJogadorDao();
        new RemoveEventoTask(eventoDao, evento, jogadorDao).execute();
        atualiza(mostrarArquivados);
    }

    private void edita(Evento evento) {
        new EditaEventoTask(evento, eventoDao).execute();
        atualiza(mostrarArquivados);
    }

//Interfaces ---------------------------------------------------------------------------------------
    public interface ConfirmaRemoveEventoListener {
        void aposRemover();
    }

    public interface AdicionaJogadoresNoEventoListener {
        void aposAdicionarJogadoresNoEvento();
    }

    public interface ConfirmaArquivaEventoListener {
        void aposConfirmar(boolean isConfirmado);
    }

    public interface ExistemEventosArquivadosListener {
        void aposConferir(boolean existem);
    }

    public interface DefineUsuarioListener {
        void aposDefinirUsuario(Usuario usuario);
    }
}