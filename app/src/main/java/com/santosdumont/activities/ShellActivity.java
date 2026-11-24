package com.santosdumont.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.santosdumont.R;
import com.santosdumont.fragments.CardapioFragment;
import com.santosdumont.fragments.AlarmesFragment;
import com.santosdumont.fragments.CalendarioFragment;
import com.santosdumont.fragments.InstagramFragment;
import com.santosdumont.fragments.MapaFragment;
import com.santosdumont.fragments.HomeFragment;
import com.santosdumont.fragments.FrequenciaFragment;
import com.santosdumont.fragments.HorariosFragment;
import com.santosdumont.fragments.ProfessoresFragment;
import com.santosdumont.fragments.NotasFragment;
import com.santosdumont.fragments.TrabalhosFragment;

/**
 * Activity principal que hospeda a barra inferior fixa
 * e troca os fragments no container central.
 */
public class ShellActivity extends AppCompatActivity {

    private Fragment fragmentAtual; // M16: referencia ao fragment atual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // M12: aplica tema de fonte ANTES do super
        String fonte = com.santosdumont.utils.ThemePrefs.getFont(this);
        if ("poppins".equals(fonte)) {
            setTheme(R.style.Theme_SantosDumont_Poppins);
        } else if ("inter".equals(fonte)) {
            setTheme(R.style.Theme_SantosDumont_Inter);
        } else {
            setTheme(R.style.Theme_SantosDumont);
        }

        super.onCreate(savedInstanceState);

        // ===== STATUS_COR: statusBar na cor do app, navBar escondida =====
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        androidx.core.view.WindowInsetsControllerCompat insetsCtrl =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        // Mostra statusBar
        insetsCtrl.show(androidx.core.view.WindowInsetsCompat.Type.statusBars());
        // N3: navBar SEMPRE visivel (bottomNav sobe/desce junto com ela)
        aplicarCorStatusBar(insetsCtrl);

        setContentView(R.layout.activity_shell);

