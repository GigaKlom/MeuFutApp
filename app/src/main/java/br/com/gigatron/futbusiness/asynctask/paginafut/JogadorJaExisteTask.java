package br.com.gigatron.futbusiness.asynctask.paginafut;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.Jogador;

public class JogadorJaExisteTask extends AsyncTask<Void, Void, Boolean> {
    private final JogadorDao jogadorDao;
    private final Fut fut;
    private final String textNome;
    private final Jogador jogador;
    private final JogadorJaExisteListener listener;

    public JogadorJaExisteTask(JogadorDao jogadorDao,
                               Fut fut,
                               String textNome,
                               Jogador jogador,
                               JogadorJaExisteListener listener) {
        this.jogadorDao = jogadorDao;
        this.fut = fut;
        this.textNome = textNome;
        this.jogador = jogador;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        for (Jogador j: jogadorDao.getJogadoresFut((int) fut.getFutId())) {
            if (j.getNome().equals(textNome) && !j.getNome().equals(jogador.getNome())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean jogadorJaExiste) {
        super.onPostExecute(jogadorJaExiste);
        listener.aposVerificar(jogadorJaExiste);
    }

    public interface JogadorJaExisteListener {
        void aposVerificar(Boolean jogadorJaExiste);
    }
}
