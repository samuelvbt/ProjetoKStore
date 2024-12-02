package br.com.constance.app.appkstore.ui.preco;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.appkstore.R;
import com.example.appkstore.databinding.FragmentProdutoBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import br.com.constance.app.appkstore.backend.controller.ProdutoController;
import br.com.constance.app.appkstore.backend.services.pojo.ProdutoPOJO;
import br.com.constance.app.appkstore.dataBase.DataBaseHelper;
import br.com.constance.app.appkstore.ui.produto.ProdutoAdapter;

public class PrecoDialogFragment extends DialogFragment {

    private FragmentProdutoBinding binding;

    private EditText editText;
    private ListView listView;
    private DataBaseHelper dbHelper;
    private Bundle bundle;
    private List<ProdutoPOJO> produtosAll;
    private ProdutoController produto;
    private RadioGroup radioGroup;
    private View root;
    private static final int REQUEST_CODE_PERMISSION = 101;

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        root = inflater.inflate(R.layout.modal, null);

        listView = root.findViewById(R.id.listaProduto);
        dbHelper = new DataBaseHelper(getActivity());
        Button startScanButton = root.findViewById(R.id.button_scanner);
        editText = root.findViewById(R.id.searchProduto);
        produto = new ProdutoController(getContext());
        bundle = getArguments();

        startScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

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
            public void afterTextChanged(Editable s) {}
        });

        builder.setView(root)
                .setTitle("Consultar Preço")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PrecoDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            initBarcodeScanner();
        }
    }

    private void initBarcodeScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setPrompt("Aponte a câmera para o código de barras");
        integrator.setBeepEnabled(true);
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

        ProdutoAdapter adapter = new ProdutoAdapter(getContext(), listaProduto,bundle);
        listView.setAdapter(adapter);
    }

    public void pesquisarPorDigitação(String search) throws Exception {
        List<ProdutoPOJO> listaProduto = new ArrayList<>();
        listaProduto = produto.buscarProdutosPorDescricao(search);

        ProdutoAdapter adapter = new ProdutoAdapter(getContext(), listaProduto,bundle);
        listView.setAdapter(adapter);
    }

    public void limpaLista(String search) {
        List<ProdutoPOJO> listaProduto = new ArrayList<>();
        PrecoAdapter adapter = new PrecoAdapter(getContext(), listaProduto, bundle,this);
        listView.setAdapter(adapter);
    }
}
