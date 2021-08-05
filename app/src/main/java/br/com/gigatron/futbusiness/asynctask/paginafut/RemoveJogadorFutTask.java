package br.com.gigatron.futbusiness.asynctask.paginafut;

import android.os.AsyncTask;

import java.util.List;

import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.JogadorEvento;
import br.com.gigatron.futbusiness.model.JogadorFut;

public class RemoveJogadorFutTask extends AsyncTask<Void, Void, Void> {
    private final JogadorFut jogadorFut;
    private final JogadorDao jogadorDao;
    private final EventoDao eventoDao;
    private final Fut fut;

    public RemoveJogadorFutTask(JogadorFut jogadorFut, JogadorDao jogadorDao,
                                EventoDao eventoDao, Fut fut) {
        this.jogadorFut = jogadorFut;
        this.jogadorDao = jogadorDao;
        this.eventoDao = eventoDao;
        this.fut = fut;
    }

    @Override
    protected Void doInBackground(Void... voids) {
//        for (Evento e: eventoDao.getEventos((int) fut.getFutId())) {
//            if (e.isAtivo()) {
//                for (JogadorEvento jogadorEvento: jogadorDao.getJogadoresEvento((int) e.getEventoId())) {
//                    if (jogadorFut.getJogadorFutId() == jogadorEvento.getJogadorEventoId()) {
//                        jogadorDao.removeJogadorEvento(jogadorEvento);
//                    }
//                }
//            }
//        }
        List<JogadorEvento> jogadoresEventoRelacionadosAoJogadorFut =
                jogadorDao.getJogadoresEventoRelacionadosAoJogadorFut(jogadorFut.getJogadorFutId());

        for (JogadorEvento jogadorEvento: jogadoresEventoRelacionadosAoJogadorFut) {
            if (jogadorEvento.isAtivo()) {
                jogadorDao.removeJogadorEvento(jogadorEvento);
            }
        }


        jogadorDao.removeJogadorFut(jogadorFut);
        return null;
    }
}
