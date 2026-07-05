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
    private SessaoDao sessaoDao; // Usando SessaoDao do Room diretamente
    private List<RelatorioMateria> listaRelatorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);

        tvVazioRelatorio = findViewById(R.id.tvVazioRelatorio);
        btnCompartilharRelatorio = findViewById(R.id.btnCompartilharRelatorio);

        // Inicializando o DAO
        sessaoDao = AppDatabase.getInstance(this).sessaoDao();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarRelatorio);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        carregarRelatorio();

        btnCompartilharRelatorio.setOnClickListener(v -> compartilharRelatorio());

        configurarMenuInferior("RELATORIO");
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarRelatorio();
    }

    private void carregarRelatorio() {
        // Chamada direta ao Room para buscar os dados
        listaRelatorio = sessaoDao.buscarRelatorioPorMateria();

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

    private void configurarMenuInferior(String telaAtual) {
        android.widget.ImageButton btnInicio = findViewById(R.id.btnNavInicio);
        android.widget.ImageButton btnHistorico = findViewById(R.id.btnNavHistorico);
        android.widget.ImageButton btnRelatorio = findViewById(R.id.btnNavRelatorio);
        android.widget.ImageButton btnConfig = findViewById(R.id.btnNavConfig);

        btnInicio.setBackgroundResource(android.R.color.transparent);
        btnInicio.setColorFilter(getColor(R.color.text_muted));
        btnHistorico.setBackgroundResource(android.R.color.transparent);
        btnHistorico.setColorFilter(getColor(R.color.text_muted));
        btnRelatorio.setBackgroundResource(android.R.color.transparent);
        btnRelatorio.setColorFilter(getColor(R.color.text_muted));
        btnConfig.setBackgroundResource(android.R.color.transparent);
        btnConfig.setColorFilter(getColor(R.color.text_muted));

        switch (telaAtual) {
            case "INICIO":
                btnInicio.setBackgroundResource(R.drawable.bg_item_selecionado);
                btnInicio.setColorFilter(getColor(R.color.coral_primary));
                break;
            case "HISTORICO":
                btnHistorico.setBackgroundResource(R.drawable.bg_item_selecionado);
                btnHistorico.setColorFilter(getColor(R.color.coral_primary));
                break;
            case "RELATORIO":
                btnRelatorio.setBackgroundResource(R.drawable.bg_item_selecionado);
                btnRelatorio.setColorFilter(getColor(R.color.coral_primary));
                break;
            case "CONFIG":
                btnConfig.setBackgroundResource(R.drawable.bg_item_selecionado);
                btnConfig.setColorFilter(getColor(R.color.coral_primary));
                break;
        }

        btnInicio.setOnClickListener(v -> {
            if (!telaAtual.equals("INICIO")) {
                android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        btnHistorico.setOnClickListener(v -> {
            if (!telaAtual.equals("HISTORICO")) {
                android.content.Intent intent = new android.content.Intent(this, HistoricoActivity.class);
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        btnRelatorio.setOnClickListener(v -> {
            if (!telaAtual.equals("RELATORIO")) {
                android.content.Intent intent = new android.content.Intent(this, RelatorioActivity.class);
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        btnConfig.setOnClickListener(v -> {
            if (!telaAtual.equals("CONFIG")) {
                android.content.Intent intent = new android.content.Intent(this, ConfiguracoesActivity.class);
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }
}