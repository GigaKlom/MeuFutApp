package br.com.gigatron.futbusiness.model;

import android.os.Parcel;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class JogadorEvento extends Jogador {

    @PrimaryKey(autoGenerate = true)
    private long jogadorEventoId;
    @ForeignKey(entity = Evento.class,
            parentColumns = "id",
            childColumns = "eventoId",
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE)
    private long eventoId;
    @ForeignKey(entity = JogadorFut.class,
                parentColumns = "id",
                childColumns = "jogadorFutId",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE)
    private long jogadorFutId;
    private boolean pago;
    private boolean furo;
    private boolean ativo;

    public JogadorEvento() {
        super();
    }

    public long getJogadorEventoId() {
        return jogadorEventoId;
    }

    public void setJogadorEventoId(long jogadorEventoId) {
        this.jogadorEventoId = jogadorEventoId;
    }

    public long getEventoId() {
        return eventoId;
    }

    public void setEventoId(long eventoId) {
        this.eventoId = eventoId;
    }

    public boolean isPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }

    public boolean isFuro() {
        return furo;
    }

    public void setFuro(boolean furo) {
        this.furo = furo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public long getJogadorFutId() {
        return jogadorFutId;
    }

    public void setJogadorFutId(long jogadorFutId) {
        this.jogadorFutId = jogadorFutId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JogadorEvento that = (JogadorEvento) o;
        return jogadorFutId == that.jogadorFutId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jogadorEventoId);
    }

//Parcelable
    public static final Creator<JogadorEvento> CREATOR = new Creator<JogadorEvento>() {
        @Override
        public JogadorEvento createFromParcel(Parcel source) {
            return new JogadorEvento(source);
        }

        @Override
        public JogadorEvento[] newArray(int size) {
            return new JogadorEvento[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(jogadorEventoId);
        dest.writeLong(eventoId);
        dest.writeLong(jogadorFutId);
        dest.writeByte((byte) (pago ? 1 : 0));
        dest.writeByte((byte) (furo ? 1 : 0));
        dest.writeByte((byte) (ativo ? 1 : 0));
    }

    public JogadorEvento(Parcel in) {
        super(in);
        jogadorEventoId = in.readLong();
        eventoId = in.readLong();
        jogadorFutId = in.readLong();
        pago = in.readByte() != 0;
        furo = in.readByte() != 0;
        ativo = in.readByte() != 0;
    }
}

