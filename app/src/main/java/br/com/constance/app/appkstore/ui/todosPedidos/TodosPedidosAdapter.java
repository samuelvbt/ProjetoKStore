package br.com.constance.app.appkstore.ui.todosPedidos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.appkstore.R;

import java.util.List;

import br.com.constance.app.appkstore.backend.controller.ParceiroController;
import br.com.constance.app.appkstore.backend.controller.PedidoController;
import br.com.constance.app.appkstore.backend.services.pojo.ParceiroPOJO;
import br.com.constance.app.appkstore.backend.services.pojo.PedidoInterfacePOJO;

public class TodosPedidosAdapter extends ArrayAdapter<PedidoInterfacePOJO> {
    Bundle bundlePedido;
    PedidoController pedidoController;
    ParceiroController parceiroController;
    boolean produtoNoPedido = false;
    public TodosPedidosAdapter(Context context, List<PedidoInterfacePOJO> pedidos) {
        super(context, 0, pedidos);
       // this.bundlePedido=bundle;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        PedidoInterfacePOJO pedido = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        pedidoController = new PedidoController(getContext());
        parceiroController = new ParceiroController(getContext());
        ParceiroPOJO parceiro = new ParceiroPOJO();
        parceiro = parceiroController.buscarParceiroPorId(pedido.getCodigoParceiro());

        TextView titleTextView = convertView.findViewById(R.id.itemTitle);
        TextView subtitleTextView = convertView.findViewById(R.id.itemSubtitle);


        titleTextView.setText(pedido.getIdPedido()+"\n"+pedido.getCodigoParceiro()+" - "+parceiro.getNomeParceiro());
        subtitleTextView.setText("Sincronização: "+pedido.getPedidoSincronizado()+"  Finalizado: "+pedido.getPedidoFinalizado());

        return convertView;
    }


}
