package br.com.gigatron.futbusiness.asynctask.paginafut;

import android.os.AsyncTask;

import java.util.List;

import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.JogadorEvento;
import br.com.gigatron.futbusiness.model.JogadorFut;

public class PreencheEstatisticasJogadorTask extends AsyncTask<Void, Void, Void> {
    private final JogadorFut jogadorFut;
    private final EventoDao eventoDao;
    private final JogadorDao jogadorDao;
    private final Fut fut;
    private final PreencheInformacoesJogadorListener listener;

    public PreencheEstatisticasJogadorTask(JogadorFut jogadorFut,
                                           EventoDao eventoDao,
                                           JogadorDao jogadorDao,
                                           Fut fut,
                                           PreencheInformacoesJogadorListener listener) {
        this.jogadorFut = jogadorFut;
        this.eventoDao = eventoDao;
        this.jogadorDao = jogadorDao;
        this.fut = fut;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        jogadorFut.setPresencasAvulso(0);
        jogadorFut.setPresencasMensalista(0);
        jogadorFut.setCalotesDados(0);
        jogadorFut.setVezesQueFurou(0);

        List<JogadorEvento> jogadoresEventoRelacionadosAoJogadorFut = jogadorDao.getJogadoresEventoRelacionadosAoJogadorFut(jogadorFut.getJogadorFutId());
        for (JogadorEvento jogadorEvento: jogadoresEventoRelacionadosAoJogadorFut) {
            if (!jogadorEvento.isAtivo()) {
                if (jogadorEvento.isFuro()) {
                    jogadorFut.setVezesQueFurou(
                            jogadorFut.getVezesQueFurou() + 1
                    );
                } else {
                    if (jogadorEvento.isMensalista()) {
                        jogadorFut.setPresencasMensalista(
                                jogadorFut.getPresencasMensalista() + 1
                        );
                    } else {
                        jogadorFut.setPresencasAvulso(
                                jogadorFut.getPresencasAvulso() + 1
                        );
                    }
                    if (!jogadorEvento.isPago()) {
                        jogadorFut.setCalotesDados(
                                jogadorFut.getCalotesDados() + 1
                        );
                    }
                }
            }
        }

//        for (Evento e: eventoDao.getEventos((int) fut.getFutId())) {
//            if (!e.isAtivo()) {
//                for (JogadorEvento jogadorEvento: jogadorDao.getJogadoresEvento(e.getEventoId())) {
//                    if (jogadorEvento.getJogadorFutId() == jogadorFut.getJogadorFutId()) {
//                        if (jogadorEvento.isFuro()) {
//                            jogadorFut.setVezesQueFurou(
//                                    jogadorFut.getVezesQueFurou() + 1
//                            );
//                        } else {
//                            if (jogadorEvento.isMensalista()) {
//                                jogadorFut.setPresencasMensalista(
//                                        jogadorFut.getPresencasMensalista() + 1
//                                );
//                            } else {
//                                jogadorFut.setPresencasAvulso(
//                                        jogadorFut.getPresencasAvulso() + 1
//                                );
//                            }
//                            if (!jogadorEvento.isPago()) {
//                                jogadorFut.setCalotesDados(
//                                        jogadorFut.getCalotesDados() + 1
//                                );
//                            }
//                        }
//                    }
//                }
//            }
//        }
        jogadorDao.editaJogadorFut(jogadorFut);
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        listener.aposPreencherInformacoesJogador();
    }

    public interface PreencheInformacoesJogadorListener {
        void aposPreencherInformacoesJogador();
    }
}
