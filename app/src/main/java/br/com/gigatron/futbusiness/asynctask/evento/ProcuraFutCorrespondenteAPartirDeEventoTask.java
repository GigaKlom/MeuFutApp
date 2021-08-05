package br.com.gigatron.futbusiness.asynctask.evento;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.FutDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Fut;

public class ProcuraFutCorrespondenteAPartirDeEventoTask extends AsyncTask<Void, Void, Fut> {
    private final FutDao futDao;
    private final Evento evento;
    private final ProcuraFutCorrespondenteAPartirDeEventoListener listener;

    public ProcuraFutCorrespondenteAPartirDeEventoTask(FutDao futDao,
                                                       Evento evento,
                                                       ProcuraFutCorrespondenteAPartirDeEventoListener listener) {
        this.futDao = futDao;
        this.evento = evento;
        this.listener = listener;
    }

    @Override
    protected Fut doInBackground(Void... voids) {
        Fut fut = null;
        for (Fut f: futDao.getList()) {
            if (f.getFutId() == evento.getFutId()) {
                fut = f;
                break;
            }
        }
        return fut;
    }

    @Override
    protected void onPostExecute(Fut fut) {
        super.onPostExecute(fut);
        listener.aposEncontrarFut(fut);
    }

    public interface ProcuraFutCorrespondenteAPartirDeEventoListener {
        void aposEncontrarFut(Fut fut);
    }
}
