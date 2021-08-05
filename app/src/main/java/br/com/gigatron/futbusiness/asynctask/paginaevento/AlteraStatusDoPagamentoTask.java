package br.com.gigatron.futbusiness.asynctask.paginaevento;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.JogadorEvento;

public class AlteraStatusDoPagamentoTask extends AsyncTask<Void, Void, Void> {
    private final JogadorEvento jogadorEvento;
    private final JogadorDao jogadorDao;
    private final AlteraStatusDoPagamentoListener alteraStatusDoPagamentoListener;

    public AlteraStatusDoPagamentoTask(JogadorEvento jogadorEvento,
                                       JogadorDao jogadorDao,
                                       AlteraStatusDoPagamentoListener alteraStatusDoPagamentoListener) {
        this.jogadorEvento = jogadorEvento;
        this.jogadorDao = jogadorDao;
        this.alteraStatusDoPagamentoListener = alteraStatusDoPagamentoListener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        jogadorEvento.setPago(!jogadorEvento.isPago());
        jogadorDao.editaJogadorEvento(jogadorEvento);
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        alteraStatusDoPagamentoListener.aposAlterarStatusDoPagamento();
    }

    public interface AlteraStatusDoPagamentoListener {
        void aposAlterarStatusDoPagamento();
    }
}
