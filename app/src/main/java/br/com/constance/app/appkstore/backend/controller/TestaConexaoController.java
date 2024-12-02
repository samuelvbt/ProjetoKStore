package br.com.constance.app.appkstore.backend.controller;

import android.os.AsyncTask;

import br.com.constance.app.appkstore.backend.util.WebServiceDecorator;

public class TestaConexaoController extends AsyncTask<Void,Void,String> {
    WebServiceDecorator webServiceDecorator;
    @Override
    protected String doInBackground(Void... voids) {
        try {
            return teste();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private String teste() throws Exception {
        webServiceDecorator = new WebServiceDecorator();
       // webServiceDecorator.setParametro("mgeSession", jSessionId);
        String responseBody = webServiceDecorator.getString("/apictc/");
       // gson = new Gson();
        //listaParceiroPOJO = gson.fromJson(responseBody, ParceiroPOJO[].class);
        return responseBody;
    }
}
