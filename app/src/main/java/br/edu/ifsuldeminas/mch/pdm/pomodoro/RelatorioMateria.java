package br.edu.ifsuldeminas.mch.pdm.pomodoro;

public class RelatorioMateria {
    private String materia;
    private int totalMinutos;

    public RelatorioMateria(String materia, int totalMinutos) {
        this.materia = materia;
        this.totalMinutos = totalMinutos;
    }

    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia; }

    public int getTotalMinutos() { return totalMinutos; }
    public void setTotalMinutos(int totalMinutos) { this.totalMinutos = totalMinutos; }
}