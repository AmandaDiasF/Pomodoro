package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class HistoricoActivity extends AppCompatActivity {

    private TextView tvVazioHistorico;
    private ListView listViewHistorico;
    private DatabaseHelper databaseHelper;

    private List<SessaoEstudo> listaSessoes;
    private ArrayList<String> itensHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        tvVazioHistorico = findViewById(R.id.tvVazioHistorico);
        listViewHistorico = findViewById(R.id.listViewHistorico);
        databaseHelper = new DatabaseHelper(this);

        registerForContextMenu(listViewHistorico);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarHistorico);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        carregarHistorico();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarHistorico();
    }

    private void carregarHistorico() {
        listaSessoes = databaseHelper.listarSessoes();
        itensHistorico = new ArrayList<>();

        if (listaSessoes == null || listaSessoes.isEmpty()) {
            tvVazioHistorico.setVisibility(View.VISIBLE);
            listViewHistorico.setVisibility(View.GONE);
            tvVazioHistorico.setText("Nenhuma sessão cadastrada ainda.");
            return;
        }

        tvVazioHistorico.setVisibility(View.GONE);
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
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_contexto_historico, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_deletar) {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            int posicao = info.position;

            if (posicao >= 0 && posicao < listaSessoes.size()) {
                SessaoEstudo sessaoSelecionada = listaSessoes.get(posicao);
                databaseHelper.excluirSessao(sessaoSelecionada.getId());
                carregarHistorico();

                Snackbar.make(listViewHistorico, "Registro excluído com sucesso!", Snackbar.LENGTH_SHORT).show();
                return true;
            }
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}