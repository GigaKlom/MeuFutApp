package br.com.gigatron.futbusiness.asynctask.evento;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.ui.adapter.MeusEventosAdapter;

public class AtualizaListaEventoTask extends AsyncTask<Void, Void, List<Evento>> {
    private final EventoDao eventoDao;
    private final Fut fut;
    private final MeusEventosAdapter adapter;
    private final boolean mostraArquivados;

    public AtualizaListaEventoTask(EventoDao eventoDao,
                                   Fut fut,
                                   MeusEventosAdapter adapter,
                                   boolean mostraArquivados) {
        this.eventoDao = eventoDao;
        this.fut = fut;
        this.adapter = adapter;
        this.mostraArquivados = mostraArquivados;
    }

    @Override
    protected List<Evento> doInBackground(Void... voids) {
        List<Evento> eventosEmOrdemDeAtividade = new ArrayList<>();
        List<Evento> finalizados = new ArrayList<>();
        List<Evento> arquivados = new ArrayList<>();
        for (Evento e: eventoDao.getEventos((int) fut.getFutId())) {
            if (e.isAtivo() && !e.isArquivado()) {
                eventosEmOrdemDeAtividade.add(e);
            } else if (!e.isAtivo() && !e.isArquivado()){
                finalizados.add(e);
            } else if (e.isArquivado()) {
                arquivados.add(e);
            }
        }
        eventosEmOrdemDeAtividade.addAll(finalizados);
        if (mostraArquivados) {
            eventosEmOrdemDeAtividade.addAll(arquivados);
        }
        return eventosEmOrdemDeAtividade;
    }

    @Override
    protected void onPostExecute(List<Evento> eventos) {
        super.onPostExecute(eventos);
        adapter.atualiza(eventos);
    }
}
