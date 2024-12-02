package br.com.constance.app.appkstore.backend.services.negociacao;

import android.content.Context;

import com.google.gson.Gson;

import br.com.constance.app.appkstore.SessionManager;
import br.com.constance.app.appkstore.backend.services.pojo.TipoNegociacaoPOJO;
import br.com.constance.app.appkstore.backend.util.WebServiceDecorator;

public class TiposNegociacaoService {
    private WebServiceDecorator webServiceDecorator;
    private TipoNegociacaoPOJO[] listaTipoNegociacaoPOJO;
    private Context context;
    private Gson gson;
    private String jSessionId;
    private String host;
    private String uri = "/apictc/v1/appkstore/tiponegociacao";

    public TiposNegociacaoService(Context context) {
        this.context = context;
    }

    public void obterDadosTipoNegociacao() throws Exception {
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
        listaTipoNegociacaoPOJO = gson.fromJson(responseBody, TipoNegociacaoPOJO[].class);
    }

    public TipoNegociacaoPOJO[] getListaTipoNegociacaoPOJO() {
        return listaTipoNegociacaoPOJO;
    }

}
