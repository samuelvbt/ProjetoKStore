package br.com.constance.app.appkstore.ui.cliente;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appkstore.R;
import com.example.appkstore.databinding.FragmentClienteBinding;

import java.util.ArrayList;

import br.com.constance.app.appkstore.SessionManager;
import br.com.constance.app.appkstore.backend.controller.ParceiroController;
import br.com.constance.app.appkstore.backend.services.pojo.ParceiroPOJO;
import br.com.constance.app.appkstore.dataBase.DataBaseHelper;

public class ClienteFragment extends Fragment {

    EditText searchEditText;
    ListView listParceiro;
    DataBaseHelper dbHelper;
    private FragmentClienteBinding binding;
    private ParceiroController parceirosController;
    private ArrayList<ParceiroPOJO> parceirosAll;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SessionManager sessionManager = SessionManager.getInstance(getContext());
        boolean isLoggedIn = sessionManager.isLoggedIn();
        if (!isLoggedIn) {
            NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
            NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.nav_login);
        }

        ClienteViewModel clienteViewModel =
                new ViewModelProvider(this).get(ClienteViewModel.class);

        binding = FragmentClienteBinding.inflate(inflater, container, false);
        View rootView = inflater.inflate(R.layout.fragment_cliente, container, false);
        parceirosController = new ParceiroController(getContext());
        parceirosAll = parceirosController.buscarTodosParceirosLocal();
        searchEditText = rootView.findViewById(R.id.searchCliente);
        listParceiro = rootView.findViewById(R.id.lista_parceiros);


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim();
                    try {
                        performSearch(searchText);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        listParceiro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NavController navController = NavHostFragment.findNavController(ClienteFragment.this);
                ParceiroPOJO parceiroSelecionado = (ParceiroPOJO) parent.getItemAtPosition(position);

                Bundle bundle = new Bundle();
                bundle.putString("codigoParceiro",parceiroSelecionado.getCodigoParceiro().toString());
                bundle.putString("cnpj",parceiroSelecionado.getCnpj());
                bundle.putString("nomeParceiro",parceiroSelecionado.getNomeParceiro());
                bundle.putString("razaoSocial", parceiroSelecionado.getRazaoSocial());
                bundle.putString("cidade",parceiroSelecionado.getCidadeParceiro());
                bundle.putString("bairro",parceiroSelecionado.getBairroParceiro());
//                navController.navigate(R.id.nav_adicionar_pedido,bundle);

            }
        });

        return rootView;
    }

    private void performSearch(String searchText) throws Exception {
        ArrayList<ParceiroPOJO> parceiros = new ArrayList<>();
        if (searchText != null && !searchText.equals("")) {
            parceiros = parceirosController.buscarTodosParceirosPorDescricao(searchText);
        }
            ParceiroAdapter adapter = new ParceiroAdapter(getContext(), parceiros);
            listParceiro.setAdapter(adapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
