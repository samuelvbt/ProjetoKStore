package br.com.constance.app.appkstore.ui.adicionarPedido;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appkstore.R;
import com.example.appkstore.databinding.FragmentAdicionarPedidoBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.constance.app.appkstore.backend.controller.PedidoController;
import br.com.constance.app.appkstore.backend.services.pojo.ItemPedidoInterfacePOJO;
import br.com.constance.app.appkstore.backend.services.pojo.ParceiroPOJO;
import br.com.constance.app.appkstore.backend.services.pojo.PedidoInterfacePOJO;
import br.com.constance.app.appkstore.ui.preco.PrecoDialogFragment;

public class AdicionarPedidoFragment extends Fragment {

    private FragmentAdicionarPedidoBinding binding;
    private PedidoController pedidoController;
    private List<PedidoInterfacePOJO> pedidosParceiro;
    private AdicionarPedidoAdapter adicionarPedidoAdapter;
    private ListView listview;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_adicionar_pedido, container, false);
        Bundle bundle = getArguments();
        ParceiroPOJO parceiroSelecionado = new ParceiroPOJO();
        parceiroSelecionado.setCodigoParceiro(new BigDecimal(bundle.getString("codigoParceiro")));
        parceiroSelecionado.setCnpj(bundle.getString("cnpj"));
        parceiroSelecionado.setRazaoSocial(bundle.getString("razaoSocial"));
        parceiroSelecionado.setNomeParceiro(bundle.getString("nomeParceiro"));
        parceiroSelecionado.setCidadeParceiro(bundle.getString("cidade"));
        parceiroSelecionado.setBairroParceiro(bundle.getString("bairro"));
        pedidoController = new PedidoController(getContext());



        TextView  labelCliente = root.findViewById(R.id.cliente);
        labelCliente.setText("CNPJ: "+ parceiroSelecionado.getCnpj()+"\n"+
                            "Razão Social: "+parceiroSelecionado.getRazaoSocial()+"\n"+
                            "Código Sankhya: "+parceiroSelecionado.getCodigoParceiro()+"\n"+
                            "Bairro: "+parceiroSelecionado.getBairroParceiro()+"  Cidade:" + parceiroSelecionado.getCidadeParceiro()+"\n");

        pedidosParceiro = pedidoController.buscarPedidosPorParceiro(parceiroSelecionado.getCodigoParceiro());
        listview = root.findViewById(R.id.itens);
        adicionarPedidoAdapter= new AdicionarPedidoAdapter(getActivity(),pedidosParceiro);
        listview.setAdapter(adicionarPedidoAdapter);




        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PedidoInterfacePOJO novoPedidoPojo = new PedidoInterfacePOJO();
                novoPedidoPojo.setCodigoParceiro(parceiroSelecionado.getCodigoParceiro());
                novoPedidoPojo.setDataNegociacao(System.currentTimeMillis());
                ArrayList<ItemPedidoInterfacePOJO> itensPedidoPojo = new ArrayList<>();
                novoPedidoPojo.setItemPedidoInterfacePOJO(itensPedidoPojo);
                PedidoController pedidoController = new PedidoController(getContext());
                PedidoInterfacePOJO pedidoInterfacePOJOSalvo = pedidoController.salvarCabecalhoPedido(novoPedidoPojo);


                Bundle bundle = new Bundle();
                bundle.putString("idPedido",pedidoInterfacePOJOSalvo.getIdPedido());
                bundle.putString("codigoParceiro",pedidoInterfacePOJOSalvo.getCodigoParceiro().toString());
                NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                NavController navController = navHostFragment.getNavController();
//                navController.navigate(R.id.nav_pedido,bundle);
            }
        });

        Button button = root.findViewById(R.id.preco);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrecoDialogFragment precoDialog = new PrecoDialogFragment();
                precoDialog.show(getParentFragmentManager(), "precoDialog");
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
//    private void openPriceModal() {
//            // Cria uma nova instância do PrecoFragment
//            PrecoFragment precoFragment = new PrecoFragment();
//
//            // Obtém o FragmentManager
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//
//            // Inicia a transação
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//
//            // Adiciona o fragmento na transação (você pode usar replace se preferir substituir um fragmento existente)
//            transaction.replace(R.id., precoFragment); // Substitua 'fragment_container' pelo ID do seu contêiner de fragmento
//
//            // Adiciona a transação à pilha de retorno, se necessário
//            transaction.addToBackStack(null);
//
//            // Finaliza a transação
//            transaction.commit();
//        }
//

//        // Configura os elementos dentro do modal
//        EditText editTextProductCode = dialog.findViewById(R.id.editTextProductCode);
//        Button buttonCheckPrice = dialog.findViewById(R.id.buttonCheckPrice);

        // Exibe o modal

}
