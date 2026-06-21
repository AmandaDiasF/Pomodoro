package br.edu.ifsuldeminas.mch.pdm.pomodoro;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmActivity extends AppCompatActivity {

    private TextView tvMensagemAlarme;
    private Button btnAcao;
    private Button btnEncerrarSessao;

    private Ringtone ringtone;
    private boolean veioDoFoco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        tvMensagemAlarme = findViewById(R.id.tvMensagemAlarme);
        btnAcao = findViewById(R.id.btnAcao);
        btnEncerrarSessao = findViewById(R.id.btnEncerrarSessao);

        veioDoFoco = getIntent().getBooleanExtra("veioDoFoco", true);

        if (veioDoFoco) {
            tvMensagemAlarme.setText("Tempo de foco finalizado! Deseja iniciar a pausa?");
            btnAcao.setText("Iniciar pausa");
        } else {
            tvMensagemAlarme.setText("Tempo de pausa finalizado! Deseja voltar ao foco?");
            btnAcao.setText("Iniciar foco");
        }

        Uri somAlarme = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (somAlarme == null) {
            somAlarme = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        ringtone = RingtoneManager.getRingtone(getApplicationContext(), somAlarme);

        if (ringtone != null) {
            ringtone.play();
        }

        btnAcao.setOnClickListener(v -> {
            pararAlarme();

            Intent resultado = new Intent();
            resultado.putExtra("acao", veioDoFoco ? "INICIAR_PAUSA" : "INICIAR_FOCO");
            setResult(RESULT_OK, resultado);
            finish();
        });

        btnEncerrarSessao.setOnClickListener(v -> {
            pararAlarme();

            Intent resultado = new Intent();
            resultado.putExtra("acao", "ENCERRAR_SESSAO");
            setResult(RESULT_OK, resultado);
            finish();
        });
    }

    private void pararAlarme() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    @Override
    protected void onDestroy() {
        pararAlarme();
        super.onDestroy();
    }
}