package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvPerfilNome, tvPerfilEmail, tvPerfilFocos, tvPerfilNivel;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = findViewById(R.id.toolbarPerfil);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        tvPerfilNome = findViewById(R.id.tvPerfilNome);
        tvPerfilEmail = findViewById(R.id.tvPerfilEmail);
        tvPerfilFocos = findViewById(R.id.tvPerfilFocos);
        tvPerfilNivel = findViewById(R.id.tvPerfilNivel);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        carregarDadosPerfil();
    }

    private void carregarDadosPerfil() {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("usuarios").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {

                            // Extrai os dados do Firestore
                            String nome = document.getString("nome");
                            String email = document.getString("email");
                            Long focos = document.getLong("focosFinalizados");
                            Long nivel = document.getLong("nivel");

                            // Atualiza a interface
                            tvPerfilNome.setText(nome != null ? nome : "Estudante");
                            tvPerfilEmail.setText(email != null ? email : "");
                            tvPerfilFocos.setText(focos != null ? String.valueOf(focos) : "0");
                            tvPerfilNivel.setText(nivel != null ? String.valueOf(nivel) : "1");

                        } else {
                            Toast.makeText(this, "Perfil não encontrado na nuvem.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Erro ao carregar dados.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}