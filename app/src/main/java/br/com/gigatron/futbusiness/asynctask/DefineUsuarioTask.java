package br.com.gigatron.futbusiness.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Usuario;

public class DefineUsuarioTask extends AsyncTask<Void, Void, Usuario> {
    private final UsuarioDao usuarioDao;
    private final DefineUsuarioListener listener;

    public DefineUsuarioTask(UsuarioDao usuarioDao, DefineUsuarioListener listener) {
        this.usuarioDao = usuarioDao;
        this.listener = listener;
    }

    @Override
    protected Usuario doInBackground(Void... voids) {
        return usuarioDao.getUsuarios().get(0);
    }

    @Override
    protected void onPostExecute(Usuario usuario) {
        super.onPostExecute(usuario);
        listener.aposDefinirUsuario(usuario);
    }

    public interface DefineUsuarioListener {
        void aposDefinirUsuario(Usuario usuario);
    }
}
