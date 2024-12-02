package br.com.constance.app.appkstore.backend.services.pojo;

import java.math.BigDecimal;
import java.util.ArrayList;

public class PedidoInterfacePOJO {
    private String idPedido;
    private BigDecimal codigoParceiro;
    private Long dataNegociacao;
    private Long dataEntrega;
    private BigDecimal codigoTipoNegociacao;
    private BigDecimal codigoUsuario;
    private String pedidoSincronizado;
    private String pedidoFinalizado;
    private String observacao;
    private ArrayList<ItemPedidoInterfacePOJO> itemPedidoInterfacePOJO;

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public BigDecimal getCodigoParceiro() {
        return codigoParceiro;
    }

    public void setCodigoParceiro(BigDecimal codigoParceiro) {
        this.codigoParceiro = codigoParceiro;
    }

    public Long getDataNegociacao() {
        return dataNegociacao;
    }

    public void setDataNegociacao(Long dataNegociacao) {
        this.dataNegociacao = dataNegociacao;
    }

    public Long getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(Long dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public BigDecimal getCodigoTipoNegociacao() {
        return codigoTipoNegociacao;
    }

    public void setCodigoTipoNegociacao(BigDecimal codigoTipoNegociacao) {
        this.codigoTipoNegociacao = codigoTipoNegociacao;
    }

    public BigDecimal getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(BigDecimal codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public String getPedidoSincronizado() {
        return pedidoSincronizado;
    }

    public void setPedidoSincronizado(String pedidoSincronizado) {
        this.pedidoSincronizado = pedidoSincronizado;
    }

    public ArrayList<ItemPedidoInterfacePOJO> getItemPedidoInterfacePOJO() {
        return itemPedidoInterfacePOJO;
    }

    public void setItemPedidoInterfacePOJO(ArrayList<ItemPedidoInterfacePOJO> itemPedidoInterfacePOJO) {
        this.itemPedidoInterfacePOJO = itemPedidoInterfacePOJO;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getPedidoFinalizado() {
        return pedidoFinalizado;
    }

    public void setPedidoFinalizado(String pedidoFinalizado) {
        this.pedidoFinalizado = pedidoFinalizado;
    }
}
