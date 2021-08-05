package br.com.gigatron.futbusiness.asynctask.paginafut;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.JogadorFut;

public class SalvaJogadorFutTask extends AsyncTask<Void, Void, Boolean> {

    private final boolean modoEditaJogador;
    private final JogadorDao jogadorDao;
    private final JogadorFut jogadorFut;
    private final SalvaJogadorFutListener listener;

    public SalvaJogadorFutTask(boolean modoEditaJogador,
                               JogadorDao jogadorDao,
                               JogadorFut jogadorFut,
                               SalvaJogadorFutListener listener) {
        this.modoEditaJogador = modoEditaJogador;
        this.jogadorDao = jogadorDao;
        this.jogadorFut = jogadorFut;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (!modoEditaJogador) {
            jogadorDao.criaJogadorFut(jogadorFut);
            return true;
        }
        jogadorDao.editaJogadorFut(jogadorFut);
        return false;
    }

    @Override
    protected void onPostExecute(Boolean isCriaJogador) {
        super.onPostExecute(isCriaJogador);
        listener.aposSalvar(isCriaJogador);
    }

    public interface SalvaJogadorFutListener {
        void aposSalvar(Boolean isCriaJogador);
    }
}
