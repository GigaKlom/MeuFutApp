package br.com.gigatron.futbusiness.asynctask.evento;

import br.com.gigatron.futbusiness.asynctask.AlteraSaldoTask;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Usuario;

public class AlteraSaldoArquivaEventoTask extends AlteraSaldoTask {
    public AlteraSaldoArquivaEventoTask(Usuario usuario, UsuarioDao usuarioDao, AlteraSaldoTask.Acao acao) {
        super(usuario, usuarioDao, acao);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (acao == Acao.DIMINUI) {
            usuario.setSaldoArquivaEvento(
                    usuario.getSaldoArquivaEvento() - 1
            );
        } else if (acao == Acao.AUMENTA) {
            usuario.setSaldoArquivaEvento(
                    usuario.getSaldoArquivaEvento() + 1
            );
        }
        usuarioDao.editaUsuario(usuario);
        return null;
    }
}
