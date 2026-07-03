package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sessoes")
public class SessaoEstudo {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String materia;
    private String tipo;

    @ColumnInfo(name = "duracao_planejada")
    private int duracaoPlanejada;

    @ColumnInfo(name = "duracao_realizada")
    private int duracaoRealizada;

    @ColumnInfo(name = "data_inicio")
    private String dataInicio;

    @ColumnInfo(name = "data_fim")
    private String dataFim;

    private String status;

    public SessaoEstudo() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getDuracaoPlanejada() { return duracaoPlanejada; }
    public void setDuracaoPlanejada(int duracaoPlanejada) { this.duracaoPlanejada = duracaoPlanejada; }

    public int getDuracaoRealizada() { return duracaoRealizada; }
    public void setDuracaoRealizada(int duracaoRealizada) { this.duracaoRealizada = duracaoRealizada; }

    public String getDataInicio() { return dataInicio; }
    public void setDataInicio(String dataInicio) { this.dataInicio = dataInicio; }

    public String getDataFim() { return dataFim; }
    public void setDataFim(String dataFim) { this.dataFim = dataFim; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}