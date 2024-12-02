package br.com.constance.app.appkstore.backend.util;

import java.math.BigDecimal;

import br.com.constance.app.appkstore.backend.services.pojo.ItemPedidoInterfacePOJO;
import br.com.constance.app.appkstore.backend.services.pojo.ItemPedidoPOJO;
import br.com.constance.app.appkstore.backend.services.pojo.PedidoInterfacePOJO;
import br.com.constance.app.appkstore.backend.services.pojo.PedidoPOJO;

public class ConvertPedidoPojoIntoPedidoInterfacePojo {

    public static PedidoInterfacePOJO converter(PedidoPOJO pedidoPOJO){
        PedidoInterfacePOJO pedidoInterfacePOJO = new PedidoInterfacePOJO();
        pedidoInterfacePOJO.setIdPedido(pedidoPOJO.getIdPedido());
        pedidoInterfacePOJO.setCodigoParceiro(pedidoPOJO.getCodigoParceiro());
        pedidoInterfacePOJO.setCodigoUsuario(pedidoPOJO.getCodigoUsuario());
        pedidoInterfacePOJO.setDataNegociacao(pedidoPOJO.getDataNegociacao());
        pedidoInterfacePOJO.setDataEntrega(pedidoPOJO.getDataEntrega());
        pedidoInterfacePOJO.setPedidoSincronizado(pedidoPOJO.getPedidoSincronizado());
        pedidoInterfacePOJO.setPedidoFinalizado(pedidoPOJO.getPedidoFinalizado());
        pedidoPOJO.getItensPedidoPOJO().sort((p1,p2) -> p1.getCodigoProduto().compareTo(p2.getCodigoProduto()));

        BigDecimal codigoProduto = BigDecimal.ZERO;

        for (ItemPedidoPOJO itemPedidoPOJO : pedidoPOJO.getItensPedidoPOJO()) {
            ItemPedidoInterfacePOJO itemPedidoInterfacePOJO = null;
            if(!itemPedidoPOJO.getCodigoProduto().equals(codigoProduto)){
                codigoProduto = itemPedidoPOJO.getCodigoProduto();
                itemPedidoInterfacePOJO = new ItemPedidoInterfacePOJO();
                itemPedidoInterfacePOJO.setIdPedido(itemPedidoInterfacePOJO.getIdPedido());
                itemPedidoInterfacePOJO.setCodigoProduto(itemPedidoInterfacePOJO.getCodigoProduto());
                itemPedidoInterfacePOJO.setPrecoUnitarioProduto(itemPedidoInterfacePOJO.getPrecoUnitarioProduto());
            }

            itemPedidoInterfacePOJO.addGrade(itemPedidoPOJO.getControle(), itemPedidoPOJO.getQuantidadeNegociada());
        }

        return pedidoInterfacePOJO;
    }
}
