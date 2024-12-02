package br.com.constance.app.appkstore.backend.services.usuario;

import android.content.Context;

import com.google.gson.Gson;

import br.com.constance.app.appkstore.SessionManager;
import br.com.constance.app.appkstore.backend.services.pojo.UsuarioPOJO;
import br.com.constance.app.appkstore.backend.util.WebServiceDecorator;

public class UsuarioService {
    private WebServiceDecorator webServiceDecorator;
    private UsuarioPOJO[] listaUsuarioPOJO;
    private Context context;
    private Gson gson;
    private String jSessionId;
    private String host;
    private String uri = "/apictc/v1/appkstore/usuario";
    private String usuario;

    public UsuarioPOJO[] getListaUsuarioPOJO() {
        return listaUsuarioPOJO;
    }

    public UsuarioService(Context context) {
        this.context = context;

    }

    public void obterDadosUsuario() throws Exception {

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
        usuario = sessionManagerInstance.getUsername();
    }


    private void buscarDados() throws Exception {
        webServiceDecorator = new WebServiceDecorator();
        webServiceDecorator.setParametro("mgeSession", jSessionId);
        webServiceDecorator.setParametro("nomeUsuario", usuario);
        String responseBody = webServiceDecorator.getString(uri);
        gson = new Gson();
        listaUsuarioPOJO = gson.fromJson(responseBody, UsuarioPOJO[].class);
    }

}
