package br.com.constance.app.appkstore.backend.services.pedido;

import android.content.Context;

import com.google.gson.Gson;

import br.com.constance.app.appkstore.SessionManager;
import br.com.constance.app.appkstore.backend.services.pojo.PedidoPOJO;
import br.com.constance.app.appkstore.backend.util.WebServiceDecorator;

public class PedidoService {

    private WebServiceDecorator webServiceDecorator;
    private Context context;
    private Gson gson;
    private String host;
    private String uri = "/apictc/v1/appkstore/pedido";
    private String jSessionId;
    private PedidoPOJO pedidosPOJO;


    public PedidoService(Context context) {
        this.context = context;
    }

    public void setPedidosPOJO(PedidoPOJO pedidosPOJO) {
        this.pedidosPOJO = pedidosPOJO;
    }

    public void sincronizarPedidos() throws Exception {
        try{
            carregarDadosSessao();
        }catch (Exception e){
            throw new Exception("Não foi possível obter os dados da sessão.");
        }

        try{
            enviarPedidoParaErp();
        }catch (Exception e){
            throw new Exception(e);
        }
    }

    private void carregarDadosSessao() {
        SessionManager sessionManagerInstance = SessionManager.getInstance(this.context);
        jSessionId = sessionManagerInstance.getSessionId();
    }

    private void enviarPedidoParaErp() throws Exception {
        webServiceDecorator = new WebServiceDecorator();
        webServiceDecorator.setParametro("mgeSession", jSessionId);
        gson = new Gson();
        String requestBody = gson.toJson(this.pedidosPOJO);
        webServiceDecorator.setCorpoChamada(requestBody);
        webServiceDecorator.postString(uri);
    }

}
