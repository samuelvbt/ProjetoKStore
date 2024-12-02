package br.com.constance.app.appkstore.backend.services.pojo;

import java.math.BigDecimal;

public class ItemPedidoPOJO {
    private String idPedido;
    private BigDecimal codigoProduto;
    private String controle;
    private BigDecimal quantidadeNegociada;
    private BigDecimal precoUnitarioProduto;

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public BigDecimal getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(BigDecimal codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public String getControle() {
        return controle;
    }

    public void setControle(String controle) {
        this.controle = controle;
    }

    public BigDecimal getQuantidadeNegociada() {
        return quantidadeNegociada;
    }

    public void setQuantidadeNegociada(BigDecimal quantidadeNegociada) {
        this.quantidadeNegociada = quantidadeNegociada;
    }

    public BigDecimal getPrecoUnitarioProduto() {
        return precoUnitarioProduto;
    }

    public void setPrecoUnitarioProduto(BigDecimal precoUnitarioProduto) {
        this.precoUnitarioProduto = precoUnitarioProduto;
    }
}
