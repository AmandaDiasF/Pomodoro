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

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
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
                            if (mAuth.getCurrentUser() == null) {
                                Toast.makeText(this, "Não foi possível concluir o cadastro. Tente novamente.", Toast.LENGTH_LONG).show();
                                return;
                            }

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
                                        Toast.makeText(
                                                this,
                                                traduzirErroFirestore(e),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    });

                        } else {
                            String mensagemErro = traduzirErroCadastro(task.getException());
                            Toast.makeText(this, mensagemErro, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private String traduzirErroCadastro(Exception exception) {
        if (exception == null) {
            return "Não foi possível realizar o cadastro.";
        }

        if (exception instanceof FirebaseAuthException) {
            String codigo = ((FirebaseAuthException) exception).getErrorCode();

            switch (codigo) {
                case "ERROR_INVALID_EMAIL":
                    return "O e-mail informado é inválido.";

                case "ERROR_EMAIL_ALREADY_IN_USE":
                    return "Este e-mail já está em uso.";

                case "ERROR_WEAK_PASSWORD":
                    return "A senha é muito fraca. Use pelo menos 6 caracteres.";

                case "ERROR_OPERATION_NOT_ALLOWED":
                    return "O cadastro por e-mail e senha não está habilitado no Firebase.";

                case "ERROR_NETWORK_REQUEST_FAILED":
                    return "Erro de conexão. Verifique sua internet e tente novamente.";

                case "ERROR_TOO_MANY_REQUESTS":
                    return "Muitas tentativas realizadas. Aguarde um momento e tente novamente.";

                default:
                    return "Não foi possível realizar o cadastro. Tente novamente.";
            }
        }

        if (exception instanceof FirebaseNetworkException) {
            return "Erro de conexão. Verifique sua internet e tente novamente.";
        }

        return "Não foi possível realizar o cadastro. Tente novamente.";
    }

    private String traduzirErroFirestore(Exception exception) {
        if (exception == null) {
            return "Erro ao salvar perfil na nuvem.";
        }

        String mensagem = exception.getMessage();

        if (mensagem != null) {
            String msg = mensagem.toLowerCase();

            if (msg.contains("permission denied") || msg.contains("missing or insufficient permissions")) {
                return "Sem permissão para salvar o perfil na nuvem.";
            }

            if (msg.contains("unavailable")) {
                return "Serviço indisponível no momento. Tente novamente.";
            }

            if (msg.contains("network")) {
                return "Erro de conexão ao salvar na nuvem. Verifique sua internet.";
            }
        }

        return "Erro ao salvar perfil na nuvem. Tente novamente.";
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}