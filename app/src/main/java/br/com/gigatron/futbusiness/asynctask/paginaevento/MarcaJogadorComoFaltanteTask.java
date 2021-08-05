package br.com.gigatron.futbusiness.asynctask.paginaevento;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.JogadorEvento;

public class MarcaJogadorComoFaltanteTask extends AsyncTask<Void, Void, Void> {

    private final JogadorEvento jogadorEvento;
    private final JogadorDao jogadorDao;
    private final MarcaJogadorComoFaltanteListener listener;

    public MarcaJogadorComoFaltanteTask(JogadorEvento jogadorEvento,
                                        JogadorDao jogadorDao,
                                        MarcaJogadorComoFaltanteListener listener) {
        this.jogadorEvento = jogadorEvento;
        this.jogadorDao = jogadorDao;
        this.listener = listener;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        if (!jogadorEvento.isFuro()) {
            jogadorEvento.setFuro(true);
            jogadorEvento.setPago(false);
        } else {
            jogadorEvento.setFuro(false);
            jogadorEvento.setPago((jogadorEvento.isMensalista() && jogadorEvento.getValorMensal() == 0) ||
                            (!jogadorEvento.isMensalista() && jogadorEvento.getValorAvulso() == 0));
        }
        jogadorDao.editaJogadorEvento(jogadorEvento);
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        listener.aposMarcarJogadorComoFaltante();
    }

    public interface MarcaJogadorComoFaltanteListener {
        void aposMarcarJogadorComoFaltante();
    }
}
