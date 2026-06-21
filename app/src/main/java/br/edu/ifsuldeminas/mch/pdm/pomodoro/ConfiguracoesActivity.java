package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

public class ConfiguracoesActivity extends AppCompatActivity {

    private Button btnSalvarConfiguracoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        btnSalvarConfiguracoes = findViewById(R.id.btnSalvarConfiguracoes);

        btnSalvarConfiguracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Configurações salvas com sucesso!", Snackbar.LENGTH_SHORT).show();
            }
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarConfiguracoes);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}