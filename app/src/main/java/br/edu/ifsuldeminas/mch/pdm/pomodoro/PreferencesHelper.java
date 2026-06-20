package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {
    private static final String PREF_NAME = "PomodoroPrefs";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // --- GESTÃO DE LOGIN ---
    public void salvarLogin(String nomeUsuario, String senha, boolean isLoggedIn) {
        editor.putString("nome_usuario", nomeUsuario);
        editor.putString("senha", senha);
        editor.putBoolean("is_logged_in", isLoggedIn);
        editor.apply();
    }

    public boolean isUsuarioLogado() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    public String getNomeUsuario() {
        return sharedPreferences.getString("nome_usuario", "");
    }

    // --- CONFIGURAÇÕES DO POMODORO ---
    public void salvarConfiguracoesTempo(int tempoFoco, int pausaCurta, int pausaLonga) {
        editor.putInt("tempo_foco", tempoFoco);
        editor.putInt("pausa_curta", pausaCurta);
        editor.putInt("pausa_longa", pausaLonga);
        editor.apply();
    }

    public int getTempoFoco() {
        return sharedPreferences.getInt("tempo_foco", 25);
    }

    public int getPausaCurta() {
        return sharedPreferences.getInt("pausa_curta", 5);
    }

    public int getPausaLonga() {
        return sharedPreferences.getInt("pausa_longa", 15);
    }
}