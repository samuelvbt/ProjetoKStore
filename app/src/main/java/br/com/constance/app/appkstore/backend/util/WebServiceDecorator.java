package br.com.constance.app.appkstore.backend.util;


import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WebServiceDecorator {

    private Map<String, String> parametros;
    private Map<String, String> propriedadesCabecalho;
    private Map<String, String> parametroExecucao;
    private Map<String, String> parametroCorpo;

    private String servico;

    private HttpURLConnection httpURLConnection;
    private String textoResponse;
    private String textoErro;
    private String multipart;
    private String boundary;

    private Boolean gerarLog = false;
    private String corpoChamada;
    private ByteArrayOutputStream file;

    private String requestMethod;
    private int responseCode;


    private boolean desabilitaHttpsRedirecionamento = false;

    public void setDesabilitaHttpsRedirecionamento(boolean desabilitaHttpsRedirecionamento) {
        this.desabilitaHttpsRedirecionamento = desabilitaHttpsRedirecionamento;
    }

    private boolean responseBase64 = false;



    public WebServiceDecorator() {
        this.parametros = new HashMap();
        this.parametroCorpo = new HashMap();
        this.propriedadesCabecalho = new HashMap<String, String>();
        this.parametroExecucao = new HashMap<String, String>();
    }

    public WebServiceDecorator(Boolean gerarLog) {
        this.gerarLog = gerarLog;
        this.parametros = new HashMap();
    }

    public WebServiceDecorator setParametro(String nome, String valor) throws Exception {
        this.parametros.put(nome, valor);
        return this;
    }

    public WebServiceDecorator setParametroCorpo(String nome, String valor) throws Exception {
        this.parametroCorpo.put(nome, valor);
        return this;
    }

    public void habilitaResponseBase64(){
        responseBase64 = true;
    }

    public void setFile(ByteArrayOutputStream file) throws Exception {
        this.file = file;
        boundary = "----" + UUID.randomUUID().toString();
        multipart = "multipart/form-data; boundary=" + boundary;
        setParametroExecucao("Multipart", multipart);
        setParametroExecucao("Content-Length", String.valueOf(file.size()));
    }

    public WebServiceDecorator setPropriedadesCabecalho(String nome, String valor) throws Exception {
        this.propriedadesCabecalho.put(nome, valor);
        return this;
    }

    public WebServiceDecorator setParametroExecucao(String nome, String valor) throws Exception {
        this.parametroExecucao.put(nome, valor);
        return this;
    }

    private String getParametroExecucao(String nome) {
        if (!this.parametroExecucao.containsKey(nome))
            return "";
        return this.parametroExecucao.get(nome);
    }

    public void setCorpoChamada(String corpoChamada) {
        this.corpoChamada = corpoChamada;
    }

    /*    public boolean proximo() throws Exception {
        if (!aberto) {
            executar();
            aberto = true;
        }
        return resultSet.next();
    }*/

    private void getService() throws Exception {
        abreConexao();

        carregaPropriedadesCabecalho();

        if(desabilitaHttpsRedirecionamento){
            httpURLConnection.setInstanceFollowRedirects(false);
        }

        String token = getParametroExecucao("Token");
        if (token != "") {
            httpURLConnection.setRequestProperty("Authorization", "Bearer " + token);
        }

        String cookie = getParametroExecucao("Cookie");
        if (cookie != "") {
            httpURLConnection.setRequestProperty("Cookie", cookie);
        }

        if (!propriedadesCabecalho.containsKey("Content-Type")) {
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        }

        if (!propriedadesCabecalho.containsKey("Accept")) {
            httpURLConnection.setRequestProperty("Accept", "application/json");
        }

        String multipart = getParametroExecucao("Multipart");
        if (multipart != "") {
            httpURLConnection.setRequestProperty("Content-Type", multipart);
        }

        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);

        if ("application/x-www-form-urlencoded".equals(propriedadesCabecalho.get("Content-Type"))) {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : parametroCorpo.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            corpoChamada = result.toString();
        }


        if (corpoChamada != null) {
            if (corpoChamada.length() > 0) {
                try (OutputStream os = httpURLConnection.getOutputStream()) {
                    byte[] input = corpoChamada.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }
        }

        if (file != null) {
            try (
                    OutputStream os = httpURLConnection.getOutputStream();
            ) {
                String header = "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"File\"; filename=\"" + "collectionsvtex.xls" + "\"\r\n" +
                        "Content-Type: " + "application/vnd.ms-excel" + "\r\n\r\n";
                String bottom = "\r\n" + "--" + boundary + "--\r\n";

                os.write(header.getBytes("UTF-8"));
                os.write(file.toByteArray(), 0, file.toByteArray().length);
                os.write(bottom.getBytes("UTF-8"));
                os.flush();
            }

        }

        responseCode = httpURLConnection.getResponseCode();
        String responseMessage = httpURLConnection.getResponseMessage();
        try {

            InputStream errorStream = httpURLConnection.getErrorStream();
            if (errorStream != null) {
                byte[] bytesErro = IOUtils.toByteArray(errorStream);
                textoErro = new String(bytesErro, "UTF-8");
            }

            InputStream inputStream = httpURLConnection.getInputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);

            textoResponse = responseBase64 ? Base64.getEncoder().encodeToString(bytes) : new String(bytes, "UTF-8");
        } catch (Exception e) {
            String mensagem = "Erro na chamada WebService. ";
            mensagem += "\nCodigo Resposta: " + responseCode + ">" + responseMessage;
            mensagem += "\nTexto erro: " + textoErro;
            mensagem += "\nErro " + e;
            if (corpoChamada == null) {
                mensagem += "\n Corpo: Corpo da chamada não existe! ";
            } else mensagem += "\n Corpo: " + corpoChamada;
            throw new Exception(mensagem);

        } finally {
            httpURLConnection.disconnect();
        }


    }

    private void abreConexao() throws IOException {
        String urlString = servico;
        StringBuilder urlParams = new StringBuilder();

        for (String parametro : parametros.keySet()) {
            if (urlParams.length() > 0) {
                urlParams.append("&");
            } else {
                urlParams.append("?");
            }

            urlParams.append(parametro);
            urlParams.append("=");
            urlParams.append(URLEncoder.encode(parametros.get(parametro),"UTF-8"));
        }

        urlString += urlParams.length() == 0 ? "" : urlParams.toString();
        URL url = new URL(urlString);

        httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod(requestMethod);
    }

    private void carregaPropriedadesCabecalho() {
        for (String key : this.propriedadesCabecalho.keySet()) {
            String value = this.propriedadesCabecalho.get(key);
            httpURLConnection.addRequestProperty(key, this.propriedadesCabecalho.get(key));
        }
    }

    public String getString(String servico) throws Exception {
        this.servico = servico;
        this.requestMethod = "GET";
        getService();
        return textoResponse;
    }

    public String postString(String servico) throws Exception {
        this.servico = servico;
        this.requestMethod = "POST";
        getService();
        return textoResponse;
    }

    public String putString(String servico) throws Exception {
        this.servico = servico;
        this.requestMethod = "PUT";
        getService();
        return textoResponse;
    }

    public String deleteString(String servico) throws Exception {
        this.servico = servico;
        this.requestMethod = "DELETE";
        getService();
        return textoResponse;
    }

    public JSONObject getJsonObject(String servico) throws Exception {
        return new JSONObject(getString(servico));
    }

    public JSONArray getJSONArray(String servico) throws Exception {
        return new JSONArray(getString(servico));
    }

    public int getResponseCode() {
        return responseCode;
    }
}
