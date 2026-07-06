package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etMateria;
    private TextView tvTimer;
    private ProgressBar progressCircular;
    private Button btnIniciar, btnPausar, btnCancelar, btnFinalizarEstudo;

    private CountDownTimer countDownTimer;
    private long tempoRestanteEmMillis;
    private long tempoFocoEmMillis;
    private long tempoPausaEmMillis;

    private boolean timerRodando = false;
    private boolean emModoFoco = true;

    private PreferencesHelper preferencesHelper;
    private SessaoDao sessaoDao;

    private String dataInicioSessao = "";
    private int totalMinutosFocoConcluidos = 0;
    private int totalMinutosPausaConcluidos = 0;

    private ActivityResultLauncher<Intent> alarmLauncher;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferencesHelper = new PreferencesHelper(this);

        if (preferencesHelper.isModoEscuro()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        sessaoDao = AppDatabase.getInstance(this).sessaoDao();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etMateria = findViewById(R.id.etMateria);
        tvTimer = findViewById(R.id.tvTimer);
        progressCircular = findViewById(R.id.progressCircular);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnPausar = findViewById(R.id.btnPausar);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnFinalizarEstudo = findViewById(R.id.btnFinalizarEstudo);

        alarmLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String acao = result.getData().getStringExtra("acao");

                        if ("INICIAR_PAUSA".equals(acao)) {
                            definirModoPausa();
                            iniciarTimer();
                        } else if ("INICIAR_FOCO".equals(acao)) {
                            definirModoFoco();
                            iniciarTimer();
                        } else if ("ENCERRAR_SESSAO".equals(acao)) {
                            finalizarEstudo();
                        }
                    }
                }
        );

        btnIniciar.setOnClickListener(v -> iniciarTimer());
        btnPausar.setOnClickListener(v -> pausarTimer());
        btnCancelar.setOnClickListener(v -> cancelarTimer());
        btnFinalizarEstudo.setOnClickListener(v -> finalizarEstudo());

        configurarMenuInferior("INICIO");

        if (savedInstanceState != null) {
            timerRodando = savedInstanceState.getBoolean("timerRodando", false);
            emModoFoco = savedInstanceState.getBoolean("emModoFoco", true);
            tempoFocoEmMillis = savedInstanceState.getLong("tempoFocoEmMillis", 25 * 60 * 1000L);
            tempoPausaEmMillis = savedInstanceState.getLong("tempoPausaEmMillis", 5 * 60 * 1000L);
            dataInicioSessao = savedInstanceState.getString("dataInicioSessao", "");
            totalMinutosFocoConcluidos = savedInstanceState.getInt("totalMinutosFocoConcluidos", 0);
            totalMinutosPausaConcluidos = savedInstanceState.getInt("totalMinutosPausaConcluidos", 0);

            if (timerRodando) {
                long tempoAlvoFim = savedInstanceState.getLong(
                        "tempoAlvoFimEmMillis",
                        System.currentTimeMillis() + savedInstanceState.getLong("tempoRestanteEmMillis", tempoFocoEmMillis)
                );
                tempoRestanteEmMillis = tempoAlvoFim - System.currentTimeMillis();

                if (tempoRestanteEmMillis > 0) {
                    retomarTimer();
                } else {
                    timerRodando = false;
                    tempoRestanteEmMillis = 0;
                    atualizarTimer();
                    atualizarProgressoCircular();
                    atualizarBotoes();
                }
            } else {
                tempoRestanteEmMillis = savedInstanceState.getLong(
                        "tempoRestanteEmMillis",
                        emModoFoco ? tempoFocoEmMillis : tempoPausaEmMillis
                );
                atualizarTimer();
                atualizarProgressoCircular();
                atualizarBotoes();
            }
        } else {
            carregarTemposSalvos();
            definirModoFoco();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("timerRodando", timerRodando);
        outState.putBoolean("emModoFoco", emModoFoco);
        outState.putLong("tempoRestanteEmMillis", tempoRestanteEmMillis);
        outState.putLong("tempoFocoEmMillis", tempoFocoEmMillis);
        outState.putLong("tempoPausaEmMillis", tempoPausaEmMillis);
        outState.putString("dataInicioSessao", dataInicioSessao);
        outState.putInt("totalMinutosFocoConcluidos", totalMinutosFocoConcluidos);
        outState.putInt("totalMinutosPausaConcluidos", totalMinutosPausaConcluidos);
        outState.putLong("tempoAlvoFimEmMillis", System.currentTimeMillis() + tempoRestanteEmMillis);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!timerRodando) {
            long tempoAnteriorFoco = tempoFocoEmMillis;
            long tempoAnteriorPausa = tempoPausaEmMillis;
            carregarTemposSalvos();

            if (tempoRestanteEmMillis == (emModoFoco ? tempoAnteriorFoco : tempoAnteriorPausa)) {
                if (emModoFoco) {
                    definirModoFoco();
                } else {
                    definirModoPausa();
                }
            }
        }
    }

    private void carregarTemposSalvos() {
        int focoMinutos = preferencesHelper.getTempoFoco();
        int pausaMinutos = preferencesHelper.getPausaCurta();

        tempoFocoEmMillis = focoMinutos * 60L * 1000L;
        tempoPausaEmMillis = pausaMinutos * 60L * 1000L;
    }

    private void definirModoFoco() {
        emModoFoco = true;
        tempoRestanteEmMillis = tempoFocoEmMillis;
        atualizarTimer();
        atualizarProgressoCircular();
        atualizarBotoes();
    }

    private void definirModoPausa() {
        emModoFoco = false;
        tempoRestanteEmMillis = tempoPausaEmMillis;
        atualizarTimer();
        atualizarProgressoCircular();
        atualizarBotoes();
    }

    private void abrirTelaAlarme(boolean veioDoFoco) {
        Intent intent = new Intent(this, AlarmActivity.class);
        intent.putExtra("veioDoFoco", veioDoFoco);
        alarmLauncher.launch(intent);
    }

    private void iniciarTimer() {
        String materia = etMateria.getText().toString().trim();

        if (materia.isEmpty()) {
            Toast.makeText(this, "Digite a matéria antes de iniciar.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (timerRodando) return;

        if (dataInicioSessao.isEmpty()) {
            dataInicioSessao = getDataHoraAtual();
        }

        executarContagemRegressiva();
    }

    private void retomarTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        executarContagemRegressiva();
    }

    private void executarContagemRegressiva() {
        countDownTimer = new CountDownTimer(tempoRestanteEmMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                tempoRestanteEmMillis = millisUntilFinished;
                atualizarTimer();
                atualizarProgressoCircular();
            }

            @Override
            public void onFinish() {
                timerRodando = false;
                tempoRestanteEmMillis = 0;
                atualizarTimer();
                atualizarProgressoCircular();
                atualizarBotoes();

                if (emModoFoco) {
                    totalMinutosFocoConcluidos += (int) (tempoFocoEmMillis / 60000);
                    atualizarPerfilFocoConcluido();
                    abrirTelaAlarme(true);
                } else {
                    totalMinutosPausaConcluidos += (int) (tempoPausaEmMillis / 60000);
                    abrirTelaAlarme(false);
                }
            }
        }.start();

        timerRodando = true;
        atualizarBotoes();
    }

    private void atualizarPerfilFocoConcluido() {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long focosAtuais = documentSnapshot.getLong("focosFinalizados");
                        long novoTotalFocos = (focosAtuais != null ? focosAtuais : 0) + 1;
                        long novoNivel = (novoTotalFocos / 5) + 1;

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("focosFinalizados", novoTotalFocos);
                        updates.put("nivel", novoNivel);

                        db.collection("usuarios").document(uid).update(updates);
                    }
                });
    }

    private void pausarTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        timerRodando = false;
        atualizarBotoes();
        Toast.makeText(this, "Timer pausado.", Toast.LENGTH_SHORT).show();
    }

    private void cancelarTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        timerRodando = false;
        definirModoFoco();
        atualizarProgressoCircular();
        dataInicioSessao = "";
        totalMinutosFocoConcluidos = 0;
        totalMinutosPausaConcluidos = 0;

        Toast.makeText(this, "Pomodoro cancelado.", Toast.LENGTH_SHORT).show();
    }

    private void finalizarEstudo() {
        String materia = etMateria.getText().toString().trim();

        if (materia.isEmpty()) {
            Toast.makeText(this, "Digite a matéria antes de finalizar.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dataInicioSessao.isEmpty()) {
            Toast.makeText(this, "Nenhuma sessão iniciada para salvar.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        timerRodando = false;

        SessaoEstudo sessao = new SessaoEstudo();
        sessao.setMateria(materia);
        sessao.setTipo("Estudo");
        sessao.setDuracaoPlanejada(totalMinutosFocoConcluidos + totalMinutosPausaConcluidos);
        sessao.setDuracaoRealizada(totalMinutosFocoConcluidos);
        sessao.setDataInicio(dataInicioSessao);
        sessao.setDataFim(getDataHoraAtual());
        sessao.setStatus("Concluído");

        long id = sessaoDao.inserirSessao(sessao);

        if (id > 0) {
            Toast.makeText(this, "Sessão salva com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Erro ao salvar sessão.", Toast.LENGTH_SHORT).show();
        }

        dataInicioSessao = "";
        totalMinutosFocoConcluidos = 0;
        totalMinutosPausaConcluidos = 0;
        definirModoFoco();
    }

    private String getDataHoraAtual() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void atualizarTimer() {
        int minutos = (int) (tempoRestanteEmMillis / 1000) / 60;
        int segundos = (int) (tempoRestanteEmMillis / 1000) % 60;

        String tempoFormatado = String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos);
        tvTimer.setText(tempoFormatado);
    }

    private void atualizarBotoes() {
        btnIniciar.setEnabled(!timerRodando);
        btnPausar.setEnabled(timerRodando);
        btnCancelar.setEnabled(true);
        btnFinalizarEstudo.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem itemTema = menu.findItem(R.id.menu_tema);
        if (preferencesHelper.isModoEscuro()) {
            itemTema.setTitle("Modo Claro");
        } else {
            itemTema.setTitle("Modo Escuro");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_tema) {
            boolean isEscuroAtual = preferencesHelper.isModoEscuro();
            boolean novoModo = !isEscuroAtual;

            preferencesHelper.setModoEscuro(novoModo);

            if (novoModo) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            return true;

        } else if (id == R.id.menu_perfil) {
            startActivity(new Intent(this, PerfilActivity.class));
            return true;

        } else if (id == R.id.menu_sair) {
            preferencesHelper.logout();
            AppDatabase.getInstance(this).clearAllTables();
            FirebaseAuth.getInstance().signOut();

            Intent intentLogin = new Intent(this, LoginActivity.class);
            intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentLogin);
            finish();
            return true;

        } else if (id == R.id.menu_compartilhar) {
            startActivity(new Intent(this, RelatorioActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void atualizarProgressoCircular() {
        long tempoTotal = emModoFoco ? tempoFocoEmMillis : tempoPausaEmMillis;

        if (tempoTotal > 0) {
            int progresso = (int) ((tempoRestanteEmMillis * 1000) / tempoTotal);
            progressCircular.setProgress(progresso);
        }
    }

    private void configurarMenuInferior(String telaAtual) {
        android.widget.ImageButton btnInicio = findViewById(R.id.btnNavInicio);
        android.widget.ImageButton btnHistorico = findViewById(R.id.btnNavHistorico);
        android.widget.ImageButton btnRelatorio = findViewById(R.id.btnNavRelatorio);
        android.widget.ImageButton btnConfig = findViewById(R.id.btnNavConfig);

        btnInicio.setBackgroundResource(android.R.color.transparent);
        btnInicio.setColorFilter(getColor(R.color.text_muted));
        btnHistorico.setBackgroundResource(android.R.color.transparent);
        btnHistorico.setColorFilter(getColor(R.color.text_muted));
        btnRelatorio.setBackgroundResource(android.R.color.transparent);
        btnRelatorio.setColorFilter(getColor(R.color.text_muted));
        btnConfig.setBackgroundResource(android.R.color.transparent);
        btnConfig.setColorFilter(getColor(R.color.text_muted));

        switch (telaAtual) {
            case "INICIO":
                btnInicio.setBackgroundResource(R.drawable.bg_item_selecionado);
                btnInicio.setColorFilter(getColor(R.color.coral_primary));
                break;
            case "HISTORICO":
                btnHistorico.setBackgroundResource(R.drawable.bg_item_selecionado);
                btnHistorico.setColorFilter(getColor(R.color.coral_primary));
                break;
            case "RELATORIO":
                btnRelatorio.setBackgroundResource(R.drawable.bg_item_selecionado);
                btnRelatorio.setColorFilter(getColor(R.color.coral_primary));
                break;
            case "CONFIG":
                btnConfig.setBackgroundResource(R.drawable.bg_item_selecionado);
                btnConfig.setColorFilter(getColor(R.color.coral_primary));
                break;
        }

        btnInicio.setOnClickListener(v -> {
            if (!telaAtual.equals("INICIO")) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        btnHistorico.setOnClickListener(v -> {
            if (!telaAtual.equals("HISTORICO")) {
                Intent intent = new Intent(this, HistoricoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        btnRelatorio.setOnClickListener(v -> {
            if (!telaAtual.equals("RELATORIO")) {
                Intent intent = new Intent(this, RelatorioActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        btnConfig.setOnClickListener(v -> {
            if (!telaAtual.equals("CONFIG")) {
                Intent intent = new Intent(this, ConfiguracoesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }
}