package br.com.constance.app.appkstore.ui.logoff;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LogoffViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LogoffViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}