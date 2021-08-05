package br.com.gigatron.futbusiness.asynctask.paginaevento;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Jogador;
import br.com.gigatron.futbusiness.model.JogadorEvento;

public class BuscaTotalObtidoELucroPrejuizoTask extends AsyncTask<Void, Void, Double[]> {
    private final EventoDao eventoDao;
    private final JogadorDao jogadorDao;
    private final Evento evento;
    private final BuscaTotalObtidoELucroPrejuizoListener listener;

    public BuscaTotalObtidoELucroPrejuizoTask(EventoDao eventoDao,
                                              JogadorDao jogadorDao,
                                              Evento evento,
                                              BuscaTotalObtidoELucroPrejuizoListener listener) {
        this.eventoDao = eventoDao;
        this.jogadorDao = jogadorDao;
        this.evento = evento;
        this.listener = listener;
    }

    @Override
    protected Double[] doInBackground(Void... voids) {
        double totalObtidoAvulsos = 0;
        List<JogadorEvento> mensalistas = new ArrayList<>();
        for (Evento e: eventoDao.getEventos((int) evento.getFutId())) {
            if (e.getMensalId() == evento.getMensalId()) {
                if (!e.isAtivo()) {
                    totalObtidoAvulsos += e.getGanhoObtido();
                    for (JogadorEvento jogadorEvento: jogadorDao.getJogadoresEvento(e.getEventoId())) {
                        if (jogadorEvento.isMensalista() && jogadorEvento.isPago()) {
                            if (!mensalistas.contains(jogadorEvento)) {
                                mensalistas.add(jogadorEvento);
                            }
                        }
                    }
                }
            }
        }
        double totalObtidoMensalistas = 0;
        for (Jogador j: mensalistas) {
            totalObtidoMensalistas += j.getValorMensal();
        }
        return new Double[] {totalObtidoAvulsos, totalObtidoMensalistas};
    }

    @Override
    protected void onPostExecute(Double[] doubles) {
        super.onPostExecute(doubles);
        listener.aposObterTotalObtidoELucroPrejuizo(doubles[0], doubles[1]);
    }

    public interface BuscaTotalObtidoELucroPrejuizoListener {
        void aposObterTotalObtidoELucroPrejuizo(double totalObtidoAvulsos,
                                                double totalObtidoMensalistas);
    }
}
