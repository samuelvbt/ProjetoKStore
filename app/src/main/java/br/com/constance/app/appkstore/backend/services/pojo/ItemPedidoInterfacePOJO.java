package br.com.constance.app.appkstore.backend.services.pojo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ItemPedidoInterfacePOJO {
    private String idPedido;
    private BigDecimal codigoProduto;
    private BigDecimal precoUnitarioProduto;
    private Map<String, BigDecimal> grade = new HashMap<>();

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

    public BigDecimal getPrecoUnitarioProduto() {
        return precoUnitarioProduto;
    }

    public void setPrecoUnitarioProduto(BigDecimal precoUnitarioProduto) {
        this.precoUnitarioProduto = precoUnitarioProduto;
    }

    public Map<String, BigDecimal> getGrade() {
        return grade;
    }

    public void setGrade(Map<String, BigDecimal> grade) {
        this.grade = grade;
    }

    public void addGrade(String controle, BigDecimal quantidadeNegociada){
        grade.put(controle, quantidadeNegociada);
    }
}
