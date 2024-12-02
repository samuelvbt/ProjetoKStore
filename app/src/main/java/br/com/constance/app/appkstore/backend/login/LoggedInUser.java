package br.com.constance.app.appkstore.backend.login;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userName;
    private String password;
    private String sessionId;

    public LoggedInUser(String userName, String password, String sessionId) {
        this.userName = userName;
        this.password = password;
        this.sessionId = sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getSessionId() {
        return sessionId;
    }
}