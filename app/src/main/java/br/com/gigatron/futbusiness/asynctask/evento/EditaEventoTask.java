package br.com.gigatron.futbusiness.asynctask.evento;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.model.Evento;

public class EditaEventoTask extends AsyncTask<Void, Void, Void> {

    private final Evento evento;
    private final EventoDao eventoDao;

    public EditaEventoTask(Evento evento, EventoDao eventoDao) {
        this.evento = evento;
        this.eventoDao = eventoDao;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        eventoDao.edita(evento);
        return null;
    }
}
