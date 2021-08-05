package br.com.gigatron.futbusiness.asynctask;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Usuario;

public class VerificaUsuarioTask extends AsyncTask<Void, Void, Void> {
    private final UsuarioDao usuarioDao;

    public VerificaUsuarioTask(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (usuarioDao.getUsuarios().size() == 0) {
            Usuario usuario = new Usuario();
            usuario.setSaldoNovoFut(1);
            usuario.setSaldoNovoJogador(4);
            usuario.setSaldoNovoEvento(1);
            usuario.setSaldoArquivaEvento(1);
            usuarioDao.criaUsuario(usuario);
        }
        return null;
    }
}
