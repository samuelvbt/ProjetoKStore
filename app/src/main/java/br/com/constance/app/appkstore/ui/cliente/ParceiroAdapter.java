package br.com.constance.app.appkstore.ui.cliente;

import android.content.Context;
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

import java.util.ArrayList;

import br.com.constance.app.appkstore.backend.services.pojo.ParceiroPOJO;

public class ParceiroAdapter extends ArrayAdapter<ParceiroPOJO> {

    public ParceiroAdapter(Context context, ArrayList<ParceiroPOJO> parceiros) {
        super(context, 0, parceiros);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ParceiroPOJO parceiro = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        TextView titleTextView = convertView.findViewById(R.id.itemTitle);
        TextView subtitleTextView = convertView.findViewById(R.id.itemSubtitle);

        titleTextView.setText(parceiro.getNomeParceiro());
        subtitleTextView.setText(parceiro.getCnpj());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment navHostFragment = (NavHostFragment) ((AppCompatActivity) getContext()).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                NavController navController = navHostFragment.getNavController();
                ParceiroPOJO parceiro = getItem(position);

                Bundle bundle = new Bundle();
                bundle.putString("codigoParceiro", parceiro.getCodigoParceiro().toString());
                bundle.putString("cnpj", parceiro.getCnpj());
                bundle.putString("nomeParceiro", parceiro.getNomeParceiro());
                bundle.putString("razaoSocial", parceiro.getRazaoSocial());
                bundle.putString("cidade", parceiro.getCidadeParceiro());
                bundle.putString("bairro", parceiro.getBairroParceiro());
//                navController.navigate(R.id.nav_adicionar_pedido, bundle);
            }
        });

        return convertView;
    }


}
