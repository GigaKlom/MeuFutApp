package br.com.gigatron.futbusiness.asynctask.fut;

import br.com.gigatron.futbusiness.asynctask.AlteraSaldoTask;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Usuario;

public class AlteraSaldoNovoFutTask extends AlteraSaldoTask {

    public AlteraSaldoNovoFutTask(Usuario usuario, UsuarioDao usuarioDao, Acao acao) {
        super(usuario, usuarioDao, acao);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (acao == Acao.DIMINUI) {
            usuario.setSaldoNovoFut(
                    usuario.getSaldoNovoFut() - 1
            );
        } else if (acao == Acao.AUMENTA) {
            usuario.setSaldoNovoFut(
                    usuario.getSaldoNovoFut() + 1
            );
        }
        usuarioDao.editaUsuario(usuario);
        return null;
    }
}
