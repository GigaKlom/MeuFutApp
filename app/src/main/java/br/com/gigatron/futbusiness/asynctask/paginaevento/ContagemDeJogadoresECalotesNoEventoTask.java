package br.com.gigatron.futbusiness.asynctask.paginaevento;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.JogadorEvento;

public class ContagemDeJogadoresECalotesNoEventoTask extends AsyncTask<Void, Void, Integer[]> {

    private final EventoDao eventoDao;
    private final JogadorDao jogadorDao;
    private final Evento evento;
    private final ContagemDeJogadoresECalotesNoEventoListener listener;

    public ContagemDeJogadoresECalotesNoEventoTask(EventoDao eventoDao,
                                                   JogadorDao jogadorDao,
                                                   Evento evento,
                                                   ContagemDeJogadoresECalotesNoEventoListener
                                                           listener) {
        this.eventoDao = eventoDao;
        this.jogadorDao = jogadorDao;
        this.evento = evento;
        this.listener = listener;
    }

    @Override
    protected Integer[] doInBackground(Void... voids) {
        List<JogadorEvento> jogadoresDiferentes = new ArrayList<>();
        int calotes = 0;
        int vezesQueFuraram = 0;
        for (Evento e: eventoDao.getEventos((int) evento.getFutId())) {
            if (e.getMensalId() == evento.getMensalId()) {
                if (!e.isAtivo()) {
                    for (JogadorEvento jogadorEvento: jogadorDao.getJogadoresEvento(e.getEventoId())) {
                        if (!jogadoresDiferentes.contains(jogadorEvento)) {
                            jogadoresDiferentes.add(jogadorEvento);
                        }
                        if (jogadorEvento.isFuro()) {
                            vezesQueFuraram++;
                        } else {
                            if (!jogadorEvento.isPago()) {
                                calotes++;
                            }
                        }
                    }
                }
            }
        }
        return new Integer[] {jogadoresDiferentes.size(), calotes, vezesQueFuraram};
    }

    @Override
    protected void onPostExecute(Integer[] integers) {
        super.onPostExecute(integers);
        listener.aposContarJogadoresECalotes(integers[0], integers[1], integers[2]);
    }

    public interface ContagemDeJogadoresECalotesNoEventoListener {
        void aposContarJogadoresECalotes(int numeroJogadores, int numeroCalotes, int nVezesQueFuraram);
    }
}
