package br.com.constance.app.appkstore.ui.itemPedido;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ItemPedidoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ItemPedidoViewModel() {
        mText = new MutableLiveData<>();
//        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}