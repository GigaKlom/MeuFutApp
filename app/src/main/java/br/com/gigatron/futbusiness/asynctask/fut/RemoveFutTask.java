package br.com.gigatron.futbusiness.asynctask.fut;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.FutDao;
import br.com.gigatron.futbusiness.model.Fut;

public class RemoveFutTask extends AsyncTask<Void, Void, Void> {
    private final FutDao futDao;
    private final Fut fut;

    public RemoveFutTask(FutDao futDao, Fut fut) {
        this.futDao = futDao;
        this.fut = fut;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        futDao.remove(fut);
        return null;
    }
}
