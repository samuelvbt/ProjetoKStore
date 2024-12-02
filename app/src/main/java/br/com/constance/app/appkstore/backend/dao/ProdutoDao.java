package br.com.constance.app.appkstore.backend.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;

import br.com.constance.app.appkstore.backend.services.pojo.ProdutoBarraPOJO;
import br.com.constance.app.appkstore.backend.services.pojo.ProdutoPOJO;
import br.com.constance.app.appkstore.backend.util.WebServiceDecorator;
import br.com.constance.app.appkstore.dataBase.DataBaseHelper;


public class ProdutoDao {
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;
    private WebServiceDecorator webServiceDecorator;

    public ProdutoDao(Context context) {
        this.context = context;
        dataBaseHelper = new DataBaseHelper(this.context);
    }

    public void open() throws SQLException {
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
    }

    public void close() {
        dataBaseHelper.close();
    }

    public void salvarProduto(ProdutoPOJO produtoPOJO) throws Exception {
        ContentValues values = new ContentValues();
        values.put("CODPROD", produtoPOJO.getCodigoProduto().intValue());
        values.put("REFERENCIA", produtoPOJO.getReferencia());
        values.put("DESCRICAO", produtoPOJO.getDescricaoProduto());
        values.put("LISTCONTEXT", produtoPOJO.getListaGrade().stream().collect(Collectors.joining("\n")));
        values.put("PRECO",produtoPOJO.getPreco()==null? BigDecimal.ZERO.doubleValue(): produtoPOJO.getPreco().doubleValue());
        values.put("URLIMG",produtoPOJO.getUrlImagemProduto());
        sqLiteDatabase.insert("TASRPRODUTOS", null, values);
    }

    public void salvarCodigosBarra(BigDecimal codigoProduto, ArrayList<String> listaCodigosDeBarra) {
        for (String codigoBarras : listaCodigosDeBarra) {
            ContentValues values = new ContentValues();
            values.put("CODPROD", codigoProduto.intValue());
            values.put("CODBARRA", codigoBarras);
            sqLiteDatabase.insert("TASRPRODUTOBARRA", null, values);
        }
    }

    public void limparTabelaProduto(){
        sqLiteDatabase.execSQL("DELETE FROM TASRPRODUTOS");
        sqLiteDatabase.execSQL("VACUUM");
    }

    public void limparTabelaBarras(){
        sqLiteDatabase.execSQL("DELETE FROM TASRPRODUTOBARRA");
        sqLiteDatabase.execSQL("VACUUM");
    }

    public boolean isTabelaProdutoPopulada() {
        boolean isPopulada = false;
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM TASRPRODUTOS" , null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                isPopulada = count > 0;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isPopulada;
    }

    public boolean isTabelaProdutoBarraPopulada() {
        boolean isPopulada = false;
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM TASRPRODUTOBARRA" , null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                isPopulada = count > 0;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isPopulada;
    }

    public ProdutoPOJO buscarProdutoPorId(BigDecimal codigoProduto) throws Exception {
        try (SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()) {
            ProdutoPOJO produtoPOJO = null;
            String[] colunas = {"CODPROD", "REFERENCIA", "DESCRICAO", "LISTCONTEXT", "PRECO","URLIMG"};

            Cursor cursor = sqLiteDatabase.query("TASRPRODUTOS",
                    colunas, // Colunas (null significa todas as colunas)
                    "CODPROD = ?", // WHERE clause
                    new String[]{codigoProduto.toString()}, // WHERE args
                    null, // Group by
                    null, // Having
                    null); // Order by

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    ArrayList<String> listaGrade = new ArrayList<>();

                    produtoPOJO = new ProdutoPOJO();
                    produtoPOJO.setCodigoProduto(new BigDecimal(cursor.getInt(0)));
                    produtoPOJO.setReferencia(cursor.getString(1));
                    produtoPOJO.setDescricaoProduto(cursor.getString(2));
                    String[] split = cursor.getString(3).split("\n");
                    for (String controle : split) {
                        listaGrade.add(controle);
                    }
                    produtoPOJO.setListaGrade(listaGrade);
                    produtoPOJO.setPreco(new BigDecimal(cursor.getDouble(4)));
                    produtoPOJO.setUrlImagemProduto(cursor.getString(5));
                    //produtoPOJO.setImagem(cursor.getBlob(5));
                }
                cursor.close();
            }

