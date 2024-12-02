package br.com.constance.app.appkstore.backend.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.constance.app.appkstore.backend.services.pojo.ItemPedidoInterfacePOJO;
import br.com.constance.app.appkstore.backend.services.pojo.ItemPedidoPOJO;

public class ConvertItemPedidoPojoIntoItemPedidoInterfacePojo {

    public static List<ItemPedidoInterfacePOJO> converter(List<ItemPedidoPOJO> listaItemPedidoPOJO) {

        List<ItemPedidoInterfacePOJO> listaItemPedidoInterfacePOJO = new ArrayList<>();

        listaItemPedidoPOJO.sort((p1, p2) -> p1.getCodigoProduto().compareTo(p2.getCodigoProduto()));

        BigDecimal codigoProduto = BigDecimal.ZERO;
        ItemPedidoInterfacePOJO itemPedidoInterfacePOJO = null;

        for (ItemPedidoPOJO itemPedidoPOJO : listaItemPedidoPOJO) {

            if (!itemPedidoPOJO.getCodigoProduto().equals(codigoProduto)) {

                itemPedidoInterfacePOJO = new ItemPedidoInterfacePOJO();

                codigoProduto = itemPedidoPOJO.getCodigoProduto();
                itemPedidoInterfacePOJO.setIdPedido(itemPedidoPOJO.getIdPedido());
                itemPedidoInterfacePOJO.setCodigoProduto(itemPedidoPOJO.getCodigoProduto());
                itemPedidoInterfacePOJO.setPrecoUnitarioProduto(itemPedidoPOJO.getPrecoUnitarioProduto());
                listaItemPedidoInterfacePOJO.add(itemPedidoInterfacePOJO);
            }

            itemPedidoInterfacePOJO.addGrade(itemPedidoPOJO.getControle(), itemPedidoPOJO.getQuantidadeNegociada());
        }

        return listaItemPedidoInterfacePOJO;
    }
}
