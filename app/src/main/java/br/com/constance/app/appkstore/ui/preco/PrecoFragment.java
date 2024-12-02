package br.com.constance.app.appkstore.ui.preco;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appkstore.R;
import com.example.appkstore.databinding.FragmentProdutoBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import br.com.constance.app.appkstore.SessionManager;
import br.com.constance.app.appkstore.backend.controller.ProdutoController;
import br.com.constance.app.appkstore.backend.services.pojo.ProdutoPOJO;
import br.com.constance.app.appkstore.dataBase.DataBaseHelper;
import br.com.constance.app.appkstore.ui.produto.ProdutoViewModel;

public class PrecoFragment extends Fragment {

    private FragmentProdutoBinding binding;
    private static final int REQUEST_CODE_PERMISSION = 101;
    private Button startScanButton;
    EditText editText;
    ListView listView;
    DataBaseHelper dbHelper;
    Bundle bundle;
    private List<ProdutoPOJO> produtosAll;
    ProdutoController produto;
    private RadioGroup radioGroup;
    View root;

    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SessionManager sessionManager = SessionManager.getInstance(getContext());
        boolean isLoggedIn = sessionManager.isLoggedIn();
        ProdutoViewModel produtoViewModel =
                new ViewModelProvider(this).get(ProdutoViewModel.class);

        root = inflater.inflate(R.layout.modal, container, false);
        listView = root.findViewById(R.id.listaProduto);
        dbHelper = new DataBaseHelper(getActivity());
        startScanButton = root.findViewById(R.id.button_scanner);
        editText = root.findViewById(R.id.searchProduto);
        produto = new ProdutoController(getContext());
        // produtosAll = produto.buscarTodosProdutosLocal();
        bundle = getArguments();
        startScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        editText.requestFocus();
        editText.setSelection(editText.getText().length());


        editText.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                RadioButton radioButton = root.findViewById(R.id.radioButton1);
                                                String searchText = s.toString().trim();
                                                if (radioButton.isChecked()) {
                                                    try {
                                                        pesquisarPorCodigo(searchText);
                                                    } catch (Exception e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                } else {
                                                    if (searchText.length() >= 4) {
                                                        try {
                                                            pesquisarPorDigitação(searchText);
                                                        } catch (Exception e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    } else {
                                                        limpaLista(searchText);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {

                                            }
                                        }

        );
        return root;
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION);
        } else {
            initBarcodeScanner();
        }
    }

    private void initBarcodeScanner() {
        // Inicializar o leitor de código de barras
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setPrompt("Aponte a câmera para o código de barras");
        integrator.setBeepEnabled(true); // Habilitar som de beep ao ler um código
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida, inicializar o leitor de código de barras
                initBarcodeScanner();
            } else {
                // Permissão negada, exibir mensagem de erro
                Toast.makeText(requireContext(), "Permissão de câmera necessária para ler códigos de barras", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(requireContext(), "Leitura cancelada", Toast.LENGTH_LONG).show();
            } else {
                String barcode = result.getContents();
                editText.setText(barcode);
                Log.d("Barcode", barcode);
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void pesquisarPorCodigo(String search) throws Exception {
        List<ProdutoPOJO> listaProduto = new ArrayList<>();
        listaProduto = produto.buscarProdutosPorCodigoBarras(search);

        PrecoAdapter adapter = new PrecoAdapter(getContext(), listaProduto, bundle, this);
        listView.setAdapter(adapter);
    }

    public void pesquisarPorDigitação(String search) throws Exception {
        List<ProdutoPOJO> listaProduto = new ArrayList<>();
        listaProduto = produto.buscarProdutosPorDescricao(search);

        PrecoAdapter adapter = new PrecoAdapter(getContext(), listaProduto, bundle, this);
        listView.setAdapter(adapter);
    }

    public void limpaLista(String search) {
        List<ProdutoPOJO> listaProduto = new ArrayList<>();
        PrecoAdapter adapter = new PrecoAdapter(getContext(), listaProduto, bundle, this);
        listView.setAdapter(adapter);
    }
}