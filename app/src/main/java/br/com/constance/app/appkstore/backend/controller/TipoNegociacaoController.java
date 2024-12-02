package br.com.constance.app.appkstore.backend.controller;

import android.content.Context;

import br.com.constance.app.appkstore.backend.dao.TipoNegociacaoDao;
import br.com.constance.app.appkstore.backend.services.negociacao.TiposNegociacaoService;
import br.com.constance.app.appkstore.backend.services.pojo.TipoNegociacaoPOJO;

public class TipoNegociacaoController {
    private TipoNegociacaoPOJO[] listaTipoNegociacaoPOJO;
    private TipoNegociacaoDao tipoNegociacaoDao;
    private TiposNegociacaoService tiposNegociacaoService;
    private Context context;

    public TipoNegociacaoController(Context context) {
        this.context = context;
        tipoNegociacaoDao = new TipoNegociacaoDao(context);
        tiposNegociacaoService = new TiposNegociacaoService(context);
    }

    public void carregarBaseTipoNegociacao() throws Exception {
        limparDadosTabela();
        buscarDadosTipoNegociacao();
        persistirDadosTipoNegociacao();
    }

    private void limparDadosTabela() {
        tipoNegociacaoDao.open();
        boolean tabelaParceiroPopulada = tipoNegociacaoDao.isTabelaTipoNegociacaoPopulada();

        if(tabelaParceiroPopulada){
            tipoNegociacaoDao.limparTabelaTipoNegociacao();
        }

        tipoNegociacaoDao.close();
    }

    private void buscarDadosTipoNegociacao() throws Exception {
        tiposNegociacaoService.obterDadosTipoNegociacao();
        listaTipoNegociacaoPOJO = tiposNegociacaoService.getListaTipoNegociacaoPOJO();
    }

    private void persistirDadosTipoNegociacao() {
        tipoNegociacaoDao.open();
        for (TipoNegociacaoPOJO tipoNegociacaoPOJO : listaTipoNegociacaoPOJO) {
            tipoNegociacaoDao.salvarTipoNegociacao(tipoNegociacaoPOJO);
        }
        tipoNegociacaoDao.close();
    }
}
