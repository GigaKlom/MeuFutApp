package br.com.gigatron.futbusiness.asynctask.paginafut;

import android.os.AsyncTask;

import java.util.List;

import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.JogadorEvento;
import br.com.gigatron.futbusiness.model.JogadorFut;

public class CriaJogadoresEventoTask extends AsyncTask<Void, Void, Void> {

    private final JogadorDao jogadorDao;
    private final List<JogadorFut> jogadoresFutSelecionados;
    private final List<Evento> eventosSelecionados;
    private final CriaJogadoresEventoListener listener;

    public CriaJogadoresEventoTask(
            JogadorDao jogadorDao,
            List<JogadorFut> jogadoresFutSelecionados,
            List<Evento> eventosSelecionados,
            CriaJogadoresEventoListener listener) {
        this.jogadorDao = jogadorDao;
        this.jogadoresFutSelecionados = jogadoresFutSelecionados;
        this.eventosSelecionados = eventosSelecionados;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (Evento evento: eventosSelecionados) {
            for (JogadorFut jogadorFut: jogadoresFutSelecionados) {
                if (evento.isAtivo() && !jogadorExisteNoEvento(evento, jogadorFut)) {
                    JogadorEvento jogadorEvento = criaJogadorEvento(evento, jogadorFut);
                    jogadorDao.criaJogadorEvento(jogadorEvento);
                }
            }
        }
        return null;
    }

    private boolean jogadorExisteNoEvento(Evento evento, JogadorFut jogadorFut) {
        for (JogadorEvento jogadorEvento: jogadorDao.getJogadoresEvento(evento.getEventoId())) {
            if (jogadorEvento.getJogadorFutId() == jogadorFut.getJogadorFutId()) {
                return true;
            }
        }
        return false;
    }

    private JogadorEvento criaJogadorEvento(Evento evento, JogadorFut jogadorFut) {
        JogadorEvento jogadorEvento = new JogadorEvento();
        jogadorEvento.setJogadorFutId(jogadorFut.getJogadorFutId());
        jogadorEvento.setNome(jogadorFut.getNome());
        jogadorEvento.setValorAvulso(jogadorFut.getValorAvulso());
        jogadorEvento.setValorMensal(jogadorFut.getValorMensal());
        jogadorEvento.setMensalista(jogadorFut.isMensalista());
        jogadorEvento.setEventoId(evento.getEventoId());
        jogadorEvento.setPago(jogadorFut.isMensalista() || jogadorFut.getValorAvulso() == 0);
        jogadorEvento.setFuro(false);
        jogadorEvento.setAtivo(true);
        return jogadorEvento;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        listener.aposCriarJogadoresEvento();
    }

    public interface CriaJogadoresEventoListener {
        void aposCriarJogadoresEvento();
    }
}
