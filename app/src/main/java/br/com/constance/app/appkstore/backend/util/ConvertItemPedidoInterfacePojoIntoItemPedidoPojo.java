package br.com.constance.app.appkstore.backend.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import br.com.constance.app.appkstore.backend.services.pojo.ItemPedidoInterfacePOJO;
import br.com.constance.app.appkstore.backend.services.pojo.ItemPedidoPOJO;

public class ConvertItemPedidoInterfacePojoIntoItemPedidoPojo {

    public static ArrayList<ItemPedidoPOJO> converter(ItemPedidoInterfacePOJO itemPedidoInterfacePOJO) {

        ArrayList<ItemPedidoPOJO> listaItensPedido = new ArrayList<>();


        for (Map.Entry<String, BigDecimal> gradeQuantidade : itemPedidoInterfacePOJO.getGrade().entrySet()) {

            ItemPedidoPOJO itemPedidoPOJO = new ItemPedidoPOJO();
            itemPedidoPOJO.setIdPedido(itemPedidoInterfacePOJO.getIdPedido());
            itemPedidoPOJO.setCodigoProduto(itemPedidoInterfacePOJO.getCodigoProduto());
            itemPedidoPOJO.setPrecoUnitarioProduto(itemPedidoInterfacePOJO.getPrecoUnitarioProduto());
            itemPedidoPOJO.setControle(gradeQuantidade.getKey());
            itemPedidoPOJO.setQuantidadeNegociada(gradeQuantidade.getValue());
            listaItensPedido.add(itemPedidoPOJO);

        }

        return listaItensPedido;
    }
}
