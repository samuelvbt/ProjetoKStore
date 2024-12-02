package br.com.constance.app.appkstore.ui.preco;

import android.content.Context;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appkstore.R;

import java.text.DecimalFormat;
import java.util.List;

import br.com.constance.app.appkstore.backend.controller.PedidoController;
import br.com.constance.app.appkstore.backend.dao.ImagemDao;
import br.com.constance.app.appkstore.backend.services.pojo.ItemPedidoInterfacePOJO;
import br.com.constance.app.appkstore.backend.services.pojo.ProdutoPOJO;

public class PrecoAdapter extends ArrayAdapter<ProdutoPOJO> {
    Bundle bundlePreco;
    PedidoController pedidoController;
    boolean produtoNoPedido = false;
    Fragment fragment;
    public PrecoAdapter(Context context, List<ProdutoPOJO> produtos, Bundle bundle, Fragment fragment) {
        super(context, 0, produtos);
        this.bundlePreco=bundle;
        this.fragment= fragment;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ProdutoPOJO produto = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_foto, parent, false);
        }
        pedidoController = new PedidoController(getContext());
        TextView titleTextView = convertView.findViewById(R.id.itemTitle);
        TextView subtitleTextView = convertView.findViewById(R.id.itemSubtitle);
        DecimalFormat df = new DecimalFormat("#,##0.00");

        // Presumindo que `Produto` tem métodos `getTituloProduto` e `getDescricaoProduto`
        titleTextView.setText(produto.getDescricaoProduto() );
        subtitleTextView.setText("Código:"+produto.getCodigoProduto().toString()+ "  Preço:R$"+ df.format(produto.getPreco()).replace('.', ','));

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


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bundlePreco!=null && bundlePreco.containsKey("tela") && bundlePreco.get("tela").equals("pedido")){
                    NavHostFragment navHostFragment = (NavHostFragment) ((AppCompatActivity) getContext()).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                    NavController navController = navHostFragment.getNavController();
                    ProdutoPOJO produtoClicado = getItem(position);
                    try {
                        List<ItemPedidoInterfacePOJO> itens =  pedidoController.buscaItens(bundlePreco.getString("idPedido"),produtoClicado.getCodigoProduto());
                        if(!itens.isEmpty()){
                            produtoNoPedido=true;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString("codigoProduto", produtoClicado.getCodigoProduto().toString());
                    bundle.putString("descricaoProduto", produtoClicado.getDescricaoProduto());
                    bundle.putString("referencia", produtoClicado.getReferencia());
                    bundle.putString("preco", produtoClicado.getPreco().toString());
                    bundle.putStringArrayList("listaGrade", produtoClicado.getListaGrade());
                    bundle.putString("atualizar",produtoNoPedido?"S":"N");
                    bundle.putString("idPedido",bundlePreco.getString("idPedido"));
                    bundle.putString("sincronizar",bundlePreco.getString("sincronizar"));
                    bundle.putString("tela","consultaPreco");
//                    navController.navigate(R.id.action_pedido_to_item, bundle);
                    ((DialogFragment) fragment).dismiss();
                }
            }
        });





        return convertView;
    }


}
