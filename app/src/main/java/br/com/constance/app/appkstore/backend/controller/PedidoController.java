package br.com.constance.app.appkstore.backend.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.appkstore.R;
import com.google.android.material.snackbar.Snackbar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.constance.app.appkstore.backend.dao.PedidoDao;
import br.com.constance.app.appkstore.backend.services.pedido.PedidoService;
import br.com.constance.app.appkstore.backend.services.pojo.ItemPedidoInterfacePOJO;
import br.com.constance.app.appkstore.backend.services.pojo.ItemPedidoPOJO;
import br.com.constance.app.appkstore.backend.services.pojo.PedidoInterfacePOJO;
import br.com.constance.app.appkstore.backend.services.pojo.PedidoPOJO;
import br.com.constance.app.appkstore.backend.util.ConvertItemPedidoInterfacePojoIntoItemPedidoPojo;
import br.com.constance.app.appkstore.backend.util.ConvertItemPedidoPojoIntoItemPedidoInterfacePojo;
import br.com.constance.app.appkstore.backend.util.ConvertPedidoInterfacePojoIntoPedidoPojo;
import br.com.constance.app.appkstore.backend.util.ConvertPedidoPojoIntoPedidoInterfacePojo;

public class PedidoController extends AsyncTask<Void, Void, String> {

    private PedidoDao pedidoDao;
    private PedidoService pedidoService;
    private ArrayList<PedidoPOJO> listaPedidoPOJO;
    private Context context;
    private View view;

    public PedidoController(Context context, View view) {
        this.context = context;
        this.view = view;
        this.pedidoDao = new PedidoDao(context);
        this.pedidoService = new PedidoService(context);
    }

    public PedidoController(Context context) {
        this.context = context;
        this.pedidoDao = new PedidoDao(context);
        this.pedidoService = new PedidoService(context);
    }

    public PedidoInterfacePOJO salvarCabecalhoPedido(PedidoInterfacePOJO pedidoInterfacePOJO) {
        PedidoPOJO pedidoPOJO = ConvertPedidoInterfacePojoIntoPedidoPojo.converter(pedidoInterfacePOJO);
        pedidoDao.open();
        String pkPedido = pedidoDao.salvarCabecalho(pedidoPOJO);
        PedidoPOJO pedidoPOJOSalvo = pedidoDao.buscarPedidoPorId(pkPedido);
        pedidoDao.close();
        return ConvertPedidoPojoIntoPedidoInterfacePojo.converter(pedidoPOJOSalvo);
    }

    public void finalizarPedido(PedidoInterfacePOJO pedidoInterfacePOJO) {
        PedidoPOJO pedidoPOJO = ConvertPedidoInterfacePojoIntoPedidoPojo.converter(pedidoInterfacePOJO);
        pedidoDao.open();
        pedidoDao.finalizarPedido(pedidoPOJO);
        pedidoDao.close();
    }

    public void salvarItensPedido(ItemPedidoInterfacePOJO itemPedidoInterfacePOJO) throws Exception {
        ArrayList<ItemPedidoPOJO> listaItemPedidoPOJO = ConvertItemPedidoInterfacePojoIntoItemPedidoPojo.converter(itemPedidoInterfacePOJO);
        for (ItemPedidoPOJO itemPedidoPOJO : listaItemPedidoPOJO) {
            if (itemPedidoPOJO.getQuantidadeNegociada().compareTo(BigDecimal.ZERO) > 0) {
                pedidoDao.salvarItens(itemPedidoPOJO);
            }
        }
    }

    public void alterarRemoverItensDoPedidoPorSku(ItemPedidoInterfacePOJO itemPedidoInterfacePOJO) throws Exception {
        ArrayList<ItemPedidoPOJO> listaItemPedidoPOJO = ConvertItemPedidoInterfacePojoIntoItemPedidoPojo.converter(itemPedidoInterfacePOJO);


        for (ItemPedidoPOJO itemPedidoPOJO : listaItemPedidoPOJO) {
            ItemPedidoPOJO itemPedidoPOJORecuperado = pedidoDao.buscarItemPedidoPorSku(itemPedidoPOJO);

            if (itemPedidoPOJORecuperado != null) {

                if (itemPedidoPOJORecuperado.getQuantidadeNegociada().compareTo(itemPedidoPOJO.getQuantidadeNegociada()) != 0 &&
                        itemPedidoPOJO.getQuantidadeNegociada().compareTo(BigDecimal.ZERO) == 0) {
                    pedidoDao.removeItem(itemPedidoPOJORecuperado);
                } else if (itemPedidoPOJORecuperado.getQuantidadeNegociada().compareTo(itemPedidoPOJO.getQuantidadeNegociada()) != 0) {
                    pedidoDao.modificarItem(itemPedidoPOJO);
                }
            } else if(itemPedidoPOJORecuperado == null && itemPedidoPOJO.getQuantidadeNegociada().compareTo(BigDecimal.ZERO) > 0){
                pedidoDao.salvarItens(itemPedidoPOJO);
            }
        }

    }


    public void removerItemDoPedidoPorProduto(String idPedido, BigDecimal codigoProduto) throws Exception {
        pedidoDao.open();
        List<ItemPedidoPOJO> listaItensPedidoPOJO = pedidoDao.buscarItensPorIdPedidoProduto(idPedido, codigoProduto);

        for (ItemPedidoPOJO itemPedidoPOJO : listaItensPedidoPOJO) {
            pedidoDao.removeItem(itemPedidoPOJO);
        }

        pedidoDao.close();
    }

    public void removerProdutoDoPedidoPorProduto(String idPedido, BigDecimal codigoProduto) throws Exception {
        pedidoDao.removerProduto(idPedido, codigoProduto);
    }

