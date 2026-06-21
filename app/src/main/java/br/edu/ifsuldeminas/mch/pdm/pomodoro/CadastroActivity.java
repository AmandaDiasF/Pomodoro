package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CadastroActivity extends AppCompatActivity {

    private EditText etNovoUsuario, etNovaSenha;
    private Button btnFinalizarCadastro;
    private PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        Toolbar toolbar = findViewById(R.id.toolbarCadastro);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        preferencesHelper = new PreferencesHelper(this);

        etNovoUsuario = findViewById(R.id.etNovoUsuario);
        etNovaSenha = findViewById(R.id.etNovaSenha);
        btnFinalizarCadastro = findViewById(R.id.btnFinalizarCadastro);

        btnFinalizarCadastro.setOnClickListener(v -> {
            String novoUsuario = etNovoUsuario.getText().toString().trim();
            String novaSenha = etNovaSenha.getText().toString().trim();

            if (novoUsuario.isEmpty() || novaSenha.isEmpty()) {
                Toast.makeText(CadastroActivity.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            preferencesHelper.salvarLogin(novoUsuario, novaSenha, true);

            Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(CadastroActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}