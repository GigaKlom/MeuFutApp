package br.com.gigatron.futbusiness.asynctask.evento;

import android.os.AsyncTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Fut;

public class CriaEventosNoMesTask extends AsyncTask<Void, Void, Boolean> {
    private final int mes;
    private final int ano;
    private final int diaDoFut;
    private final EventoDao eventoDao;
    private final Fut fut;
    private final CriaEventosNoMesListener listener;

    public CriaEventosNoMesTask(int mes,
                                int ano,
                                int diaDoFut,
                                EventoDao eventoDao,
                                Fut fut,
                                CriaEventosNoMesListener listener) {
        this.mes = mes;
        this.ano = ano;
        this.diaDoFut = diaDoFut;
        this.eventoDao = eventoDao;
        this.fut = fut;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String buildMensalId = String.valueOf(mes + 1) + String.valueOf(ano);
        long mensalId = Long.parseLong(buildMensalId);

        for (Evento e: eventoDao.getEventos((int) fut.getFutId())) {
            if (e.getMensalId() == mensalId) {
                return false;
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(ano, mes, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(ano, mes, day);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == diaDoFut) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String data = simpleDateFormat.format(calendar.getTime());
                Evento evento = new Evento();
                evento.setFutId(fut.getFutId());
                evento.setMensalId(mensalId);
                evento.setHorario(fut.getHorario());
                evento.setData(data);
                evento.setAtivo(true);
                evento.setArquivado(false);
                evento.setMensal(fut.isMensal());
                evento.setPrecoMensalQuadra(fut.getAluguelDaQuadra());
                eventoDao.cria(evento);
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean sucesso) {
        super.onPostExecute(sucesso);
        listener.aposCriarEventosNoMes(sucesso);
    }

    public interface CriaEventosNoMesListener {
        void aposCriarEventosNoMes(Boolean sucesso);
    }
}
