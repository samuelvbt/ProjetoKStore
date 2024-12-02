package br.com.constance.app.appkstore.backend.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.appkstore.R;
import com.google.android.material.snackbar.Snackbar;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.constance.app.appkstore.backend.dao.ParceiroDao;
import br.com.constance.app.appkstore.backend.services.parceiro.ParceiroService;
import br.com.constance.app.appkstore.backend.services.pojo.ParceiroPOJO;

public class ParceiroController extends AsyncTask<Void,Void, String>{

    private ParceiroPOJO[] listaParceiroPOJO;
    private ParceiroDao parceiroDao;
    private ParceiroService parceiroService;
    private Context context;
    private View view;


    public ParceiroController(Context context, View view) {
        this.context = context;
        this.view = view;
        parceiroDao = new ParceiroDao(context);
        parceiroService = new ParceiroService(context);
    }

    public ParceiroController(Context context) {
        this.context = context;
        parceiroDao = new ParceiroDao(context);
        parceiroService = new ParceiroService(context);
    }

    public ArrayList<ParceiroPOJO> buscarTodosParceirosLocal(){
        parceiroDao.open();
       ArrayList<ParceiroPOJO> parceiroPOJOS = parceiroDao.buscarTodosParceiros();
        parceiroDao.close();
        return parceiroPOJOS;
    }

    public ArrayList<ParceiroPOJO> buscarTodosParceirosPorDescricao(String descricao) throws Exception {
        return parceiroDao.buscarParceirosPorDescricao(descricao);
    }

    public ParceiroPOJO buscarParceiroPorId(BigDecimal codigoParceiro){
        return parceiroDao.buscarParceiroPorId(codigoParceiro);
    }

    public void carregarBaseParceiros() throws Exception {
        limparDadosTabela();
        buscarDadosParceiros();
        persistirDadosParceiro();
    }

    private void limparDadosTabela() {
        parceiroDao.open();
        boolean tabelaParceiroPopulada = parceiroDao.isTabelaParceiroPopulada();

        if(tabelaParceiroPopulada){
            parceiroDao.limparTabelaParceiro();
        }

        parceiroDao.close();
    }

    private void buscarDadosParceiros() throws Exception {
        parceiroService.obterDadosParceiro();
        this.listaParceiroPOJO=parceiroService.getListaParceiroPOJO();
    }

    private void persistirDadosParceiro() {
        parceiroDao.open();
        for (ParceiroPOJO parceiroPOJO : listaParceiroPOJO) {
            parceiroDao.salvarParceiro(parceiroPOJO);
        }
        parceiroDao.close();
    }


    @Override
    protected String doInBackground(Void... voids) {
        try {
            carregarBaseParceiros();
            return "Dados do Parceiro baixados com sucesso";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    protected void onPostExecute(String s) {
        Snackbar.make(view,s, Snackbar.LENGTH_SHORT).show();
        ProgressBar progressBar =view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }
}
