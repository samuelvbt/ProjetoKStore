package br.com.constance.app.appkstore.ui.adicionarPedido;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appkstore.R;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import br.com.constance.app.appkstore.Utils.Util;
import br.com.constance.app.appkstore.backend.controller.PedidoController;
import br.com.constance.app.appkstore.backend.services.pojo.PedidoInterfacePOJO;

public class AdicionarPedidoAdapter extends ArrayAdapter<PedidoInterfacePOJO> {
    NavHostFragment navHostFragment;
    PedidoController pedidoController;
    List<PedidoInterfacePOJO> pedidos;
    public AdicionarPedidoAdapter(Context context, List<PedidoInterfacePOJO> pedidos) {
        super(context, 0, pedidos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PedidoInterfacePOJO pedido = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView itemTitle = convertView.findViewById(R.id.itemTitle);
        itemTitle.setText(pedido.getIdPedido());
        BigDecimal total =BigDecimal.ZERO;
        pedidoController = new PedidoController(getContext());
        try {
            total = pedidoController.obterTotalPedido(pedido.getIdPedido());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        TextView itemSubtititle = convertView.findViewById(R.id.itemSubtitle);
        try {
            itemSubtititle.setText("Finalizado: "+pedido.getPedidoFinalizado() +"\nSincronizado:"+pedido.getPedidoSincronizado()+"\nTotal: "+Util.adicionarMascaraMonetaria(total.toString()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // Adicionar um ouvinte de clique ao item da lista
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pedido.getPedidoSincronizado().equals("S")){
                    showOptionsDialog(position);
                    navHostFragment = (NavHostFragment) ((AppCompatActivity) getContext()).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                }else{
                    openEditar(position);
                }

            }
        });


        return convertView;
    }
    private void showOptionsDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Escolha uma ação");
        builder.setItems(new CharSequence[]{"Adicionar itens ao pedido", "Excluir pedido"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        openEditar(position);
                        break;
                    case 1:
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
        NavHostFragment navHostFragment = (NavHostFragment) ((AppCompatActivity) getContext()).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = navHostFragment.getNavController();
        PedidoInterfacePOJO pedidoInterfacePOJO = getItem(position);

        Bundle bundle = new Bundle();
        bundle.putString("codigoParceiro", pedidoInterfacePOJO.getCodigoParceiro().toString());
        bundle.putString("idPedido",pedidoInterfacePOJO.getIdPedido());
        bundle.putString("sincronizar",pedidoInterfacePOJO.getPedidoSincronizado());
//        navController.navigate(R.id.nav_pedido, bundle);

    }
    public void updateData(List<PedidoInterfacePOJO> newPedidos) {
        clear();
        addAll(newPedidos);
        notifyDataSetChanged();
    }
    private void deleteItem(int position) throws Exception {
        PedidoInterfacePOJO pedidoInterfacePOJO = getItem(position);
        pedidoController.excluirPedido(pedidoInterfacePOJO.getIdPedido());
        remove(pedidoInterfacePOJO);
        notifyDataSetChanged();
        AdicionarPedidoAdapter adapter = new AdicionarPedidoAdapter(getContext(), pedidoController.buscarPedidosPorParceiro(pedidoInterfacePOJO.getCodigoParceiro()));
        List<PedidoInterfacePOJO> listaAtualizada = pedidoController.buscarPedidosPorParceiro(pedidoInterfacePOJO.getCodigoParceiro());
        adapter.updateData(listaAtualizada);
    }
}