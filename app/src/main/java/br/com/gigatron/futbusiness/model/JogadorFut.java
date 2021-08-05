package br.com.gigatron.futbusiness.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class JogadorFut extends Jogador {

    @PrimaryKey(autoGenerate = true)
    private long jogadorFutId;
    @ForeignKey(entity = Fut.class,
            parentColumns = "id",
            childColumns = "futId",
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE)
    private long futId;
    private int presencasAvulso;
    private int presencasMensalista;
    private int calotesDados;
    private int vezesQueFurou;

    public JogadorFut() {
        super();
    }

    public long getJogadorFutId() {
        return jogadorFutId;
    }

    public void setJogadorFutId(long jogadorFutId) {
        this.jogadorFutId = jogadorFutId;
    }

    public long getFutId() {
        return futId;
    }

    public void setFutId(long futId) {
        this.futId = futId;
    }

    public int getPresencasAvulso() {
        return presencasAvulso;
    }

    public void setPresencasAvulso(int presencasAvulso) {
        this.presencasAvulso = presencasAvulso;
    }

    public int getPresencasMensalista() {
        return presencasMensalista;
    }

    public void setPresencasMensalista(int presencasMensalista) {
        this.presencasMensalista = presencasMensalista;
    }

    public int getCalotesDados() {
        return calotesDados;
    }

    public void setCalotesDados(int calotesDados) {
        this.calotesDados = calotesDados;
    }

    public int getVezesQueFurou() {
        return vezesQueFurou;
    }

    public void setVezesQueFurou(int vezesQueFurou) {
        this.vezesQueFurou = vezesQueFurou;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JogadorFut that = (JogadorFut) o;
        return jogadorFutId == that.jogadorFutId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jogadorFutId);
    }

//Parcelable
    public static final Creator<JogadorFut> CREATOR = new Parcelable.Creator<JogadorFut>() {
        @Override
        public JogadorFut createFromParcel(Parcel source) {
            return new JogadorFut(source);
        }

        @Override
        public JogadorFut[] newArray(int size) {
            return new JogadorFut[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(jogadorFutId);
        dest.writeLong(futId);
        dest.writeInt(presencasAvulso);
        dest.writeInt(presencasMensalista);
        dest.writeInt(calotesDados);
        dest.writeInt(vezesQueFurou);
    }

    private JogadorFut(Parcel in) {
        super(in);
        jogadorFutId = in.readLong();
        futId = in.readLong();
        presencasAvulso = in.readInt();
        presencasMensalista = in.readInt();
        calotesDados = in.readInt();
        vezesQueFurou = in.readInt();
    }
}