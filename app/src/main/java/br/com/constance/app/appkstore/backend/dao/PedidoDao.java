package br.com.constance.app.appkstore.backend.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.constance.app.appkstore.SessionManager;
import br.com.constance.app.appkstore.backend.services.pojo.ItemPedidoPOJO;
import br.com.constance.app.appkstore.backend.services.pojo.PedidoPOJO;
import br.com.constance.app.appkstore.dataBase.DataBaseHelper;

public class PedidoDao {
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
    private Context context;

    public PedidoDao(Context context) {
        this.context = context;
        dataBaseHelper = new DataBaseHelper(this.context);
    }

    public void open() throws SQLException {
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
    }

    public void close() {
        dataBaseHelper.close();
    }

    public String salvarCabecalho(PedidoPOJO pedidoPOJO) {
        Date dataAtual = new Date();
        String codigoUsuario = SessionManager.getInstance(this.context).getCodusu();
        String idPedido = "SR:" + codigoUsuario + ":" + simpleDateFormat.format(dataAtual);

        ContentValues values = new ContentValues();
        values.put("IDPEDIDO", idPedido);
        values.put("CODPARC", pedidoPOJO.getCodigoParceiro().intValue());
        values.put("DTNEG", pedidoPOJO.getDataNegociacao());
        values.put("CODUSU", codigoUsuario);
        values.put("SINCRONIZADO", "N");
        values.put("FINALIZADO", "N");
        values.put("EXCLUIDO","N");

        sqLiteDatabase.insert("TASRPEDIDOCABECALHO", null, values);

        return idPedido;
    }

    public void finalizarPedido(PedidoPOJO pedidoPOJO) {

        String[] parametros = {pedidoPOJO.getIdPedido()};

        ContentValues values = new ContentValues();
        values.put("DTENTREGA", pedidoPOJO.getDataEntrega());
        values.put("CODTIPVENDA", pedidoPOJO.getCodigoTipoNegociacao().intValue());
        values.put("OBSERVACAO", pedidoPOJO.getObservacao());
        values.put("FINALIZADO", "S");

        sqLiteDatabase.update("TASRPEDIDOCABECALHO", values, "IDPEDIDO = ?", parametros);

    }

    public void salvarItens(ItemPedidoPOJO itemPedidoPOJO) throws Exception {
        try(SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put("IDPEDIDO", itemPedidoPOJO.getIdPedido());
            values.put("CODPROD", itemPedidoPOJO.getCodigoProduto().intValue());
            values.put("CONTROLE", itemPedidoPOJO.getControle());
            values.put("QTDNEG", itemPedidoPOJO.getQuantidadeNegociada().intValue());
            values.put("VLRUNITARIO", itemPedidoPOJO.getPrecoUnitarioProduto().doubleValue());

            sqLiteDatabase.insert("TASRPEDIDOITENS", null, values);
        }catch (Exception e){
            throw new Exception("Não foi possível salvar item do pedido");
        }
    }

    public void removeItem(ItemPedidoPOJO itemPedidoPOJO) throws Exception {
        try(SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()){
            String[] paramentros = {itemPedidoPOJO.getIdPedido(),
                    itemPedidoPOJO.getCodigoProduto().toString(),
                    itemPedidoPOJO.getControle()};

            sqLiteDatabase.delete("TASRPEDIDOITENS", "IDPEDIDO = ? AND CODPROD = ? AND CONTROLE = ?", paramentros);
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("Não foi possível remover SKU do pedido");
        }
    }

    public void removerProduto(String idPedido, BigDecimal codigoProduto) throws Exception {
        try(SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()){
            String[] paramentros = {idPedido,
                    codigoProduto.toString()};

            sqLiteDatabase.delete("TASRPEDIDOITENS", "IDPEDIDO = ? AND CODPROD = ?", paramentros);
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("Não foi possível remover produto do pedido");
        }
    }

    public void removerFakePedido(String idPedido) throws Exception {
        try(SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()){
            String[] paramentros = {idPedido};
            ContentValues values = new ContentValues();
            values.put("EXCLUIDO", "S");

            sqLiteDatabase.update("TASRPEDIDOCABECALHO", values,"IDPEDIDO = ?", paramentros);
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("Não foi possível remover pedido");
        }
    }

