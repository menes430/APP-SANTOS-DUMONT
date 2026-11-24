package com.santosdumont.utils;

import android.content.Context;

import com.santosdumont.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Le o changelog.xml em res/raw/ e retorna as mudancas.
 */
public class ChangelogHelper {

    public static class Item {
        public String tipo;       // NOVIDADE, MELHORIA, CORRECAO
        public String texto;
        public Item(String tipo, String texto) {
            this.tipo = tipo;
            this.texto = texto;
        }
    }

    public static class Versao {
        public String numero;
        public String data;
        public String descricao;
        public List<Item> itens = new ArrayList<>();
    }

    /**
     * Retorna todas as versoes do changelog.
     */
    public static List<Versao> getTodasVersoes(Context ctx) {
        List<Versao> versoes = new ArrayList<>();
        try {
            InputStream is = ctx.getResources().openRawResource(R.raw.changelog);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList listaVersoes = doc.getElementsByTagName("versao");
            for (int i = 0; i < listaVersoes.getLength(); i++) {
                Node n = listaVersoes.item(i);
                if (n.getNodeType() != Node.ELEMENT_NODE) continue;
                Element eV = (Element) n;

                Versao v = new Versao();
                v.numero = eV.getAttribute("numero");
                v.data = eV.getAttribute("data");

                NodeList descList = eV.getElementsByTagName("descricao");
                if (descList.getLength() > 0) {
                    v.descricao = descList.item(0).getTextContent();
                }

                v.itens.addAll(getItens(eV, "novidades", "NOVO"));
                v.itens.addAll(getItens(eV, "melhorias", "MELHORIA"));
                v.itens.addAll(getItens(eV, "correcoes", "CORRECAO"));

                versoes.add(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versoes;
    }

    private static List<Item> getItens(Element eV, String tag, String tipo) {
        List<Item> lista = new ArrayList<>();
        NodeList tags = eV.getElementsByTagName(tag);
        if (tags.getLength() == 0) return lista;
        Element eTag = (Element) tags.item(0);
        NodeList itens = eTag.getElementsByTagName("item");
        for (int i = 0; i < itens.getLength(); i++) {
            String txt = itens.item(i).getTextContent().trim();
            if (!txt.isEmpty()) lista.add(new Item(tipo, txt));
        }
        return lista;
    }

    /**
     * Retorna as versoes que o usuario ainda nao viu.
     * @param versaoVista versao que o usuario tinha antes (ex: "1.0.0"). Se vazio, retorna a ultima.
     */
    public static List<Versao> getNovidades(Context ctx, String versaoVista) {
        List<Versao> todas = getTodasVersoes(ctx);
        if (todas.isEmpty()) return todas;

        // Se nao tem versao vista, retorna a ultima
        if (versaoVista == null || versaoVista.isEmpty()) {
            List<Versao> ultima = new ArrayList<>();
            ultima.add(todas.get(todas.size() - 1));
            return ultima;
        }

        // Acha o indice da versao vista
        int idx = -1;
        for (int i = 0; i < todas.size(); i++) {
            if (todas.get(i).numero.equals(versaoVista)) {
                idx = i;
                break;
            }
        }
        if (idx < 0) idx = -1;

        // Retorna todas as versoes DEPOIS da vista
        List<Versao> novas = new ArrayList<>();
        for (int i = idx + 1; i < todas.size(); i++) {
            novas.add(todas.get(i));
        }
        return novas;
    }
}