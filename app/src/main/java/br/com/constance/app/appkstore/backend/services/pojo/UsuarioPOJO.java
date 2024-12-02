package br.com.constance.app.appkstore.backend.services.pojo;

public class UsuarioPOJO {
    public int id;
    public String nomeUsario;
    public String senha;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNomeUsario() {
        return nomeUsario;
    }

    public void setNomeUsario(String nomeUsario) {
        this.nomeUsario = nomeUsario;
    }
}
