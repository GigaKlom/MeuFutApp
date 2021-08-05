package br.com.gigatron.futbusiness.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.gigatron.futbusiness.model.Usuario;

@Dao
public interface UsuarioDao {

    @Query("SELECT * FROM Usuario")
    List<Usuario> getUsuarios();

    @Insert
    void criaUsuario(Usuario usuario);

    @Delete
    void removeUsuario(Usuario usuario);

    @Update
    void editaUsuario(Usuario usuario);

}
