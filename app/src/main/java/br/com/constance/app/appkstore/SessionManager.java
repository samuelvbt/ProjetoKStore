package br.com.constance.app.appkstore;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import br.com.constance.app.appkstore.backend.login.LoginAsync;

public class SessionManager {
    private static final String PREF_NAME = "SessionPrefs";
    private static final String KEY_IS_LOGGED_IN = "";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_SESSION_ID = "sessionId";
    private static final String KEY_CODUSU = "codusu";

    private static SessionManager instance;
    private SharedPreferences sharedPreferences;

    private boolean isLoggedIn = false;
    private String username = null;
    private String password = null;
    private String sessionId = null;
    private String codusu = null;

    SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        username = sharedPreferences.getString(KEY_USERNAME, null);
        password = sharedPreferences.getString(KEY_PASSWORD, null);
        sessionId = sharedPreferences.getString(KEY_SESSION_ID, null);
        codusu=sharedPreferences.getString(KEY_CODUSU,null);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        sharedPreferences.edit().putString(KEY_USERNAME, username).apply();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        sharedPreferences.edit().putString(KEY_PASSWORD, password).apply();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
        sharedPreferences.edit().putString(KEY_SESSION_ID, sessionId).apply();
    }

    public String getCodusu() {
        return codusu;
    }

    public void setCodusu(String codusu) {

        this.codusu = codusu;
        sharedPreferences.edit().putString(KEY_CODUSU,codusu).apply();
    }


    public void login(View view) throws ExecutionException, InterruptedException {
        LoginAsync login = new LoginAsync(view);
        JSONObject response = login.execute(getUsername(),getPassword()).get();
        if (response!=null && !response.isNull("responseBody")) {
            JSONObject responseBody = null;
                        try {
                            responseBody = response.getJSONObject("responseBody");
                            JSONObject jsessionIdObj = responseBody.getJSONObject("jsessionid");
                            setSessionId(jsessionIdObj.getString("$").toString());

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
        }

    }
}
