package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "pomodoro.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_SESSOES = "sessoes";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MATERIA = "materia";
    public static final String COLUMN_TIPO = "tipo";
    public static final String COLUMN_DURACAO_PLANEJADA = "duracao_planejada";
    public static final String COLUMN_DURACAO_REALIZADA = "duracao_realizada";
    public static final String COLUMN_DATA_INICIO = "data_inicio";
    public static final String COLUMN_DATA_FIM = "data_fim";
    public static final String COLUMN_STATUS = "status";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_SESSOES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MATERIA + " TEXT NOT NULL, "
                + COLUMN_TIPO + " TEXT NOT NULL, "
                + COLUMN_DURACAO_PLANEJADA + " INTEGER NOT NULL, "
                + COLUMN_DURACAO_REALIZADA + " INTEGER NOT NULL, "
                + COLUMN_DATA_INICIO + " TEXT NOT NULL, "
                + COLUMN_DATA_FIM + " TEXT, "
                + COLUMN_STATUS + " TEXT NOT NULL"
                + ");";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSOES);
        onCreate(db);
    }

    // ========================================================
    // MÉTODOS DA PESSOA 2 (OPERAÇÕES DA BASE DE DADOS)
    // ========================================================

    // 1. Inserir sessão no banco
    public long inserirSessao(SessaoEstudo sessao) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MATERIA, sessao.getMateria());
        values.put(COLUMN_TIPO, sessao.getTipo());
        values.put(COLUMN_DURACAO_PLANEJADA, sessao.getDuracaoPlanejada());
        values.put(COLUMN_DURACAO_REALIZADA, sessao.getDuracaoRealizada());
        values.put(COLUMN_DATA_INICIO, sessao.getDataInicio());
        values.put(COLUMN_DATA_FIM, sessao.getDataFim());
        values.put(COLUMN_STATUS, sessao.getStatus());

        long id = db.insert(TABLE_SESSOES, null, values);
        db.close();
        return id;
    }

    // 2. Listar tudo para o histórico
    public List<SessaoEstudo> listarSessoes() {
        List<SessaoEstudo> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Retorna as sessões da mais recente para a mais antiga
        String selectQuery = "SELECT * FROM " + TABLE_SESSOES + " ORDER BY " + COLUMN_ID + " DESC";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SessaoEstudo sessao = new SessaoEstudo();
                sessao.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                sessao.setMateria(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MATERIA)));
                sessao.setTipo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)));
                sessao.setDuracaoPlanejada(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURACAO_PLANEJADA)));
                sessao.setDuracaoRealizada(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURACAO_REALIZADA)));
                sessao.setDataInicio(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATA_INICIO)));
                sessao.setDataFim(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATA_FIM)));
                sessao.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));

                lista.add(sessao);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    // 3. Somar minutos por matéria para o relatório
    public List<RelatorioMateria> buscarRelatorioPorMateria() {
        List<RelatorioMateria> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta agregada: soma a duração de todas as sessões "Concluídas" por cada matéria
        String query = "SELECT " + COLUMN_MATERIA + ", SUM(" + COLUMN_DURACAO_REALIZADA + ") as total_minutos " +
                "FROM " + TABLE_SESSOES + " " +
                "WHERE " + COLUMN_STATUS + " = 'Concluído' " +
                "GROUP BY " + COLUMN_MATERIA + " " +
                "ORDER BY total_minutos DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String materia = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MATERIA));
                int totalMinutos = cursor.getInt(cursor.getColumnIndexOrThrow("total_minutos"));

                lista.add(new RelatorioMateria(materia, totalMinutos));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    // 4. Excluir sessão
    public void excluirSessao(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SESSOES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}