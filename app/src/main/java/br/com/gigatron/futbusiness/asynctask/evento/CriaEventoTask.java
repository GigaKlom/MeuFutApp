package br.com.gigatron.futbusiness.asynctask.evento;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Fut;

public class CriaEventoTask extends AsyncTask<Void, Void, Void> {
    private final Evento evento;
    private final Fut fut;
    private final EventoDao eventoDao;

    public CriaEventoTask(Evento evento, Fut fut, EventoDao eventoDao) {
        this.evento = evento;
        this.fut = fut;
        this.eventoDao = eventoDao;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        evento.setFutId(fut.getFutId());
        eventoDao.cria(evento);
        return null;
    }
}