    public void modificarItem(ItemPedidoPOJO itemPedidoPOJO) throws Exception {
        try(SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()){
        ContentValues values = new ContentValues();
        values.put("QTDNEG", itemPedidoPOJO.getQuantidadeNegociada().intValue());

        String[] paramentros = {itemPedidoPOJO.getIdPedido(),
                itemPedidoPOJO.getCodigoProduto().toString(),
                itemPedidoPOJO.getControle()};

        sqLiteDatabase.update("TASRPEDIDOITENS", values, "IDPEDIDO = ? AND CODPROD = ?  AND CONTROLE = ?", paramentros);
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("Não foi possível atualizar o item");
        }
    }


    public List<PedidoPOJO> buscarPedidosPorParceiro(BigDecimal codigoParceiro) {
        List<PedidoPOJO> listaPedidos = new ArrayList<>();
        String[] colunas = {"IDPEDIDO", "CODPARC", "DTNEG", "DTENTREGA", "CODTIPVENDA", "CODUSU","SINCRONIZADO","FINALIZADO"};

        Cursor cursor = sqLiteDatabase.query("TASRPEDIDOCABECALHO",
                colunas, // Colunas (null significa todas as colunas)
                "CODPARC = ? AND EXCLUIDO = ?", // WHERE clause
                new String[]{codigoParceiro.toString(), "N"}, // WHERE args
                null, // Group by
                null, // Having
                null); // Order by

        if (cursor != null) {
            while (cursor.moveToNext()) {
                PedidoPOJO pedidoPOJO = new PedidoPOJO();
                pedidoPOJO.setIdPedido(cursor.getString(0));
                pedidoPOJO.setCodigoParceiro(new BigDecimal(cursor.getInt(1)));
                pedidoPOJO.setDataNegociacao(cursor.getLong(2));
                pedidoPOJO.setDataEntrega(cursor.getLong(3));
                pedidoPOJO.setCodigoTipoNegociacao(new BigDecimal(cursor.getInt(4)));
                pedidoPOJO.setCodigoUsuario(new BigDecimal(cursor.getInt(5)));
                pedidoPOJO.setPedidoSincronizado(cursor.getString(6));
                pedidoPOJO.setPedidoFinalizado(cursor.getString(7));
                listaPedidos.add(pedidoPOJO);
            }
            cursor.close();
        }
        return listaPedidos;
    }

