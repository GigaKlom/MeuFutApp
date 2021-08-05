package br.com.gigatron.futbusiness.asynctask.evento;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Jogador;
import br.com.gigatron.futbusiness.model.JogadorEvento;

public class AtualizaDadosEventoTask extends AsyncTask<Void, Void, Void> {
    private final Evento evento;
    private final JogadorDao jogadorDao;
    private final EventoDao eventoDao;
    private final AtualizaDadosEventoListener listener;

    public AtualizaDadosEventoTask(Evento evento,
                                   JogadorDao jogadorDao,
                                   EventoDao eventoDao,
                                   AtualizaDadosEventoListener listener) {
        this.evento = evento;
        this.jogadorDao = jogadorDao;
        this.eventoDao = eventoDao;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        evento.setGanhoPrevisto(0);
        evento.setGanhoObtido(0);
        evento.setNumeroDeJogadoresNoEvento(0);
        evento.setNumeroDeJogadoresComPagamentoConfirmado(0);
        for (JogadorEvento jogadorEvento: jogadorDao.getJogadoresEvento(evento.getEventoId())) {
            evento.setNumeroDeJogadoresNoEvento(
                    evento.getNumeroDeJogadoresNoEvento() + 1
            );
            if (!jogadorEvento.isMensalista()) {
                evento.setGanhoPrevisto(
                        evento.getGanhoPrevisto() + jogadorEvento.getValorAvulso()
                );
                if (jogadorEvento.isPago()) {
                    evento.setGanhoObtido(
                            evento.getGanhoObtido() + jogadorEvento.getValorAvulso()
                    );
                }
            }
            if (jogadorEvento.isPago()) {
                evento.setNumeroDeJogadoresComPagamentoConfirmado(
                        evento.getNumeroDeJogadoresComPagamentoConfirmado() + 1
                );
            }
        }
        eventoDao.edita(evento);
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        listener.aposAtualizarDadosEvento();
    }

    public interface AtualizaDadosEventoListener {
        void aposAtualizarDadosEvento();
    }
}
