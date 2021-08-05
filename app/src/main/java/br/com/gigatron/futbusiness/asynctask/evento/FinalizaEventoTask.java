package br.com.gigatron.futbusiness.asynctask.evento;

import android.os.AsyncTask;

import java.util.List;

import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.JogadorEvento;

public class FinalizaEventoTask extends AsyncTask<Void, Void, Void> {
    private final List<Evento> eventosSelecionados;
    private final JogadorDao jogadorDao;
    private final EventoDao eventoDao;
    private final FinalizaEventoListener listener;

    public FinalizaEventoTask(
            List<Evento> eventosSelecionados,
            JogadorDao jogadorDao,
            EventoDao eventoDao,
            FinalizaEventoListener listener
    ) {
        this.eventosSelecionados = eventosSelecionados;
        this.jogadorDao = jogadorDao;
        this.eventoDao = eventoDao;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (Evento e: eventosSelecionados) {
            e.setAtivo(false);

            List<JogadorEvento> jogadoresEvento =
                    jogadorDao.getJogadoresEvento(e.getEventoId());

            for (JogadorEvento jogadorEvento: jogadoresEvento) {
                jogadorEvento.setAtivo(false);
                jogadorDao.editaJogadorEvento(jogadorEvento);
            }
            eventoDao.edita(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        listener.aposfinalizarEvento();
    }

    public interface FinalizaEventoListener {
        void aposfinalizarEvento();
    }
}
