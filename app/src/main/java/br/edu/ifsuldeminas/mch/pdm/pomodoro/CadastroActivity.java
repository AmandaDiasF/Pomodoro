package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CadastroActivity extends AppCompatActivity {

    private EditText etNome, etNovoUsuario, etNovaSenha;
    private Button btnFinalizarCadastro;
    private PreferencesHelper preferencesHelper;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbarCadastro);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        preferencesHelper = new PreferencesHelper(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etNome = findViewById(R.id.etNome);
        etNovoUsuario = findViewById(R.id.etNovoUsuario);
        etNovaSenha = findViewById(R.id.etNovaSenha);
        btnFinalizarCadastro = findViewById(R.id.btnFinalizarCadastro);

        btnFinalizarCadastro.setOnClickListener(v -> {
            String nome = etNome.getText().toString().trim();
            String novoEmail = etNovoUsuario.getText().toString().trim();
            String novaSenha = etNovaSenha.getText().toString().trim();

            if (nome.isEmpty() || novoEmail.isEmpty() || novaSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (novaSenha.length() < 6) {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(novoEmail, novaSenha)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();

                            Map<String, Object> perfil = new HashMap<>();
                            perfil.put("nome", nome);
                            perfil.put("email", novoEmail);
                            perfil.put("focosFinalizados", 0);
                            perfil.put("nivel", 1);

                            db.collection("usuarios").document(uid)
                                    .set(perfil)
                                    .addOnSuccessListener(aVoid -> {
                                        preferencesHelper.setLogin(true);
                                        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Erro ao criar perfil na nuvem: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });

                        } else {
                            String mensagemErro = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Erro ao cadastrar usuário.";
                            Toast.makeText(this, mensagemErro, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}