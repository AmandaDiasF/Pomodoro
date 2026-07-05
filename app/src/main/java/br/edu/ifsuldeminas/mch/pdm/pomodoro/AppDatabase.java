package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SessaoEstudo.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract SessaoDao sessaoDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "pomodoro.db")
                            .allowMainThreadQueries() // Permite rodar na mesma thread que o código antigo rodava
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}