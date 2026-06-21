package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private DatabaseHelper databaseHelper;

    private String dataInicioSessao = "";
    private int totalMinutosFocoConcluidos = 0;
    private int totalMinutosPausaConcluidos = 0;

    private ActivityResultLauncher<Intent> alarmLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        preferencesHelper = new PreferencesHelper(this);
        databaseHelper = new DatabaseHelper(this);

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

        carregarTemposSalvos();
        definirModoFoco();

        btnIniciar.setOnClickListener(v -> iniciarTimer());
        btnPausar.setOnClickListener(v -> pausarTimer());
        btnCancelar.setOnClickListener(v -> cancelarTimer());
        btnFinalizarEstudo.setOnClickListener(v -> finalizarEstudo());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!timerRodando) {
            carregarTemposSalvos();

            if (emModoFoco) {
                definirModoFoco();
            } else {
                definirModoPausa();
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

        long id = databaseHelper.inserirSessao(sessao);

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
        return true;
    }

    @Override
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
            startActivity(new Intent(this, RelatorioActivity.class));
            return true;
        } else if (id == R.id.menu_sair) {
            preferencesHelper.setLogin(false);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
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
    }
