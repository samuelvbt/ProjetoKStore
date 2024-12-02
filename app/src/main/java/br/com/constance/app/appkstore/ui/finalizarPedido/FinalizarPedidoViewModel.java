package br.com.constance.app.appkstore.ui.finalizarPedido;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FinalizarPedidoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public FinalizarPedidoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}