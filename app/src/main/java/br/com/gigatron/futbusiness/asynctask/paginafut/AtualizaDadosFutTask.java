package br.com.gigatron.futbusiness.asynctask.paginafut;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.FutDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.JogadorFut;

public class AtualizaDadosFutTask extends AsyncTask<Void, Void, Void> {
    private final FutDao futDao;
    private final JogadorDao jogadorDao;
    private final Fut fut;
    private final AtualizaDadosFutListener listener;

    public AtualizaDadosFutTask(FutDao futDao,
                                JogadorDao jogadorDao,
                                Fut fut,
                                AtualizaDadosFutListener listener) {
        this.futDao = futDao;
        this.jogadorDao = jogadorDao;
        this.fut = fut;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        fut.setNumeroJogadores(0);
        fut.setNumeroMensalistas(0);
        fut.setGanhoEsperadoMensalistas(0);
        fut.setGanhoEsperadoAvulsos(0);
        fut.setLucroPrejuizo(0);
        for (JogadorFut jogadorFut: jogadorDao.getJogadoresFut((int) fut.getFutId())) {
            fut.setNumeroJogadores(
                    fut.getNumeroJogadores() + 1
            );
            if (jogadorFut.isMensalista()) {
                fut.setNumeroMensalistas(
                        fut.getNumeroMensalistas() + 1
                );
                fut.setGanhoEsperadoMensalistas(
                        fut.getGanhoEsperadoMensalistas() + jogadorFut.getValorMensal()
                );
            } else {
                fut.setGanhoEsperadoAvulsos(
                        fut.getGanhoEsperadoAvulsos() + jogadorFut.getValorAvulso()
                );
            }
        }
        fut.setLucroPrejuizo(
                fut.getGanhoEsperadoMensalistas() - fut.getAluguelDaQuadra()
        );
        futDao.edita(fut);
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        listener.aposAtualizarDadosFut();
    }

    public interface AtualizaDadosFutListener {
        void aposAtualizarDadosFut();
    }
}
