package com.meteordevelopments.study.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PhysicsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PhysicsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is physics fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}