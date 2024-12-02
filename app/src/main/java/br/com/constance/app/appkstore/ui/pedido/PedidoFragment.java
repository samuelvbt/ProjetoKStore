package br.com.constance.app.appkstore.ui.pedido;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appkstore.R;
import com.example.appkstore.databinding.FragmentPedidoBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.constance.app.appkstore.Utils.Util;
import br.com.constance.app.appkstore.backend.controller.PedidoController;
import br.com.constance.app.appkstore.backend.controller.ProdutoController;
import br.com.constance.app.appkstore.backend.services.pojo.ItemPedidoInterfacePOJO;
import br.com.constance.app.appkstore.backend.services.pojo.PedidoPOJO;
import br.com.constance.app.appkstore.backend.services.pojo.ProdutoPOJO;
import br.com.constance.app.appkstore.ui.preco.PrecoDialogFragment;

public class PedidoFragment extends Fragment {

    private FragmentPedidoBinding binding;
    private PedidoPOJO pedidoSelecionadoPOJO ;
    private Button finalizarPedido;
    private ListView listaProdutos;
    private ListAdapter listAdapter;
    private ArrayList<ProdutoPOJO> produtosPedido;
    private RecyclerView recyclerView;
    private Bundle  bundlePedido;
    private PedidoController pedidoController;
    private List<ItemPedidoInterfacePOJO> itensPedido;
    private PedidoAdapter pedidoAdapter;
    TextView valorPedido;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_pedido, container, false);
        bundlePedido = getArguments();
        finalizarPedido = root.findViewById(R.id.finaliza_pedido);
        listaProdutos = root.findViewById(R.id.itens);
        FloatingActionButton fab = root.findViewById(R.id.fabPedido);
        ProdutoController produtoController = new ProdutoController(getContext());
        pedidoController = new PedidoController(getContext());

        if (bundlePedido != null) {
            pedidoSelecionadoPOJO = new PedidoPOJO();
            pedidoSelecionadoPOJO.setIdPedido(bundlePedido.getString("idPedido"));
            pedidoSelecionadoPOJO.setCodigoParceiro(new BigDecimal(bundlePedido.getString("codigoParceiro")));
        }
        if(("S").equals(bundlePedido.getString("sincronizar"))){
            fab.setEnabled(false);
            finalizarPedido.setEnabled(false);
        }
            TextView pedido;
            pedido = root.findViewById(R.id.pedido);

            valorPedido = root.findViewById(R.id.total);
        try {
            pedido.setText("ID PEDIDO: " + pedidoSelecionadoPOJO.getIdPedido() + "\n" +
                    "Cód. Parc.: " + pedidoSelecionadoPOJO.getCodigoParceiro());
            valorPedido.setText("VALOR TOTAL PEDIDO:\n"+adicionarMascaraMonetaria(pedidoController.obterTotalPedido(pedidoSelecionadoPOJO.getIdPedido()).toString())+"\nQtd. Itens:"+pedidoController.obterTotalItensPorPedido(pedidoSelecionadoPOJO.getIdPedido()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        itensPedido = new ArrayList<>();

        itensPedido = pedidoController.buscaProdutos(bundlePedido.getString("idPedido"));
        produtosPedido = new ArrayList<>();
        for (ItemPedidoInterfacePOJO item : itensPedido){
            try {
                produtosPedido.add(produtoController.obterDadosProdutoPorId(item.getCodigoProduto()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if(produtosPedido.size()>0){
            pedidoAdapter = new PedidoAdapter(getContext(),produtosPedido,bundlePedido,this );
            listaProdutos.setAdapter(pedidoAdapter);
        }




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("PedidoFragment", "FloatingActionButton clicado");
                NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                if (navHostFragment != null) {
                    NavController navController = navHostFragment.getNavController();
                    // incluir dados do pedido
                    Bundle pedido = new Bundle();
                    pedido.putString("idPedido",bundlePedido.getString("idPedido")); // Substitua por um valor real
                    pedido.putString("sincronizar",bundlePedido.getString("sincronizar"));
//                    navController.navigate(R.id.nav_produto, pedido);
                } else {
                    Log.e("PedidoFragment", "NavHostFragment is null");
                }
            }
        });


        finalizarPedido.setOnClickListener(v -> {
            NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
            if (navHostFragment != null) {
                NavController navController = navHostFragment.getNavController();
                Bundle bundle = new Bundle();
                bundle.putString("idPedido",bundlePedido.getString("idPedido"));
//                navController.navigate(R.id.nav_finaliza_pedido, bundle);
            } else {
                Log.e("PedidoFragment", "NavHostFragment is null");
            }
        });

        Button button = root.findViewById(R.id.preco);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("tela","pedido");
                bundle.putString("idPedido",bundlePedido.getString("idPedido")); // Substitua por um valor real
                bundle.putString("sincronizar",bundlePedido.getString("sincronizar"));
                PrecoDialogFragment precoDialog = new PrecoDialogFragment();
                precoDialog.setArguments(bundle);
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
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            produtosPedido.remove(position);
           // pedidoAdapter.notifyItemRemoved(position);
        }
    };
    private static String adicionarMascaraMonetaria(String valor) throws ParseException {
        // Converte a string para um número
        double valorNumerico = Double.parseDouble(valor.replace(",", "."));

        // Formata o valor numérico como moeda brasileira
        NumberFormat formatador = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return formatador.format(valorNumerico);
    }
    public void atualizarValorTotal() throws Exception {
        valorPedido.setText("VALOR TOTAL PEDIDO:\n"+ Util.adicionarMascaraMonetaria(pedidoController.obterTotalPedido(bundlePedido.getString("idPedido")).toString()));
    }

}
