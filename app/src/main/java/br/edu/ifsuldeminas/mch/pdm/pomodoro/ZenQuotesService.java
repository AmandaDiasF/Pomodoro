package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ZenQuotesService {

    @GET("api/random")
    Call<List<Quote>> buscarFraseAleatoria();
}