package br.com.gigatron.futbusiness.asynctask.paginaevento;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Jogador;
import br.com.gigatron.futbusiness.model.JogadorEvento;
import br.com.gigatron.futbusiness.ui.adapter.PaginaEventoAdapter;

public class AtualizaListaJogadorEventoTask extends AsyncTask<Void, Void, List<JogadorEvento>> {
    private final JogadorDao jogadorDao;
    private final Evento evento;
    private final PaginaEventoAdapter adapter;
    private final AtualizaListaJogadorEventoListener listener;

    public AtualizaListaJogadorEventoTask(JogadorDao jogadorDao,
                                          Evento evento,
                                          PaginaEventoAdapter adapter,
                                          AtualizaListaJogadorEventoListener listener) {
        this.jogadorDao = jogadorDao;
        this.evento = evento;
        this.adapter = adapter;
        this.listener = listener;
    }

    @Override
    protected List<JogadorEvento> doInBackground(Void... voids) {
        List<JogadorEvento> avulsos = new ArrayList<>();
        List<JogadorEvento> mensalistas = new ArrayList<>();
        for (JogadorEvento jogadorEvento: jogadorDao.getJogadoresEvento(evento.getEventoId())) {
            if (jogadorEvento.isMensalista()) {
                mensalistas.add(jogadorEvento);
            } else {
                avulsos.add(jogadorEvento);
            }
        }
        List<JogadorEvento> jogadoresEventoEmOrdemDeAvulsosPrimeiro = new ArrayList<>(avulsos);
        Collections.sort(jogadoresEventoEmOrdemDeAvulsosPrimeiro, Jogador::compareTo);
        Collections.sort(mensalistas, Jogador::compareTo);
        jogadoresEventoEmOrdemDeAvulsosPrimeiro.addAll(mensalistas);
        return jogadoresEventoEmOrdemDeAvulsosPrimeiro;
    }

    @Override
    protected void onPostExecute(List<JogadorEvento> jogadores) {
        super.onPostExecute(jogadores);
        adapter.atualiza(jogadores);
        listener.aposAtualizarListaJogadorEvento();
    }

    public interface AtualizaListaJogadorEventoListener {
        void aposAtualizarListaJogadorEvento();
    }
}
