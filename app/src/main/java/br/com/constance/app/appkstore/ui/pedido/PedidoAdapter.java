package br.com.constance.app.appkstore.ui.pedido;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appkstore.R;

import java.util.ArrayList;
import java.util.List;

import br.com.constance.app.appkstore.backend.controller.PedidoController;
import br.com.constance.app.appkstore.backend.controller.ProdutoController;
import br.com.constance.app.appkstore.backend.dao.ImagemDao;
import br.com.constance.app.appkstore.backend.services.pojo.ItemPedidoInterfacePOJO;
import br.com.constance.app.appkstore.backend.services.pojo.ProdutoPOJO;

public class PedidoAdapter extends ArrayAdapter<ProdutoPOJO> {
    Bundle bundlePedido;
    View convertView;
    ViewGroup parent;
    NavHostFragment navHostFragment;
    PedidoController pedidoController;
    PedidoFragment pedidoFragment;


    public PedidoAdapter(Context context, List<ProdutoPOJO> produtos,Bundle bundle, PedidoFragment pedidoFragment) {
        super(context, 0, produtos);
        this.bundlePedido=bundle;
        this.pedidoFragment = pedidoFragment;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ProdutoPOJO produto = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_foto, parent, false);

        }
        this.convertView=convertView;
        this.parent = parent;
        TextView titleTextView =  this.convertView.findViewById(R.id.itemTitle);
        TextView subtitleTextView =  this.convertView.findViewById(R.id.itemSubtitle);
        pedidoController = new PedidoController(getContext());
        titleTextView.setText(produto.getCodigoProduto().toString()+" - "+produto.getDescricaoProduto());
        try {
            subtitleTextView.setText("Qtd. Neg: "+ pedidoController.obterTotalItensPorPedidoProduto(bundlePedido.getString("idPedido"),produto.getCodigoProduto()).toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ImagemDao imagemDao = new ImagemDao(getContext());
        try {
            imagemDao.open();
            byte[] imagem = imagemDao.buscaIimagemPorId(produto.getCodigoProduto());
            imagemDao.close();
            if (imagem != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imagem, 0, imagem.length);
                ImageView imagemProduto = convertView.findViewById(R.id.imageView3);
                imagemProduto.setImageBitmap(bitmap);
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

        // Adicionar um ouvinte de clique ao item da lista
        this.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!("S").equals(bundlePedido.getString("sincronizar"))){
                    showOptionsDialog(position);
                    navHostFragment = (NavHostFragment) ((AppCompatActivity) getContext()).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);

                }else{
                    openEditar(position);
                }
            }
        });

        return  this.convertView;
    }
    private void showOptionsDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Escolha uma ação");
        builder.setItems(new CharSequence[]{"Editar Quantidades", "Excluir item"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
//                        // Abrir a página correspondente ao item clicado
                        openEditar(position);
                        break;
                    case 1:
                        // Excluir o item clicado
                        try {
                            deleteItem(position);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }
        });
        builder.show();
    }
    private void openEditar(int position){
        Bundle bundle = new Bundle();
        ProdutoPOJO produtoClicado = getItem(position);
        bundle.putString("codigoProduto", produtoClicado.getCodigoProduto().toString());
        bundle.putString("descricaoProduto", produtoClicado.getDescricaoProduto());
        bundle.putString("referencia", produtoClicado.getReferencia());
        bundle.putString("preco", produtoClicado.getPreco().toString());
        bundle.putStringArrayList("listaGrade", produtoClicado.getListaGrade());
        // incluir dados do pedido
        bundle.putString("idPedido",bundlePedido.getString("idPedido"));
        bundle.putString("sincronizar", bundlePedido.getString("sincronizar"));
        bundle.putString("atualizar","S");
        NavController navController = Navigation.findNavController(getView(position,convertView,parent));
//        navController.navigate(R.id.nav_item,bundle);

    }
    private void deleteItem(int position) throws Exception {
        ProdutoPOJO produtoPOJO = getItem(position);
        PedidoController pedidoController = new PedidoController(getContext());
        ProdutoController produtoController = new ProdutoController(getContext());
        pedidoController.removerProdutoDoPedidoPorProduto(bundlePedido.getString("idPedido"),produtoPOJO.getCodigoProduto());
        View convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_pedido, parent, false);
        remove(produtoPOJO);
        notifyDataSetChanged();
        List<ItemPedidoInterfacePOJO> itens = new ArrayList<>();
        List<ProdutoPOJO> produtosPedido = new ArrayList<>();
        itens = pedidoController.buscaProdutos(bundlePedido.getString("idPedido"));
        for (ItemPedidoInterfacePOJO item : itens){
            try {
                produtosPedido.add(produtoController.obterDadosProdutoPorId(item.getCodigoProduto()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        PedidoAdapter adapter = new PedidoAdapter(getContext(),produtosPedido,bundlePedido, pedidoFragment);


        pedidoFragment.atualizarValorTotal();
        adapter.updateData(produtosPedido);

    }
    public void updateData(List<ProdutoPOJO> newProdutos) {
        clear();
        addAll(newProdutos);
        notifyDataSetChanged();
    }


}
