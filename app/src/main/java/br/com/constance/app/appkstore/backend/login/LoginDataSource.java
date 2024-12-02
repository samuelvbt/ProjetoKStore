package br.com.constance.app.appkstore.backend.login;



import android.view.View;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    public Result<LoggedInUser> login(String username, String password) {
        try {
            // Crie uma instância de LoginAsync e chame seu método doInBackground para autenticar o usuário
            View view = null;
            LoginAsync loginAsync = new LoginAsync(view);
            JSONObject response = loginAsync.doInBackground(username, password);

            // Verifique se a resposta é nula ou se há algum erro
            if (response == null) {
                return new Result.Error(new IOException("Erro de comunicação"));
            } else if (!response.isNull("responseBody")) {
                // Se houver um "responseBody", isso indica que o login foi bem-sucedido
                // Crie e retorne um usuário autenticado com base no nome de usuário fornecido
                JSONObject responseBody = response.getJSONObject("responseBody");
                JSONObject jsessionIdObj = responseBody.getJSONObject("jsessionid");
                String jsessionIdValue = jsessionIdObj.getString("$");
                LoggedInUser authenticatedUser = new LoggedInUser(username, password,jsessionIdValue);
                return new Result.Success<>(authenticatedUser);
            } else {
                // Se não houver "responseBody", isso indica que houve um erro de autenticação
                return new Result.Error(new IOException("Credenciais inválidas"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Erro ao fazer login", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}