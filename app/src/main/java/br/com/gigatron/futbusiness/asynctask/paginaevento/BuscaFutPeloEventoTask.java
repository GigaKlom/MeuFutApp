package br.com.gigatron.futbusiness.asynctask.paginaevento;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.FutDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Fut;

public class BuscaFutPeloEventoTask extends AsyncTask<Void, Void, Fut> {
    private final FutDao futDao;
    private final Evento evento;
    private final BuscaFutPeloEventoListener listener;

    public BuscaFutPeloEventoTask(FutDao futDao,
                                  Evento evento,
                                  BuscaFutPeloEventoListener listener) {
        this.futDao = futDao;
        this.evento = evento;
        this.listener = listener;
    }

    @Override
    protected Fut doInBackground(Void... voids) {
        for (Fut f: futDao.getList()) {
            if (f.getFutId() == evento.getFutId()) {
                return f;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Fut fut) {
        super.onPostExecute(fut);
        listener.aposBuscarFutPeloEvento(fut);
    }

    public interface BuscaFutPeloEventoListener {
        void aposBuscarFutPeloEvento(Fut fut);
    }
}