            return produtoPOJO;
        }catch (Exception e){
            throw new Exception("Não foi possível encontrar o produto");
        }
    }

    public ArrayList<ProdutoPOJO> buscarTodosProduto(){

        ArrayList<ProdutoPOJO> listProdutos = new ArrayList<>();
        String[] colunas = {"CODPROD", "REFERENCIA", "DESCRICAO", "LISTCONTEXT", "PRECO"};

        Cursor cursor = sqLiteDatabase.query("TASRPRODUTOS",
                colunas, // Colunas (null significa todas as colunas)
                null, // WHERE clause
                null, // WHERE args
                null, // Group by
                null, // Having
                null); // Order by

        if (cursor != null) {
            while (cursor.moveToNext()) {
                ArrayList<String> listaGrade = new ArrayList<>();

                ProdutoPOJO produtoPOJO = new ProdutoPOJO();
                produtoPOJO.setCodigoProduto(new BigDecimal(cursor.getInt(0)));
                produtoPOJO.setReferencia(cursor.getString(1));
                produtoPOJO.setDescricaoProduto(cursor.getString(2));
                String[] split = cursor.getString(3).split("\n");
                for (String controle : split) {
                    listaGrade.add(controle);
                }
                produtoPOJO.setListaGrade(listaGrade);
                produtoPOJO.setPreco(new BigDecimal(cursor.getDouble(4)));
                listProdutos.add(produtoPOJO);
            }
            cursor.close();
        }

        return listProdutos;
    }

    public ArrayList<ProdutoPOJO> buscarProdutoPorDescricao(String descricao) throws Exception {
        try (SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()) {

            ArrayList<ProdutoPOJO> listProdutos = new ArrayList<>();
            String[] colunas = {"CODPROD", "REFERENCIA", "DESCRICAO", "LISTCONTEXT", "PRECO"};
            String descricaoArgumento = "%" + descricao + "%";
            String[] argumentos = {descricaoArgumento, descricaoArgumento};

            Cursor cursor = sqLiteDatabase.query("TASRPRODUTOS",
                    colunas, // Colunas (null significa todas as colunas)
                    "(DESCRICAO LIKE ? OR REFERENCIA LIKE ?)", // WHERE clause
                    argumentos, // WHERE args
                    null, // Group by
                    null, // Having
                    null); // Order by

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    ArrayList<String> listaGrade = new ArrayList<>();

                    ProdutoPOJO produtoPOJO = new ProdutoPOJO();
                    produtoPOJO.setCodigoProduto(new BigDecimal(cursor.getInt(0)));
                    produtoPOJO.setReferencia(cursor.getString(1));
                    produtoPOJO.setDescricaoProduto(cursor.getString(2));
                    String[] split = cursor.getString(3).split("\n");
                    for (String controle : split) {
                        listaGrade.add(controle);
                    }
                    produtoPOJO.setListaGrade(listaGrade);
                    produtoPOJO.setPreco(new BigDecimal(cursor.getDouble(4)));
                    listProdutos.add(produtoPOJO);
                }
                cursor.close();
            }

            return listProdutos;

        }catch (Exception e){
            throw new Exception("Não foi possível retonar os dados dos produtos");
        }
    }


    public ProdutoBarraPOJO buscarProdutoPorCodigoBarras(String codigo) throws Exception {
        try (SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()) {

            ProdutoBarraPOJO produtoBarraPOJO = null;
            String[] colunas = {"CODPROD", "CODBARRA"};
            String[] argumentos = {codigo};

            Cursor cursor = sqLiteDatabase.query("TASRPRODUTOBARRA",
                    colunas, // Colunas (null significa todas as colunas)
                    "CODBARRA = ?", // WHERE clause
                    argumentos, // WHERE args
                    null, // Group by
                    null, // Having
                    null); // Order by

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    produtoBarraPOJO = new ProdutoBarraPOJO();
                    produtoBarraPOJO.setCodigoProduto(new BigDecimal(cursor.getInt(0)));
                    produtoBarraPOJO.setCodigoBarras(cursor.getString(1));
                }
                cursor.close();
            }

            return produtoBarraPOJO;

        }catch (Exception e){
            throw new Exception("Não foi possível retonar os dados dos código de barras");
        }
    }


}
