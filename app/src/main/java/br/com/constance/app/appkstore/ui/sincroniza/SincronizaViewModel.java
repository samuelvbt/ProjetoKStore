package br.com.constance.app.appkstore.ui.sincroniza;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SincronizaViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SincronizaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}