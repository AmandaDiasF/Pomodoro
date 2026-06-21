package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class ConfiguracoesActivity extends AppCompatActivity {

    private EditText etTempoFoco, etPausaCurta;
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

            if (focoStr.isEmpty() || pausaCurtaStr.isEmpty()) {
                Snackbar.make(v, "Preencha todos os campos.", Snackbar.LENGTH_SHORT).show();
                return;
            }

            int tempoFoco = Integer.parseInt(focoStr);
            int pausaCurta = Integer.parseInt(pausaCurtaStr);

            preferencesHelper.salvarConfiguracoesTempo(tempoFoco, pausaCurta);

            Snackbar.make(v, "Configurações salvas com sucesso!", Snackbar.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}