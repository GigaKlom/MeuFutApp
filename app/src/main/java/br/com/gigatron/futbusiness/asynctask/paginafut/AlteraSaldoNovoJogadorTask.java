package br.com.gigatron.futbusiness.asynctask.paginafut;

import android.util.Log;

import br.com.gigatron.futbusiness.asynctask.AlteraSaldoTask;
import br.com.gigatron.futbusiness.asynctask.fut.AlteraSaldoNovoFutTask;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Usuario;

public class AlteraSaldoNovoJogadorTask extends AlteraSaldoTask {

    public AlteraSaldoNovoJogadorTask(Usuario usuario, UsuarioDao usuarioDao, Acao acao) {
        super(usuario, usuarioDao, acao);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (acao == Acao.DIMINUI) {
            Log.i("CUDOLEO", "Antes: " + usuario.getSaldoNovoJogador());
            usuario.setSaldoNovoJogador(
                    usuario.getSaldoNovoJogador() - 1
            );
            Log.i("CUDOLEO", "Depois: " + usuario.getSaldoNovoJogador());
        } else if (acao == Acao.AUMENTA) {
            usuario.setSaldoNovoJogador(
                    usuario.getSaldoNovoJogador() + 4
            );
        }
        usuarioDao.editaUsuario(usuario);
        return null;
    }
}
