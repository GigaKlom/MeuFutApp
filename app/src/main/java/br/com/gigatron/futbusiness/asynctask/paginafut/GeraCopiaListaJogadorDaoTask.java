package br.com.gigatron.futbusiness.asynctask.paginafut;

import android.os.AsyncTask;

import java.util.List;

import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.JogadorEvento;

public class GeraCopiaListaJogadorDaoTask extends AsyncTask<Void, Void, List<JogadorEvento>> {
    private final JogadorDao jogadorDao;
    private final Evento eventoCorrespondente;
    private final GeraCopiaListaJogadorDaoListener listener;

    public GeraCopiaListaJogadorDaoTask(JogadorDao jogadorDao,
                                        Evento eventoCorrespondente,
                                        GeraCopiaListaJogadorDaoListener listener) {
        this.jogadorDao = jogadorDao;
        this.eventoCorrespondente = eventoCorrespondente;
        this.listener = listener;
    }

    @Override
    protected List<JogadorEvento> doInBackground(Void... voids) {
        return jogadorDao.getJogadoresEvento(eventoCorrespondente.getEventoId());
    }

    @Override
    protected void onPostExecute(List<JogadorEvento> jogadoresDoEvento) {
        super.onPostExecute(jogadoresDoEvento);
        listener.aposGerarCopiaListaJogadorDao(jogadoresDoEvento);
    }

    public interface GeraCopiaListaJogadorDaoListener {
        void aposGerarCopiaListaJogadorDao(List<JogadorEvento> jogadoresDoEvento);
    }
}
