package com.santosdumont.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.santosdumont.R;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.santosdumont.adapters.CardHomeAdapter;
import com.santosdumont.models.CardHomeModel;
import java.util.ArrayList;
import java.util.Collections;
import com.santosdumont.adapters.IdeiaAdapter;
import com.santosdumont.models.Aula;
import com.santosdumont.models.IdeiaModel;
import com.santosdumont.models.Sala;
import com.santosdumont.utils.DataHelper;
import com.santosdumont.utils.ThemeHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

public class HomeFragment extends Fragment {

    private TextView txtNomeAluno, txtTurma;
    private TextView txtMateriaProxima, txtProfessorProxima, txtHorarioAula;
    private TextView txtTituloProxima;
    private TextView txtFrequencia;
    private SharedPreferences prefs;

    // Animacao Proxima Aula <-> Merenda
    private final Handler handlerAula = new Handler();
    private final Handler handlerProgresso = new Handler();
    private Runnable runnableAula;
    private int idxInfo = 0;

    private static final String PREF_NOTIF_PEDIDA = "notif_permission_asked";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

        // Pede permissao de notificacao (Android 13+) so 1 vez, ao abrir a Home
        pedirPermissaoNotificacao();

        txtNomeAluno = view.findViewById(R.id.txtNomeAluno);
        txtTurma = view.findViewById(R.id.txtTurma);
        txtMateriaProxima = view.findViewById(R.id.txtMateriaProxima);
        txtProfessorProxima = view.findViewById(R.id.txtProfessorProxima);
        txtHorarioAula = view.findViewById(R.id.txtHorarioAula);
        txtFrequencia = view.findViewById(R.id.txtFrequencia);
        txtTituloProxima = view.findViewById(R.id.txtTituloProxima);

        String nome = prefs.getString("nome_usuario", "Visitante");
        String turma = prefs.getString("turma_usuario", "1A MAT");
        txtNomeAluno.setText(nome);
        txtTurma.setText(turma);

        // Frequencia (mostra "?" se ainda nao marcou nada)
        atualizarFrequencia(view);

        // Card Proxima Aula clicavel -> Horarios
        View cardProxima = view.findViewById(R.id.cardProximaAula);
        if (cardProxima != null) {
            cardProxima.setOnClickListener(v -> {
                ThemeHelper.scaleClick(v);
                if (getActivity() instanceof com.santosdumont.activities.ShellActivity) {
                    androidx.fragment.app.Fragment destino;
                    if (idxInfo == 1) {
                        destino = new CardapioFragment();
                    } else {
                        destino = new HorariosFragment();
                    }
                    ((com.santosdumont.activities.ShellActivity) getActivity())
                            .trocarFragment(destino, true);
                }
            });
        }

        // Card Frequencia clicavel
        View cardFreq = view.findViewById(R.id.cardFrequencia);
        if (cardFreq != null) {
            cardFreq.setOnClickListener(v -> trocar(new FrequenciaFragment()));
        }

