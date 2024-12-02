package br.com.constance.app.appkstore.backend.services.pojo;

import java.math.BigDecimal;

public class ParceiroPOJO {
    private BigDecimal codigoParceiro;
    private String razaoSocial;
    private String nomeParceiro;
    private String cnpj;
    private String cidadeParceiro;
    private String bairroParceiro;

    public BigDecimal getCodigoParceiro() {
        return codigoParceiro;
    }

    public void setCodigoParceiro(BigDecimal codigoParceiro) {
        this.codigoParceiro = codigoParceiro;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeParceiro() {
        return nomeParceiro;
    }

    public void setNomeParceiro(String nomeParceiro) {
        this.nomeParceiro = nomeParceiro;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCidadeParceiro() {
        return cidadeParceiro;
    }

    public void setCidadeParceiro(String cidadeParceiro) {
        this.cidadeParceiro = cidadeParceiro;
    }

    public String getBairroParceiro() {
        return bairroParceiro;
    }

    public void setBairroParceiro(String bairroParceiro) {
        this.bairroParceiro = bairroParceiro;
    }
}
