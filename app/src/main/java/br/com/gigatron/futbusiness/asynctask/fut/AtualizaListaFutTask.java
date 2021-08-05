package br.com.gigatron.futbusiness.asynctask.fut;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import br.com.gigatron.futbusiness.database.dao.FutDao;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.ui.adapter.MeusFutsAdapter;

public class AtualizaListaFutTask extends AsyncTask<Void, Void, List<Fut>> {
    private final MeusFutsAdapter adapter;
    private final FutDao dao;

    public AtualizaListaFutTask(MeusFutsAdapter adapter, FutDao dao) {
        this.adapter = adapter;
        this.dao = dao;
    }

    @Override
    protected List<Fut> doInBackground(Void[] objects) {
        List<Fut> listaFutEmOrdemMensal = new ArrayList<>();
        List<Fut> listaFutsAvulsos = new ArrayList<>();
        for (Fut f: dao.getList()) {
            if (f.isMensal()) {
                listaFutEmOrdemMensal.add(f);
            } else {
                listaFutsAvulsos.add(f);
            }
        }
        listaFutEmOrdemMensal.addAll(listaFutsAvulsos);
        return listaFutEmOrdemMensal;
    }

    @Override
    protected void onPostExecute(List<Fut> futList) {
        super.onPostExecute(futList);
        adapter.atualiza(futList);
    }
}