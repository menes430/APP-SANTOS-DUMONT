package com.santosdumont.utils;

import com.santosdumont.models.Aula;
import com.santosdumont.models.CardapioItem;
import com.santosdumont.models.ProfessorModel;
import com.santosdumont.models.Sala;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHelper {

    // ==================== TURMAS ====================
    public static List<Sala> getTurmas() {
        List<Sala> turmas = new ArrayList<>();
        turmas.add(new Sala("1A MAT", ""));
        turmas.add(new Sala("1B MAT", ""));
        turmas.add(new Sala("2A MAT", ""));
        turmas.add(new Sala("2B MAT", ""));
        turmas.add(new Sala("2C MAT", ""));
        turmas.add(new Sala("2D MAT", ""));
        turmas.add(new Sala("3A MAT", ""));
        turmas.add(new Sala("3B MAT", ""));
        turmas.add(new Sala("3C MAT", ""));
        return turmas;
    }

    // ==================== CARDÁPIO EJA 2026 ====================
    public static List<CardapioItem> getCardapioSemana(int semana) {
        switch (semana) {
            case 1: return getSemana1();
            case 2: return getSemana2();
            case 3: return getSemana3();
            case 4: return getSemana4();
            default: return new ArrayList<>();
        }
    }

    private static List<CardapioItem> getSemana1() {
        return Arrays.asList(
            new CardapioItem("Segunda", "Cuscuz, Ovos Coloridos (Cebola, Pimentão e Tomate)", 230, 9, 5, 36, 148, 1),
            new CardapioItem("Terça", "Macarronada à Bolonhesa (Carne Moída e Soja)", 312, 13, 6, 51, 148, 1),
            new CardapioItem("Quarta", "Sanduíche Especial (Pão, Ovos, Cenoura e Alface)", 261, 10, 5, 42, 148, 1),
            new CardapioItem("Quinta", "Risoto de Frango (Arroz e Frango)", 258, 14, 5, 56, 148, 1),
            new CardapioItem("Sexta", "Mungunzá com Canela e Goiaba", 230, 9, 5, 36, 148, 1)
        );
    }

    private static List<CardapioItem> getSemana2() {
        return Arrays.asList(
            new CardapioItem("Segunda", "Vitamina de Banana, Biscoito Doce e Laranja", 312, 13, 6, 51, 148, 2),
            new CardapioItem("Terça", "Macaxeira Cozida, Ovos Mexidos", 261, 10, 5, 42, 148, 2),
            new CardapioItem("Quarta", "Cuscuz, Carne Moída e Soja", 258, 14, 5, 56, 148, 2),
            new CardapioItem("Quinta", "Sopa de Legumes com Frango (Chuchu, Batata, Cenoura, Abóbora, Repolho)", 230, 9, 5, 36, 148, 2),
            new CardapioItem("Sexta", "Sanduíche Especial, Suco de Acerola e Melancia", 312, 13, 6, 51, 148, 2)
        );
    }

    private static List<CardapioItem> getSemana3() {
        return Arrays.asList(
            new CardapioItem("Segunda", "Macarronada à Bolonhesa (Carne e Soja)", 261, 10, 5, 42, 148, 3),
            new CardapioItem("Terça", "Batata Doce, Carne Moída, Soja e Goiaba", 258, 14, 5, 56, 148, 3),
            new CardapioItem("Quarta", "Sopa de Legumes com Carne Bovina (Chuchu, Batata, Cenoura, Abóbora, Repolho)", 230, 9, 5, 36, 148, 3),
            new CardapioItem("Quinta", "Cuscuz, Ovos Coloridos (Cebola, Pimentão e Tomate)", 312, 13, 6, 51, 148, 3),
            new CardapioItem("Sexta", "Bebida Láctea, Biscoito Salgado e Banana", 261, 10, 5, 42, 148, 3)
        );
    }

    private static List<CardapioItem> getSemana4() {
        return Arrays.asList(
            new CardapioItem("Segunda", "Cuscuz e Ovos Coloridos", 258, 14, 5, 56, 148, 4),
            new CardapioItem("Terça", "Sopa de Legumes com Carne Bovina", 230, 9, 5, 36, 148, 4),
            new CardapioItem("Quarta", "Risoto de Frango (Arroz e Frango)", 312, 13, 6, 51, 148, 4),
            new CardapioItem("Quinta", "Mungunzá com Canela e Melancia", 261, 10, 5, 42, 148, 4),
            new CardapioItem("Sexta", "Arrumadinho de Frango (Feijão Fradinho, Farinha e Frango), Vinagrete", 258, 14, 5, 56, 148, 4)
        );
    }

    // ==================== HORÁRIOS ====================
    // Estrutura: 5 aulas + 1 linha de Intervalo (9h40) + 1 linha de Saída (11h40)
    public static List<Aula> getHorarios(String turma, String dia) {
        Map<String, Map<String, List<Aula>>> todosHorarios = getTodosHorarios();
        if (todosHorarios.containsKey(turma)) {
            Map<String, List<Aula>> dias = todosHorarios.get(turma);
            if (dias.containsKey(dia)) {
                return dias.get(dia);
            }
        }
        return new ArrayList<>();
    }

    private static Map<String, Map<String, List<Aula>>> getTodosHorarios() {
        Map<String, Map<String, List<Aula>>> turmas = new HashMap<>();

        // ==================== 1A MAT ====================
        Map<String, List<Aula>> t1A = new HashMap<>();
        t1A.put("Segunda", Arrays.asList(
            new Aula("7h", "BIO", "Dayse"),
            new Aula("8h", "BIO", "Dayse"),
            new Aula("9h", "MAT", "Ana Paula"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "MAT", "Ana Paula"),
            new Aula("11h", "FIS", "Herbert"),
            new Aula("11h40", "Saída", "")
        ));
        t1A.put("Terça", Arrays.asList(
            new Aula("7h", "PORT", "Rozi"),
            new Aula("8h", "PORT", "Rozi"),
            new Aula("9h", "EF", "Adriano"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PIT", "Adriano"),
            new Aula("11h", "PIT", "Adriano"),
            new Aula("11h40", "Saída", "")
        ));
        t1A.put("Quarta", Arrays.asList(
            new Aula("7h", "FIL", "Márcia"),
            new Aula("8h", "MAT", "Ana Paula"),
            new Aula("9h", "MAT", "Ana Paula"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "HIS", "Roberto"),
            new Aula("11h", "ING", "Robson"),
            new Aula("11h40", "Saída", "")
        ));
        t1A.put("Quinta", Arrays.asList(
            new Aula("7h", "PORT", "Rozi"),
            new Aula("8h", "PORT", "Rozi"),
            new Aula("9h", "ART", "Henrique"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "SOC", "Ana Luiza"),
            new Aula("11h", "QUI", "Heloyza"),
            new Aula("11h40", "Saída", "")
        ));
        t1A.put("Sexta", Arrays.asList(
            new Aula("7h", "TAC", "Dayse"),
            new Aula("8h", "TAC", "Dayse"),
            new Aula("9h", "Sem Aula", ""),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "GEO", "Moral"),
            new Aula("11h", "PDV", "Thayse"),
            new Aula("11h40", "Saída", "")
        ));
        turmas.put("1A MAT", t1A);

        // ==================== 1B MAT ====================
        Map<String, List<Aula>> t1B = new HashMap<>();
        t1B.put("Segunda", Arrays.asList(
            new Aula("7h", "MAT", "Ana Paula"),
            new Aula("8h", "MAT", "Ana Paula"),
            new Aula("9h", "Sem Aula", ""),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "FIS", "Herbert"),
            new Aula("11h", "TAC", "Dayse"),
            new Aula("11h40", "Saída", "")
        ));
        t1B.put("Terça", Arrays.asList(
            new Aula("7h", "ART", "Thayse"),
            new Aula("8h", "MAT", "Ana Paula"),
            new Aula("9h", "MAT", "Ana Paula"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PORT", "Rozi"),
            new Aula("11h", "PORT", "Rozi"),
            new Aula("11h40", "Saída", "")
        ));
        t1B.put("Quarta", Arrays.asList(
            new Aula("7h", "PDV", "Ana Paula"),
            new Aula("8h", "PIT", "Débora"),
            new Aula("9h", "PIT", "Débora"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "BIO", "Thays"),
            new Aula("11h", "BIO", "Thays"),
            new Aula("11h40", "Saída", "")
        ));
        t1B.put("Quinta", Arrays.asList(
            new Aula("7h", "QUI", "Heloiza"),
            new Aula("8h", "FIL", "Márcia"),
            new Aula("9h", "PORT", "Rozi"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PORT", "Rozi"),
            new Aula("11h", "HIS", "Roberto"),
            new Aula("11h40", "Saída", "")
        ));
        t1B.put("Sexta", Arrays.asList(
            new Aula("7h", "ING", "Robson"),
            new Aula("8h", "EF", "Douglas"),
            new Aula("9h", "SOC", "Ana Luiza"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "TAC", "Dayse"),
            new Aula("11h", "GEO", "Moral"),
            new Aula("11h40", "Saída", "")
        ));
        turmas.put("1B MAT", t1B);

        // ==================== 2A MAT ====================
        Map<String, List<Aula>> t2A = new HashMap<>();
        t2A.put("Segunda", Arrays.asList(
            new Aula("7h", "FIS", "Camila"),
            new Aula("8h", "PORT", "Luciano"),
            new Aula("9h", "PORT", "Luciano"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "BIO", "Dayse"),
            new Aula("11h", "MAT", "Ana Paula"),
            new Aula("11h40", "Saída", "")
        ));
        t2A.put("Terça", Arrays.asList(
            new Aula("7h", "MAT", "Ana Paula"),
            new Aula("8h", "ART", "Thayse"),
            new Aula("9h", "ING", "Claudia"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "MAT", "Ana Paula"),
            new Aula("11h", "MAT", "Ana Paula"),
            new Aula("11h40", "Saída", "")
        ));
        t2A.put("Quarta", Arrays.asList(
            new Aula("7h", "PORT", "Luciano"),
            new Aula("8h", "PORT", "Luciano"),
            new Aula("9h", "PDV", "Roberto"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "EF", "Suyllane"),
            new Aula("11h", "APROF", "Cássia"),
            new Aula("11h40", "Saída", "")
        ));
        t2A.put("Quinta", Arrays.asList(
            new Aula("7h", "HIS", "Roberto"),
            new Aula("8h", "QUI", "Jhosi"),
            new Aula("9h", "SOC", "Ana Luiza"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PRAT", "Josenildo"),
            new Aula("11h", "PRAT", "Josenildo"),
            new Aula("11h40", "Saída", "")
        ));
        t2A.put("Sexta", Arrays.asList(
            new Aula("7h", "GEO", "Moral"),
            new Aula("8h", "QUI", "Jhosi"),
            new Aula("9h", "FIL", "Márcia"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "Sem Aula", ""),
            new Aula("11h", "APROF", "Cássia"),
            new Aula("11h40", "Saída", "")
        ));
        turmas.put("2A MAT", t2A);

        // ==================== 2B MAT ====================
        Map<String, List<Aula>> t2B = new HashMap<>();
        t2B.put("Segunda", Arrays.asList(
            new Aula("7h", "Sem Aula", ""),
            new Aula("8h", "FIS", "Camila"),
            new Aula("9h", "PDV", "Herbert"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PRAT", "Heloiza"),
            new Aula("11h", "PRAT", "Heloiza"),
            new Aula("11h40", "Saída", "")
        ));
        t2B.put("Terça", Arrays.asList(
            new Aula("7h", "MAT", "Jailson"),
            new Aula("8h", "MAT", "Ana Paula"),
            new Aula("9h", "GEO", "Moral"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "ING", "Claudia"),
            new Aula("11h", "ART", "Thayse"),
            new Aula("11h40", "Saída", "")
        ));
        t2B.put("Quarta", Arrays.asList(
            new Aula("7h", "APROF", "Jailson"),
            new Aula("8h", "EF", "Suyllane"),
            new Aula("9h", "BIO", "Thays"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PORT", "Danilo"),
            new Aula("11h", "PORT", "Danilo"),
            new Aula("11h40", "Saída", "")
        ));
        t2B.put("Quinta", Arrays.asList(
            new Aula("7h", "MAT", "Jailson"),
            new Aula("8h", "HIS", "Roberto"),
            new Aula("9h", "QUI", "Jhosi"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "APROF", "Jailson"),
            new Aula("11h", "MAT", "Jailson"),
            new Aula("11h40", "Saída", "")
        ));
        t2B.put("Sexta", Arrays.asList(
            new Aula("7h", "FIL", "Márcia"),
            new Aula("8h", "SOC", "Ana Luiza"),
            new Aula("9h", "QUI", "Jhosi"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PORT", "Danilo"),
            new Aula("11h", "PORT", "Danilo"),
            new Aula("11h40", "Saída", "")
        ));
        turmas.put("2B MAT", t2B);

        // ==================== 2C MAT ====================
        Map<String, List<Aula>> t2C = new HashMap<>();
        t2C.put("Segunda", Arrays.asList(
            new Aula("7h", "ART", "Cícera"),
            new Aula("8h", "Sem Aula", ""),
            new Aula("9h", "MAT", "Jailson"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "MAT", "Jailson"),
            new Aula("11h", "MAT", "Jailson"),
            new Aula("11h40", "Saída", "")
        ));
        t2C.put("Terça", Arrays.asList(
            new Aula("7h", "ING", "Claudia"),
            new Aula("8h", "APROF", "Moral"),
            new Aula("9h", "BIO", "Dayse"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "FIS", "Camila"),
            new Aula("11h", "APROF", "Moral"),
            new Aula("11h40", "Saída", "")
        ));
        t2C.put("Quarta", Arrays.asList(
            new Aula("7h", "PDV", "Débora"),
            new Aula("8h", "PORT", "Danilo"),
            new Aula("9h", "PORT", "Danilo"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PRAT", "Robson"),
            new Aula("11h", "EF", "Suyllane"),
            new Aula("11h40", "Saída", "")
        ));
        t2C.put("Quinta", Arrays.asList(
            new Aula("7h", "QUI", "Jhosi"),
            new Aula("8h", "MAT", "Jailson"),
            new Aula("9h", "HIS", "Roberto"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PORT", "Danilo"),
            new Aula("11h", "PORT", "Danilo"),
            new Aula("11h40", "Saída", "")
        ));
        t2C.put("Sexta", Arrays.asList(
            new Aula("7h", "SOC", "Ana Luiza"),
            new Aula("8h", "FIL", "Márcia"),
            new Aula("9h", "GEO", "Moral"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "QUI", "Jhosi"),
            new Aula("11h", "PRAT", "Robson"),
            new Aula("11h40", "Saída", "")
        ));
        turmas.put("2C MAT", t2C);

        // ==================== 2D MAT ====================
        Map<String, List<Aula>> t2D = new HashMap<>();
        t2D.put("Segunda", Arrays.asList(
            new Aula("7h", "QUI", "Heloiza"),
            new Aula("8h", "QUI", "Heloiza"),
            new Aula("9h", "FIS", "Camila"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "APROF", "Niedja"),
            new Aula("11h", "APROF", "Niedja"),
            new Aula("11h40", "Saída", "")
        ));
        t2D.put("Terça", Arrays.asList(
            new Aula("7h", "GEO", "Moral"),
            new Aula("8h", "ING", "Claudia"),
            new Aula("9h", "MAT", "Jailson"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PDV", "Thayse"),
            new Aula("11h", "Sem Aula", ""),
            new Aula("11h40", "Saída", "")
        ));
        t2D.put("Quarta", Arrays.asList(
            new Aula("7h", "EF", "Suyllane"),
            new Aula("8h", "MAT", "Jailson"),
            new Aula("9h", "MAT", "Jailson"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "MAT", "Jailson"),
            new Aula("11h", "HIS", "Roberto"),
            new Aula("11h40", "Saída", "")
        ));
        t2D.put("Quinta", Arrays.asList(
            new Aula("7h", "PORT", "Danilo"),
            new Aula("8h", "PORT", "Danilo"),
            new Aula("9h", "MAT", "Jailson"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "ART", "Henrique"),
            new Aula("11h", "PRAT", "Robson"),
            new Aula("11h40", "Saída", "")
        ));
        t2D.put("Sexta", Arrays.asList(
            new Aula("7h", "PORT", "Danilo"),
            new Aula("8h", "PORT", "Danilo"),
            new Aula("9h", "BIO", "Dayse"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PRAT", "Robson"),
            new Aula("11h", "SOC", "Ana Luiza"),
            new Aula("11h40", "Saída", "")
        ));
        turmas.put("2D MAT", t2D);

        // ==================== 3A MAT ====================
        Map<String, List<Aula>> t3A = new HashMap<>();
        t3A.put("Segunda", Arrays.asList(
            new Aula("7h", "APROF", "Niedja"),
            new Aula("8h", "APROF", "Niedja"),
            new Aula("9h", "PORT", "Judy"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PORT", "Judy"),
            new Aula("11h", "FIS", "Camila"),
            new Aula("11h40", "Saída", "")
        ));
        t3A.put("Terça", Arrays.asList(
            new Aula("7h", "BIO", "Dayse"),
            new Aula("8h", "Sem Aula", ""),
            new Aula("9h", "ART", "Thayse"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PDV", "Moral"),
            new Aula("11h", "FIS", "Camila"),
            new Aula("11h40", "Saída", "")
        ));
        t3A.put("Quarta", Arrays.asList(
            new Aula("7h", "PORT", "Judy"),
            new Aula("8h", "PORT", "Judy"),
            new Aula("9h", "MAT", "Cássia"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "MAT", "Cássia"),
            new Aula("11h", "GEO", "Jéssica"),
            new Aula("11h40", "Saída", "")
        ));
        t3A.put("Quinta", Arrays.asList(
            new Aula("7h", "FIL", "Márcia"),
            new Aula("8h", "SOC", "Ana Luiza"),
            new Aula("9h", "PRAT", "Robson"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PRAT", "Robson"),
            new Aula("11h", "QUI", "Jhosi"),
            new Aula("11h40", "Saída", "")
        ));
        t3A.put("Sexta", Arrays.asList(
            new Aula("7h", "MAT", "Cássia"),
            new Aula("8h", "MAT", "Cássia"),
            new Aula("9h", "ING", "Robson"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "HIS", "Niedja"),
            new Aula("11h", "EF", "Douglas"),
            new Aula("11h40", "Saída", "")
        ));
        turmas.put("3A MAT", t3A);

        // ==================== 3B MAT ====================
        Map<String, List<Aula>> t3B = new HashMap<>();
        t3B.put("Segunda", Arrays.asList(
            new Aula("7h", "PORT", "Judy"),
            new Aula("8h", "PORT", "Judy"),
            new Aula("9h", "PRAT", "Heloiza"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "FIS", "Camila"),
            new Aula("11h", "PDV", "Judy"),
            new Aula("11h40", "Saída", "")
        ));
        t3B.put("Terça", Arrays.asList(
            new Aula("7h", "Sem Aula", ""),
            new Aula("8h", "BIO", "Dayse"),
            new Aula("9h", "FIS", "Camila"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "APROF", "Dayse"),
            new Aula("11h", "APROF", "Dayse"),
            new Aula("11h40", "Saída", "")
        ));
        t3B.put("Quarta", Arrays.asList(
            new Aula("7h", "MAT", "Cássia"),
            new Aula("8h", "MAT", "Cássia"),
            new Aula("9h", "GEO", "Jéssica"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PORT", "Judy"),
            new Aula("11h", "PORT", "Judy"),
            new Aula("11h40", "Saída", "")
        ));
        t3B.put("Quinta", Arrays.asList(
            new Aula("7h", "SOC", "Ana Luiza"),
            new Aula("8h", "ART", "Henrique"),
            new Aula("9h", "PRAT", "Heloiza"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "QUI", "Jhosi"),
            new Aula("11h", "FIL", "Márcia"),
            new Aula("11h40", "Saída", "")
        ));
        t3B.put("Sexta", Arrays.asList(
            new Aula("7h", "EF", "Douglas"),
            new Aula("8h", "ING", "Robson"),
            new Aula("9h", "MAT", "Cássia"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "MAT", "Cássia"),
            new Aula("11h", "HIS", "Niedja"),
            new Aula("11h40", "Saída", "")
        ));
        turmas.put("3B MAT", t3B);

        // ==================== 3C MAT ====================
        Map<String, List<Aula>> t3C = new HashMap<>();
        t3C.put("Segunda", Arrays.asList(
            new Aula("7h", "MAT", "Jailson"),
            new Aula("8h", "MAT", "Jailson"),
            new Aula("9h", "BIO", "Dayse"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PORT", "Luciano"),
            new Aula("11h", "PORT", "Luciano"),
            new Aula("11h40", "Saída", "")
        ));
        t3C.put("Terça", Arrays.asList(
            new Aula("7h", "FIS", "Camila"),
            new Aula("8h", "FIS", "Camila"),
            new Aula("9h", "Sem Aula", ""),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "MAT", "Jailson"),
            new Aula("11h", "MAT", "Jailson"),
            new Aula("11h40", "Saída", "")
        ));
        t3C.put("Quarta", Arrays.asList(
            new Aula("7h", "APROF", "Jéssica"),
            new Aula("8h", "APROF", "Jéssica"),
            new Aula("9h", "PORT", "Luciano"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "GEO", "Jéssica"),
            new Aula("11h", "PORT", "Luciano"),
            new Aula("11h40", "Saída", "")
        ));
        t3C.put("Quinta", Arrays.asList(
            new Aula("7h", "ART", "Henrique"),
            new Aula("8h", "ING", "Robson"),
            new Aula("9h", "PRAT", "Márcia"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "PRAT", "Márcia"),
            new Aula("11h", "SOC", "Ana Luiza"),
            new Aula("11h40", "Saída", "")
        ));
        t3C.put("Sexta", Arrays.asList(
            new Aula("7h", "QUI", "Jhosi"),
            new Aula("8h", "HIS", "Moral"),
            new Aula("9h", "PDV", "Douglas"),
            new Aula("9h40", "Intervalo", ""),
            new Aula("10h10", "EF", "Douglas"),
            new Aula("11h", "FIL", "Márcia"),
            new Aula("11h40", "Saída", "")
        ));
        turmas.put("3C MAT", t3C);

        return turmas;
    }

    // ==================== PROFESSORES POR DIA ====================
    public static Map<String, List<ProfessorModel>> getProfessoresPorDia() {
        Map<String, List<ProfessorModel>> mapa = new HashMap<>();
        String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta"};
        Map<String, Map<String, List<Aula>>> todasTurmas = getTodosHorarios();

        for (String dia : dias) {
            Map<String, ProfessorModel> profMap = new HashMap<>();
            for (String turma : todasTurmas.keySet()) {
                Map<String, List<Aula>> diasTurma = todasTurmas.get(turma);
                if (diasTurma.containsKey(dia)) {
                    for (Aula aula : diasTurma.get(dia)) {
                        if (aula.getDisciplina().equals("Intervalo") || aula.getDisciplina().equals("Saída") || aula.getDisciplina().equals("Sem Aula")) continue;
                        String nomeProf = aula.getProfessor();
                        if (!profMap.containsKey(nomeProf)) {
                            ProfessorModel p = new ProfessorModel(nomeProf);
                            profMap.put(nomeProf, p);
                        }
                        ProfessorModel p = profMap.get(nomeProf);
                        if (!p.getDisciplinas().contains(aula.getDisciplina())) {
                            p.addDisciplina(aula.getDisciplina());
                        }
                        if (!p.getTurmas().contains(turma)) {
                            p.addTurma(turma);
                        }
                    }
                }
            }
            mapa.put(dia, new ArrayList<>(profMap.values()));
        }
        return mapa;
    }

    public static List<Aula> getHorariosProfessorPorDia(String professor, String dia) {
        List<Aula> resultado = new ArrayList<>();
        Map<String, Map<String, List<Aula>>> todasTurmas = getTodosHorarios();
        for (String turma : todasTurmas.keySet()) {
            Map<String, List<Aula>> diasTurma = todasTurmas.get(turma);
            if (diasTurma.containsKey(dia)) {
                for (Aula aula : diasTurma.get(dia)) {
                    if (aula.getProfessor().equals(professor)) {
                        resultado.add(new Aula(aula.getHorario() + " | " + turma, aula.getDisciplina(), aula.getProfessor()));
                    }
                }
            }
        }
        return resultado;
    }
}