package br.com.gigatron.futbusiness.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import br.com.gigatron.futbusiness.database.dao.EventoDao;
import br.com.gigatron.futbusiness.database.dao.FutDao;
import br.com.gigatron.futbusiness.database.dao.JogadorDao;
import br.com.gigatron.futbusiness.database.dao.UsuarioDao;
import br.com.gigatron.futbusiness.model.Evento;
import br.com.gigatron.futbusiness.model.Fut;
import br.com.gigatron.futbusiness.model.JogadorEvento;
import br.com.gigatron.futbusiness.model.JogadorFut;
import br.com.gigatron.futbusiness.model.Usuario;

@Database(entities = {
        Fut.class, JogadorFut.class, JogadorEvento.class,
        Evento.class, Usuario.class},
        version = 41, exportSchema = false)
public abstract class FutBusinessDatabase extends RoomDatabase {
    public abstract FutDao getFutDao();

    public abstract JogadorDao getJogadorDao();

    public abstract EventoDao getEventoDao();

    public abstract UsuarioDao getUsuarioDao();

    public static FutBusinessDatabase getInstance(Context context) {
        return Room.databaseBuilder(context, FutBusinessDatabase.class, "futbusiness.db")
                .addMigrations(new Migration(39, 40) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("CREATE TABLE IF NOT EXISTS `Usuario` (" +
                                "`usuarioId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                "`saldoNovoFut` INTEGER NOT NULL, " +
                                "`saldoNovoJogador` INTEGER NOT NULL, " +
                                "`saldoArquivaEvento` INTEGER NOT NULL" +
                                ")");
                    }
                }, new Migration(40, 41) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("ALTER TABLE `Usuario` ADD COLUMN `saldoNovoEvento` INTEGER NOT NULL DEFAULT 0");
                    }
                })
                .build();
    }
}