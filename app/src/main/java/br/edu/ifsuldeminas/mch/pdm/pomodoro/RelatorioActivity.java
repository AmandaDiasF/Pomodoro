package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class RelatorioActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarRelatorio);
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