package br.com.constance.app.appkstore.backend.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import br.com.constance.app.appkstore.backend.services.pojo.TipoNegociacaoPOJO;
import br.com.constance.app.appkstore.dataBase.DataBaseHelper;

public class TipoNegociacaoDao {
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    public TipoNegociacaoDao(Context context) {
        this.context = context;
        dataBaseHelper = new DataBaseHelper(this.context);
    }

    public void open() throws SQLException {
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
    }

    public void close() {
        dataBaseHelper.close();
    }

    public void salvarTipoNegociacao(TipoNegociacaoPOJO tipoNegociacaoPOJO){
        ContentValues values = new ContentValues();
        values.put("CODTIPVENDA", tipoNegociacaoPOJO.getCodigoTipoNegociacao().intValue());
        values.put("DESCRTIPVENDA", tipoNegociacaoPOJO.getDescricaoTipoNegociacao());
        sqLiteDatabase.insert("TASRTIPOSNEGOCIACAO", null, values);
    }

    public void limparTabelaTipoNegociacao(){
        sqLiteDatabase.execSQL("DELETE FROM TASRTIPOSNEGOCIACAO");
        sqLiteDatabase.execSQL("VACUUM");
    }

    public boolean isTabelaTipoNegociacaoPopulada() {
        boolean isPopulada = false;
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM TASRTIPOSNEGOCIACAO" , null);
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
}
