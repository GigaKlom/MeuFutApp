package br.com.gigatron.futbusiness.asynctask.paginafut;

import android.os.AsyncTask;

import java.util.List;

import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.JogadorEvento;
import br.com.gigatron.futbusiness.model.JogadorFut;

public class AlteraStatusJogadorTask extends AsyncTask<Void, Void, Void> {
    private final JogadorFut jogadorFut;
    private final JogadorDao jogadorDao;
    private final EventoDao eventoDao;
    private final Fut fut;

    public AlteraStatusJogadorTask(JogadorFut jogadorFut, JogadorDao jogadorDao,
                                   EventoDao eventoDao, Fut fut) {
        this.jogadorFut = jogadorFut;
        this.jogadorDao = jogadorDao;
        this.eventoDao = eventoDao;
        this.fut = fut;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<JogadorEvento> jogadoresEventoRelacionadosAoJogadorFut =
                jogadorDao.getJogadoresEventoRelacionadosAoJogadorFut(jogadorFut.getJogadorFutId());

        for (JogadorEvento jogadorEvento: jogadoresEventoRelacionadosAoJogadorFut) {
            if (jogadorEvento.isAtivo()) {
                jogadorEvento.setMensalista(jogadorFut.isMensalista());
                jogadorEvento.setPago(jogadorFut.isMensalista());
                jogadorDao.editaJogadorEvento(jogadorEvento);
            }
        }


//        for (Evento e: eventoDao.getEventos((int) fut.getFutId())) {
//            if (e.isAtivo()) {
//                for (JogadorEvento jogadorEvento: jogadorDao.getJogadoresEvento(e.getEventoId())) {
//                    if (jogadorFut.getJogadorFutId() == jogadorEvento.getJogadorEventoId()) {
//                        jogadorEvento.setMensalista(jogadorFut.isMensalista());
//                        jogadorEvento.setPago(jogadorFut.isMensalista());
//                        jogadorDao.editaJogadorEvento(jogadorEvento);
//                    }
//                }
//            }
//        }
        jogadorDao.editaJogadorFut(jogadorFut);
        return null;
    }
}
