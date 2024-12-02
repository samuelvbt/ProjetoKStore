package br.com.constance.app.appkstore.backend.services.pojo;

import java.math.BigDecimal;

public class ProdutoBarraPOJO {
    private BigDecimal codigoProduto;
    private String codigoBarras;

    public BigDecimal getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(BigDecimal codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }
}
