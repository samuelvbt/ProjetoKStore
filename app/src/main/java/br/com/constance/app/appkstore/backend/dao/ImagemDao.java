package br.com.constance.app.appkstore.backend.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import br.com.constance.app.appkstore.backend.services.pojo.ImagemPOJO;
import br.com.constance.app.appkstore.dataBase.DataBaseHelper;


public class ImagemDao {
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    public ImagemDao(Context context) {
        this.context = context;
        dataBaseHelper = new DataBaseHelper(this.context);
    }

    public void open() throws SQLException {
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
    }

    public void close() {
        dataBaseHelper.close();
    }

    public void salvarImagem(ImagemPOJO imagemPOJO) throws Exception {
        ContentValues values = new ContentValues();
        values.put("CODPROD", imagemPOJO.getCodigoProduto().intValue());
        values.put("IMAGEM",imagemPOJO.getImagem());
        sqLiteDatabase.insert("TASRIMAGEMPRODUTOS", null, values);
    }


    public void limparTabelaProduto(){
        sqLiteDatabase.execSQL("DELETE FROM TASRIMAGEMPRODUTOS");
        sqLiteDatabase.execSQL("VACUUM");
    }

    public boolean isTabelaProdutoPopulada() {
        boolean isPopulada = false;
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM TASRIMAGEMPRODUTOS" , null);
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

    public byte[] buscaIimagemPorId(BigDecimal codigoProduto) throws Exception {
        try (SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()) {
            ImagemPOJO imagemPOJO = null;
            String[] colunas = {"CODPROD", "IMAGEM"};

            Cursor cursor = sqLiteDatabase.query("TASRIMAGEMPRODUTOS",
                    colunas, // Colunas (null significa todas as colunas)
                    "CODPROD = ?", // WHERE clause
                    new String[]{codigoProduto.toString()}, // WHERE args
                    null, // Group by
                    null, // Having
                    null); // Order by

            if (cursor != null && cursor.moveToFirst()) {
                    imagemPOJO = new ImagemPOJO();
                    imagemPOJO.setCodigoProduto(new BigDecimal(cursor.getInt(0)));
                    imagemPOJO.setImagem(cursor.getBlob(1));
                cursor.close();
            }
            if(imagemPOJO!=null){
                return imagemPOJO.getImagem();
            }
           return null;
        }catch (Exception e){
            throw new Exception("Não foi possível encontrar o produto");
        }
    }



}
