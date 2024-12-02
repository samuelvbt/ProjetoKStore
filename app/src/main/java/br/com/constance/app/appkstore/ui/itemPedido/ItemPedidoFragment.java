package br.com.constance.app.appkstore.ui.itemPedido;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appkstore.R;
import android.app.Dialog;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.constance.app.appkstore.SessionManager;
import br.com.constance.app.appkstore.backend.controller.PedidoController;
import br.com.constance.app.appkstore.backend.dao.ImagemDao;
import br.com.constance.app.appkstore.backend.services.pojo.ItemPedidoInterfacePOJO;
import br.com.constance.app.appkstore.backend.services.pojo.ProdutoPOJO;

public class ItemPedidoFragment extends Fragment {
    EditText editText1;
    EditText editText33;
    EditText editText34;
    EditText editText35;
    EditText editText36;
    EditText editText37;
    EditText editText38;
    EditText editText39;
    EditText editText40;
    EditText editText41;
    EditText editText42;
    EditText editTextP;
    EditText editTextM;
    EditText editTextG;
    Button adicionarProdutoPedido;
    ProdutoPOJO produtoSelecionado;
    NavController navController;
    NavHostFragment navHostFragment;
    Bundle bundle;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SessionManager sessionManager = SessionManager.getInstance(getContext());
        boolean isLoggedIn = sessionManager.isLoggedIn();
        navHostFragment = new NavHostFragment();
        navController = new NavController(getContext());
        if (!isLoggedIn) {
            navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
            navController = navHostFragment.getNavController();
            navController.navigate(R.id.nav_login);
        }
        ItemPedidoViewModel itemPedidoViewModel =
                new ViewModelProvider(this).get(ItemPedidoViewModel.class);

        View root = inflater.inflate(R.layout.fragment_item_pedido, container, false);
        editText1 = root.findViewById(R.id.quantity_1);
        editText33 = root.findViewById(R.id.quantity_33);
        editText34 = root.findViewById(R.id.quantity_34);
        editText35 = root.findViewById(R.id.quantity_35);
        editText36 = root.findViewById(R.id.quantity_36);
        editText37 = root.findViewById(R.id.quantity_37);
        editText38 = root.findViewById(R.id.quantity_38);
        editText39 = root.findViewById(R.id.quantity_39);
        editText40 = root.findViewById(R.id.quantity_40);
        editText41 = root.findViewById(R.id.quantity_41);
        editText42 = root.findViewById(R.id.quantity_42);
        editTextP = root.findViewById(R.id.quantity_p);
        editTextM = root.findViewById(R.id.quantity_m);
        editTextG = root.findViewById(R.id.quantity_g);
        adicionarProdutoPedido = root.findViewById(R.id.button_adicionar);
        bundle = new Bundle();
        bundle = getArguments();
        produtoSelecionado = new ProdutoPOJO();
        produtoSelecionado.setCodigoProduto(new BigDecimal(bundle.getString("codigoProduto")));
        produtoSelecionado.setDescricaoProduto(bundle.getString("descricaoProduto"));
        produtoSelecionado.setReferencia(bundle.getString("referencia"));
        produtoSelecionado.setPreco(new BigDecimal(bundle.getString("preco")));
        produtoSelecionado.setListaGrade(bundle.getStringArrayList("listaGrade"));
      //  produtoSelecionado.setImagem(bundle.getByteArray("imagem"));

