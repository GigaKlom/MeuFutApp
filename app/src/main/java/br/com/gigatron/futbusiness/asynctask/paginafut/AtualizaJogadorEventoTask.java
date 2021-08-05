package br.com.gigatron.futbusiness.asynctask.paginafut;

import android.os.AsyncTask;

import java.util.List;

import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.JogadorEvento;
import br.com.gigatron.futbusiness.model.JogadorFut;

public class AtualizaJogadorEventoTask extends AsyncTask<Void, Void, Void> {
    private final EventoDao eventoDao;
    private final Fut fut;
    private final JogadorDao jogadorDao;
    private final JogadorFut jogadorFut;
    private final String textNome;
    private final boolean checkedJogadorMensalista;
    private final boolean checkedPadraoAvulso;
    private final String textValorAvulso;
    private final boolean checkedPadraoMensal;
    private final String textValorMensal;
    private final AtualizaJogadorEventoListener listener;

    public AtualizaJogadorEventoTask(EventoDao eventoDao, Fut fut,
                                     JogadorDao jogadorDao, JogadorFut jogadorFut,
                                     String textNome, boolean checkedJogadorMensalista,
                                     boolean checkedPadraoAvulso, String textValorAvulso,
                                     boolean checkedPadraoMensal, String textValorMensal,
                                     AtualizaJogadorEventoListener listener) {
        this.eventoDao = eventoDao;
        this.jogadorDao = jogadorDao;
        this.fut = fut;
        this.jogadorFut = jogadorFut;
        this.textNome = textNome;
        this.checkedJogadorMensalista = checkedJogadorMensalista;
        this.checkedPadraoAvulso = checkedPadraoAvulso;
        this.textValorAvulso = textValorAvulso;
        this.checkedPadraoMensal = checkedPadraoMensal;
        this.textValorMensal = textValorMensal;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<JogadorEvento> jogadoresEventoRelacionadosAoJogadorFut = jogadorDao.getJogadoresEventoRelacionadosAoJogadorFut(jogadorFut.getJogadorFutId());
        for (JogadorEvento jogadorEvento: jogadoresEventoRelacionadosAoJogadorFut) {
            jogadorEvento.setNome(textNome);
            if (jogadorEvento.isAtivo()) {
                jogadorEvento.setMensalista(checkedJogadorMensalista);
                if (checkedPadraoAvulso) {
                    jogadorEvento.setValorAvulso(fut.getValorAvulso());
                } else {
                    jogadorEvento.setValorAvulso(Double.parseDouble(textValorAvulso));
                }
                if (checkedPadraoMensal) {
                    jogadorEvento.setValorMensal(fut.getValorMensal());
                } else {
                    jogadorEvento.setValorMensal(Double.parseDouble(textValorMensal));
                }
                jogadorEvento.setPago(jogadorEvento.isMensalista() || jogadorEvento.getValorAvulso() == 0);
            }
            jogadorDao.editaJogadorEvento(jogadorEvento);
        }


//        for (Evento e: eventoDao.getEventos((int) fut.getFutId())) {
//            for (JogadorEvento jogadorEvento: jogadorDao.getJogadoresEvento(e.getEventoId())) {
//                if (jogadorEvento.getJogadorEventoId() == jogadorFut.getJogadorFutId()) {
//                    jogadorEvento.setNome(textNome);
//                    if (e.isAtivo()) {
//                        jogadorEvento.setMensalista(checkedJogadorMensalista);
//                        if (checkedPadraoAvulso) {
//                            jogadorEvento.setValorAvulso(fut.getValorAvulso());
//                        } else {
//                            jogadorEvento.setValorAvulso(Double.parseDouble(textValorAvulso));
//                        }
//                        if (checkedPadraoMensal) {
//                            jogadorEvento.setValorMensal(fut.getValorMensal());
//                        } else {
//                            jogadorEvento.setValorMensal(Double.parseDouble(textValorMensal));
//                        }
//                        jogadorEvento.setPago(jogadorEvento.isMensalista() || jogadorEvento.getValorAvulso() == 0);
//                    }
//                }
//                jogadorDao.editaJogadorEvento(jogadorEvento);
//            }
//        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        listener.aposAtualizarJogadorEvento();
    }

    public interface AtualizaJogadorEventoListener {
        void aposAtualizarJogadorEvento();
    }
}
