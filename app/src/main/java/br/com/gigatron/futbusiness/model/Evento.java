package br.com.gigatron.futbusiness.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class Evento implements Serializable {

    private static final Long serialVersionUID = 1L;

    @PrimaryKey(autoGenerate = true)
    private long eventoId = 0;
    @ForeignKey(entity = Fut.class,
                parentColumns = "id",
                childColumns = "futId",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE)
    private long futId;
    private long mensalId;
    private String horario;
    private String data;
    private boolean ativo;
    private boolean arquivado;
    private boolean mensal;
    private double precoMensalQuadra;
    private double ganhoPrevisto;
    private double ganhoObtido;
    private int numeroDeJogadoresNoEvento;
    private int numeroDeJogadoresComPagamentoConfirmado;

    public long getEventoId() {
        return eventoId;
    }

    public void setEventoId(long eventoId) {
        this.eventoId = eventoId;
    }

    public long getFutId() {
        return futId;
    }

    public void setFutId(long futId) {
        this.futId = futId;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getGanhoPrevisto() {
        return ganhoPrevisto;
    }

    public void setGanhoPrevisto(double ganhoPrevisto) {
        this.ganhoPrevisto = ganhoPrevisto;
    }

    public double getGanhoObtido() {
        return ganhoObtido;
    }

    public void setGanhoObtido(double ganhoObtido) {
        this.ganhoObtido = ganhoObtido;
    }

    public int getNumeroDeJogadoresNoEvento() {
        return numeroDeJogadoresNoEvento;
    }

    public void setNumeroDeJogadoresNoEvento(int numeroDeJogadoresNoEvento) {
        this.numeroDeJogadoresNoEvento = numeroDeJogadoresNoEvento;
    }

    public int getNumeroDeJogadoresComPagamentoConfirmado() {
        return numeroDeJogadoresComPagamentoConfirmado;
    }

    public void setNumeroDeJogadoresComPagamentoConfirmado(int numeroDeJogadoresComPagamentoConfirmado) {
        this.numeroDeJogadoresComPagamentoConfirmado = numeroDeJogadoresComPagamentoConfirmado;
    }

    public void setAtivo(boolean isAtivo) {
        this.ativo = isAtivo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public long getMensalId() {
        return mensalId;
    }

    public void setMensalId(long mensalId) {
        this.mensalId = mensalId;
    }

    public boolean isArquivado() {
        return arquivado;
    }

    public void setArquivado(boolean arquivado) {
        this.arquivado = arquivado;
    }

    public boolean isMensal() {
        return mensal;
    }

    public void setMensal(boolean mensal) {
        this.mensal = mensal;
    }

    public double getPrecoMensalQuadra() {
        return precoMensalQuadra;
    }

    public void setPrecoMensalQuadra(double precoMensalQuadra) {
        this.precoMensalQuadra = precoMensalQuadra;
    }

    @Override
    public String toString() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return eventoId == evento.eventoId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventoId);
    }
}
