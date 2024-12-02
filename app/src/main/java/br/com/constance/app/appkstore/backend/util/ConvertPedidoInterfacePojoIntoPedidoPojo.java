package br.com.constance.app.appkstore.backend.util;

import java.math.BigDecimal;
import java.util.Map;

import br.com.constance.app.appkstore.backend.services.pojo.ItemPedidoInterfacePOJO;
import br.com.constance.app.appkstore.backend.services.pojo.ItemPedidoPOJO;
import br.com.constance.app.appkstore.backend.services.pojo.PedidoInterfacePOJO;
import br.com.constance.app.appkstore.backend.services.pojo.PedidoPOJO;

public class ConvertPedidoInterfacePojoIntoPedidoPojo {

    public static PedidoPOJO converter(PedidoInterfacePOJO pedidoInterfacePOJO){
        PedidoPOJO pedidoPOJO = new PedidoPOJO();
        pedidoPOJO.setIdPedido(pedidoInterfacePOJO.getIdPedido());
        pedidoPOJO.setCodigoParceiro(pedidoInterfacePOJO.getCodigoParceiro());
        pedidoPOJO.setCodigoUsuario(pedidoInterfacePOJO.getCodigoUsuario());
        pedidoPOJO.setDataNegociacao(pedidoInterfacePOJO.getDataNegociacao());
        pedidoPOJO.setDataEntrega(pedidoInterfacePOJO.getDataEntrega());
        pedidoPOJO.setPedidoSincronizado(pedidoInterfacePOJO.getPedidoSincronizado());
        pedidoPOJO.setObservacao(pedidoInterfacePOJO.getObservacao());
        pedidoPOJO.setCodigoTipoNegociacao(pedidoInterfacePOJO.getCodigoTipoNegociacao());

        if(pedidoInterfacePOJO.getItemPedidoInterfacePOJO()!=null){

            for (ItemPedidoInterfacePOJO itemPedidoInterfacePOJO : pedidoInterfacePOJO.getItemPedidoInterfacePOJO()) {
                for (Map.Entry<String, BigDecimal> gradeQuantidade : itemPedidoInterfacePOJO.getGrade().entrySet()) {

                    ItemPedidoPOJO itemPedidoPOJO = new ItemPedidoPOJO();
                    itemPedidoPOJO.setIdPedido(itemPedidoInterfacePOJO.getIdPedido());
                    itemPedidoPOJO.setCodigoProduto(itemPedidoInterfacePOJO.getCodigoProduto());
                    itemPedidoPOJO.setPrecoUnitarioProduto(itemPedidoInterfacePOJO.getPrecoUnitarioProduto());
                    itemPedidoPOJO.setControle(gradeQuantidade.getKey());
                    itemPedidoPOJO.setQuantidadeNegociada(gradeQuantidade.getValue());
                    pedidoPOJO.addItemPedidoPOJO(itemPedidoPOJO);
                }
            }

        }

        return pedidoPOJO;
    }
}
