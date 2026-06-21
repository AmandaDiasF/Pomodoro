package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario, etSenha;
    private Button btnEntrar, btnCadastrar;
    private PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferencesHelper = new PreferencesHelper(this);

        etUsuario = findViewById(R.id.etUsuario);
        etSenha = findViewById(R.id.etSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        solicitarPermissaoNotificacao();

        if (preferencesHelper.isUsuarioLogado()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        btnEntrar.setOnClickListener(v -> {
            String usuarioDigitado = etUsuario.getText().toString().trim();
            String senhaDigitada = etSenha.getText().toString().trim();

            if (usuarioDigitado.isEmpty() || senhaDigitada.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Preencha usuário e senha.", Toast.LENGTH_SHORT).show();
                return;
            }

            String usuarioSalvo = preferencesHelper.getNomeUsuario();
            String senhaSalva = preferencesHelper.getSenha();

            if (usuarioDigitado.equals(usuarioSalvo) && senhaDigitada.equals(senhaSalva)) {
                preferencesHelper.setLogin(true);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Usuário ou senha inválidos.", Toast.LENGTH_SHORT).show();
            }
        });

        btnCadastrar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });
    }

    private void solicitarPermissaoNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100
                );
            }
        }
    }
}