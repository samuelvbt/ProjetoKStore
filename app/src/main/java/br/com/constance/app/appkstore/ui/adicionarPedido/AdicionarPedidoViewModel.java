package br.com.constance.app.appkstore.ui.adicionarPedido;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdicionarPedidoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AdicionarPedidoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}