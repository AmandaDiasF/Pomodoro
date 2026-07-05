package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    private static final String PREF_NAME = "pomodoro_prefs";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // ==========================================
    // --- AUTENTICAÇÃO E LOGIN ---
    // ==========================================

    public void salvarLogin(String usuario, String senha, boolean logado) {
        editor.putString("usuario", usuario);
        editor.putString("senha", senha);
        editor.putBoolean("is_logado", logado);
        editor.apply();
    }

    public void salvarLogin(String usuario, String senha) {
        salvarLogin(usuario, senha, true);
    }

    public void setLogado(boolean logado) {
        editor.putBoolean("is_logado", logado);
        editor.apply();
    }

    public void setLogin(boolean logado) {
        setLogado(logado);
    }

    public boolean isLogado() {
        return sharedPreferences.getBoolean("is_logado", false);
    }

    public boolean isUsuarioLogado() {
        return isLogado();
    }

    public String getUsuario() {
        return sharedPreferences.getString("usuario", "");
    }

    public String getNomeUsuario() {
        return getUsuario();
    }

    public String getSenha() {
        return sharedPreferences.getString("senha", "");
    }

    public void logout() {
        editor.putBoolean("is_logado", false);
        editor.apply();
    }

    // ==========================================
    // --- TEMA E MODO ESCURO ---
    // ==========================================

    public boolean isModoEscuro() {
        return sharedPreferences.getBoolean("modo_escuro", false);
    }

    public void setModoEscuro(boolean escuro) {
        editor.putBoolean("modo_escuro", escuro);
        editor.apply();
    }

    // ==========================================
    // --- CONFIGURAÇÕES DE TEMPO POMODORO ---
    // ==========================================

    public void salvarConfiguracoesTempo(int tempoFoco, int pausaCurta, int pausaLonga) {
        editor.putInt("tempo_foco", tempoFoco);
        editor.putInt("pausa_curta", pausaCurta);
        editor.putInt("pausa_longa", pausaLonga);
        editor.apply();
    }

    public void salvarConfiguracoesTempo(int tempoFoco, int pausaCurta) {
        salvarConfiguracoesTempo(tempoFoco, pausaCurta, getPausaLonga());
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