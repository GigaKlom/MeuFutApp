package br.com.gigatron.futbusiness.asynctask.fut;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.JogadorEvento;
import br.com.gigatron.futbusiness.model.JogadorFut;

public class AlteraValoresPadraoDosJogadoresTask extends AsyncTask<Void, Void, Void> {

    private final JogadorDao jogadorDao;
    private final EventoDao eventoDao;
    private final Fut fut;
    private final String textValorAvulso;
    private final String textValorMensal;
    private AlteraValoresPadraoDosJogadoresListener listener;

    public AlteraValoresPadraoDosJogadoresTask(
            JogadorDao jogadorDao,
            EventoDao eventoDao,
            Fut fut,
            String textValorAvulso,
            String textValorMensal,
            AlteraValoresPadraoDosJogadoresListener listener
    ) {
        this.jogadorDao = jogadorDao;
        this.eventoDao = eventoDao;
        this.fut = fut;
        this.textValorAvulso = textValorAvulso;
        this.textValorMensal = textValorMensal;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (JogadorFut jogadorFut: jogadorDao.getJogadoresFut((int) fut.getFutId())) {
            if (jogadorFut.getValorAvulso() == fut.getValorAvulso()) {
                jogadorFut.setValorAvulso(Double.parseDouble(textValorAvulso));
                jogadorDao.editaJogadorFut(jogadorFut);
            }
            if (jogadorFut.getValorMensal() == fut.getValorMensal()) {
                jogadorFut.setValorMensal(Double.parseDouble(textValorMensal));
                jogadorDao.editaJogadorFut(jogadorFut);
            }
        }
        for (Evento e: eventoDao.getEventos((int) fut.getFutId())) {
            if (e.isAtivo()) {
                for (JogadorEvento jogadorEvento: jogadorDao.getJogadoresEvento((int) e.getEventoId())) {
                    if (jogadorEvento.getValorAvulso() == fut.getValorAvulso()) {
                        jogadorEvento.setValorAvulso(Double.parseDouble(textValorAvulso));
                        jogadorDao.editaJogadorEvento(jogadorEvento);
                    }
                    if (jogadorEvento.getValorMensal() == fut.getValorMensal()) {
                        jogadorEvento.setValorMensal(Double.parseDouble(textValorMensal));
                        jogadorDao.editaJogadorEvento(jogadorEvento);
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        listener.aposAlterarValoresPadraoDosJogadores();
    }

    public interface AlteraValoresPadraoDosJogadoresListener {
        void aposAlterarValoresPadraoDosJogadores();
    }
}
