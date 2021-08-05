package br.com.gigatron.futbusiness.asynctask.evento;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Fut;

public class ExistemEventosArquivadosTask extends AsyncTask<Void, Void, Boolean> {
    private final EventoDao eventoDao;
    private final Fut fut;
    private final ExistemEventosArquivadosListener listener;

    public ExistemEventosArquivadosTask(EventoDao eventoDao,
                                        Fut fut,
                                        ExistemEventosArquivadosListener listener) {
        this.eventoDao = eventoDao;
        this.fut = fut;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        for (Evento e: eventoDao.getEventos((int) fut.getFutId())) {
            if (e.isArquivado()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean existem) {
        super.onPostExecute(existem);
        listener.aposVerificarExistenciaDeEventosArquivados(existem);
    }

    public interface ExistemEventosArquivadosListener {
        void aposVerificarExistenciaDeEventosArquivados(Boolean existem);
    }
}
