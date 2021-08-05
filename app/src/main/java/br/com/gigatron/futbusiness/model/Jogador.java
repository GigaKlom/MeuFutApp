package br.com.gigatron.futbusiness.model;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class Jogador implements Parcelable, Comparable<Jogador> {

    private String nome;
    private double valorAvulso;
    private double valorMensal;
    private boolean mensalista;

    protected Jogador() {

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getValorAvulso() {
        return valorAvulso;
    }

    public void setValorAvulso(double valorAvulso) {
        this.valorAvulso = valorAvulso;
    }

    public double getValorMensal() {
        return valorMensal;
    }

    public void setValorMensal(double valorMensal) {
        this.valorMensal = valorMensal;
    }

    public boolean isMensalista() {
        return mensalista;
    }

    public void setMensalista(boolean mensalista) {
        this.mensalista = mensalista;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public int compareTo(Jogador jogador) {
        return this.getNome().compareTo(jogador.getNome());
    }

    //Parcelable
    protected Jogador(Parcel in) {
        nome = in.readString();
        valorAvulso = in.readDouble();
        valorMensal = in.readDouble();
        mensalista = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeDouble(valorAvulso);
        dest.writeDouble(valorMensal);
        dest.writeByte((byte) (mensalista ? 1 : 0));
    }
}
