package br.com.constance.app.appkstore.backend.services.produto;

import android.content.Context;

import com.google.gson.Gson;

import br.com.constance.app.appkstore.SessionManager;
import br.com.constance.app.appkstore.backend.services.pojo.ProdutoPOJO;
import br.com.constance.app.appkstore.backend.util.WebServiceDecorator;

public class ProdutoService {

    private Context context;
    private ProdutoPOJO[] listaProdutoPOJO;
    private String host;
    private String uri = "/apictc/v1/appkstore/produto";
    private String jSessionId;
    private WebServiceDecorator webServiceDecorator;
    private Gson gson;

    public ProdutoService(Context context) {
        this.context = context;
    }

    public void obterDadosProduto() throws Exception {
        try{
            carregarDadosSessao();
        }catch (Exception e){
            throw new Exception("Não foi possível obter os dados da sessão.");
        }

        try{
            buscarDados();
        }catch (Exception e){
            if(webServiceDecorator.getResponseCode() == 400){
                throw new Exception("BadRequest {}: " + e.getMessage());
            }

            if(webServiceDecorator.getResponseCode() == 401){
                throw new Exception("Unauthorized");
            }
        }

    }

    private void carregarDadosSessao() {
        SessionManager sessionManagerInstance = SessionManager.getInstance(this.context);
        jSessionId = sessionManagerInstance.getSessionId();
    }

    private void buscarDados() throws Exception {
        webServiceDecorator = new WebServiceDecorator();
        webServiceDecorator.setParametro("mgeSession", jSessionId);
        String responseBody = webServiceDecorator.getString(uri);
        gson = new Gson();
        listaProdutoPOJO = gson.fromJson(responseBody, ProdutoPOJO[].class);
    }

    public ProdutoPOJO[] getListaProdutoPOJO() {
        return listaProdutoPOJO;
    }
}