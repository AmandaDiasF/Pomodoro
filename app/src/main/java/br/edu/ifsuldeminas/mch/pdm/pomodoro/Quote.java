package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import com.google.gson.annotations.SerializedName;

public class Quote {

    @SerializedName("q")
    private String frase;

    @SerializedName("a")
    private String autor;

    public String getFrase() {
        return frase;
    }

    public String getAutor() {
        return autor;
    }
}