package br.com.gigatron.futbusiness.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.gigatron.futbusiness.model.Evento;

@Dao
public interface EventoDao {

    @Query("SELECT * FROM Evento " +
            "WHERE futId = :futId")
    List<Evento> getEventos(int futId);

    @Insert
    void cria(Evento evento);

    @Delete
    void remove(Evento evento);

    @Update
    void edita(Evento evento);
}
