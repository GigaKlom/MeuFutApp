package br.com.gigatron.futbusiness.asynctask.evento;

import br.com.gigatron.futbusiness.asynctask.AlteraSaldoTask;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Usuario;

public class AlteraSaldoNovoEventoTask extends AlteraSaldoTask {
    public AlteraSaldoNovoEventoTask(Usuario usuario, UsuarioDao usuarioDao, Acao acao) {
        super(usuario, usuarioDao, acao);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (acao == Acao.DIMINUI) {
            usuario.setSaldoNovoEvento(
                    usuario.getSaldoNovoEvento() - 1
            );
        } else if (acao == Acao.AUMENTA) {
            usuario.setSaldoNovoEvento(
                    usuario.getSaldoNovoEvento() + 1
            );
        }
        usuarioDao.editaUsuario(usuario);
        return null;
    }
}
