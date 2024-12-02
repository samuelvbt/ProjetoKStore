package br.com.constance.app.appkstore.backend.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.constance.app.appkstore.backend.services.pojo.ParceiroPOJO;
import br.com.constance.app.appkstore.dataBase.DataBaseHelper;

public class ParceiroDao {
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    public ParceiroDao(Context context) {
        this.context = context;
        dataBaseHelper = new DataBaseHelper(this.context);
    }

    public void open() throws SQLException {
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
    }

    public void close() {
        dataBaseHelper.close();
    }

    public void salvarParceiro(ParceiroPOJO parceiroPOJO) {
        ContentValues values = new ContentValues();
        values.put("CODPARC", parceiroPOJO.getCodigoParceiro().intValue());
        values.put("RAZAOSOCIAL", parceiroPOJO.getNomeParceiro());
        values.put("NOMEPARC", parceiroPOJO.getNomeParceiro());
        values.put("CNPJ", parceiroPOJO.getCnpj());
        values.put("CIDADE", parceiroPOJO.getCidadeParceiro());
        values.put("BAIRRO", parceiroPOJO.getBairroParceiro());
        sqLiteDatabase.insert("TASRPARCEIROS", null, values);

    }

    public void limparTabelaParceiro() {
        sqLiteDatabase.execSQL("DELETE FROM TASRPARCEIROS");
        sqLiteDatabase.execSQL("VACUUM");
    }

    public boolean isTabelaParceiroPopulada() {
        boolean isPopulada = false;
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM TASRPARCEIROS", null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                isPopulada = count > 0;
            }
        } catch (SQLiteException e) {
            // Table does not exist or another SQLite error occurred
            // Log the error or handle it as per your application's requirements
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isPopulada;
    }


    public ArrayList<ParceiroPOJO> buscarTodosParceiros() {
        ArrayList<ParceiroPOJO> listaParceiros = new ArrayList<>();
        String[] colunas = {"CODPARC", "RAZAOSOCIAL", "NOMEPARC", "CNPJ", "CIDADE", "BAIRRO"};
        Cursor cursor = sqLiteDatabase.query("TASRPARCEIROS",
                colunas, // Colunas (null significa todas as colunas)
                null, // WHERE clause
                null, // WHERE args
                null, // Group by
                null, // Having
                null); // Order by
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ParceiroPOJO parceiroPOJO = new ParceiroPOJO();
                parceiroPOJO.setCodigoParceiro(new BigDecimal(cursor.getInt(0)));
                parceiroPOJO.setRazaoSocial(cursor.getString(1));
                parceiroPOJO.setNomeParceiro(cursor.getString(2));
                parceiroPOJO.setCnpj(cursor.getString(3));
                parceiroPOJO.setCidadeParceiro(cursor.getString(4));
                parceiroPOJO.setBairroParceiro(cursor.getString(5));
                listaParceiros.add(parceiroPOJO);
            }
            cursor.close();
        }

        return listaParceiros;
    }

    public ParceiroPOJO buscarParceiroPorId(BigDecimal codigoParceiro) {
        try(SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()) {
            ParceiroPOJO parceiroPOJO = null;
            String[] colunas = {"CODPARC", "RAZAOSOCIAL", "NOMEPARC", "CNPJ", "CIDADE", "BAIRRO"};
            Cursor cursor = sqLiteDatabase.query("TASRPARCEIROS",
                    colunas, // Colunas (null significa todas as colunas)
                    "CODPARC = ?", // WHERE clause
                    new String[]{codigoParceiro.toString()}, // WHERE args
                    null, // Group by
                    null, // Having
                    null); // Order by
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    parceiroPOJO = new ParceiroPOJO();
                    parceiroPOJO.setCodigoParceiro(new BigDecimal(cursor.getInt(0)));
                    parceiroPOJO.setRazaoSocial(cursor.getString(1));
                    parceiroPOJO.setNomeParceiro(cursor.getString(2));
                    parceiroPOJO.setCnpj(cursor.getString(3));
                    parceiroPOJO.setCidadeParceiro(cursor.getString(4));
                    parceiroPOJO.setBairroParceiro(cursor.getString(5));
                }
                cursor.close();
            }

            return parceiroPOJO;
        }catch (Exception e){
            throw new RuntimeException("Não foi possível localizar o parceiro");
        }
    }

    public ArrayList<ParceiroPOJO> buscarParceirosPorDescricao(String descricao) throws Exception {
        try (SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()) {
            ArrayList<ParceiroPOJO> listaParceiros = new ArrayList<>();
            String descricaoParametro = "%" + descricao + "%";
            String[] colunas = {"CODPARC", "RAZAOSOCIAL", "NOMEPARC", "CNPJ", "CIDADE", "BAIRRO"};
            String[] argumentos = {descricaoParametro, descricaoParametro, descricaoParametro};

            Cursor cursor = sqLiteDatabase.query("TASRPARCEIROS",
                    colunas, // Colunas (null significa todas as colunas)
                    "(NOMEPARC LIKE ? OR RAZAOSOCIAL LIKE ? OR CNPJ LIKE ?)", // WHERE clause
                    argumentos, // WHERE args
                    null, // Group by
                    null, // Having
                    null); // Order by
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    ParceiroPOJO parceiroPOJO = new ParceiroPOJO();
                    parceiroPOJO.setCodigoParceiro(new BigDecimal(cursor.getInt(0)));
                    parceiroPOJO.setRazaoSocial(cursor.getString(1));
                    parceiroPOJO.setNomeParceiro(cursor.getString(2));
                    parceiroPOJO.setCnpj(cursor.getString(3));
                    parceiroPOJO.setCidadeParceiro(cursor.getString(4));
                    parceiroPOJO.setBairroParceiro(cursor.getString(5));
                    listaParceiros.add(parceiroPOJO);
                }
                cursor.close();
            }

            return listaParceiros;
        }catch (Exception e){
            throw new Exception("Não foi possível carregar os dados dos parceiros.");
        }
    }

}



