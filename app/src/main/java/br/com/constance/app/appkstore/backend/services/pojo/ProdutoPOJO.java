package br.com.constance.app.appkstore.backend.services.pojo;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ProdutoPOJO  {
    private BigDecimal codigoProduto;
    private String referencia;
    private String descricaoProduto;
    private ArrayList<String> listaGrade;
    private ArrayList<String> listaCodigosDeBarra;
    private BigDecimal preco;
    private String urlImagemProduto;

    public BigDecimal getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(BigDecimal codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public ArrayList<String> getListaGrade() {
        return listaGrade;
    }

    public void setListaGrade(ArrayList<String> listaGrade) {
        this.listaGrade = listaGrade;
    }

    public ArrayList<String> getListaCodigosDeBarra() {
        return listaCodigosDeBarra;
    }

    public void setListaCodigosDeBarra(ArrayList<String> listaCodigosDeBarra) {
        this.listaCodigosDeBarra = listaCodigosDeBarra;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getUrlImagemProduto() {
        return urlImagemProduto;
    }

    public void setUrlImagemProduto(String urlImagemProduto) {
        this.urlImagemProduto = urlImagemProduto;
    }
}
