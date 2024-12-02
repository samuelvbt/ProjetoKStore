package br.com.constance.app.appkstore.backend.login;

import com.google.gson.annotations.SerializedName;

public class MobileLoginRequest {
    @SerializedName("serviceName")
    private String serviceName;

    @SerializedName("requestBody")
    private RequestBody requestBody;

    public MobileLoginRequest(String serviceName, RequestBody requestBody) {
        this.serviceName = serviceName;
        this.requestBody = requestBody;
    }

    public static class RequestBody {
        @SerializedName("NOMUSU")
        private Field nomusu;

        @SerializedName("INTERNO")
        private Field interno;

        @SerializedName("KEEPCONNECTED")
        private Field keepConnected;

        public RequestBody(Field nomusu, Field interno, Field keepConnected) {
            this.nomusu = nomusu;
            this.interno = interno;
            this.keepConnected = keepConnected;
        }
    }

    public static class Field {
        @SerializedName("$")
        private String value;

        public Field(String value) {
            this.value = value;
        }
    }
}
