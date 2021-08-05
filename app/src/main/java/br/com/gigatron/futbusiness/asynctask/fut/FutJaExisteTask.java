package br.com.gigatron.futbusiness.asynctask.fut;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.FutDao;
import br.com.gigatron.futbusiness.model.Fut;

public class FutJaExisteTask extends AsyncTask<Void, Void, Boolean> {
    private final FutDao futDao;
    private final String textLocal;
    private final boolean mensal;
    private final Fut fut;
    private final FutExisteListener listener;

    public FutJaExisteTask(FutDao futDao,
                           String textLocal,
                           boolean mensal,
                           Fut fut,
                           FutExisteListener listener) {
        this.futDao = futDao;
        this.textLocal = textLocal;
        this.mensal = mensal;
        this.fut = fut;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        for (Fut f: futDao.getList()) {
            if (f.getLocal().equals(textLocal) && !f.getLocal().equals(fut.getLocal()) && f.isMensal() == mensal) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        listener.aposVerificarSeFutExiste(aBoolean);
    }

    public interface FutExisteListener {
        void aposVerificarSeFutExiste(Boolean aBoolean);
    }
}
