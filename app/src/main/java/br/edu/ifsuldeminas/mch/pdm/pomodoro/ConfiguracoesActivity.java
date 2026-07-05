package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class ConfiguracoesActivity extends AppCompatActivity {

    private EditText etTempoFoco, etPausaCurta, etPausaLonga;
    private MaterialButton btnSalvarConfiguracoes;
    private PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        Toolbar toolbar = findViewById(R.id.toolbarConfiguracoes);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        preferencesHelper = new PreferencesHelper(this);

        etTempoFoco = findViewById(R.id.etTempoFoco);
        etPausaCurta = findViewById(R.id.etPausaCurta);
        btnSalvarConfiguracoes = findViewById(R.id.btnSalvarConfiguracoes);

        etTempoFoco.setText(String.valueOf(preferencesHelper.getTempoFoco()));
        etPausaCurta.setText(String.valueOf(preferencesHelper.getPausaCurta()));

        btnSalvarConfiguracoes.setOnClickListener(v -> {
            String focoStr = etTempoFoco.getText().toString().trim();
            String pausaCurtaStr = etPausaCurta.getText().toString().trim();
            String pausaLongaStr = etPausaLonga.getText().toString().trim();

            if (focoStr.isEmpty() || pausaCurtaStr.isEmpty() || pausaLongaStr.isEmpty()) {
                Snackbar.make(v, "Preencha todos os campos.", Snackbar.LENGTH_SHORT).show();
                return;
            }

            int tempoFoco = Integer.parseInt(focoStr);
            int pausaCurta = Integer.parseInt(pausaCurtaStr);

            preferencesHelper.salvarConfiguracoesTempo(tempoFoco, pausaCurta);

            Snackbar.make(v, "Configurações salvas com sucesso!", Snackbar.LENGTH_SHORT).show();
        });

        configurarMenuInferior("CONFIG");
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