    public List<PedidoInterfacePOJO> buscarPedidosPorParceiro(BigDecimal codigoParceiro){
        List<PedidoInterfacePOJO> pedidoInterfacePOJOList = new ArrayList<>();
        pedidoDao.open();
        List<PedidoPOJO> pedidoPOJOS = pedidoDao.buscarPedidosPorParceiro(codigoParceiro);
        pedidoDao.close();
        for (PedidoPOJO pedidoPOJO : pedidoPOJOS) {
            pedidoInterfacePOJOList.add(ConvertPedidoPojoIntoPedidoInterfacePojo.converter(pedidoPOJO));
        }
        return pedidoInterfacePOJOList;
    }


    private void sincronizarPedidos() throws Exception {

        try {
            obterListaPedidosASincronizar();
        } catch (Exception e) {
            throw new Exception("Não foi possível recuperar os pedidos na base local. Favor verificar. Erro: " + e);
        }


        enviarPedidosParaErp();

    }

    private void obterListaPedidosASincronizar() {
        pedidoDao.open();
        listaPedidoPOJO = pedidoDao.buscarPedidosFinalizadosParaSincronizar();
        pedidoDao.close();
    }

    private void enviarPedidosParaErp() {

        for (PedidoPOJO pedidoPOJO : listaPedidoPOJO) {
            pedidoService = new PedidoService(this.context);
            pedidoService.setPedidosPOJO(pedidoPOJO);

            try {
                pedidoService.sincronizarPedidos();
                pedidoDao.atualizarStatusSincronizadoPedido(pedidoPOJO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public List<ItemPedidoInterfacePOJO> buscaProdutos(String idPedido) {
        pedidoDao.open();
        List<ItemPedidoPOJO> itemPedidoPOJOS = pedidoDao.buscarProdutosPorIdPedido(idPedido);
        pedidoDao.close();
        return ConvertItemPedidoPojoIntoItemPedidoInterfacePojo.converter(itemPedidoPOJOS);
    }

    public List<ItemPedidoInterfacePOJO> buscaItens(String idPedido, BigDecimal codprod) throws Exception {
        List<ItemPedidoPOJO> itemPedidoPOJOS = pedidoDao.buscarItensPorIdPedidoProduto(idPedido, codprod);
        return ConvertItemPedidoPojoIntoItemPedidoInterfacePojo.converter(itemPedidoPOJOS);
    }

    public BigDecimal obterTotalItensPorPedido(String idPedido) throws Exception {
        return pedidoDao.obterTotalItensPedido(idPedido);
    }

    public BigDecimal obterTotalItensPorPedidoProduto(String idPedido, BigDecimal codigoProduto) throws Exception {
        return pedidoDao.obterTotalItensPedidoProduto(idPedido, codigoProduto);
    }

    public BigDecimal obterTotalPedido(String idPedido) throws Exception {
        return pedidoDao.obterTotalPedidoPorId(idPedido);
    }

    public void excluirPedido(String idPedido) throws Exception {
        pedidoDao.removerFakePedido(idPedido);
    }
    public List<PedidoPOJO>buscarTodosPedidos() throws Exception {

        return pedidoDao.buscarTodosPedidos();
    }

    public List<PedidoInterfacePOJO> buscarPedidosFinalizados(){
        List<PedidoInterfacePOJO> pedidoInterfacePOJOList = new ArrayList<>();
        List<PedidoPOJO> pedidoPOJOS = pedidoDao.buscarPedidos(null, "S");
        for (PedidoPOJO pedidoPOJO : pedidoPOJOS) {
            pedidoInterfacePOJOList.add(ConvertPedidoPojoIntoPedidoInterfacePojo.converter(pedidoPOJO));
        }
        return pedidoInterfacePOJOList;
    }

    public List<PedidoInterfacePOJO> buscarPedidosNaoFinalizados(){
        List<PedidoInterfacePOJO> pedidoInterfacePOJOList = new ArrayList<>();
        List<PedidoPOJO> pedidoPOJOS = pedidoDao.buscarPedidos(null, "N");
        for (PedidoPOJO pedidoPOJO : pedidoPOJOS) {
            pedidoInterfacePOJOList.add(ConvertPedidoPojoIntoPedidoInterfacePojo.converter(pedidoPOJO));
        }
        return pedidoInterfacePOJOList;
    }

    public List<PedidoInterfacePOJO> buscarPedidosNaoSincronizados(){
        List<PedidoInterfacePOJO> pedidoInterfacePOJOList = new ArrayList<>();
        List<PedidoPOJO> pedidoPOJOS = pedidoDao.buscarPedidos("N", null);
        for (PedidoPOJO pedidoPOJO : pedidoPOJOS) {
            pedidoInterfacePOJOList.add(ConvertPedidoPojoIntoPedidoInterfacePojo.converter(pedidoPOJO));
        }
        return pedidoInterfacePOJOList;
    }

    public List<PedidoInterfacePOJO> buscarPedidosSincronizados(){
        List<PedidoInterfacePOJO> pedidoInterfacePOJOList = new ArrayList<>();
        List<PedidoPOJO> pedidoPOJOS = pedidoDao.buscarPedidos("S", null);
        for (PedidoPOJO pedidoPOJO : pedidoPOJOS) {
            pedidoInterfacePOJOList.add(ConvertPedidoPojoIntoPedidoInterfacePojo.converter(pedidoPOJO));
        }
        return pedidoInterfacePOJOList;
    }


    @Override
    protected String doInBackground(Void... voids) {
        try {
            sincronizarPedidos();
            return "Pedidos Enviados com sucesso!";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Snackbar.make(view,s, Snackbar.LENGTH_SHORT).show();
        ProgressBar progressBar =view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }
}