    public List<PedidoPOJO> buscarPedidos(String sincronizado, String finalizado) {
        try (SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()) {
            List<PedidoPOJO> listaPedidos = new ArrayList<>();
            String[] colunas = {"IDPEDIDO", "CODPARC", "DTNEG", "DTENTREGA", "CODTIPVENDA", "CODUSU", "SINCRONIZADO", "FINALIZADO"};
            ArrayList<String> argumentos = new ArrayList<>();

            String camposWhere = "";

            if (sincronizado != null) {
                camposWhere = "SINCRONIZADO = ?";
                argumentos.add(sincronizado);
            }

            if (finalizado != null) {
                if (camposWhere.length() > 0) {
                    camposWhere = camposWhere.concat(" AND ");
                }
                camposWhere = camposWhere.concat("FINALIZADO = ?");
                argumentos.add(finalizado);
            }

            if (camposWhere.length() > 0) {
                camposWhere = camposWhere.concat(" AND EXCLUIDO = ?");
            } else {
                camposWhere = camposWhere.concat("EXCLUIDO = ?");
            }

            argumentos.add("N");

            String[] argumentosWhere = new String[argumentos.size()];


            Cursor cursor = sqLiteDatabase.query("TASRPEDIDOCABECALHO",
                    colunas, // Colunas (null significa todas as colunas)
                    camposWhere, // WHERE clause
                    argumentos.toArray(argumentosWhere), // WHERE args
                    null, // Group by
                    null, // Having
                    null); // Order by

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    PedidoPOJO pedidoPOJO = new PedidoPOJO();
                    pedidoPOJO.setIdPedido(cursor.getString(0));
                    pedidoPOJO.setCodigoParceiro(new BigDecimal(cursor.getInt(1)));
                    pedidoPOJO.setDataNegociacao(cursor.getLong(2));
                    pedidoPOJO.setDataEntrega(cursor.getLong(3));
                    pedidoPOJO.setCodigoTipoNegociacao(new BigDecimal(cursor.getInt(4)));
                    pedidoPOJO.setCodigoUsuario(new BigDecimal(cursor.getInt(5)));
                    pedidoPOJO.setPedidoSincronizado(cursor.getString(6));
                    pedidoPOJO.setPedidoFinalizado(cursor.getString(7));
                    listaPedidos.add(pedidoPOJO);
                }
                cursor.close();
            }
            return listaPedidos;
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível recuperar os pedidos.");
        }
    }


    public PedidoPOJO buscarPedidoPorId(String idPedido) {
        PedidoPOJO pedidoPOJO = null;
        String[] colunas = {"IDPEDIDO", "CODPARC", "DTNEG", "DTENTREGA", "CODTIPVENDA", "CODUSU"};

        Cursor cursor = sqLiteDatabase.query("TASRPEDIDOCABECALHO",
                colunas, // Colunas (null significa todas as colunas)
                "IDPEDIDO = ?", // WHERE clause
                new String[]{idPedido}, // WHERE args
                null, // Group by
                null, // Having
                null); // Order by

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                pedidoPOJO = new PedidoPOJO();
                pedidoPOJO.setIdPedido(cursor.getString(0));
                pedidoPOJO.setCodigoParceiro(new BigDecimal(cursor.getInt(1)));
                pedidoPOJO.setDataNegociacao(cursor.getLong(2));
                pedidoPOJO.setDataEntrega(cursor.getLong(3));
                pedidoPOJO.setCodigoTipoNegociacao(new BigDecimal(cursor.getInt(4)));
                pedidoPOJO.setCodigoUsuario(new BigDecimal(cursor.getInt(5)));
            }
            cursor.close();
        }
        return pedidoPOJO;
    }

    public ArrayList<ItemPedidoPOJO> buscarItensPorIdPedido(String idPedido) {
        ArrayList<ItemPedidoPOJO> listaItensPedido = new ArrayList<>();
        String[] colunas = {"IDPEDIDO", "CODPROD", "CONTROLE", "QTDNEG", "VLRUNITARIO"};

        Cursor cursor = sqLiteDatabase.query("TASRPEDIDOITENS",
                colunas, // Colunas (null significa todas as colunas)
                "IDPEDIDO = ?", // WHERE clause
                new String[]{idPedido}, // WHERE args
                null, // Group by
                null, // Having
                null); // Order by

        if (cursor != null) {
            while (cursor.moveToNext()) {
                ItemPedidoPOJO itemPedidoPOJO = new ItemPedidoPOJO();
                itemPedidoPOJO.setIdPedido(cursor.getString(0));
                itemPedidoPOJO.setCodigoProduto(new BigDecimal(cursor.getInt(1)));
                itemPedidoPOJO.setControle(cursor.getString(2));
                itemPedidoPOJO.setQuantidadeNegociada(new BigDecimal(cursor.getInt(3)));
                itemPedidoPOJO.setPrecoUnitarioProduto(new BigDecimal(cursor.getInt(4)));
                listaItensPedido.add(itemPedidoPOJO);
            }
            cursor.close();
        }
        return listaItensPedido;
    }

    public BigDecimal obterTotalPedidoPorId(String idPedido) throws Exception {
        try(SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()){

            String[] parametros = {idPedido};
            BigDecimal valorTotalPedido = BigDecimal.ZERO;

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT SUM(QTDNEG * VLRUNITARIO) VLRTOTAL FROM TASRPEDIDOITENS WHERE IDPEDIDO = ?", parametros);


            if (cursor != null) {
                if(cursor.moveToFirst()){
                    valorTotalPedido = new BigDecimal(cursor.getDouble(0));
                }

                cursor.close();
            }
            return valorTotalPedido;
        }catch (Exception e){
            throw new Exception("Não foi possível obter o total do pedido.");
        }
    }

    public BigDecimal obterTotalItensPedido(String idPedido) throws Exception {
        try(SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()){

            String[] parametros = {idPedido};
            BigDecimal quantidadeItensPedido = BigDecimal.ZERO;

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT SUM(QTDNEG) QTDNEG FROM TASRPEDIDOITENS WHERE IDPEDIDO = ?", parametros);


            if (cursor != null) {
                if(cursor.moveToFirst()){
                    quantidadeItensPedido = new BigDecimal(cursor.getDouble(0));
                }

                cursor.close();
            }
            return quantidadeItensPedido;
        }catch (Exception e){
            throw new Exception("Não foi possível obter a quantidade total de itens.");
        }
    }

    public BigDecimal obterTotalItensPedidoProduto(String idPedido, BigDecimal codigoProduto) throws Exception {
        try(SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()){

            String[] parametros = {idPedido, codigoProduto.toString()};
            BigDecimal quantidadeItensProduto = BigDecimal.ZERO;

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT SUM(QTDNEG) QTDNEG FROM TASRPEDIDOITENS WHERE IDPEDIDO = ? AND CODPROD = ?", parametros);


            if (cursor != null) {
                if(cursor.moveToFirst()){
                    quantidadeItensProduto = new BigDecimal(cursor.getDouble(0));
                }

                cursor.close();
            }
            return quantidadeItensProduto;
        }catch (Exception e){
            throw new Exception("Não foi possível obter a quantidade itens por produto.");
        }
    }

    public List<ItemPedidoPOJO> buscarProdutosPorIdPedido(String idPedido) {
        List<ItemPedidoPOJO> listaItensPedido = new ArrayList<>();
        String[] colunas = {"IDPEDIDO", "CODPROD"};
        String colunasAgrupamento = "CODPROD";

        Cursor cursor = sqLiteDatabase.query("TASRPEDIDOITENS",
                colunas, // Colunas (null significa todas as colunas)
                "IDPEDIDO = ?", // WHERE clause
                new String[]{idPedido}, // WHERE args
                colunasAgrupamento, // Group by
                null, // Having
                null); // Order by

        if (cursor != null) {
            while (cursor.moveToNext()) {
                ItemPedidoPOJO itemPedidoPOJO = new ItemPedidoPOJO();
                itemPedidoPOJO.setIdPedido(cursor.getString(0));
                itemPedidoPOJO.setCodigoProduto(new BigDecimal(cursor.getInt(1)));
                listaItensPedido.add(itemPedidoPOJO);
            }
            cursor.close();
        }
        return listaItensPedido;

    }

    public List<ItemPedidoPOJO> buscarItensPorIdPedidoProduto(String idPedido, BigDecimal codigoProduto) throws Exception {
        try(SQLiteDatabase sqLiteDatabase=dataBaseHelper.getWritableDatabase()){

            List<ItemPedidoPOJO> listaItensPedido = new ArrayList<>();
            String[] colunas = {"IDPEDIDO", "CODPROD", "CONTROLE", "QTDNEG", "VLRUNITARIO"};

            Cursor cursor = sqLiteDatabase.query("TASRPEDIDOITENS",
                    colunas, // Colunas (null significa todas as colunas)
                    "IDPEDIDO = ? AND CODPROD = ?", // WHERE clause
                    new String[]{idPedido, codigoProduto.toString()}, // WHERE args
                    null, // Group by
                    null, // Having
                    null); // Order by

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    ItemPedidoPOJO itemPedidoPOJO = new ItemPedidoPOJO();
                    itemPedidoPOJO.setIdPedido(cursor.getString(0));
                    itemPedidoPOJO.setCodigoProduto(new BigDecimal(cursor.getInt(1)));
                    itemPedidoPOJO.setControle(cursor.getString(2));
                    itemPedidoPOJO.setQuantidadeNegociada(new BigDecimal(cursor.getInt(3)));
                    itemPedidoPOJO.setPrecoUnitarioProduto(new BigDecimal(cursor.getInt(4)));
                    listaItensPedido.add(itemPedidoPOJO);
                }
                cursor.close();
            }
            return listaItensPedido;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("Não foi possível retornar itens");
        }
    }

    public ItemPedidoPOJO buscarItemPedidoPorSku(ItemPedidoPOJO itemPedidoPOJO) throws Exception {
        try(SQLiteDatabase sqLiteDatabase=dataBaseHelper.getWritableDatabase()){
            ItemPedidoPOJO itemPedidoPOJORecuperado = null;
            String[] colunas = {"IDPEDIDO", "CODPROD", "CONTROLE", "QTDNEG", "VLRUNITARIO"};
            String[] parametros = {itemPedidoPOJO.getIdPedido(),
                    itemPedidoPOJO.getCodigoProduto().toString(),
                    itemPedidoPOJO.getControle()};

            Cursor cursor = sqLiteDatabase.query("TASRPEDIDOITENS",
                    colunas, // Colunas (null significa todas as colunas)
                    "IDPEDIDO = ? AND CODPROD = ? AND CONTROLE = ?", // WHERE clause
                    parametros, // WHERE args
                    null, // Group by
                    null, // Having
                    null); // Order by

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    itemPedidoPOJORecuperado = new ItemPedidoPOJO();
                    itemPedidoPOJORecuperado.setIdPedido(cursor.getString(0));
                    itemPedidoPOJORecuperado.setCodigoProduto(new BigDecimal(cursor.getInt(1)));
                    itemPedidoPOJORecuperado.setControle(cursor.getString(2));
                    itemPedidoPOJORecuperado.setQuantidadeNegociada(new BigDecimal(cursor.getInt(3)));
                    itemPedidoPOJORecuperado.setPrecoUnitarioProduto(new BigDecimal(cursor.getInt(4)));
                }
                cursor.close();
            }

            return itemPedidoPOJORecuperado;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("Não foi possível retornar itens");
        }
    }

    public ArrayList<PedidoPOJO> buscarPedidosFinalizadosParaSincronizar() {
        ArrayList<PedidoPOJO> listaPedidos = new ArrayList<>();
        String[] colunas = {"IDPEDIDO", "CODPARC", "DTNEG", "DTENTREGA", "CODTIPVENDA", "CODUSU", "OBSERVACAO"};

        Cursor cursor = sqLiteDatabase.query("TASRPEDIDOCABECALHO",
                colunas, // Colunas (null significa todas as colunas)
                "FINALIZADO = ? AND SINCRONIZADO = ? AND EXCLUIDO = ?", // WHERE clause
                new String[]{"S", "N", "N"}, // WHERE args
                null, // Group by
                null, // Having
                null); // Order by

        if (cursor != null) {
            while (cursor.moveToNext()) {
                PedidoPOJO pedidoPOJO = new PedidoPOJO();
                pedidoPOJO.setIdPedido(cursor.getString(0));
                pedidoPOJO.setCodigoParceiro(new BigDecimal(cursor.getInt(1)));
                pedidoPOJO.setDataNegociacao(cursor.getLong(2));
                pedidoPOJO.setDataEntrega(cursor.getLong(3));
                pedidoPOJO.setCodigoTipoNegociacao(new BigDecimal(cursor.getInt(4)));
                pedidoPOJO.setCodigoUsuario(new BigDecimal(cursor.getInt(5)));
                pedidoPOJO.setObservacao(cursor.getString(6));

                ArrayList<ItemPedidoPOJO> itemPedidoPOJOS = buscarItensPorIdPedido(pedidoPOJO.getIdPedido());
                pedidoPOJO.setItensPedidoPOJO(itemPedidoPOJOS);

                listaPedidos.add(pedidoPOJO);
            }
            cursor.close();
        }

        return listaPedidos;
    }

    public void atualizarStatusSincronizadoPedido(PedidoPOJO pedidoPOJO) {
        try(SQLiteDatabase sqLiteDatabase=dataBaseHelper.getWritableDatabase()) {
            String[] parametros = {pedidoPOJO.getIdPedido()};

            ContentValues values = new ContentValues();
            values.put("SINCRONIZADO", "S");

            sqLiteDatabase.update("TASRPEDIDOCABECALHO", values, "IDPEDIDO = ?", parametros);
        }
    }
    public List<PedidoPOJO> buscarTodosPedidos() throws Exception {
        try(SQLiteDatabase sqLiteDatabase=dataBaseHelper.getWritableDatabase()) {
            ArrayList<PedidoPOJO> listaPedidos = new ArrayList<>();
            String[] colunas = {"IDPEDIDO", "CODPARC", "DTNEG", "DTENTREGA", "CODTIPVENDA", "CODUSU", "OBSERVACAO"};

            Cursor cursor = sqLiteDatabase.query("TASRPEDIDOCABECALHO",
                    colunas, // Colunas (null significa todas as colunas)
                    "EXCLUIDO = ?", // WHERE clause
                    new String[]{"N"}, // WHERE args
                    null, // Group by
                    null, // Having
                    null); // Order by

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    PedidoPOJO pedidoPOJO = new PedidoPOJO();
                    pedidoPOJO.setIdPedido(cursor.getString(0));
                    pedidoPOJO.setCodigoParceiro(new BigDecimal(cursor.getInt(1)));
                    pedidoPOJO.setDataNegociacao(cursor.getLong(2));
                    pedidoPOJO.setDataEntrega(cursor.getLong(3));
                    pedidoPOJO.setCodigoTipoNegociacao(new BigDecimal(cursor.getInt(4)));
                    pedidoPOJO.setCodigoUsuario(new BigDecimal(cursor.getInt(5)));
                    pedidoPOJO.setObservacao(cursor.getString(6));

                    ArrayList<ItemPedidoPOJO> itemPedidoPOJOS = buscarItensPorIdPedido(pedidoPOJO.getIdPedido());
                    pedidoPOJO.setItensPedidoPOJO(itemPedidoPOJOS);

                    listaPedidos.add(pedidoPOJO);
                }
                cursor.close();
            }

            return listaPedidos;
        }catch (Exception e){
            throw  new Exception("Erro ao trazer todos os pedidos");
        }
    }
}
