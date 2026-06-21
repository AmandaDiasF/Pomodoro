package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //vinculando toolbar do xml ao suporte de telas
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
    }

    //menu main na app bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    //cliques dos botões na appbar
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_historico) {
            startActivity(new Intent(this, HistoricoActivity.class));
            return true;
        } else if (id == R.id.menu_relatorio) {
            startActivity(new Intent(this, RelatorioActivity.class));
            return true;
        } else if (id == R.id.menu_configuracoes) {
            startActivity(new Intent(this, ConfiguracoesActivity.class));
            return true;
        } else if (id == R.id.menu_compartilhar) {
            // Lógica de Compartilhamento via Intent implícita (Item 9 do PDF)
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Estou usando o Pomodoro Educacional para focar nos meus estudos!");
            startActivity(Intent.createChooser(shareIntent, "Compartilhar via"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}