        if(("S").equals(bundle.getString("sincronizar"))){
            adicionarProdutoPedido.setEnabled(false);
            desativaEdicaoTodasGrades();
        }
        ImagemDao imagemDao = new ImagemDao(getContext());
        ImageView imagemProduto = root.findViewById(R.id.foto_produto);
        try {
            imagemDao.open();
            byte[] imagem = imagemDao.buscaIimagemPorId(produtoSelecionado.getCodigoProduto());
            imagemDao.close();
            if(imagem !=null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(imagem, 0,imagem.length);

                imagemProduto.setImageBitmap(bitmap);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        TextView descricao = root.findViewById(R.id.descr);
        descricao.setText(produtoSelecionado.getCodigoProduto().toString()+ " - " + produtoSelecionado.getDescricaoProduto());
        TextView referencia = root.findViewById(R.id.ref);
        referencia.setText(produtoSelecionado.getReferencia());
        TextView preco = root.findViewById(R.id.preco);

        imagemProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              try {
                    imagemDao.open();
                    byte[] imagem = imagemDao.buscaIimagemPorId(produtoSelecionado.getCodigoProduto());
                    imagemDao.close();
                    if(imagem !=null){
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imagem, 0,imagem.length);
                        showFullScreenImage(bitmap);
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        });





        try {
            preco.setText(adicionarMascaraMonetaria(produtoSelecionado.getPreco().toString()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if(bundle.containsKey("atualizar") && bundle.getString("atualizar").equals("S")){
            try {
                setaGrade(produtoSelecionado.getCodigoProduto());
                somaQtdItens(root);
                calculaValor(root);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        desativaEdicao(produtoSelecionado);


        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                somaQtdItens(root);
                try {
                    calculaValor(root);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        editText33.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                somaQtdItens(root);
                    try {
        calculaValor(root);
    } catch (ParseException e) {
        e.printStackTrace();
    };

            }
        });
        editText34.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                somaQtdItens(root);
                    try {
        calculaValor(root);
    } catch (ParseException e) {
        e.printStackTrace();
    };

            }
        });
        editText35.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                somaQtdItens(root);
                    try {
        calculaValor(root);
    } catch (ParseException e) {
        e.printStackTrace();
    };

            }
        });
        editText36.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                somaQtdItens(root);
                    try {
        calculaValor(root);
    } catch (ParseException e) {
        e.printStackTrace();
    };

            }
        });
        editText37.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                somaQtdItens(root);
                    try {
        calculaValor(root);
    } catch (ParseException e) {
        e.printStackTrace();
    };

            }
        });
        editText38.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                somaQtdItens(root);
                    try {
        calculaValor(root);
    } catch (ParseException e) {
        e.printStackTrace();
    };

            }
        });
        editText39.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                somaQtdItens(root);
                    try {
        calculaValor(root);
    } catch (ParseException e) {
        e.printStackTrace();
    };

            }
        });
        editText40.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                somaQtdItens(root);
                    try {
        calculaValor(root);
    } catch (ParseException e) {
        e.printStackTrace();
    };

            }
        });
        editText41.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                somaQtdItens(root);
                    try {
        calculaValor(root);
    } catch (ParseException e) {
        e.printStackTrace();
    };

            }
        });
        editText42.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                somaQtdItens(root);
                    try {
        calculaValor(root);
    } catch (ParseException e) {
        e.printStackTrace();
    };

            }
        });
        editTextP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                somaQtdItens(root);
                    try {
        calculaValor(root);
    } catch (ParseException e) {
        e.printStackTrace();
    };
            }
        });
        editTextM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                somaQtdItens(root);
                    try {
        calculaValor(root);
    } catch (ParseException e) {
        e.printStackTrace();
    };
            }
        });
        editTextG.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                somaQtdItens(root);
                    try {
        calculaValor(root);
    } catch (ParseException e) {
        e.printStackTrace();
    };
            }
        });
        adicionarProdutoPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemPedidoInterfacePOJO item = new ItemPedidoInterfacePOJO();
                item.setIdPedido(bundle.getString("idPedido"));
                item.setCodigoProduto(produtoSelecionado.getCodigoProduto());
                item.setPrecoUnitarioProduto(produtoSelecionado.getPreco());
                item.setGrade(preencheGrade());
                PedidoController pedido = new PedidoController(getContext());
                if(bundle.containsKey("atualizar") && bundle.getString("atualizar").equals("S")){
                    try {
                        pedido.alterarRemoverItensDoPedidoPorSku(item);
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    try {
                        pedido.salvarItensPedido(item);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    if(bundle.containsKey("tela")&&bundle.getString("tela").equals("consultaPreco")){
                        fragmentManager.popBackStack();
                    }
                    else{
                        fragmentManager.popBackStack();
                        fragmentManager.popBackStack();
                    }

                }

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void somaQtdItens(View root){


        TextView textViewQtdItens = root.findViewById(R.id.qtd_itens);
        int somaItens =0;
        somaItens += parseEditTextToInt(editText1);
        somaItens += parseEditTextToInt(editText33);
        somaItens += parseEditTextToInt(editText34);
        somaItens += parseEditTextToInt(editText35);
        somaItens += parseEditTextToInt(editText36);
        somaItens += parseEditTextToInt(editText37);
        somaItens += parseEditTextToInt(editText38);
        somaItens += parseEditTextToInt(editText39);
        somaItens += parseEditTextToInt(editText40);
        somaItens += parseEditTextToInt(editText41);
        somaItens += parseEditTextToInt(editText42);
        somaItens += parseEditTextToInt(editTextP);
        somaItens += parseEditTextToInt(editTextM);
        somaItens += parseEditTextToInt(editTextG);

        textViewQtdItens.setText(String.valueOf(somaItens));
    }

    private int parseEditTextToInt(EditText editText) {
        String text = editText.getText().toString();
        return text.isEmpty() ? 0 : Integer.parseInt(text);
    }

    private void calculaValor(View root)  throws ParseException {
        TextView textViewQtdItens = root.findViewById(R.id.qtd_itens);
        TextView textViewTotal = root.findViewById(R.id.valor);
        TextView textViewPreco = root.findViewById(R.id.preco);
        String precoString = removerMascaraMonetaria(textViewPreco.getText().toString()).replace(",", ".");
        BigDecimal qtdItens = new BigDecimal(textViewQtdItens.getText().toString());
        BigDecimal preco = new BigDecimal(precoString);
        textViewTotal.setText(adicionarMascaraMonetaria(String.valueOf(qtdItens.multiply(preco))));
    }
    private void desativaEdicao(ProdutoPOJO produtoSelecionado) {
        ArrayList<String> listaGrade = produtoSelecionado.getListaGrade();
        int corCinza = getResources().getColor(android.R.color.darker_gray);
        if (!listaGrade.contains("33")) {
            editText33.setEnabled(false);
            editText33.setBackgroundColor(corCinza);
        }
        if (!listaGrade.contains("34")) {
            editText34.setEnabled(false);
            editText34.setBackgroundColor(corCinza);
        }
        if (!listaGrade.contains("35")) {
            editText35.setEnabled(false);
            editText35.setBackgroundColor(corCinza);
        }
        if (!listaGrade.contains("36")) {
            editText36.setEnabled(false);
            editText36.setBackgroundColor(corCinza);
        }
        if (!listaGrade.contains("37")) {
            editText37.setEnabled(false);
            editText37.setBackgroundColor(corCinza);
        }
        if (!listaGrade.contains("38")) {
            editText38.setEnabled(false);
            editText38.setBackgroundColor(corCinza);
        }
        if (!listaGrade.contains("39")) {
            editText39.setEnabled(false);
            editText39.setBackgroundColor(corCinza);
        }
        if (!listaGrade.contains("40")) {
            editText40.setEnabled(false);
            editText40.setBackgroundColor(corCinza);
        }
        if (!listaGrade.contains("41")) {
            editText41.setEnabled(false);
            editText41.setBackgroundColor(corCinza);
        }
        if (!listaGrade.contains("42")) {
            editText42.setEnabled(false);
            editText42.setBackgroundColor(corCinza);
        }
        if (!listaGrade.contains("P")) {
            editTextP.setEnabled(false);
            editTextP.setBackgroundColor(corCinza);
        }
        if (!listaGrade.contains("M")) {
            editTextM.setEnabled(false);
            editTextM.setBackgroundColor(corCinza);
        }
        if (!listaGrade.contains("G")) {
            editTextG.setEnabled(false);
            editTextG.setBackgroundColor(corCinza);
        }
        if (!listaGrade.contains("1")) {
            editText1.setEnabled(false);
            editText1.setBackgroundColor(corCinza);
        }
    }
    private static String adicionarMascaraMonetaria(String valor) throws ParseException {
        // Converte a string para um número
       double valorNumerico = Double.parseDouble(valor.replace(",", "."));

        // Formata o valor numérico como moeda brasileira
        NumberFormat formatador = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return formatador.format(valorNumerico);
    }
    private static String removerMascaraMonetaria(String valorMonetario) throws ParseException {
        NumberFormat formatador = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        Number numero = formatador.parse(valorMonetario);
        return numero.toString().replace(".", ",");
    }
    private Map<String,BigDecimal> preencheGrade(){
        Map<String,BigDecimal> grade = new HashMap<>();
        grade.put("33",new BigDecimal(editText33.getText().toString().isEmpty() ? "0" : editText33.getText().toString()));
        grade.put("34",new BigDecimal(editText34.getText().toString().isEmpty() ? "0" : editText34.getText().toString()));
        grade.put("35",new BigDecimal(editText35.getText().toString().isEmpty() ? "0" : editText35.getText().toString()));
        grade.put("36",new BigDecimal(editText36.getText().toString().isEmpty() ? "0" : editText36.getText().toString()));
        grade.put("37",new BigDecimal(editText37.getText().toString().isEmpty() ? "0" : editText37.getText().toString()));
        grade.put("38",new BigDecimal(editText38.getText().toString().isEmpty() ? "0" : editText38.getText().toString()));
        grade.put("39",new BigDecimal(editText39.getText().toString().isEmpty() ? "0" : editText39.getText().toString()));
        grade.put("40",new BigDecimal(editText40.getText().toString().isEmpty() ? "0" : editText40.getText().toString()));
        grade.put("41",new BigDecimal(editText41.getText().toString().isEmpty() ? "0" : editText41.getText().toString()));
        grade.put("42",new BigDecimal(editText42.getText().toString().isEmpty() ? "0" : editText42.getText().toString()));
        grade.put("1",new BigDecimal(editText1.getText().toString().isEmpty() ? "0" : editText1.getText().toString()));
        grade.put("P",new BigDecimal(editTextP.getText().toString().isEmpty() ? "0" : editTextP.getText().toString()));
        grade.put("M",new BigDecimal(editTextM.getText().toString().isEmpty() ? "0" : editTextM.getText().toString()));
        grade.put("G",new BigDecimal(editTextG.getText().toString().isEmpty() ? "0" : editTextG.getText().toString()));

        return grade;
    }

    private void setaGrade(BigDecimal codigoProduto) throws Exception {
        PedidoController pedidoController = new PedidoController(getContext());
        List<ItemPedidoInterfacePOJO> itemPedidoInterfacePOJOS = pedidoController.buscaItens(bundle.getString("idPedido"), new BigDecimal(bundle.getString("codigoProduto")));
        for (Map.Entry<String, BigDecimal> gradeQuantidade : itemPedidoInterfacePOJOS.get(0).getGrade().entrySet()) {
                if(gradeQuantidade.getKey().equals("33")){
                    editText33.setText(gradeQuantidade.getValue().toString());
                }
            if(gradeQuantidade.getKey().equals("34")){
                editText34.setText(gradeQuantidade.getValue().toString());
            }
            if(gradeQuantidade.getKey().equals("35")){
                editText35.setText(gradeQuantidade.getValue().toString());
            }
            if(gradeQuantidade.getKey().equals("36")){
                editText36.setText(gradeQuantidade.getValue().toString());
            }
            if(gradeQuantidade.getKey().equals("37")){
                editText37.setText(gradeQuantidade.getValue().toString());
            }
            if(gradeQuantidade.getKey().equals("38")){
                editText38.setText(gradeQuantidade.getValue().toString());
            }
            if(gradeQuantidade.getKey().equals("39")){
                editText39.setText(gradeQuantidade.getValue().toString());
            }
            if(gradeQuantidade.getKey().equals("40")){
                editText40.setText(gradeQuantidade.getValue().toString());
            }
            if(gradeQuantidade.getKey().equals("41")){
                editText41.setText(gradeQuantidade.getValue().toString());
            }
            if(gradeQuantidade.getKey().equals("42")){
                editText42.setText(gradeQuantidade.getValue().toString());
            }
            if(gradeQuantidade.getKey().equals("P")){
                editTextP.setText(gradeQuantidade.getValue().toString());
            }
            if(gradeQuantidade.getKey().equals("M")){
                editTextM.setText(gradeQuantidade.getValue().toString());
            }
            if(gradeQuantidade.getKey().equals("G")){
                editTextG.setText(gradeQuantidade.getValue().toString());
            }
            if(gradeQuantidade.getKey().equals("1")){
                editText1.setText(gradeQuantidade.getValue().toString());
            }

            }
        }

    private void desativaEdicaoTodasGrades() {
        int corCinza = getResources().getColor(android.R.color.darker_gray);

            editText33.setEnabled(false);
            editText33.setBackgroundColor(corCinza);


            editText34.setEnabled(false);
            editText34.setBackgroundColor(corCinza);


            editText35.setEnabled(false);
            editText35.setBackgroundColor(corCinza);


            editText36.setEnabled(false);
            editText36.setBackgroundColor(corCinza);


            editText37.setEnabled(false);
            editText37.setBackgroundColor(corCinza);


            editText38.setEnabled(false);
            editText38.setBackgroundColor(corCinza);


            editText39.setEnabled(false);
            editText39.setBackgroundColor(corCinza);


            editText40.setEnabled(false);
            editText40.setBackgroundColor(corCinza);


            editText41.setEnabled(false);
            editText41.setBackgroundColor(corCinza);


            editText42.setEnabled(false);
            editText42.setBackgroundColor(corCinza);


            editTextP.setEnabled(false);
            editTextP.setBackgroundColor(corCinza);


            editTextM.setEnabled(false);
            editTextM.setBackgroundColor(corCinza);


            editTextG.setEnabled(false);
            editTextG.setBackgroundColor(corCinza);


            editText1.setEnabled(false);
            editText1.setBackgroundColor(corCinza);

    }
    private void showFullScreenImage(Bitmap bitmap) {
        // Cria um Dialog para exibir a imagem em tela cheia
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_fullscreen_image);

        ImageView fullScreenImageView = dialog.findViewById(R.id.fullScreenImageView);
        fullScreenImageView.setImageBitmap(bitmap);

        dialog.show();
    }


}
