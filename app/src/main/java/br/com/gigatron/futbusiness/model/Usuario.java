package br.com.gigatron.futbusiness.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Usuario implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long usuarioId;
    private int saldoNovoFut;
    private int saldoNovoJogador;
    private int saldoNovoEvento;
    private int saldoArquivaEvento;

    public Usuario() {

    }

    protected Usuario(Parcel in) {
        usuarioId = in.readLong();
        saldoNovoFut = in.readInt();
        saldoNovoJogador = in.readInt();
        saldoNovoEvento = in.readInt();
        saldoArquivaEvento = in.readInt();
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getSaldoNovoFut() {
        return saldoNovoFut;
    }

    public void setSaldoNovoFut(int saldoNovoFut) {
        this.saldoNovoFut = saldoNovoFut;
    }

    public int getSaldoNovoJogador() {
        return saldoNovoJogador;
    }

    public void setSaldoNovoJogador(int saldoNovoJogador) {
        this.saldoNovoJogador = saldoNovoJogador;
    }

    public int getSaldoNovoEvento() {
        return saldoNovoEvento;
    }

    public void setSaldoNovoEvento(int saldoNovoEvento) {
        this.saldoNovoEvento = saldoNovoEvento;
    }

    public int getSaldoArquivaEvento() {
        return saldoArquivaEvento;
    }

    public void setSaldoArquivaEvento(int saldoArquivaEvento) {
        this.saldoArquivaEvento = saldoArquivaEvento;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(usuarioId);
        dest.writeInt(saldoNovoFut);
        dest.writeInt(saldoNovoJogador);
        dest.writeInt(saldoNovoEvento);
        dest.writeInt(saldoArquivaEvento);
    }
}
