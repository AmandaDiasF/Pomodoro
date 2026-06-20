package br.edu.ifsuldeminas.mch.pdm.pomodoro;

public class SessaoEstudo {
    private int id;
    private String materia;
    private String tipo; // Foco, Pausa Curta, Pausa Longa
    private int duracaoPlanejada;
    private int duracaoRealizada;
    private String dataInicio;
    private String dataFim;
    private String status; // Concluído, Interrompido

    public SessaoEstudo() {}

    // Getters e Setters
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