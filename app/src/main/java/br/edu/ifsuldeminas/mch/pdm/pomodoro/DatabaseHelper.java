package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

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
}