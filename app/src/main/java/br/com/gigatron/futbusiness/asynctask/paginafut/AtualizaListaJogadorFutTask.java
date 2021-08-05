package br.com.gigatron.futbusiness.asynctask.paginafut;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.Jogador;
import br.com.gigatron.futbusiness.model.JogadorFut;
import br.com.gigatron.futbusiness.ui.adapter.PaginaFutAdapter;

public class AtualizaListaJogadorFutTask extends AsyncTask<Void, Void, List<JogadorFut>> {
    private final JogadorDao dao;
    private final Fut fut;
    private final PaginaFutAdapter adapter;
    private final AtualizaListaJogadorFutListener listener;

    public AtualizaListaJogadorFutTask(JogadorDao dao,
                                       Fut fut,
                                       PaginaFutAdapter adapter,
                                       AtualizaListaJogadorFutListener listener) {
        this.dao = dao;
        this.fut = fut;
        this.adapter = adapter;
        this.listener = listener;
    }

    @Override
    protected List<JogadorFut> doInBackground(Void... voids) {
        List<JogadorFut> jogadoresFutEmOrdemDeStatus = new ArrayList<>();
        List<JogadorFut> jogadoresAvulsos = new ArrayList<>();
        List<JogadorFut> jogadoresFut = dao.getJogadoresFut((int) fut.getFutId());
        for (JogadorFut jogadorFut: jogadoresFut) {
            if (jogadorFut.isMensalista()) {
                jogadoresFutEmOrdemDeStatus.add(jogadorFut);
            } else {
                jogadoresAvulsos.add(jogadorFut);
            }
        }
        Collections.sort(jogadoresFutEmOrdemDeStatus, Jogador::compareTo);
        Collections.sort(jogadoresAvulsos, Jogador::compareTo);
        jogadoresFutEmOrdemDeStatus.addAll(jogadoresAvulsos);
        return jogadoresFutEmOrdemDeStatus;
    }

    @Override
    protected void onPostExecute(List<JogadorFut> jogadoresFut) {
        super.onPostExecute(jogadoresFut);
        adapter.atualiza(jogadoresFut);
        listener.aposAtualizarListaJogadorFut();
    }

    public interface AtualizaListaJogadorFutListener {
        void aposAtualizarListaJogadorFut();
    }
}
