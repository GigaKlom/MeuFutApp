package br.com.gigatron.futbusiness.asynctask.fut;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Usuario;

public class VerificaSaldoNovoFutTask extends AsyncTask<Void, Void, Boolean> {
    private final UsuarioDao usuarioDao;
    private VerificaSaldoNovoFutListener listener;

    public VerificaSaldoNovoFutTask(UsuarioDao usuarioDao, VerificaSaldoNovoFutListener listener) {
        this.usuarioDao = usuarioDao;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Usuario usuario = usuarioDao.getUsuarios().get(0);
        return usuario.getSaldoNovoFut() > 0;
    }

    @Override
    protected void onPostExecute(Boolean hasSaldo) {
        super.onPostExecute(hasSaldo);
        listener.aposVerificar(hasSaldo);
    }

    public interface VerificaSaldoNovoFutListener {
        void aposVerificar(boolean hasSaldo);
    }
}
