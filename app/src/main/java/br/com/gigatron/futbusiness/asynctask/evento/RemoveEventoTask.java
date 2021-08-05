package br.com.gigatron.futbusiness.asynctask.evento;

import android.os.AsyncTask;

import java.util.List;

import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.JogadorEvento;

public class RemoveEventoTask extends AsyncTask<Void, Void, Void> {
    private final EventoDao dao;
    private final Evento evento;
    private final JogadorDao jogadorDao;

    public RemoveEventoTask(EventoDao dao, Evento evento, JogadorDao jogadorDao) {
        this.dao = dao;
        this.evento = evento;
        this.jogadorDao = jogadorDao;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<JogadorEvento> jogadoresEvento = jogadorDao.getJogadoresEvento(evento.getEventoId());
        for (JogadorEvento jogadorEvento: jogadoresEvento) {
            jogadorDao.removeJogadorEvento(jogadorEvento);
        }
        dao.remove(evento);
        return null;
    }
}
