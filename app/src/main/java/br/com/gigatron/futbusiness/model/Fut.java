package br.com.gigatron.futbusiness.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

@Entity
public class Fut implements Serializable {

    private static final Long serialVersionUID = 1L;

    @PrimaryKey(autoGenerate = true)
    private long futId = 0;
    private String local;
    private double valorAvulso;
    private double valorMensal;
    private double aluguelDaQuadra;
    private boolean mensal;
    private int diaDaSemana;
    private String horario;
    private int numeroJogadores;
    private int numeroMensalistas;
    private double ganhoEsperadoMensalistas;
    private double ganhoEsperadoAvulsos;
    private double lucroPrejuizo;

    public long getFutId() {
        return futId;
    }

    public void setFutId(long futId) {
        this.futId = futId;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
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

    public double getAluguelDaQuadra() {
        return aluguelDaQuadra;
    }

    public void setAluguelDaQuadra(double aluguelDaQuadra) {
        this.aluguelDaQuadra = aluguelDaQuadra;
    }

    public boolean isMensal() {
        return mensal;
    }

    public void setMensal(boolean mensal) {
        this.mensal = mensal;
    }

    public int getDiaDaSemana() {
        return diaDaSemana;
    }

    public void setDiaDaSemana(int diaDaSemana) {
        this.diaDaSemana = diaDaSemana;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public int getNumeroJogadores() {
        return numeroJogadores;
    }

    public void setNumeroJogadores(int numeroJogadores) {
        this.numeroJogadores = numeroJogadores;
    }

    public int getNumeroMensalistas() {
        return numeroMensalistas;
    }

    public void setNumeroMensalistas(int numeroMensalistas) {
        this.numeroMensalistas = numeroMensalistas;
    }

    public double getGanhoEsperadoMensalistas() {
        return ganhoEsperadoMensalistas;
    }

    public void setGanhoEsperadoMensalistas(double ganhoEsperadoMensalistas) {
        this.ganhoEsperadoMensalistas = ganhoEsperadoMensalistas;
    }

    public double getGanhoEsperadoAvulsos() {
        return ganhoEsperadoAvulsos;
    }

    public void setGanhoEsperadoAvulsos(double ganhoEsperadoAvulsos) {
        this.ganhoEsperadoAvulsos = ganhoEsperadoAvulsos;
    }

    public double getLucroPrejuizo() {
        return lucroPrejuizo;
    }

    public void setLucroPrejuizo(double lucroPrejuizo) {
        this.lucroPrejuizo = lucroPrejuizo;
    }

    @Override
    public String toString() {
        return local;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fut fut = (Fut) o;
        return local.equals(fut.local);
    }

    @Override
    public int hashCode() {
        return Objects.hash(local);
    }
}