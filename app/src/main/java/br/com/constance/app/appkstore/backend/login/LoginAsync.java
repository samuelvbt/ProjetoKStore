package br.com.constance.app.appkstore.backend.login;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.appkstore.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;

public class LoginAsync extends AsyncTask<String,Void, JSONObject> {
    View view;
    public LoginAsync(View view ) {
         this.view = view;
    }

    @Override
    public JSONObject doInBackground(String... strings) {
        String usuario = strings[0];
        String senha = strings[1];
        try {
            return doLogin(usuario, senha);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private JSONObject doLogin(String usuario, String senha) throws Exception {
        // Criar os campos internos
        MobileLoginRequest.Field nomusuField = new MobileLoginRequest.Field(usuario);
        MobileLoginRequest.Field internoField = new MobileLoginRequest.Field(senha);
        MobileLoginRequest.Field keepConnectedField = new MobileLoginRequest.Field("S");

// Criar o corpo da solicitação
        MobileLoginRequest.RequestBody requestBody = new MobileLoginRequest.RequestBody(nomusuField, internoField, keepConnectedField);

// Criar o objeto de solicitação
        MobileLoginRequest mobileLoginRequest = new MobileLoginRequest("MobileLoginSP.login", requestBody);
        Gson gson = new Gson();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
//        WebServiceDecorator webServiceDecorator = new WebServiceDecorator(host+"/mge/service.sbr?serviceName=MobileLoginSP.login&outputType=json");
//        webServiceDecorator.setCorpoChamada(gson.toJson(mobileLoginRequest));
//        String response  = webServiceDecorator.postString("");
//                if (response.isEmpty()) {
//                    throw new IOException("Unexpected code " + response);
//                }
               JSONObject jsonResponse = new JSONObject("");
               return jsonResponse;
    }
    @Override
    protected void onPostExecute(JSONObject s) {
        try {
            if(!"1".equals(s.getString("status"))){
                Snackbar.make(view,"Erro ao login: "+s.getString("statusMessage"), Snackbar.LENGTH_SHORT).show();
               ProgressBar progressBar =view.findViewById(R.id.loading);
               progressBar.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

}
