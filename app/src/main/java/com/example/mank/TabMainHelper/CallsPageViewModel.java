package com.example.mank.TabMainHelper;

import static com.example.mank.MainActivity.MainActivityStaticContext;

import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mank.RecyclerViewClassesFolder.CallsRecyclerViewAdapter;

import java.util.ArrayList;

public class CallsPageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    public CallsRecyclerViewAdapter callsRecyclerViewAdapter;


    private LiveData<CallsRecyclerViewAdapter> mText = Transformations.map(mIndex, new Function<Integer, CallsRecyclerViewAdapter>() {
        @Override
        public CallsRecyclerViewAdapter apply(Integer input) {

            Log.d("log-CallsPageViewModel", "Transformations.map || mIndex:" + mIndex.getValue());
            Log.d("log-CallsPageViewModel", "Transformations.map || input:" + input);

            ArrayList<Long> contactStatusList = new ArrayList<>();
            contactStatusList.add((long)99);
            callsRecyclerViewAdapter = new CallsRecyclerViewAdapter(MainActivityStaticContext, contactStatusList);
            return  callsRecyclerViewAdapter;
        }
    });

    public void setIndex(int index) {
        mIndex.setValue(index);
        Log.d("log-CallsPageViewModel", "setIndex || mIndex:" + mIndex.getValue());
    }

    public LiveData<CallsRecyclerViewAdapter> getCallsRecyclerViewAdapter() {
        Log.d("log-CallsPageViewModel", "getText || mIndex:" + mIndex.getValue());
        return mText;
    }

    public LiveData<String> getTry() {
        Log.d("log-CallsPageViewModel", "mIndex:" + mIndex);
        return tryString;
    }

    private LiveData<String> tryString = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            return "null string";
        }
    });
}