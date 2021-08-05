package br.com.gigatron.futbusiness.database.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.gigatron.futbusiness.model.Jogador;
import br.com.gigatron.futbusiness.model.JogadorEvento;
import br.com.gigatron.futbusiness.model.JogadorFut;

@Dao
public interface JogadorDao {

    @Query("SELECT * FROM JogadorFut " +
            "WHERE futId = :futId")
    List<JogadorFut> getJogadoresFut(int futId);

    @Insert
    void criaJogadorFut(JogadorFut jogadorFut);

    @Delete
    void removeJogadorFut(JogadorFut jogadorFut);

    @Update
    void editaJogadorFut(JogadorFut jogadorFut);

    @Query("SELECT * FROM JogadorEvento " +
            "WHERE eventoId = :eventoId")
    List<JogadorEvento> getJogadoresEvento(long eventoId);

    @Query("SELECT * FROM JogadorEvento WHERE jogadorFutId = :jogadorFutId")
    List<JogadorEvento> getJogadoresEventoRelacionadosAoJogadorFut(long jogadorFutId);

    @Insert
    void criaJogadorEvento(JogadorEvento jogadorEvento);

    @Delete
    void removeJogadorEvento(JogadorEvento jogadorEvento);

    @Update
    void editaJogadorEvento(JogadorEvento jogadorEvento);
}
