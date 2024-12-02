package br.com.constance.app.appkstore.Utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Util {
    public static String adicionarMascaraMonetaria(String valor) throws ParseException {
        // Converte a string para um número
        double valorNumerico = Double.parseDouble(valor.replace(",", "."));

        // Formata o valor numérico como moeda brasileira
        NumberFormat formatador = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return formatador.format(valorNumerico);
    }
    public static String removerMascaraMonetaria(String valorMonetario) throws ParseException {
        NumberFormat formatador = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        Number numero = formatador.parse(valorMonetario);
        return numero.toString().replace(".", ",");
    }
}
