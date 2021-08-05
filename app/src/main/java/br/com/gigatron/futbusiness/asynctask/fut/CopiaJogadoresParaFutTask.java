package br.com.gigatron.futbusiness.asynctask.fut;

import android.os.AsyncTask;

import java.util.List;

import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.Jogador;
import br.com.gigatron.futbusiness.model.JogadorFut;

public class CopiaJogadoresParaFutTask extends AsyncTask<Void, Void, Void> {
    private final JogadorDao jogadorDao;
    private final List<JogadorFut> jogadoresAInserir;
    private final List<Fut> futsSelecionados;
    private final SalvaJogadoresNoFutListener listener;
    public CopiaJogadoresParaFutTask(JogadorDao jogadorDao,
                                     List<JogadorFut> jogadoresAInserir,
                                     List<Fut> futsSelecionados,
                                     SalvaJogadoresNoFutListener listener) {
        this.jogadorDao = jogadorDao;
        this.jogadoresAInserir = jogadoresAInserir;
        this.futsSelecionados = futsSelecionados;
        this.listener = listener;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        for (Fut fut: futsSelecionados) {
            for (JogadorFut jogadorAInserir: jogadoresAInserir) {
                if (!jogadorExisteNoFut(fut, jogadorAInserir)) {
                    JogadorFut copia = getCopia(jogadorAInserir, fut);
                    jogadorDao.criaJogadorFut(copia);
                }
            }
        }
        return null;
    }

    private boolean jogadorExisteNoFut(Fut fut, JogadorFut jogadorAInserir) {
        List<JogadorFut> jogadoresFut = jogadorDao.getJogadoresFut((int) fut.getFutId());
        for (JogadorFut jogadorFut: jogadoresFut) {
            if (jogadorFut.getNome().equals(jogadorAInserir.getNome())) {
                return true;
            }
        }
        return false;
    }

    private JogadorFut getCopia(Jogador j, Fut f) {
        JogadorFut copia = new JogadorFut();
        copia.setNome(j.getNome());
        copia.setValorMensal(f.getValorMensal());
        copia.setValorAvulso(f.getValorAvulso());
        copia.setMensalista(false);
        copia.setFutId(f.getFutId());
        return copia;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        listener.aposSalvarJogadoresNoFut();

    }

    public interface SalvaJogadoresNoFutListener {
        void aposSalvarJogadoresNoFut();
    }
}
