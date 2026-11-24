package com.santosdumont.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.santosdumont.R;
import com.santosdumont.utils.PresencaHelper;

import com.santosdumont.BuildConfig;

public class DevMenuActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_menu);

        // RODAPE: versao real do BuildConfig
        TextView txtFooterDev = findViewById(R.id.txtDevVersaoFooter);
        if (txtFooterDev != null) {
            txtFooterDev.setText("App Santos Dumont v" + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
        }

        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        // ============================================================
        // SETUP DAS PASTAS EXPANSIVEIS
        // ============================================================
        setupFolder(R.id.headerTelas,    R.id.containerTelas,    R.id.arrowTelas);
        setupFolder(R.id.headerNotif,    R.id.containerNotif,    R.id.arrowNotif);
        setupFolder(R.id.headerFirebase, R.id.containerFirebase, R.id.arrowFirebase);
        setupFolder(R.id.headerDados,    R.id.containerDados,    R.id.arrowDados);
        setupFolder(R.id.headerConfig,   R.id.containerConfig,   R.id.arrowConfig);
        setupFolder(R.id.headerAvisos,   R.id.containerAvisos,   R.id.arrowAvisos);
        setupFolder(R.id.headerVersao,   R.id.containerVersao,   R.id.arrowVersao);

        // ============================================================
        // FORCAR DIALOG DE PRESENCA
        // ============================================================
        findViewById(R.id.btnDevNotifPresenca).setOnClickListener(v -> {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            String dataFreq = cal.get(java.util.Calendar.YEAR) + "-"
                    + cal.get(java.util.Calendar.MONTH) + "-"
                    + cal.get(java.util.Calendar.DAY_OF_MONTH);
            com.santosdumont.utils.NotificationHelper.enviarNotificacaoComAcoes(
                    this, "Matemática", "Prof. João", " 7:00", dataFreq);
            Toast.makeText(this, "Notificacao de presenca disparada", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btnDevNotifIntervalo).setOnClickListener(v -> {
            com.santosdumont.utils.NotificationHelper.enviarNotificacao(this,
                    getString(R.string.notif_intervalo),
                    getString(R.string.notif_intervalo_corpo));
            Toast.makeText(this, "Notificacao de intervalo disparada", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btnDevNotifDespedida).setOnClickListener(v -> {
            com.santosdumont.utils.NotificationHelper.enviarNotificacao(this,
                    getString(R.string.notif_despedida),
                    getString(R.string.notif_despedida_corpo));
            Toast.makeText(this, "Notificacao de despedida disparada", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btnDevPresenca).setOnClickListener(v -> {
            PresencaHelper.salvarResposta(this, false); // reseta
            // Forca modo teste
            PresencaHelper.MODO_TESTE = true;
            startActivity(new Intent(this, DialogPresencaActivity.class));
        });

        // NOVO 3: Forcar presenca pos-load (limpa flag e volta pro Shell)
        View btnPosLoad = findViewById(R.id.btnDevForcarPresencaPosLoad);
        if (btnPosLoad != null) {
            btnPosLoad.setOnClickListener(v -> {
                String hoje = new java.text.SimpleDateFormat("yyyy-MM-dd",
                        java.util.Locale.US).format(new java.util.Date());
                prefs.edit().remove("presenca_ja_aberta_" + hoje).apply();
                Toast.makeText(this, "Flag limpa. Voltando ao Shell...", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, ShellActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            });
        }

        // ============================================================
        // TOGGLE MODO TESTE
        // ============================================================
        final TextView txtToggle = findViewById(R.id.txtDevToggleTeste);
        atualizarTextoToggle(txtToggle);
        findViewById(R.id.btnDevToggleTeste).setOnClickListener(v -> {
            PresencaHelper.MODO_TESTE = !PresencaHelper.MODO_TESTE;
            prefs.edit().putBoolean("modo_teste_forcado", PresencaHelper.MODO_TESTE).apply();
            atualizarTextoToggle(txtToggle);
            Toast.makeText(this, "Modo teste: " + PresencaHelper.MODO_TESTE, Toast.LENGTH_SHORT).show();
        });

        // ============================================================
        // FORCAR BLOQUEIO (tela preta)
        // ============================================================
        findViewById(R.id.btnDevBloqueio).setOnClickListener(v -> {
            Intent i = new Intent(this, BloqueioActivity.class);
            startActivity(i);
        });

        // ============================================================
        // RESETAR SAIDAS (contador de "saiu do app")
        // ============================================================
        findViewById(R.id.btnDevResetSaidas).setOnClickListener(v -> {
            prefs.edit()
                    .putInt("saidas_consecutivas", 0)
                    .remove("aula_vaga_fim")
                    .apply();
            Toast.makeText(this, "Saidas resetadas", Toast.LENGTH_SHORT).show();
        });

        // ============================================================
        // FORCAR CHANGELOG
        // ============================================================
        findViewById(R.id.btnDevChangelog).setOnClickListener(v -> {
            prefs.edit().remove("versao_vista").apply();
            startActivity(new Intent(this, ChangelogActivity.class));
        });

        // ============================================================
        // FORCAR TUTORIAL
        // ============================================================
        findViewById(R.id.btnDevTutorial).setOnClickListener(v -> {
            prefs.edit().putBoolean("tutorial_visto", false).apply();
            startActivity(new Intent(this, TutorialActivity.class));
        });

        // ============================================================
        // LIMPAR DADOS DO APP (SharedPreferences)
        // ============================================================
        findViewById(R.id.btnDevLimparDados).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Toast.makeText(this, "Dados limpos! Reinicie o app.", Toast.LENGTH_LONG).show();
        });

        // ============================================================
        // VER LOGS DE ERRO
        // ============================================================
        findViewById(R.id.btnDevLogs).setOnClickListener(v -> {
            try {
                java.io.File dir = new java.io.File(getFilesDir(), "erros");
                if (dir.exists()) {
                    java.io.File[] files = dir.listFiles();
                    if (files != null && files.length > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (java.io.File f : files) {
                            sb.append(f.getName()).append("\n");
                        }
                        new androidx.appcompat.app.AlertDialog.Builder(this)
                                .setTitle("Logs de erro (" + files.length + ")")
                                .setMessage(sb.toString())
                                .setPositiveButton("OK", null)
                                .show();
                    } else {
                        Toast.makeText(this, "Nenhum log de erro", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Nenhum log de erro", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // ============================================================
        // VER DADOS COLETADOS
        // ============================================================
        findViewById(R.id.btnDevVerDados).setOnClickListener(v -> {
            String dados = com.santosdumont.utils.DataCollector.lerTudo(this);
            long tam = com.santosdumont.utils.DataCollector.tamanho(this);

            String header = "Arquivo: dados_uso.txt\n" +
                    "Tamanho: " + (tam / 1024) + " KB\n" +
                    "Local: " + com.santosdumont.utils.DataCollector.getArquivo(this).getAbsolutePath() + "\n\n";

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Dados coletados")
                    .setMessage(header + dados)
                    .setPositiveButton("OK", null)
                    .show();
        });

        // ============================================================
        // LIMPAR DADOS COLETADOS
        // ============================================================
        findViewById(R.id.btnDevLimparColeta).setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Apagar dados?")
                    .setMessage("Isso apaga o arquivo dados_uso.txt.")
                    .setPositiveButton("Apagar", (d, w) -> {
                        com.santosdumont.utils.DataCollector.limpar(this);
                        Toast.makeText(this, "Dados apagados", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        // ============================================================
        // ENVIAR PRO FIREBASE
        // ============================================================
        findViewById(R.id.btnDevEnviarFirebase).setOnClickListener(v -> {
            Toast.makeText(this, "Enviando...", Toast.LENGTH_SHORT).show();
            com.santosdumont.utils.FirebaseUploader.enviarAgora(this);
            Toast.makeText(this, "Enviado! Veja no Firebase Console.", Toast.LENGTH_LONG).show();
        });

        // ============================================================
        // SERVICO EM 2o PLANO
        // ============================================================
        findViewById(R.id.btnDevIniciarServico).setOnClickListener(v -> {
            Intent i = new Intent(this, com.santosdumont.utils.AulaForegroundService.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(i);
            } else {
                startService(i);
            }
            Toast.makeText(this, "Servico iniciado", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnDevPararServico).setOnClickListener(v -> {
            Intent i = new Intent(this, com.santosdumont.utils.AulaForegroundService.class);
            stopService(i);
            Toast.makeText(this, "Servico parado", Toast.LENGTH_SHORT).show();
        });

        // ============================================================
        // AGENDAR ALARMES
        // ============================================================
        findViewById(R.id.btnDevAgendarAlarmes).setOnClickListener(v -> {
            com.santosdumont.utils.AulaScheduler.agendarTudo(this);
            Toast.makeText(this, "Alarmes agendados!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnDevCancelarAlarmes).setOnClickListener(v -> {
            com.santosdumont.utils.AulaScheduler.cancelarTudo(this);
            Toast.makeText(this, "Alarmes cancelados", Toast.LENGTH_SHORT).show();
        });

        // ============================================================
        // SIMULAR EVENTOS
        // ============================================================
        findViewById(R.id.btnDevSimularIniciar).setOnClickListener(v -> {
            Intent i = new Intent(this, com.santosdumont.utils.AulaAlarmReceiver.class);
            i.setAction(com.santosdumont.utils.AulaScheduler.ACTION_INICIAR);
            sendBroadcast(i);
            Toast.makeText(this, "Simulado: INICIAR", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnDevSimularParar).setOnClickListener(v -> {
            Intent i = new Intent(this, com.santosdumont.utils.AulaAlarmReceiver.class);
            i.setAction(com.santosdumont.utils.AulaScheduler.ACTION_PARAR);
            sendBroadcast(i);
            Toast.makeText(this, "Simulado: PARAR", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnDevSimularEnvio).setOnClickListener(v -> {
            com.santosdumont.utils.FirebaseUploader.enviarAgora(this);
            Toast.makeText(this, "Enviando pro Firebase...", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnDevSimularBoot).setOnClickListener(v -> {
            Intent i = new Intent(this, com.santosdumont.utils.BootReceiver.class);
            i.setAction(Intent.ACTION_BOOT_COMPLETED);
            sendBroadcast(i);
            Toast.makeText(this, "Simulado: BOOT", Toast.LENGTH_SHORT).show();
        });

        // ============================================================
        // VER FLAGS
        // ============================================================
        findViewById(R.id.btnDevVerFlags).setOnClickListener(v -> {
            String hoje = com.santosdumont.utils.DataCollector.dataHoje();
            boolean usou = prefs.getBoolean("usou_hoje_" + hoje, false);
            boolean notifPedida = prefs.getBoolean("notif_permission_asked", false);
            boolean boasVindas = prefs.getBoolean("boas_vindas_mostrada", false);
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Flags do app")
                    .setMessage(
                        "Data hoje: " + hoje + "\n" +
                        "usou_hoje: " + usou + "\n" +
                        "notif_permission_asked: " + notifPedida + "\n" +
                        "boas_vindas_mostrada: " + boasVindas + "\n" +
                        "Modo teste: " + PresencaHelper.MODO_TESTE)
                    .setPositiveButton("OK", null)
                    .show();
        });

        findViewById(R.id.btnDevResetNotif).setOnClickListener(v -> {
            prefs.edit()
                    .remove("notif_permission_asked")
                    .remove("boas_vindas_mostrada")
                    .apply();
            Toast.makeText(this, "Flags de notificacao resetadas", Toast.LENGTH_SHORT).show();
        });


        // ============================================================
        // TESTES DE NOTIFICACAO
        // ============================================================
        findViewById(R.id.btnDevTestarTodas).setOnClickListener(v -> testarTodasNotificacoes());

        findViewById(R.id.btnDevTestarBoasVindas).setOnClickListener(v -> {
            com.santosdumont.utils.NotificationScheduler.enviarBoasVindas(this);
            Toast.makeText(this, "Boas-vindas enviada!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnDevTestarAulaVaga).setOnClickListener(v -> {
            Intent i = new Intent(this, com.santosdumont.utils.AulaVagaReceiver.class);
            sendBroadcast(i);
            Toast.makeText(this, "Aula vaga enviada!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnDevTestarLembrete).setOnClickListener(v -> {
            Intent i = new Intent(this, com.santosdumont.utils.NotificationReceiver.class);
            i.putExtra("titulo", "Teste de lembrete");
            i.putExtra("mensagem", "Notificacao persistente de teste");
            i.putExtra("persistente", true);
            sendBroadcast(i);
            Toast.makeText(this, "Lembrete enviado!", Toast.LENGTH_SHORT).show();
        });
        // ============================================================
        // SAIR DO MODO DEV
        // ============================================================
        findViewById(R.id.btnDevSair).setOnClickListener(v -> {
            prefs.edit().putBoolean("modo_dev", false).apply();
            startActivity(new Intent(this, ShellActivity.class));
            finish();
        });

        // ============================================================
        // INFORMACOES
        // ============================================================
        TextView txtInfo = findViewById(R.id.txtDevInfo);
        if (txtInfo != null) {
            String versao = getString(R.string.app_version);
            String nome = prefs.getString("nome_usuario", "-");
            String turma = prefs.getString("turma_usuario", "-");
            int saidas = prefs.getInt("saidas_consecutivas", 0);
            long aulaVaga = prefs.getLong("aula_vaga_fim", 0);
            boolean aulaVagaAtiva = System.currentTimeMillis() < aulaVaga;

            txtInfo.setText(
                    "Versao: " + versao + "\n" +
                    "Nome: " + nome + "\n" +
                    "Turma: " + turma + "\n" +
                    "Saidas: " + saidas + "\n" +
                    "Aula Vaga: " + (aulaVagaAtiva ? "ATIVA" : "inativa") + "\n" +
                    "Modo Teste: " + PresencaHelper.MODO_TESTE
            );
        }
    }

    private void atualizarTextoToggle(TextView txt) {
        if (txt != null) {
            txt.setText("Modo Teste: " + (PresencaHelper.MODO_TESTE ? "LIGADO" : "DESLIGADO"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Atualiza info ao voltar
        TextView txtInfo = findViewById(R.id.txtDevInfo);
        if (txtInfo != null && prefs != null) {
            int saidas = prefs.getInt("saidas_consecutivas", 0);
            long aulaVaga = prefs.getLong("aula_vaga_fim", 0);
            boolean aulaVagaAtiva = System.currentTimeMillis() < aulaVaga;
            txtInfo.setText(
                    "Versao: " + getString(R.string.app_version) + "\n" +
                    "Saidas: " + saidas + "\n" +
                    "Aula Vaga: " + (aulaVagaAtiva ? "ATIVA" : "inativa")
            );
        }
    }

    // ============================================================
    // TESTAR TODAS AS NOTIFICACOES (com delay progressivo)
    // ============================================================
    private void testarTodasNotificacoes() {
        Toast.makeText(this, "Testando todas... veja as notificacoes!", Toast.LENGTH_LONG).show();

        // 1. Boas-vindas (imediata)
        com.santosdumont.utils.NotificationScheduler.enviarBoasVindas(this);

        // 2. Aula vaga (delay 3s)
        new android.os.Handler().postDelayed(() -> {
            Intent i = new Intent(this, com.santosdumont.utils.AulaVagaReceiver.class);
            sendBroadcast(i);
        }, 3000);

        // 3. Lembrete persistente (delay 6s)
        new android.os.Handler().postDelayed(() -> {
            Intent i = new Intent(this, com.santosdumont.utils.NotificationReceiver.class);
            i.putExtra("titulo", "Teste de lembrete");
            i.putExtra("mensagem", "Notificacao persistente de teste");
            i.putExtra("persistente", true);
            sendBroadcast(i);
        }, 6000);

        // 4. Aula agendada (delay 9s)
        new android.os.Handler().postDelayed(() -> {
            Intent i = new Intent(this, com.santosdumont.utils.NotificationReceiver.class);
            i.putExtra("titulo", "Aula agendada");
            i.putExtra("mensagem", "Teste de notificacao agendada");
            i.putExtra("persistente", false);
            sendBroadcast(i);
        }, 9000);

        // ============================================================
        // F8: AVISOS 1a ABERTURA

        // ============================================================
        // F8: AVISOS 1a ABERTURA

        // ============================================================
        // F8: AVISOS 1a ABERTURA
        // ============================================================
        findViewById(R.id.btnDevForcarAvisoCardapio).setOnClickListener(v -> {
            com.santosdumont.utils.ThemePrefs.setCardapioAvisoVisto(this, false);
            Toast.makeText(this, "Flag do aviso do Merenda limpa. Voltando ao app...", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, ShellActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        });

        findViewById(R.id.btnDevForcarAvisoFrequencia).setOnClickListener(v -> {
            com.santosdumont.utils.ThemePrefs.setFrequenciaAvisoVisto(this, false);
            Toast.makeText(this, "Flag do aviso da Frequencia limpa. Voltando ao app...", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, ShellActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        });

        // ============================================================
        // F8: VERSAO
        // ============================================================
        findViewById(R.id.btnDevMostrarVersao).setOnClickListener(v -> {
            String versao = getString(R.string.app_version);
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Versao do app")
                    .setMessage("Versao: " + versao + "\n" +
                            "versionCode: 201000\n" +
                            "Build: release")
                    .setPositiveButton("OK", null)
                    .show();
        });

    }

    // ============================================================
    // SETUP DE PASTA EXPANSIVEL (header + container + setinha)
    // ============================================================
    private void setupFolder(int headerId, int containerId, int arrowId) {
        View header = findViewById(headerId);
        View container = findViewById(containerId);
        TextView arrow = findViewById(arrowId);

        if (header == null || container == null || arrow == null) return;

        // Estado inicial da seta conforme visibilidade do container
        arrow.setText(container.getVisibility() == View.VISIBLE ? "\u25BC" : "\u25B6");

        header.setOnClickListener(v -> {
            boolean aberto = container.getVisibility() == View.VISIBLE;
            container.setVisibility(aberto ? View.GONE : View.VISIBLE);
            arrow.setText(aberto ? "\u25B6" : "\u25BC");
        });
    }
}