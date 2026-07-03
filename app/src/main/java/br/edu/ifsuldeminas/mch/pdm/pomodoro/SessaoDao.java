package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SessaoDao {

    @Insert
    long inserirSessao(SessaoEstudo sessao);

    @Query("SELECT * FROM sessoes ORDER BY id DESC")
    List<SessaoEstudo> listarSessoes();

    @Query("SELECT materia, SUM(duracao_realizada) as total_minutos FROM sessoes WHERE status = 'Concluído' GROUP BY materia ORDER BY total_minutos DESC")
    List<RelatorioMateria> buscarRelatorioPorMateria();

    @Query("DELETE FROM sessoes WHERE id = :id")
    void excluirSessao(int id);
}