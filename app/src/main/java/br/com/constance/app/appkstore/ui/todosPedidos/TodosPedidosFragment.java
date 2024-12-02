package br.com.constance.app.appkstore.ui.todosPedidos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appkstore.R;
import com.example.appkstore.databinding.FragmentTodosPedidosBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import br.com.constance.app.appkstore.SessionManager;
import br.com.constance.app.appkstore.backend.controller.ParceiroController;
import br.com.constance.app.appkstore.backend.controller.PedidoController;
import br.com.constance.app.appkstore.backend.services.pojo.ParceiroPOJO;
import br.com.constance.app.appkstore.backend.services.pojo.PedidoInterfacePOJO;

public class TodosPedidosFragment extends Fragment {

    private FragmentTodosPedidosBinding binding;
    private TodosPedidosAdapter todosPedidosAdapter;
    private EditText editText;
//    private Button dadosProduto;
//    private Button dadosParceiro;
//    private Button dadosNegociacao;
//    private Button enviarPedidos;
//    private Button buttonTeste;
//    private ProdutoController produtoController;
//    private ParceiroController parceiroController;
//    private TipoNegociacaoController tipoNegociacaoController;
//    private TestaConexaoController testaConexaoController;
//    private PedidoController pedidoController;
    private View root;
    private ListView listview;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SessionManager sessionManager = SessionManager.getInstance(getContext());
        PedidoController pedidoController = new PedidoController(getContext());
        // Inflar o layout usando databinding
        binding = FragmentTodosPedidosBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        View headerView = inflater.inflate(R.layout.nav_header_main, container, false);
        boolean isLoggedIn = sessionManager.isLoggedIn();
        if (!isLoggedIn) {
            NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
            NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.nav_login);
        }
        RadioGroup radioGroupFilters = root.findViewById(R.id.radioGroupFilters);
        String[] options = {"Filtrar por Cliente","Finalizado", "Sincronizado","Não Finalizados","Não Sincronizados"};

        for (int i = 0; i < options.length; i++) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setId(i); // Define um ID único para cada RadioButton
            radioButton.setText(options[i]);
            radioGroupFilters.addView(radioButton);
        }
        editText= root.findViewById(R.id.search_pedido);
        listview = root.findViewById(R.id.todosPedidos);


        radioGroupFilters.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                List<PedidoInterfacePOJO> pedidoInterfacePOJOList = new ArrayList<>();
                switch (checkedId) {
                    case 0:
                        editText.setEnabled(true);
                        break;
                    case 1:
                        editText.setEnabled(false);
                        try {
                            pedidoInterfacePOJOList = pedidoController.buscarPedidosFinalizados();
                            todosPedidosAdapter= new TodosPedidosAdapter(getActivity(),pedidoInterfacePOJOList);
                            listview.setAdapter(todosPedidosAdapter);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case 2:
                        editText.setEnabled(false);
                        pedidoInterfacePOJOList = pedidoController.buscarPedidosSincronizados();
                        todosPedidosAdapter= new TodosPedidosAdapter(getActivity(),pedidoInterfacePOJOList);
                        listview.setAdapter(todosPedidosAdapter);
                        break;
                    case 3:
                        editText.setEnabled(false);
                        pedidoInterfacePOJOList = pedidoController.buscarPedidosNaoFinalizados();
                        todosPedidosAdapter= new TodosPedidosAdapter(getActivity(),pedidoInterfacePOJOList);
                        listview.setAdapter(todosPedidosAdapter);
                        break;
                    case 4:
                        editText.setEnabled(false);
                        pedidoInterfacePOJOList = pedidoController.buscarPedidosNaoSincronizados();
                        todosPedidosAdapter= new TodosPedidosAdapter(getActivity(),pedidoInterfacePOJOList);
                        listview.setAdapter(todosPedidosAdapter);
                        break;
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {


                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                List<PedidoInterfacePOJO> pedidoInterfacePOJOList = new ArrayList<>();
                                                int selectedId = radioGroupFilters.getCheckedRadioButtonId();

                                                if (selectedId == -1) {
                                                    Snackbar.make(root,"Selecione uma opção de filtro", Snackbar.LENGTH_SHORT).show();
                                                }
                                                if(selectedId==0) {
                                                    if(!s.toString().trim().equals("")){
                                                        ParceiroController parceiroController = new ParceiroController(getContext());
                                                        List<ParceiroPOJO> parceiroPOJOS = new ArrayList<>();
                                                        try {
                                                            parceiroPOJOS = parceiroController.buscarTodosParceirosPorDescricao(s.toString().trim());
                                                            List<PedidoInterfacePOJO> pedidosPorParceiro = new ArrayList<>();
                                                            for (ParceiroPOJO parceiro : parceiroPOJOS) {
                                                                pedidoInterfacePOJOList.addAll(pedidoController.buscarPedidosPorParceiro(parceiro.getCodigoParceiro()));
                                                            }
                                                            todosPedidosAdapter = new TodosPedidosAdapter(getActivity(), pedidoInterfacePOJOList);
                                                            listview.setAdapter(todosPedidosAdapter);
                                                        } catch (Exception e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }else{
                                                        pedidoInterfacePOJOList = new ArrayList<>();
                                                        todosPedidosAdapter = new TodosPedidosAdapter(getActivity(), pedidoInterfacePOJOList);
                                                        listview.setAdapter(todosPedidosAdapter);
                                                    }

                                                }
                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {

                                            }
        });






        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



}