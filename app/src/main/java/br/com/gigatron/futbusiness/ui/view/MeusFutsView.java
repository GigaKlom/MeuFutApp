package br.com.gigatron.futbusiness.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.gigatron.futbusiness.asynctask.DefineUsuarioTask;
import br.com.gigatron.futbusiness.asynctask.fut.AlteraSaldoNovoFutTask;
import br.com.gigatron.futbusiness.asynctask.fut.AtualizaListaFutTask;
import br.com.gigatron.futbusiness.asynctask.fut.RemoveFutTask;
import br.com.gigatron.futbusiness.asynctask.fut.CopiaJogadoresParaFutTask;
import br.com.gigatron.futbusiness.database.dao.FutDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.JogadorFut;
import br.com.gigatron.futbusiness.model.Usuario;
import br.com.gigatron.futbusiness.ui.adapter.MeusFutsAdapter;

public class MeusFutsView {

    private final Context context;
    private final MeusFutsAdapter adapter;
    private final FutDao futDao;
    private final TextView novosFutsDisponiveis;
    private final UsuarioDao usuarioDao;
    private Usuario usuario;

    public MeusFutsView(
            Context context,
            MeusFutsAdapter adapter,
            FutDao futDao,
            TextView novosFutsDisponiveis,
            UsuarioDao usuarioDao
    ) {
        this.context = context;
        this.adapter = adapter;
        this.futDao = futDao;
        this.novosFutsDisponiveis = novosFutsDisponiveis;
        this.usuarioDao = usuarioDao;
    }

//Public methods -----------------------------------------------------------------------------------
//Level 1 ------------------------------------------------------------------------------------------
    public void atualiza() {
        new AtualizaListaFutTask(adapter, futDao).execute();
        new DefineUsuarioTask(usuarioDao, usuario -> {
            this.usuario = usuario;
            atualizaNovosFutsDisponiveis();
        }).execute();
    }

    public void aumentaSaldoNovoFut(UsuarioDao usuarioDao) {
        new AlteraSaldoNovoFutTask(usuario, usuarioDao, AlteraSaldoNovoFutTask.Acao.AUMENTA)
                .execute();
    }

    public void confirmaRemove(ConfirmaRemoveFutListener listener) {
        new AlertDialog.Builder(context)
                .setTitle("Remover fut")
                .setMessage("Deseja remover esse(s) " + adapter.getFutsSelecionados().size() +
                        " fut(s) permanentemente?")
                .setNegativeButton("Não", null)

                .setPositiveButton("Sim", (dialog, which) -> {
                    for (Fut f: adapter.getFutsSelecionados()) {
                        remove(f);
                    }
                    feedbackAoUser("Fut(s) removido(s)!");
                    listener.aposRemover();
                })
                .show();
    }

//Level 2 ------------------------------------------------------------------------------------------
    public void insereJogadoresCopiados(List<JogadorFut> jogadoresAInserir,
                                        JogadorDao jogadorDao,
                                        List<Fut> futsSelecionados,
                                        InsereJogadoresCopiadosListener listener) {

        new CopiaJogadoresParaFutTask(jogadorDao, jogadoresAInserir, futsSelecionados, () -> {
                    feedbackAoUser("Jogadore(s) copiado(s)!");
                    listener.aposInserirJogadoresCopiados();
                }).execute();
    }

//Level 4 ------------------------------------------------------------------------------------------
    public void remove(Fut fut) {
        new RemoveFutTask(futDao, fut).execute();
        atualiza();
    }

//Private methods ----------------------------------------------------------------------------------
    private void atualizaNovosFutsDisponiveis() {
        String novosFutsDisponiveisText = usuario.getSaldoNovoFut() + " disponíveis";
        novosFutsDisponiveis.setText(novosFutsDisponiveisText);
    }

    private void feedbackAoUser(String text) {
        new Toast(context);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

//Interfaces ---------------------------------------------------------------------------------------
    public interface ConfirmaRemoveFutListener {
        void aposRemover();
    }

    public interface InsereJogadoresCopiadosListener {
        void aposInserirJogadoresCopiados();
    }
}
