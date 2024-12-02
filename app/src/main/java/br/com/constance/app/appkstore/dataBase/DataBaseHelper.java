package br.com.constance.app.appkstore.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SANKHYA.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;



    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criar a tabela quando o banco de dados é criado pela primeira vez
        String[] sqlFiles = {
                "USUARIOS.sql",
                "TASRTIPOSNEGOCIACAO.sql",
                "TASRPRODUTOS.sql",
                "TASRPRODUTOBARRA.sql",
                "TASRPARCEIROS.sql",
                "TASRPEDIDOCABECALHO.sql",
                "TASRPEDIDOITENS.sql",
                "TASRIMAGEMPRODUTOS.sql"
        };

        for (String sqlFile : sqlFiles) {
            String sql = readSqlFile(sqlFile);
            if (sql != null) {
                db.execSQL(sql);
            }
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    private String readSqlFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try {
            File file = new File(context.getFilesDir(), fileName);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return content.toString();
    }
}
