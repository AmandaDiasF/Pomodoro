package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class HistoricoActivity extends AppCompatActivity {

    private LinearLayout layoutHistoricoVazio;
    private ListView listViewHistorico;
    private SessaoDao sessaoDao; // Usando SessaoDao do Room

    private List<SessaoEstudo> listaSessoes;
    private ArrayList<String> itensHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        layoutHistoricoVazio = findViewById(R.id.layoutHistoricoVazio);
        listViewHistorico = findViewById(R.id.listViewHistorico);

        // Inicializando o DAO
        sessaoDao = AppDatabase.getInstance(this).sessaoDao();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarHistorico);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        carregarHistorico();
        configurarMenuInferior("HISTORICO");
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarHistorico();
    }

    private void carregarHistorico() {
        // Chamada direta ao DAO
        listaSessoes = sessaoDao.listarSessoes();
        itensHistorico = new ArrayList<>();

        if (listaSessoes == null || listaSessoes.isEmpty()) {
            layoutHistoricoVazio.setVisibility(View.VISIBLE);
            listViewHistorico.setVisibility(View.GONE);
            return;
        }

        layoutHistoricoVazio.setVisibility(View.GONE);
        listViewHistorico.setVisibility(View.VISIBLE);

        for (SessaoEstudo sessao : listaSessoes) {
            String item = "Matéria: " + sessao.getMateria()
                    + "\nTempo estudado: " + sessao.getDuracaoRealizada() + " min"
                    + "\nData: " + sessao.getDataInicio()
                    + "\nStatus: " + sessao.getStatus();

            itensHistorico.add(item);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                itensHistorico
        );

        listViewHistorico.setAdapter(adapter);

        listViewHistorico.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(HistoricoActivity.this, "Pressione e segure numa sessão para a apagar.", Toast.LENGTH_SHORT).show();
        });

        listViewHistorico.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(HistoricoActivity.this)
                    .setTitle("Apagar Sessão")
                    .setMessage("Tem certeza que deseja apagar esta sessão do histórico?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        SessaoEstudo sessaoSelecionada = listaSessoes.get(position);
                        // Excluindo diretamente com o DAO do Room
                        sessaoDao.excluirSessao(sessaoSelecionada.getId());
                        carregarHistorico(); // Atualiza a lista na hora
                        Snackbar.make(listViewHistorico, "Sessão apagada com sucesso!", Snackbar.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Não", null)
                    .show();
            return true;
        });
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