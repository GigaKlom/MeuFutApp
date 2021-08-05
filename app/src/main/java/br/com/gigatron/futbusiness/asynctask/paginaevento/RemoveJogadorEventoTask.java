package br.com.gigatron.futbusiness.asynctask.paginaevento;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.JogadorEvento;

public class RemoveJogadorEventoTask extends AsyncTask<Void, Void, Void> {
    private final JogadorEvento jogadorEvento;
    private final JogadorDao jogadorDao;
    private final RemoveJogadorEventoListener listener;

    public RemoveJogadorEventoTask(JogadorEvento jogadorEvento,
                                   JogadorDao jogadorDao,
                                   RemoveJogadorEventoListener listener) {
        this.jogadorEvento = jogadorEvento;
        this.jogadorDao = jogadorDao;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        jogadorDao.removeJogadorEvento(jogadorEvento);
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        listener.aposRemoverJogadorEvento();
    }

    public interface RemoveJogadorEventoListener {
        void aposRemoverJogadorEvento();
    }
}