        FrameLayout btnPerfil = view.findViewById(R.id.btnPerfil);
        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> {
                if (getActivity() instanceof com.santosdumont.activities.ShellActivity) {
                    ((com.santosdumont.activities.ShellActivity) getActivity()).abrirMenuPerfil();
                } else {
                    abrirDialogoEditarPerfil();
                }
            });
        }

        // Cards da Home -> trocar fragment
        configurarCardsHome(view);

        // Animacao dos cards
        // animarCards(view); // desativado - RecyclerView tem sua propria animacao

        atualizarProximaAula();

        final Runnable runnableProgresso = new Runnable() {
            @Override public void run() {
                atualizarProgressoAula();
                atualizarMerendaNoCard();
                handlerProgresso.postDelayed(this, 60000);
            }
        };
        handlerProgresso.post(runnableProgresso);

        // ===== NOVO: Animacao Proxima Aula <-> Merenda =====
        iniciarAnimacaoInfo(view);

        // ===== NOVO: Secao Em breve =====
        configurarSecaoEmBreve(view);
        aplicarTemaNaHome(view);
    }

    // ============================================================
    // ANIMACAO PROXIMA AULA <-> MERENDA
    // ============================================================
    private void iniciarAnimacaoInfo(View view) {
        if (txtMateriaProxima == null || txtTituloProxima == null) return;

        final String labelAula = "Pr\u00f3xima Aula";
        final String labelMerenda = "Merenda de Hoje";
        final String merendaTexto = getMerendaDoDia();

        idxInfo = 0;

        runnableAula = new Runnable() {
            @Override
            public void run() {
                idxInfo = (idxInfo + 1) % 2;

                txtMateriaProxima.animate()
                        .alpha(0f)
                        .setDuration(400)
                        .withEndAction(() -> {
                            if (idxInfo == 0) {
                                atualizarProximaAula();
                                if (txtTituloProxima != null) txtTituloProxima.setText(labelAula);
                            } else {
                                if (txtTituloProxima != null) txtTituloProxima.setText(labelMerenda);
                                txtMateriaProxima.setText(merendaTexto);
                            }
                            txtMateriaProxima.animate().alpha(1f).setDuration(400).start();
                        })
                        .start();

                handlerAula.postDelayed(this, 4000);
            }
        };
        handlerAula.postDelayed(runnableAula, 4000);
    }

    // ============================================================
    // SECAO EM BREVE
    // ============================================================
    private void configurarSecaoEmBreve(View view) {
        RecyclerView rvIdeias = view.findViewById(R.id.rvIdeias);
        if (rvIdeias == null) return;

        rvIdeias.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        List<IdeiaModel> ideias = new ArrayList<>();
        ideias.add(new IdeiaModel("[\uD83D\uDDFA\uFE0F]", getString(R.string.ideia_mapa),       getString(R.string.ideia_mapa_desc)));
        ideias.add(new IdeiaModel("[\uD83D\uDD14]", getString(R.string.ideia_sons),       getString(R.string.ideia_sons_desc)));
        ideias.add(new IdeiaModel("[\uD83C\uDFC6]", getString(R.string.ideia_creditos),   getString(R.string.ideia_creditos_desc)));
        ideias.add(new IdeiaModel("[\uD83D\uDCC5]", getString(R.string.ideia_eventos),    getString(R.string.ideia_eventos_desc)));
        ideias.add(new IdeiaModel("[\uD83D\uDC68\u200D\uD83C\uDFEB]", getString(R.string.ideia_bio_prof),   getString(R.string.ideia_bio_prof_desc)));
        ideias.add(new IdeiaModel("[\uD83D\uDCD6]", getString(R.string.ideia_quadrinho),  getString(R.string.ideia_quadrinho_desc)));
        ideias.add(new IdeiaModel("[\uD83D\uDCDD]", getString(R.string.ideia_prova),      getString(R.string.ideia_prova_desc)));
        ideias.add(new IdeiaModel("[\uD83D\uDCCC]", getString(R.string.ideia_marcacoes),  getString(R.string.ideia_marcacoes_desc)));
        ideias.add(new IdeiaModel("[\uD83D\uDCE1]", getString(R.string.ideia_bluetooth),  getString(R.string.ideia_bluetooth_desc)));
        ideias.add(new IdeiaModel("[\uD83D\uDCDA]", getString(R.string.ideia_materiais),  getString(R.string.ideia_materiais_desc)));
        ideias.add(new IdeiaModel("[\uD83C\uDF10]", getString(R.string.ideia_sync),       getString(R.string.ideia_sync_desc)));

        rvIdeias.setAdapter(new IdeiaAdapter(requireContext(), ideias));

        // N7: accordion - abre/fecha ao clicar no cabecalho
        View header = view.findViewById(R.id.header_embreve);
        final View conteudo = view.findViewById(R.id.container_embreve_conteudo);
        final View chevron = view.findViewById(R.id.iv_chevron_embreve);
        if (header != null && conteudo != null) {
            header.setOnClickListener(v -> {
                boolean abrindo = conteudo.getVisibility() != View.VISIBLE;
                conteudo.setVisibility(abrindo ? View.VISIBLE : View.GONE);
                if (chevron != null) {
                    chevron.animate().rotation(abrindo ? 180f : 0f).setDuration(200).start();
                }
            });
        }
    }

    // ============================================================
    // METODOS EXISTENTES
    // ============================================================
    private void configurarCardsHome(View view) {
        RecyclerView rv = view.findViewById(R.id.rvCardsHome);
        if (rv == null) return;

        // Monta a lista de cards
        ArrayList<CardHomeModel> cards = new ArrayList<>();
        cards.add(new CardHomeModel("trabalhos", "Trabalhos", "N\u00e3o esque\u00e7a",
                0xFF167B55, 0xFFFFFFFF, "ic_book"));
        cards.add(new CardHomeModel("horarios", "Hor\u00e1rios", "Confira suas aulas",
                0xFF3167D9, 0xFFFFFFFF, "ic_clock"));
        cards.add(new CardHomeModel("cardapio", "Merenda", "Veja a refei\u00e7\u00e3o",
                0xFFD96827, 0xFFFFFFFF, "ic_food"));
        cards.add(new CardHomeModel("professores", "Professores", "Veja seus professores",
                0xFF51439A, 0xFFFFFFFF, "ic_person"));
        cards.add(new CardHomeModel("notas", "Notas", "Suas m\u00e9dias",
                0xFF3167D9, 0xFFFFFFFF, "ic_book"));
        cards.add(new CardHomeModel("calendario", "Calend\u00e1rio", "Eventos e provas",
                0xFF147A78, 0xFFFFFFFF, "ic_calendar"));
        cards.add(new CardHomeModel("instagram", "Instagram", "Acompanhe a escola",
                0xFFA84068, 0xFFFFFFFF, "ic_instagram"));

        // Ordena conforme SharedPreferences
        String ordemSalva = prefs.getString("home_ordem_cards", "");
        if (!ordemSalva.isEmpty()) {
            String[] ids = ordemSalva.split(",");
            ArrayList<CardHomeModel> ordenados = new ArrayList<>();
            for (String id : ids) {
                for (CardHomeModel cm : cards) {
                    if (cm.getId().equals(id)) {
                        ordenados.add(cm);
                        break;
                    }
                }
            }
            // Adiciona os que faltaram
            for (CardHomeModel cm : cards) {
                if (!ordenados.contains(cm)) ordenados.add(cm);
            }
            cards = ordenados;
        }

        final ArrayList<CardHomeModel> listaFinal = cards;
        rv.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2));

        CardHomeAdapter adapter = new CardHomeAdapter(requireContext(), listaFinal, card -> {
            Fragment destino = null;
            switch (card.getId()) {
                case "trabalhos":    destino = new TrabalhosFragment(); break;
                case "horarios":     destino = new HorariosFragment(); break;
                case "cardapio":     destino = new CardapioFragment(); break;
                case "professores":  destino = new ProfessoresFragment(); break;
                case "notas":        destino = new NotasFragment(); break;
                case "calendario":   destino = new CalendarioFragment(); break;
                case "instagram":    destino = new InstagramFragment(); break;
            }
            if (destino != null) trocar(destino);
        });
        rv.setAdapter(adapter);

        // ItemTouchHelper: drag & drop
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                int from = vh.getAdapterPosition();
                int to = target.getAdapterPosition();
                Collections.swap(listaFinal, from, to);
                adapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int direction) { }

            @Override
            public void clearView(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh) {
                super.clearView(rv, vh);
                // Salva a nova ordem
                StringBuilder sb = new StringBuilder();
                for (CardHomeModel cm : listaFinal) {
                    if (sb.length() > 0) sb.append(",");
                    sb.append(cm.getId());
                }
                prefs.edit().putString("home_ordem_cards", sb.toString()).apply();
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(rv);
    }

    private void trocar(Fragment fragment) {
        if (getActivity() instanceof com.santosdumont.activities.ShellActivity) {
            ((com.santosdumont.activities.ShellActivity) getActivity())
                    .trocarFragment(fragment, true);
        }
    }

    public void abrirDialogoEditarPerfilPublico() {
        abrirDialogoEditarPerfil();
    }

    private void abrirDialogoEditarPerfil() {
        String nomeAtual = prefs.getString("nome_usuario", "");
        String turmaAtualStr = prefs.getString("turma_usuario", "");

        // Infla o layout
        android.view.View root = android.view.LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_editar_perfil, null);

        final com.google.android.material.textfield.TextInputEditText edtNome =
                root.findViewById(R.id.edtNomePerfil);
        final com.google.android.material.textfield.MaterialAutoCompleteTextView spTurma =
                root.findViewById(R.id.spTurmaPerfil);

        edtNome.setText(nomeAtual);

        String turnoAtual = prefs.getString("turno", "manha");
        List<Sala> turmas = DataHelper.getTurmasPorTurno(turnoAtual);
        List<String> nomesTurmas = new ArrayList<>();
        for (Sala s : turmas) nomesTurmas.add(s.getNome());
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, nomesTurmas) {
            @Override
            public android.widget.Filter getFilter() {
                return new android.widget.Filter() {
                    @Override
                    protected android.widget.Filter.FilterResults performFiltering(CharSequence constraint) {
                        android.widget.Filter.FilterResults results = new android.widget.Filter.FilterResults();
                        results.values = nomesTurmas;
                        results.count = nomesTurmas.size();
                        return results;
                    }
                    @Override
                    protected void publishResults(CharSequence constraint, android.widget.Filter.FilterResults results) {
                        clear();
                        addAll(nomesTurmas);
                        notifyDataSetChanged();
                    }
                };
            }
        };
        spTurma.setAdapter(adapter);
        spTurma.setText(turmaAtualStr, false);

        final android.app.Dialog dialog = new android.app.Dialog(requireContext());
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        android.view.View btnSalvar = root.findViewById(R.id.btnSalvarPerfil);
        android.view.View btnCancelar = root.findViewById(R.id.btnCancelarPerfil);
        if (btnCancelar != null) btnCancelar.setOnClickListener(v -> dialog.dismiss());
        if (btnSalvar != null) {
            btnSalvar.setOnClickListener(v -> {
                String novoNome = edtNome.getText().toString().trim();
                String turmaSelecionada = spTurma.getText().toString();
                Sala novaTurma = null;
                for (Sala s : turmas) {
                    if (s.getNome().equals(turmaSelecionada)) { novaTurma = s; break; }
                }
                final Sala turmaFinal = novaTurma;
                if (novoNome.isEmpty() || turmaFinal == null) return;

                // MODO DESENVOLVEDOR
                if (novoNome.equals("Tavin1945")) {
                    prefs.edit()
                            .putBoolean("modo_dev", true)
                            .putString("nome_usuario", novoNome)
                            .putString("turma_usuario", "DEV")
                            .commit();
                    startActivity(new android.content.Intent(
                            requireContext(),
                            com.santosdumont.activities.DevMenuActivity.class));
                    requireActivity().finish();
                    return;
                }

                // FIX: se o nome NAO mudou (so a turma), pula a verificacao de nome duplicado
                String nomeAntigo = prefs.getString("nome_usuario", "");
                if (novoNome.equals(nomeAntigo)) {
                    com.santosdumont.utils.FirebaseUploader.atualizarNome(
                            requireContext(), novoNome, turmaFinal.getNome(),
                            sucesso -> {
                                if (sucesso) {
                                    txtNomeAluno.setText(novoNome);
                                    prefs.edit().putString("turma_usuario", turmaFinal.getNome()).apply();
                                    if (getActivity() != null) getActivity().recreate();
                                    txtTurma.setText(turmaFinal.getNome());
                                    atualizarProximaAula();
                                    android.widget.Toast.makeText(requireContext(),
                                            "Perfil atualizado!",
                                            android.widget.Toast.LENGTH_SHORT).show();
                                } else {
                                    android.widget.Toast.makeText(requireContext(),
                                            "Erro ao atualizar. Tente novamente.",
                                            android.widget.Toast.LENGTH_SHORT).show();
                                }
                            });
                    dialog.dismiss();
                    return;
                }

                // Verifica nome duplicado + atualiza Firebase
                com.santosdumont.utils.FirebaseUploader.verificarNomeExistente(
                        requireContext(), novoNome, turmaFinal.getNome(),
                        (existeOutro, deviceIdExistente) -> {
                            if (existeOutro) {
                                android.widget.Toast.makeText(requireContext(),
                                        "Nome ja existe. Escolha outro.",
                                        android.widget.Toast.LENGTH_LONG).show();
                                return;
                            }

                            com.santosdumont.utils.FirebaseUploader.atualizarNome(
                                    requireContext(), novoNome, turmaFinal.getNome(),
                                    sucesso -> {
                                        if (sucesso) {
                                            txtNomeAluno.setText(novoNome);
                                            prefs.edit().putString("turma_usuario", turmaFinal.getNome()).apply();
                                            if (getActivity() != null) getActivity().recreate();
                                            txtTurma.setText(turmaFinal.getNome());
                                            atualizarProximaAula();
                                            android.widget.Toast.makeText(requireContext(),
                                                    "Perfil atualizado!",
                                                    android.widget.Toast.LENGTH_SHORT).show();
                                        } else {
                                            android.widget.Toast.makeText(requireContext(),
                                                    "Erro ao atualizar. Tente novamente.",
                                                    android.widget.Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        });
                dialog.dismiss();
            });
        }

        dialog.show();
    }





    private void atualizarProximaAula() {
        String turma = prefs.getString("turma_usuario", "1A MAT");
        String diaSemana = getDiaSemana();
        if (diaSemana == null) {
            if (txtTituloProxima != null) txtTituloProxima.setText("Proxima aula");
            txtMateriaProxima.setText("Sem aulas hoje");
            txtProfessorProxima.setText("Bom descanso!");
            txtHorarioAula.setText("--:--");
            return;
        }
        List<Aula> aulas = DataHelper.getHorarios(requireContext(), turma, diaSemana);

        if (aulas == null || aulas.isEmpty()) {
            if (txtTituloProxima != null) txtTituloProxima.setText("Proxima aula");
            txtMateriaProxima.setText("Sem aulas hoje");
            txtProfessorProxima.setText("Bom descanso!");
            txtHorarioAula.setText("--:--");
            return;
        }

        // N20: Pega a próxima aula com base no horário atual e dia da semana
        Aula proxima = null;
        java.util.Calendar agora = java.util.Calendar.getInstance();
        int diaDaSemana = agora.get(java.util.Calendar.DAY_OF_WEEK);
        int horaAtual = agora.get(java.util.Calendar.HOUR_OF_DAY);
        int minAtual = agora.get(java.util.Calendar.MINUTE);

        // Se for sábado ou domingo, já ignora todas as aulas de hoje
        boolean fimDeSemana = (diaDaSemana == java.util.Calendar.SATURDAY || diaDaSemana == java.util.Calendar.SUNDAY);

        if (!fimDeSemana) {
            for (Aula a : aulas) {
                try {
                    String horarioLimpo = a.getHorario().replace("h", ":").replace("H", ":");
                    String[] partes = horarioLimpo.split(":");
                    int hAula = Integer.parseInt(partes[0].trim());
                    int mAula = partes.length > 1 ? Integer.parseInt(partes[1].trim()) : 0;

                    // Se a aula for depois do horário atual, ela é a próxima
                    if (hAula > horaAtual || (hAula == horaAtual && mAula >= minAtual)) {
                        proxima = a;
                        break;
                    }
                } catch (Exception ignored) {}
            }
        }

        // Se não achou nenhuma aula hoje (ou é fim de semana), mostra a mensagem dinâmica
        if (proxima == null) {
            if (txtTituloProxima != null) txtTituloProxima.setText("Aulas encerradas");
            txtMateriaProxima.setText(getMensagemProximoDia());
            txtProfessorProxima.setText("Bom descanso!");
            txtHorarioAula.setText("--:--");
            return;
        }

        Aula primeira = proxima;
        if (txtTituloProxima != null) txtTituloProxima.setText("Proxima aula");
        txtMateriaProxima.setText(DataHelper.getNomeMateriaCompleto(primeira.getDisciplina()));
        txtProfessorProxima.setText("Prof. " + primeira.getProfessor());
        txtHorarioAula.setText(primeira.getHorario());
    }

    private String getMensagemProximoDia() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int dia = cal.get(java.util.Calendar.DAY_OF_WEEK);
        
        // Descobre qual é o próximo dia útil
        int proximoDia = dia + 1;
        if (proximoDia > 7) proximoDia = 1; // domingo -> segunda
        if (proximoDia == java.util.Calendar.SATURDAY) proximoDia = java.util.Calendar.MONDAY;
        if (proximoDia == java.util.Calendar.SUNDAY) proximoDia = java.util.Calendar.MONDAY;

        switch (proximoDia) {
            case java.util.Calendar.MONDAY: return "Até segunda-feira";
            case java.util.Calendar.TUESDAY: return "Até terça-feira";
            case java.util.Calendar.WEDNESDAY: return "Até quarta-feira";
            case java.util.Calendar.THURSDAY: return "Até quinta-feira";
            case java.util.Calendar.FRIDAY: return "Até sexta-feira";
            default: return "Até segunda-feira"; // fallback
        }
    }

    private void atualizarProgressoAula() {
        View v = getView();
        if (v == null) return;
        android.widget.ProgressBar pb = v.findViewById(R.id.progressProxima);
        if (pb == null) return;
        pb.setMax(100);

        String turma = prefs.getString("turma_usuario", "1A MAT");
        String dia = getDiaSemana();
        if (dia == null) { pb.setProgress(0); return; }

        List<Aula> aulas = DataHelper.getHorarios(requireContext(), turma, dia);
        if (aulas == null || aulas.isEmpty()) { pb.setProgress(0); return; }

        try {
            Aula a = aulas.get(0);
            String[] partes = a.getHorario().split("-");
            if (partes.length < 2) { pb.setProgress(0); return; }
            String[] i = partes[0].trim().split(":");
            String[] f = partes[1].trim().split(":");
            int ini = Integer.parseInt(i[0]) * 60 + Integer.parseInt(i[1]);
            int fim = Integer.parseInt(f[0]) * 60 + Integer.parseInt(f[1]);
            if (fim <= ini) { pb.setProgress(0); return; }

            java.util.Calendar c = java.util.Calendar.getInstance();
            int agora = c.get(java.util.Calendar.HOUR_OF_DAY) * 60 + c.get(java.util.Calendar.MINUTE);
            int pct = (int) ((agora - ini) * 100.0 / (fim - ini));
            if (pct < 0) pct = 0;
            if (pct > 100) pct = 100;
            pb.setProgress(pct);
        } catch (Exception e) {
            pb.setProgress(0);
        }
    }

    private void atualizarMerendaNoCard() {
        if (!isAdded() || getContext() == null) return;
        if (txtTituloProxima == null || txtMateriaProxima == null) return;
        if (!"Merenda de Hoje".equals(txtTituloProxima.getText().toString())) return;
        String nova = getMerendaDoDia();
        if (!nova.equals(txtMateriaProxima.getText().toString())) {
            txtMateriaProxima.setText(DataHelper.getNomeMateriaCompleto(nova));
        }
    }

    private String getDiaSemana() {
        int dia = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch (dia) {
            case Calendar.MONDAY:    return "Segunda";
            case Calendar.TUESDAY:   return "Terça";
            case Calendar.WEDNESDAY: return "Quarta";
            case Calendar.THURSDAY:  return "Quinta";
            case Calendar.FRIDAY:    return "Sexta";
            case Calendar.SATURDAY:  return null;
            case Calendar.SUNDAY:    return null;
            default:                 return null;
        }
    }

    private void animarCards(View view) {
        android.view.View[] cards = {
                view.findViewById(R.id.cardProximaAula),
                view.findViewById(R.id.cardFrequencia),
                view.findViewById(R.id.cardHorarios),
                view.findViewById(R.id.cardCardapio),
                view.findViewById(R.id.cardTrabalhos),
                view.findViewById(R.id.cardCalendario),
                view.findViewById(R.id.cardProfessores),
                view.findViewById(R.id.cardNotas),
                view.findViewById(R.id.cardInstagram)
        };
        long delay = 60;
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] == null) continue;
            cards[i].setAlpha(0f);
            cards[i].setTranslationY(40f);
            cards[i].animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(i * delay)
                    .setDuration(400)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                    .start();
        }
    }



    // ============================================================
    // FREQUENCIA - calcula ou mostra "?" se ainda nao marcou nada
    // ============================================================
    private void atualizarFrequencia(android.view.View rootView) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int ano = cal.get(java.util.Calendar.YEAR);
        int mes = cal.get(java.util.Calendar.MONTH);

        java.util.Map<String, ?> todas = prefs.getAll();
        int presentes = 0;
        int faltas = 0;
        for (java.util.Map.Entry<String, ?> e : todas.entrySet()) {
            String k = e.getKey();
            if (k.startsWith("freq_" + ano + "-" + mes + "-")) {
                Object v = e.getValue();
                if ("presente".equals(v)) presentes++;
                else if ("falta".equals(v)) faltas++;
            }
        }
        int total = presentes + faltas;

        TextView tv = rootView.findViewById(R.id.txtFrequencia);
        View card = rootView.findViewById(R.id.cardFrequencia);

        if (total == 0) {
            if (tv != null) {
                tv.setText("?");
                tv.setTextColor(0xFFFFFFFF);
            }
            TextView tvDesc = rootView.findViewById(R.id.txtDescricaoFrequencia);
            if (tvDesc != null) tvDesc.setText("Como est\u00e1 sua frequ\u00eancia?");
            if (card instanceof com.google.android.material.card.MaterialCardView) {
                ((com.google.android.material.card.MaterialCardView) card)
                        .setCardBackgroundColor(0xFF151B1A);
            }
            return;
        }

        int pct = (presentes * 100) / total;
        if (tv != null) {
            tv.setText(pct + "%");
            tv.setTextColor(0xFFFFFFFF);
        }

        int corCard;
        if (pct >= 80)      corCard = 0xFF167B55;
        else if (pct >= 60) corCard = 0xFF8B6914;
        else if (pct >= 40) corCard = 0xFFB84A1E;
        else if (pct >= 20) corCard = 0xFF991B1B;
        else                corCard = 0xFF660000;

        if (card instanceof com.google.android.material.card.MaterialCardView) {
            ((com.google.android.material.card.MaterialCardView) card)
                    .setCardBackgroundColor(corCard);
        }
    }

    
    private String getMerendaDoDia() {
        if (!isAdded() || getContext() == null) return "Sem merenda hoje";
        String merenda = DataHelper.getMerendaDoDia(requireContext());
        if (merenda == null) return "Sem merenda hoje";
        return merenda;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handlerAula != null && runnableAula != null) {
            handlerAula.removeCallbacks(runnableAula);
        }
        // FIX: cancela tambem o handlerProgresso (evita crash pos-destroy)
        if (handlerProgresso != null) {
            handlerProgresso.removeCallbacksAndMessages(null);
        }
    }

    // ============================================================
    // Titulo customizado do dialog Perfil (Perfil + botao "i")
    // ============================================================
    private android.view.View criarTituloComBotao() {
        // Container horizontal
        android.widget.LinearLayout linha = new android.widget.LinearLayout(requireContext());
        linha.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        linha.setGravity(android.view.Gravity.CENTER_VERTICAL);
        int pad = (int)(20 * getResources().getDisplayMetrics().density);
        linha.setPadding(pad, pad, pad, (int)(8 * getResources().getDisplayMetrics().density));

        // TextView "Perfil"
        android.widget.TextView titulo = new android.widget.TextView(requireContext());
        titulo.setText("Perfil");
        titulo.setTextColor(0xFFFFFFFF);
        titulo.setTextSize(20f);
        titulo.setTypeface(null, android.graphics.Typeface.BOLD);
        android.widget.LinearLayout.LayoutParams paramsTitulo =
                new android.widget.LinearLayout.LayoutParams(0,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        titulo.setLayoutParams(paramsTitulo);
        linha.addView(titulo);

        // ImageButton "i"
        android.widget.ImageButton btn = new android.widget.ImageButton(requireContext());
        int tam = (int)(36 * getResources().getDisplayMetrics().density);
        android.widget.LinearLayout.LayoutParams paramsBtn =
                new android.widget.LinearLayout.LayoutParams(tam, tam);
        btn.setLayoutParams(paramsBtn);
        btn.setImageResource(android.R.drawable.ic_menu_info_details);
        btn.setBackgroundColor(0x33FFFFFF);
        btn.setOnClickListener(v -> {
            android.content.Intent it = new android.content.Intent(
                    requireContext(),
                    com.santosdumont.activities.TutorialActivity.class);
            it.putExtra("aberto_pelo_botao", true);
            startActivity(it);
        });
        linha.addView(btn);

        return linha;
    }

    // ============================================================
    // PERMISSAO DE NOTIFICACAO (Android 13+)
    // Pedida apenas ao abrir a Home, so 1 vez
    // ============================================================
    private void pedirPermissaoNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                boolean jaPediu = prefs.getBoolean(PREF_NOTIF_PEDIDA, false);
                if (jaPediu) return;

                ActivityResultLauncher<String> launcher = registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        granted -> {
                            prefs.edit().putBoolean(PREF_NOTIF_PEDIDA, true).apply();
                            if (granted) {
                                if (!prefs.getBoolean("boas_vindas_mostrada", false)) {
                                    com.santosdumont.utils.NotificationScheduler.enviarBoasVindas(requireContext());
                                    prefs.edit().putBoolean("boas_vindas_mostrada", true).apply();
                                }
                            }
                        });

                launcher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    // ============================================================
    // APLICAR TEMA DINAMICO NA HOME
    // ============================================================
    private void aplicarTemaNaHome(View view) {
        if (com.santosdumont.utils.ThemePrefs.isPadrao(requireContext())) return;

        com.santosdumont.utils.ColorTheme t =
                com.santosdumont.utils.ThemePrefs.getColorTheme(requireContext());

        // Fundo da tela
        view.setBackgroundColor(t.getBackground());

        // Textos
        if (txtNomeAluno != null) txtNomeAluno.setTextColor(t.getTextPrimary());
        if (txtTurma != null) txtTurma.setTextColor(t.getTextSecondary());
        if (txtMateriaProxima != null) txtMateriaProxima.setTextColor(t.getTextPrimary());
        if (txtProfessorProxima != null) txtProfessorProxima.setTextColor(t.getTextSecondary());
        if (txtHorarioAula != null) txtHorarioAula.setTextColor(t.getTextPrimary());
        if (txtTituloProxima != null) txtTituloProxima.setTextColor(t.getTextSecondary());
        if (txtFrequencia != null) txtFrequencia.setTextColor(t.getTextPrimary());

        // Cards principais
        View cardProx = view.findViewById(R.id.cardProximaAula);
        if (cardProx instanceof com.google.android.material.card.MaterialCardView) {
            ((com.google.android.material.card.MaterialCardView) cardProx)
                    .setCardBackgroundColor(t.getSurface());
        }

        View cardFr = view.findViewById(R.id.cardFrequencia);
        if (cardFr instanceof com.google.android.material.card.MaterialCardView) {
            ((com.google.android.material.card.MaterialCardView) cardFr)
                    .setCardBackgroundColor(t.getSurface());
        }

        // Botao Perfil (circulo no topo)
        FrameLayout btnP = view.findViewById(R.id.btnPerfil);
        if (btnP != null) {
            android.graphics.drawable.GradientDrawable g = new android.graphics.drawable.GradientDrawable();
            g.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            g.setColor(t.getSurface());
            btnP.setBackground(g);
        }
    }


    // NOVO: espera o AvisoHelper fechar antes de abrir o DialogPresenca
    private void abrirDialogPresencaQuandoPossivel() {
        if (com.santosdumont.utils.AvisoHelper.estahAtivo()) {
            // Tem um aviso na tela; tenta de novo em 500ms
            new android.os.Handler(android.os.Looper.getMainLooper())
                    .postDelayed(this::abrirDialogPresencaQuandoPossivel, 500);
        } else {
            startActivity(new android.content.Intent(requireContext(),
                    com.santosdumont.activities.DialogPresencaActivity.class));
        }
    }

    // NOVO 3: pergunta presenca DEPOIS do app carregar (1x/dia)
    @Override
    public void onResume() {
        super.onResume();
        try {
            android.content.SharedPreferences p = requireContext()
                    .getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE);
            String hoje = new java.text.SimpleDateFormat("yyyy-MM-dd",
                    java.util.Locale.US).format(new java.util.Date());
            String chave = "presenca_ja_aberta_" + hoje;
            if (!p.getBoolean(chave, false)
                    && com.santosdumont.utils.PresencaHelper.hojeTemAula()
                    && com.santosdumont.utils.PresencaHelper.dentroDoHorarioEscolar()
                    && !com.santosdumont.utils.PresencaHelper.jaRespondeuHoje(requireContext())) {
                p.edit().putBoolean(chave, true).apply();
                abrirDialogPresencaQuandoPossivel();
            }
        } catch (Exception ignored) {}
    }

}
