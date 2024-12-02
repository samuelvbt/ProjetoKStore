package br.com.constance.app.appkstore.backend.services.parceiro;

import android.content.Context;

import com.google.gson.Gson;

import br.com.constance.app.appkstore.SessionManager;
import br.com.constance.app.appkstore.backend.services.pojo.ParceiroPOJO;
import br.com.constance.app.appkstore.backend.util.WebServiceDecorator;

public class ParceiroService {

    private WebServiceDecorator webServiceDecorator;
    private ParceiroPOJO[] listaParceiroPOJO;
    private Context context;
    private Gson gson;
    private String jSessionId;
    private String uri = "/apictc/v1/appkstore/parceiro";

    public ParceiroService(Context context) {
        this.context = context;

    }


    public void obterDadosParceiro() throws Exception {


        try {
            carregarDadosSessao();
            buscarDados();

        } catch (Exception e) {

            String errorMessage = null;
            if (webServiceDecorator != null) {
                int responseCode = webServiceDecorator.getResponseCode();
                if (responseCode == 400) {
                    errorMessage = "BadRequest: " + e.getMessage();
                } else if (responseCode == 401) {
                    errorMessage = "Unauthorized";
                }
            }
            throw new Exception(errorMessage);
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
        listaParceiroPOJO = gson.fromJson(responseBody, ParceiroPOJO[].class);
    }

    public ParceiroPOJO[] getListaParceiroPOJO() {
        return listaParceiroPOJO;
    }

    public interface ParceiroServiceCallback {
        void onSuccess(ParceiroPOJO[] listaParceiroPOJO);

        void onError(Exception exception);
    }

}
