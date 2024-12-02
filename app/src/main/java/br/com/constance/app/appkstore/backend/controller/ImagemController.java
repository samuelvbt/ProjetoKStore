package br.com.constance.app.appkstore.backend.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.appkstore.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import br.com.constance.app.appkstore.backend.dao.ProdutoDao;
import br.com.constance.app.appkstore.backend.services.pojo.ImagemPOJO;
import br.com.constance.app.appkstore.backend.services.pojo.ProdutoPOJO;

public class ImagemController extends AsyncTask<Void,Void,String> {

    private ArrayList<ImagemPOJO> listaImagemPOJO;

    private ProdutoDao produtoDao;

    private ArrayList<ProdutoPOJO> listaProdutoPOJO;
    private Context context;
    private View view;

    public ImagemController(Context context, View view){
        this.context = context;
        this.view = view;
        produtoDao= new ProdutoDao(context);
    }
    public void buscarTodosProdutosLocal(){
        produtoDao.open();
        listaProdutoPOJO = produtoDao.buscarTodosProduto();
        produtoDao.close();
    }
    @Override
    protected String doInBackground(Void... voids) {
        try {
            //carregarBaseImagens();
            return  "Dados da Imagem Sincronizadas";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Snackbar.make(view,s, Snackbar.LENGTH_SHORT).show();
        ProgressBar progressBar =view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }
}

