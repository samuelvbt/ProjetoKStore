package br.com.constance.app.appkstore.backend.services.pojo;

import java.math.BigDecimal;

public class TipoNegociacaoPOJO {
    private BigDecimal codigoTipoNegociacao;
    private String descricaoTipoNegociacao;

    public BigDecimal getCodigoTipoNegociacao() {
        return codigoTipoNegociacao;
    }

    public void setCodigoTipoNegociacao(BigDecimal codigoTipoNegociacao) {
        this.codigoTipoNegociacao = codigoTipoNegociacao;
    }

    public String getDescricaoTipoNegociacao() {
        return descricaoTipoNegociacao;
    }

    public void setDescricaoTipoNegociacao(String descricaoTipoNegociacao) {
        this.descricaoTipoNegociacao = descricaoTipoNegociacao;
    }
}
