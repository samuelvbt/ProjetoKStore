package br.com.constance.app.appkstore.backend.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.appkstore.R;
import com.google.android.material.snackbar.Snackbar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.constance.app.appkstore.backend.dao.ImagemDao;
import br.com.constance.app.appkstore.backend.dao.ProdutoDao;
import br.com.constance.app.appkstore.backend.services.imagem.ImagemService;
import br.com.constance.app.appkstore.backend.services.pojo.ImagemPOJO;
import br.com.constance.app.appkstore.backend.services.pojo.ProdutoBarraPOJO;
import br.com.constance.app.appkstore.backend.services.pojo.ProdutoPOJO;
import br.com.constance.app.appkstore.backend.services.produto.ProdutoService;

public class ProdutoController extends AsyncTask<Void,Void,String> {

    private ProdutoPOJO[] listaProdutoPOJO;
    private ProdutoDao produtoDao;
    private ProdutoService produtoService;
    private Context context;
    private View view;
    private ImagemDao imagemDao;
    private ImagemService imagemService;
    private ArrayList<ImagemPOJO> listaImagemPOJO;


    public ProdutoController(Context context, View view){
        this.context = context;
        this.view = view;
        produtoDao = new ProdutoDao(context);
        produtoService = new ProdutoService(context);
        imagemDao = new ImagemDao(context);
        imagemService = new ImagemService();
        listaImagemPOJO = new ArrayList<>();
    }
    public ProdutoController(Context context) {
        this.context = context;
        produtoDao = new ProdutoDao(context);
        produtoService = new ProdutoService(context);
    }

    public ProdutoPOJO obterDadosProdutoPorId(BigDecimal codigoProduto) throws Exception {
        ProdutoPOJO produtoPOJO = produtoDao.buscarProdutoPorId(codigoProduto);
        return produtoPOJO;
    }

    public ArrayList<ProdutoPOJO> buscarTodosProdutosLocal(){
        produtoDao.open();
        ArrayList<ProdutoPOJO> produtoPOJOS = produtoDao.buscarTodosProduto();
        produtoDao.close();
        return produtoPOJOS;
    }

    public void carregarBaseProdutos() throws Exception {
        limparDadosTabela();
            buscarDadosProduto();
        persistirDadosProduto();
    }

    private void limparDadosTabela() {
        produtoDao.open();
        boolean tabelaProdutoPopulada = produtoDao.isTabelaProdutoPopulada();
        boolean tabelaProdutoBarraPopulada = produtoDao.isTabelaProdutoBarraPopulada();

        if(tabelaProdutoPopulada){
            produtoDao.limparTabelaProduto();
        }

        if(tabelaProdutoBarraPopulada){
            produtoDao.limparTabelaBarras();
        }

        produtoDao.close();
    }

    private void buscarDadosProduto() throws Exception {
        produtoService.obterDadosProduto();
        listaProdutoPOJO = produtoService.getListaProdutoPOJO();
    }

    private void persistirDadosProduto() throws Exception {
        produtoDao.open();

        for (ProdutoPOJO produtoPOJO : listaProdutoPOJO) {
            produtoDao.salvarProduto(produtoPOJO);
            produtoDao.salvarCodigosBarra(produtoPOJO.getCodigoProduto(), produtoPOJO.getListaCodigosDeBarra());
          //  produtoDao.salvarImagem(produtoPOJO);
        }

        produtoDao.close();
    }

    public List<ProdutoPOJO> buscarProdutosPorDescricao(String descricao) throws Exception {
        return produtoDao.buscarProdutoPorDescricao(descricao);
    }

    public List<ProdutoPOJO> buscarProdutosPorCodigoBarras(String codigo) throws Exception {
        List<ProdutoPOJO> produtoPOJOList = new ArrayList<>();
        ProdutoBarraPOJO produtoBarraPOJO = produtoDao.buscarProdutoPorCodigoBarras(codigo);
        if(produtoBarraPOJO!=null){
            ProdutoPOJO produtoPOJO = produtoDao.buscarProdutoPorId(produtoBarraPOJO.getCodigoProduto());
            produtoPOJOList.add(produtoPOJO);
        }
        return produtoPOJOList;
    }
    public void carregarBaseImagens() throws Exception {
        limparDadosTabelaImagem();
        buscarImagens();
        persistirDadosImagens();
    }
    public void limparDadosTabelaImagem() {
        imagemDao.open();
        boolean tabelaProdutoPopulada = imagemDao.isTabelaProdutoPopulada();

        if(tabelaProdutoPopulada){
            imagemDao.limparTabelaProduto();
        }
        imagemDao.close();
    }

    private void buscarImagens() {
        for (ProdutoPOJO produtoPOJO : listaProdutoPOJO) {
            if(produtoPOJO.getUrlImagemProduto()!=null){
                ImagemPOJO imagemPOJO = new ImagemPOJO();
                imagemPOJO.setCodigoProduto(produtoPOJO.getCodigoProduto());
                imagemPOJO.setImagem(imagemService.buscaImagem(produtoPOJO.getUrlImagemProduto()));
                listaImagemPOJO.add(imagemPOJO);
            }
        }
    }
    private void persistirDadosImagens() throws Exception {
        imagemDao.open();
        for(ImagemPOJO imagem : listaImagemPOJO){

            imagemDao.salvarImagem(imagem);

        }
        imagemDao.close();
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            carregarBaseProdutos();
            carregarBaseImagens();
            return  "Dados do Produto Sincronizado";
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
