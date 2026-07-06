package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MotivacaoActivity extends AppCompatActivity {

    private TextView tvFrase, tvAutor;
    private MaterialButton btnBuscarFrase;
    private ZenQuotesService zenQuotesService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motivacao);

        tvFrase = findViewById(R.id.tvFrase);
        tvAutor = findViewById(R.id.tvAutor);
        btnBuscarFrase = findViewById(R.id.btnBuscarFrase);

        zenQuotesService = RetrofitClient
                .getRetrofitInstance()
                .create(ZenQuotesService.class);

        btnBuscarFrase.setOnClickListener(v -> buscarFrase());

        buscarFrase();
    }

    private void buscarFrase() {
        tvFrase.setText("Carregando frase...");
        tvAutor.setText("");

        Call<List<Quote>> call = zenQuotesService.buscarFraseAleatoria();

        call.enqueue(new Callback<List<Quote>>() {
            @Override
            public void onResponse(Call<List<Quote>> call, Response<List<Quote>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Quote quote = response.body().get(0);
                    tvFrase.setText("\"" + quote.getFrase() + "\"");
                    tvAutor.setText("- " + quote.getAutor());
                } else {
                    tvFrase.setText("Não foi possível carregar a frase.");
                    tvAutor.setText("");
                    Toast.makeText(MotivacaoActivity.this, "Erro ao buscar frase.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Quote>> call, Throwable t) {
                tvFrase.setText("Erro ao conectar com a internet.");
                tvAutor.setText("");
                Toast.makeText(MotivacaoActivity.this, "Falha na conexão.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}