package br.com.gigatron.futbusiness.asynctask;

import android.os.AsyncTask;

import br.com.gigatron.futbusiness.asynctask.fut.AlteraSaldoNovoFutTask;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Usuario;

public abstract class AlteraSaldoTask extends AsyncTask<Void, Void, Void> {

    protected final Usuario usuario;
    protected final UsuarioDao usuarioDao;
    protected final Acao acao;
    public enum Acao {
        DIMINUI, AUMENTA
    }

    public AlteraSaldoTask(
            Usuario usuario,
            UsuarioDao usuarioDao,
            AlteraSaldoNovoFutTask.Acao acao) {
        this.usuario = usuario;
        this.usuarioDao = usuarioDao;
        this.acao = acao;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }
}
