package br.com.gigatron.futbusiness.asynctask.fut;

import android.content.Intent;
import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.FutDao;
import br.com.gigatron.futbusiness.model.Fut;

import static br.com.gigatron.futbusiness.Keys.ABRE_FORMULARIO_DO_FUT_EM_MODO_EDICAO_EXTRA_FUT;

public class SalvaFutTask extends AsyncTask<Void, Void, Boolean> {

    private final boolean modoEditaFut;
    private final FutDao futDao;
    private final Fut fut;
    private final String textLocal;
    private final String textValorAvulso;
    private final String textValorMensal;
    private final String textAluguelDaQuadra;
    private final boolean checkedMensal;
    private final int intDiaDaSemana;
    private final String horario;
    private final SalvaFutListener listener;

    public SalvaFutTask(boolean modoEditaFut,
                        FutDao futDao,
                        Fut fut,
                        String textLocal,
                        String textValorAvulso,
                        String textValorMensal,
                        String textAluguelDaQuadra,
                        boolean checkedMensal,
                        int intDiaDaSemana,
                        String horario,
                        SalvaFutListener listener) {
        this.modoEditaFut = modoEditaFut;
        this.futDao = futDao;
        this.fut = fut;
        this.textLocal = textLocal;
        this.textValorAvulso = textValorAvulso;
        this.textValorMensal = textValorMensal;
        this.textAluguelDaQuadra = textAluguelDaQuadra;
        this.checkedMensal = checkedMensal;
        this.intDiaDaSemana = intDiaDaSemana;
        this.horario = horario;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        fut.setLocal(textLocal);
        fut.setValorAvulso(Double.parseDouble(textValorAvulso));
        if (checkedMensal) {
            fut.setValorMensal(Double.parseDouble(textValorMensal));
        } else {
            fut.setValorMensal(0);
        }
        fut.setAluguelDaQuadra(Double.parseDouble(textAluguelDaQuadra));
        fut.setMensal(checkedMensal);
        fut.setDiaDaSemana(intDiaDaSemana);
        fut.setHorario(horario);
        if (modoEditaFut) {
            futDao.edita(fut);
            return false;
        }
        futDao.cria(fut);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean isCriaFut) {
        super.onPostExecute(isCriaFut);
        listener.aposSalvar(isCriaFut);
    }

    public interface SalvaFutListener {
        void aposSalvar(Boolean isCriaFut);
    }
}
