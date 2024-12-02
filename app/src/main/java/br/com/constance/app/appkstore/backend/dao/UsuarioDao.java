package br.com.constance.app.appkstore.backend.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import br.com.constance.app.appkstore.backend.services.pojo.UsuarioPOJO;
import br.com.constance.app.appkstore.dataBase.DataBaseHelper;


public class UsuarioDao {

    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    public UsuarioDao(Context context) {
        this.context = context;
        dataBaseHelper = new DataBaseHelper(this.context);
    }

    public void open() throws SQLException {
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
    }

    public void close() {
        dataBaseHelper.close();
    }

    public void salvarUsuario(UsuarioPOJO usuarioPOJO) throws Exception {
        open();
        ContentValues values = new ContentValues();
        values.put("ID", usuarioPOJO.getId());
        values.put("NOMEUSUARIO", usuarioPOJO.getNomeUsario().toString());
        values.put("SENHA", usuarioPOJO.getSenha().toString());
        sqLiteDatabase.insert("USUARIOS", null, values);
        close();
    }

    public UsuarioPOJO buscaUsuario(String nome, String senha) throws Exception {
        try (SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()) {

            UsuarioPOJO usuarioPOJO = null;
            String[] colunas = {"NOMEUSUARIO", "SENHA"};
            String[] argumentos = {nome, senha};

            Cursor cursor = sqLiteDatabase.query("USUARIOS",
                    colunas, // Colunas (null significa todas as colunas)
                    "NOMEUSUARIO = ? AND SENHA = ?", // WHERE clause
                    argumentos, // WHERE args
                    null, // Group by
                    null, // Having
                    null); // Order by

            if (cursor != null && cursor.moveToFirst()) {
                if (cursor.moveToFirst()) {
                    String nomeUsuario = cursor.getString(cursor.getColumnIndexOrThrow("NOMEUSUARIO"));
                    String senhaUsuario = cursor.getString(cursor.getColumnIndexOrThrow("SENHA"));

                    // Preenche o objeto POJO
                    usuarioPOJO = new UsuarioPOJO();
                    usuarioPOJO.setNomeUsario(nomeUsuario);
                    usuarioPOJO.setSenha(senhaUsuario);
                } else {
                    // Nenhum resultado encontrado
                    Log.d("Consulta", "Nenhum usuário encontrado com essas credenciais.");
                }

            }

            if (cursor != null) {
                cursor.close();
            }

            return usuarioPOJO;

        } catch (Exception e) {
            throw new Exception("Não foi possível realizar o login.");
        }
    }
}
