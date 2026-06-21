package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class RelatorioActivity extends AppCompatActivity {

    private TextView tvVazioRelatorio;
    private MaterialButton btnCompartilharRelatorio;
    private DatabaseHelper databaseHelper;
    private List<RelatorioMateria> listaRelatorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);

        tvVazioRelatorio = findViewById(R.id.tvVazioRelatorio);
        btnCompartilharRelatorio = findViewById(R.id.btnCompartilharRelatorio);
        databaseHelper = new DatabaseHelper(this);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarRelatorio);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        carregarRelatorio();

        btnCompartilharRelatorio.setOnClickListener(v -> compartilharRelatorio());
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarRelatorio();
    }

    private void carregarRelatorio() {
        listaRelatorio = databaseHelper.buscarRelatorioPorMateria();

        if (listaRelatorio == null || listaRelatorio.isEmpty()) {
            tvVazioRelatorio.setText("Ainda não há dados para o relatório.");
            return;
        }

        StringBuilder textoRelatorio = new StringBuilder();

        for (RelatorioMateria item : listaRelatorio) {
            textoRelatorio.append("Matéria: ").append(item.getMateria()).append("\n");
            textoRelatorio.append("Total estudado: ").append(item.getTotalMinutos()).append(" min\n\n");
        }

        tvVazioRelatorio.setText(textoRelatorio.toString());
    }

    private void compartilharRelatorio() {
        if (listaRelatorio == null || listaRelatorio.isEmpty()) {
            Toast.makeText(this, "Ainda não há relatório para compartilhar.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Meu Relatório de Estudos no Pomodoro Educacional:\n\n");

        for (RelatorioMateria item : listaRelatorio) {
            mensagem.append("Matéria: ").append(item.getMateria()).append("\n");
            mensagem.append("Total estudado: ").append(item.getTotalMinutos()).append(" min\n\n");
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mensagem.toString());
        startActivity(Intent.createChooser(shareIntent, "Compartilhar relatório via"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}