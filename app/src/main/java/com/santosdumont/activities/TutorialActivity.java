package com.santosdumont.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.santosdumont.R;

public class TutorialActivity extends AppCompatActivity {

    private ViewPager2 pager;
    private TextView txtPular, txtProximo;
    private View[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        pager = findViewById(R.id.tutorialPager);
        txtPular = findViewById(R.id.txtPularTutorial);
        txtProximo = findViewById(R.id.txtProximoTutorial);

        // 6 paginas
        TutorialPagerAdapter adapter = new TutorialPagerAdapter();
        pager.setAdapter(adapter);

        // Dots (indicadores)
        android.widget.LinearLayout llDots = findViewById(R.id.llDots);
        llDots.removeAllViews();
        int total = adapter.getItemCount();
        dots = new View[total];
        int tam = (int) (8 * getResources().getDisplayMetrics().density);
        int margem = (int) (6 * getResources().getDisplayMetrics().density);
        for (int i = 0; i < total; i++) {
            View d = new View(this);
            android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(tam, tam);
            if (i < total - 1) lp.setMarginEnd(margem);
            d.setLayoutParams(lp);
            d.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.text_primary));
            llDots.addView(d);
            dots[i] = d;
        }
        atualizarDots(0);

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                atualizarDots(position);
                if (position == dots.length - 1) {
                    txtProximo.setText("Come\u00e7ar");
                } else {
                    txtProximo.setText("Pr\u00f3ximo");
                }
            }
        });

        txtPular.setOnClickListener(v -> finalizarTutorial());
        txtProximo.setOnClickListener(v -> {
            int atual = pager.getCurrentItem();
            if (atual < dots.length - 1) {
                pager.setCurrentItem(atual + 1);
            } else {
                finalizarTutorial();
            }
        });
    }

    private void atualizarDots(int pos) {
        for (int i = 0; i < dots.length; i++) {
            if (dots[i] != null) {
                dots[i].setAlpha(i == pos ? 1f : 0.3f);
            }
        }
    }

    private void finalizarTutorial() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        prefs.edit().putBoolean("tutorial_visto", true).apply();

        boolean abertoPeloBotao = getIntent().getBooleanExtra("aberto_pelo_botao", false);
        boolean abertoAutomatico = getIntent().getBooleanExtra("aberto_automatico", false);
        if (abertoPeloBotao) {
            // Abriu pelo botao "i" -> so fecha, volta pra tela anterior
            finish();
        } else if (abertoAutomatico) {
            // Aberto apos o login -> vai pra Shell
            startActivity(new Intent(this, ShellActivity.class));
            finish();
        } else {
            // Fluxo normal -> vai pra Shell
            Intent i = new Intent(this, ShellActivity.class);
            startActivity(i);
            finish();
        }
    }

    // ============================================================
    // ADAPTER
    // ============================================================
    static class TutorialPagerAdapter extends RecyclerView.Adapter<TutorialPagerAdapter.VH> {

        final String[][] DADOS = {
                {"Bem-vindo!", "Veja como usar cada parte do app da Escola Santos Dumont", "ic_plane"},
                {"In\u00edcio", "Veja sua pr\u00f3xima aula, a merenda do dia e sua frequ\u00eancia", "ic_home"},
                {"Acesso r\u00e1pido", "Toque nos cards da Home ou use a barra de baixo para ir direto a cada \u00e1rea", "ic_book"},
                {"Hor\u00e1rios", "Confira suas aulas e escolha o dia que quiser ver", "ic_clock"},
                {"Trabalhos", "Cadastre trabalhos com nome, data, hora, descri\u00e7\u00e3o e foto para n\u00e3o esquecer", "ic_book"},
                {"Merenda", "Veja a refei\u00e7\u00e3o do dia", "ic_food"},
                {"Professores", "Veja seus professores, as disciplinas e os hor\u00e1rios de cada um", "ic_person"},
                {"Notas", "Veja suas notas, a maior, a pior e a m\u00e9dia por mat\u00e9ria", "ic_book"},
                {"Calend\u00e1rio", "Veja eventos e provas e toque num dia para ver os detalhes", "ic_calendar"},
                {"Frequ\u00eancia", "Acompanhe sua presen\u00e7a no calend\u00e1rio. O app pergunta se voc\u00ea foi \u00e0 aula", "ic_event"},
                {"Alarmes", "Crie alarmes e lembretes para estudar e entregar trabalhos", "ic_clock"},
                {"Instagram", "Acompanhe as novidades da escola", "ic_instagram"},
                {"Seu perfil", "No menu do perfil voc\u00ea edita nome e turma e muda o tema e a fonte", "ic_person"},
                {"Tudo pronto!", "Bons estudos!", "ic_plane"}
        };

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_tutorial_passo, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            String[] d = DADOS[position];
            h.txtTitulo.setText(d[0]);
            h.txtDesc.setText(d[1]);
            int resId = h.itemView.getResources().getIdentifier(
                    d[2], "drawable", h.itemView.getContext().getPackageName());
            if (resId != 0) h.img.setImageResource(resId);

            // Animacao de entrada
            h.itemView.setAlpha(0f);
            h.itemView.setTranslationY(40f);
            h.itemView.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(500)
                    .start();
        }

        @Override
        public int getItemCount() {
            return DADOS.length;
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView txtTitulo, txtDesc;
            ImageView img;
            VH(@NonNull View v) {
                super(v);
                txtTitulo = v.findViewById(R.id.txtTituloTutorial);
                txtDesc = v.findViewById(R.id.txtDescTutorial);
                img = v.findViewById(R.id.imgIconeTutorial);
            }
        }
    }
}