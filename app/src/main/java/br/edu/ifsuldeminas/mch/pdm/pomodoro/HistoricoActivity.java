package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class HistoricoActivity extends AppCompatActivity {
    private TextView tvVazioHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        tvVazioHistorico = findViewById(R.id.tvVazioHistorico);

        // registra o componente para disparar o menu ao clicar
        registerForContextMenu(tvVazioHistorico);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarHistorico);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    // criação menu de contexto
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_contexto_historico, menu);
    }

    // trata a ação do menu de contexto e joga um Toast
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_deletar) {
            Toast.makeText(this, "Ação de deletar acionada!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    // Faz a setinha do voltar funcionar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}