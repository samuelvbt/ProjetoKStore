package br.com.constance.app.appkstore.ui.sincroniza;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appkstore.R;
import com.example.appkstore.databinding.FragmentSincronizaBinding;

import br.com.constance.app.appkstore.SessionManager;
import br.com.constance.app.appkstore.backend.controller.ImagemController;
import br.com.constance.app.appkstore.backend.controller.ParceiroController;
import br.com.constance.app.appkstore.backend.controller.PedidoController;
import br.com.constance.app.appkstore.backend.controller.ProdutoController;
import br.com.constance.app.appkstore.backend.controller.TestaConexaoController;
import br.com.constance.app.appkstore.backend.controller.TipoNegociacaoController;

public class SincronizaFragment extends Fragment {

    private FragmentSincronizaBinding binding;
    private Button dadosProduto;
    private Button dadosParceiro;
    private Button dadosNegociacao;
    private Button enviarPedidos;
    private Button buttonTeste;
    private ProdutoController produtoController;
    private ParceiroController parceiroController;
    private TipoNegociacaoController tipoNegociacaoController;
    private TestaConexaoController testaConexaoController;
    private PedidoController pedidoController;
    private ImagemController imagemController;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SessionManager sessionManager = SessionManager.getInstance(getContext());
        hideKeyboard();
        // Inflar o layout usando databinding
        binding = FragmentSincronizaBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        View headerView = inflater.inflate(R.layout.nav_header_main, container, false);
        dadosProduto = root.findViewById(R.id.produto);
        dadosParceiro = root.findViewById(R.id.cliente);
        //dadosNegociacao = root.findViewById(R.id.negociacao);
        enviarPedidos= root.findViewById(R.id.pedido);
       // buttonTeste=root.findViewById(R.id.buttonTeste);
        boolean isLoggedIn = sessionManager.isLoggedIn();
        if (!isLoggedIn) {
            NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
            NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.nav_login);
        }

        dadosProduto.setOnClickListener(v -> {
            ProgressBar progressBar = root.findViewById(R.id.progressBar);
            String response = null;
            produtoController = new ProdutoController(getContext(),root);
            imagemController = new ImagemController(getContext(),root);
            try {
                progressBar.setVisibility(View.VISIBLE);
                sessionManager.login(root);
                 response= String.valueOf(produtoController.execute());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            });
        dadosParceiro.setOnClickListener(v -> {
            ProgressBar progressBar = root.findViewById(R.id.progressBar);
            String response = null;
            parceiroController = new ParceiroController(getContext(),root);
            try {
                sessionManager.login(root);
                progressBar.setVisibility(View.VISIBLE);
                parceiroController.execute();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        enviarPedidos.setOnClickListener(v -> {
            ProgressBar progressBar = root.findViewById(R.id.progressBar);
            pedidoController = new PedidoController(getContext(),root);
            try {
                sessionManager.login(root);
                progressBar.setVisibility(View.VISIBLE);
                pedidoController.execute();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });



        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void hideKeyboard() {
        // Obtenha o InputMethodManager
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        // Encontre a View que está atualmente focada
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            // Esconder o teclado
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}