package br.com.constance.app.appkstore.backend.services.pojo;

import java.math.BigDecimal;

public class ImagemPOJO {
    private BigDecimal codigoProduto;
    private byte[] imagem;

    public BigDecimal getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(BigDecimal codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }
}
