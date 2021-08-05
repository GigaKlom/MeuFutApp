package br.com.gigatron.futbusiness.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.gigatron.futbusiness.model.Fut;

@Dao
public interface FutDao {
    @Insert
    void cria(Fut fut);

    @Delete
    void remove(Fut fut);

    @Update
    void edita(Fut fut);

    @Query("SELECT * FROM Fut")
    List<Fut> getList();
}