        // EDGE TO EDGE: padding no topo do fragment_container (statusBar)
        View root = findViewById(R.id.fragment_container);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            androidx.core.graphics.Insets bars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, bars.top, 0, 0);
            return insets;
        });

        // BOTTOM NAV: sobe quando navBar do Android aparece (listener direto no bottomNav)
        final View bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(bottomNav, (v, insets) -> {
                androidx.core.graphics.Insets bars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
                float density = getResources().getDisplayMetrics().density;
                int margemOriginalPx = (int) (18 * density);
                android.view.ViewGroup.MarginLayoutParams params =
                        (android.view.ViewGroup.MarginLayoutParams) v.getLayoutParams();
                params.bottomMargin = margemOriginalPx + bars.bottom;
                v.setLayoutParams(params);
                return insets;
            });
            // Forca o primeiro dispatch
            androidx.core.view.ViewCompat.requestApplyInsets(bottomNav);
        }

        // Agenda alarmes de aula (AulaScheduler)
        try {
            com.santosdumont.utils.AulaScheduler.agendarTudo(this);
        } catch (Exception ignored) {}

        configurarBarra();
        aplicarTemaNaBarra();

        // TOPBAR: desativada (REVERTER_TOPBAR) - usando statusBar nativa
        // iniciarTopBar();

        // Abre a Home por padrao
        if (savedInstanceState == null) {
            trocarFragment(new HomeFragment(), false);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            androidx.core.view.WindowInsetsControllerCompat c =
                    androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
            c.show(androidx.core.view.WindowInsetsCompat.Type.statusBars());
            c.hide(androidx.core.view.WindowInsetsCompat.Type.navigationBars());
            c.setSystemBarsBehavior(
                    androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            aplicarCorStatusBar(c);
        }
    }

    // ============================================================
    // TOPBAR: hora + dia + bateria + rede
    // ============================================================
    private final android.os.Handler topBarHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private final Runnable topBarRunnable = new Runnable() {
        @Override public void run() {
            atualizarTopBar();
            topBarHandler.postDelayed(this, 30000); // 30s
        }
    };
    private android.content.BroadcastReceiver batteryReceiver;

    private void iniciarTopBar() {
        // Atualiza hora/dia/bateria agora
        atualizarTopBar();
        topBarHandler.postDelayed(topBarRunnable, 30000);

        // Escuta mudanca de bateria
        batteryReceiver = new android.content.BroadcastReceiver() {
            @Override public void onReceive(android.content.Context c, android.content.Intent i) {
                int nivel = i.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1);
                int escala = i.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1);
                if (nivel >= 0 && escala > 0) {
                    int pct = nivel * 100 / escala;
                    android.widget.TextView txt = findViewById(R.id.txtBateriaPct);
                    if (txt != null) txt.setText(pct + "%");
                }
            }
        };
        registerReceiver(batteryReceiver, new android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED));
    }

    private void atualizarTopBar() {
        // Hora + dia
        java.util.Calendar c = java.util.Calendar.getInstance();
        String[] dias = {"dom", "seg", "ter", "qua", "qui", "sex", "sab"};
        String[] meses = {"jan", "fev", "mar", "abr", "mai", "jun",
                          "jul", "ago", "set", "out", "nov", "dez"};

        String hora = String.format(java.util.Locale.getDefault(), "%02d:%02d",
                c.get(java.util.Calendar.HOUR_OF_DAY), c.get(java.util.Calendar.MINUTE));
        String dia = dias[c.get(java.util.Calendar.DAY_OF_WEEK) - 1]
                + ", " + c.get(java.util.Calendar.DAY_OF_MONTH)
                + " de " + meses[c.get(java.util.Calendar.MONTH)];

        android.widget.TextView txtHora = findViewById(R.id.txtHoraTopo);
        android.widget.TextView txtDia = findViewById(R.id.txtDiaTopo);
        if (txtHora != null) txtHora.setText(hora);
        if (txtDia != null) txtDia.setText(dia);

        // Wifi / Sinal
        android.net.ConnectivityManager cm =
                (android.net.ConnectivityManager) getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        android.widget.ImageView imgWifi = findViewById(R.id.imgWifi);
        android.widget.ImageView imgSinal = findViewById(R.id.imgSinal);
        if (cm != null) {
            android.net.NetworkInfo info = cm.getActiveNetworkInfo();
            boolean conectado = info != null && info.isConnected();
            boolean isWifi = conectado && info.getType() == android.net.ConnectivityManager.TYPE_WIFI;
            boolean isMobile = conectado && info.getType() == android.net.ConnectivityManager.TYPE_MOBILE;
            if (imgWifi != null) imgWifi.setAlpha(isWifi ? 1f : 0.3f);
            if (imgSinal != null) imgSinal.setAlpha(isMobile ? 1f : 0.3f);
        }

        // Cor dos icones: conforme o tema
        int corIcone = 0xFFFFFFFF;
        if (!com.santosdumont.utils.ThemePrefs.isPadrao(this)) {
            corIcone = com.santosdumont.utils.ThemePrefs.getColorTheme(this).getTextPrimary();
        }
        if (imgWifi != null) imgWifi.setColorFilter(corIcone);
        if (imgSinal != null) imgSinal.setColorFilter(corIcone);
        android.widget.ImageView imgBat = findViewById(R.id.imgBateria);
        if (imgBat != null) imgBat.setColorFilter(corIcone);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (batteryReceiver != null) {
            try { unregisterReceiver(batteryReceiver); } catch (Exception ignored) {}
        }
        topBarHandler.removeCallbacks(topBarRunnable);
    }

    private void aplicarCorStatusBar(androidx.core.view.WindowInsetsControllerCompat c) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int corFundo;
            if (com.santosdumont.utils.ThemePrefs.isPadrao(this)) {
                corFundo = 0xFF121716; // Fundo padrao do app
            } else {
                corFundo = com.santosdumont.utils.ThemePrefs.getColorTheme(this).getBackground();
            }
            getWindow().setStatusBarColor(corFundo);

            // Calcula se o fundo e' escuro para mudar a cor do texto (icones)
            int r = (corFundo >> 16) & 0xFF;
            int g = (corFundo >> 8) & 0xFF;
            int b = corFundo & 0xFF;
            double lum = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0;
            boolean escuro = lum < 0.5;
            c.setAppearanceLightStatusBars(!escuro); // true = icones escuros
        }
    }

    // ============================================================
    // DRAWER: Menu de Perfil e Aparencia
    // ============================================================
    private android.widget.PopupWindow popupPerfil;

    public void abrirMenuPerfil() {
        if (popupPerfil != null && popupPerfil.isShowing()) return;

        // Inflar o layout
        android.view.View menuView = getLayoutInflater().inflate(R.layout.drawer_menu_perfil, null);

        // Preencher dados do usuario
        android.widget.TextView txtNome = menuView.findViewById(R.id.drawerNome);
        android.widget.TextView txtTurma = menuView.findViewById(R.id.drawerTurma);
        android.content.SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        if (txtNome != null) txtNome.setText(prefs.getString("nome_usuario", "Visitante"));
        if (txtTurma != null) txtTurma.setText(prefs.getString("turma_usuario", "1A MAT"));

        // Criar PopupWindow (largura 300dp, altura wrap_content)
        int widthDp = 300;
        float density = getResources().getDisplayMetrics().density;
        int widthPx = (int) (widthDp * density);
        
        popupPerfil = new android.widget.PopupWindow(
                menuView,
                widthPx,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                true // focusable
        );
        
        // Fundo nulo: remove o fundo padrao do tema (cinza)
        popupPerfil.setBackgroundDrawable(null);
        popupPerfil.setClippingEnabled(true);
        popupPerfil.setOutsideTouchable(true);
        
        // Animacao de deslizar da direita
        popupPerfil.setAnimationStyle(R.style.PopupAnimationRight);

        // Exibir ancorado ao canto superior direito
        android.view.View rootView = findViewById(android.R.id.content);
        
        // Calcula o offset (margem de 16dp do topo e 16dp da direita)
        int marginPx = (int) (16 * density);
        int topMarginPx = (int) (80 * density); // Para ficar abaixo da statusBar e do botao de perfil
        
        popupPerfil.showAtLocation(rootView, android.view.Gravity.END | android.view.Gravity.TOP, marginPx, topMarginPx);

        // Acoes do menu
        menuView.findViewById(R.id.menuAparencia).setOnClickListener(v -> {
            popupPerfil.dismiss();
            com.santosdumont.utils.ThemeEditor.abrir(this, () -> recreate());
        });


        menuView.findViewById(R.id.menuEditarPerfil).setOnClickListener(v -> {
            popupPerfil.dismiss();
            if (fragmentAtual instanceof HomeFragment) {
                ((HomeFragment) fragmentAtual).abrirDialogoEditarPerfilPublico();
            } else {
                trocarFragment(new HomeFragment(), false);
            }
        });
    }

    private void configurarBarra() {
        View navTrabalhos = findViewById(R.id.navTrabalhos);
        View navHorarios = findViewById(R.id.navHorarios);
        View navInicio = findViewById(R.id.navInicio);
        View navCardapio = findViewById(R.id.navCardapio);
        View navProfessores = findViewById(R.id.navProfessores);

        if (navTrabalhos != null) navTrabalhos.setOnClickListener(v -> trocarFragment(new TrabalhosFragment(), true));
        if (navHorarios != null) navHorarios.setOnClickListener(v -> trocarFragment(new HorariosFragment(), true));
        if (navInicio != null) navInicio.setOnClickListener(v -> {
            // M16: se ja esta no Inicio, anima "voltando"
            if (fragmentAtual instanceof HomeFragment) {
                animarVoltarHome();
                return;
            }
            trocarFragment(new HomeFragment(), false);
        });
        if (navCardapio != null) navCardapio.setOnClickListener(v -> trocarFragment(new CardapioFragment(), true));
        if (navProfessores != null) navProfessores.setOnClickListener(v -> trocarFragment(new ProfessoresFragment(), true));
    }

    public void trocarFragment(Fragment fragment, boolean addToBackStack) {
        // N11: evita recriar o mesmo fragment ao clicar varias vezes na bottomNav
        if (fragmentAtual != null && fragmentAtual.getClass() == fragment.getClass()) {
            return;
        }


        // Coleta: registra abertura de tela
        try {
            com.santosdumont.utils.DataCollector.registrar(this,
                    "TELA_" + fragment.getClass().getSimpleName(), "");
        } catch (Exception ignored) {}
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.fade_slide_out,
                R.anim.slide_in_right,
                R.anim.fade_slide_out);     // sai pra esquerda (voltar)
        transaction.replace(R.id.fragment_container, fragment);
        fragmentAtual = fragment; // M16: guarda referencia
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    // ============================================================
    // M16: animacao "voltando" ao apertar Inicio ja estando no Inicio
    // ============================================================
    private void animarVoltarHome() {
        if (fragmentAtual == null) return;
        View v = fragmentAtual.getView();
        if (v != null) {
            v.animate()
                    .alpha(0.7f)
                    .scaleX(0.98f)
                    .scaleY(0.98f)
                    .setDuration(120)
                    .withEndAction(() -> v.animate()
                            .alpha(1f)
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(180)
                            .start())
                    .start();
        }
    }

    // ============================================================
    // APLICAR TEMA NA BARRA INFERIOR
    // ============================================================
    private void aplicarTemaNaBarra() {
        if (com.santosdumont.utils.ThemePrefs.isPadrao(this)) return;
        com.santosdumont.utils.ColorTheme t = com.santosdumont.utils.ThemePrefs.getColorTheme(this);
        View bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav instanceof com.google.android.material.card.MaterialCardView) {
            int corFundo = (t.getSurface() & 0x00FFFFFF) | 0x80000000;
            ((com.google.android.material.card.MaterialCardView) bottomNav).setCardBackgroundColor(corFundo);
        }
        int corTexto = t.getTextPrimary();
        int[] ids = { R.id.navTrabalhos, R.id.navHorarios, R.id.navInicio, R.id.navCardapio, R.id.navProfessores };
        for (int id : ids) {
            View nav = findViewById(id);
            if (nav == null) continue;
            aplicarCoresNaNav(nav, corTexto);
        }
    }

    // ============================================================
    // M17: esconder/mostrar barra inferior (teclado aberto)
    // ============================================================
    public void esconderBottomNav() {
        View bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;
        bottomNav.animate()
                .translationY(bottomNav.getHeight() + 40)
                .setDuration(250)
                .start();
    }

    public void mostrarBottomNav() {
        View bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;
        bottomNav.animate()
                .translationY(0f)
                .setDuration(250)
                .start();
    }

    private void aplicarCoresNaNav(View nav, int cor) {
        if (nav instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) nav;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View filho = vg.getChildAt(i);
                if (filho instanceof android.widget.ImageView) {
                    ((android.widget.ImageView) filho).setColorFilter(cor);
                } else if (filho instanceof android.widget.TextView) {
                    ((android.widget.TextView) filho).setTextColor(cor);
                }
            }
        }
    }
}
