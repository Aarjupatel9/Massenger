package com.example.mank.TabMainHelper;

import static com.example.mank.MainActivity.MainActivityStaticContext;
import static com.example.mank.MainActivity.user_login_id;

import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mank.RecyclerViewClassesFolder.StatusRecyclerViewAdapter;

import java.util.ArrayList;

public class StatusPageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    public StatusRecyclerViewAdapter statusRecyclerViewAdapter;


    private LiveData<StatusRecyclerViewAdapter> mText = Transformations.map(mIndex, new Function<Integer, StatusRecyclerViewAdapter>() {
        @Override
        public StatusRecyclerViewAdapter apply(Integer input) {

            Log.d("log-StatusPageViewModel", "Transformations.map || mIndex:" + mIndex.getValue());
            Log.d("log-StatusPageViewModel", "Transformations.map || input:" + input);

            ArrayList<Long> contactStatusList = new ArrayList<>();
            contactStatusList.add(user_login_id);
            statusRecyclerViewAdapter = new StatusRecyclerViewAdapter(MainActivityStaticContext, contactStatusList);
            return  statusRecyclerViewAdapter;
        }
    });

    public void setIndex(int index) {
        mIndex.setValue(index);
        Log.d("log-PageViewModel", "setIndex || mIndex:" + mIndex.getValue());
    }

    public LiveData<StatusRecyclerViewAdapter> getStatusRecyclerViewAdapter() {

        Log.d("log-StatusPageViewModel", "getText || mIndex:" + mIndex.getValue());
        return mText;

    }


    public LiveData<String> getTry() {
        Log.d("log-PageViewModel", "mIndex:" + mIndex);
        return tryString;
    }

    private LiveData<String> tryString = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            return "null string";
        }
    });
}