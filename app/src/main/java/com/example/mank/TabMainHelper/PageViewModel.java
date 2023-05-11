package com.example.mank.TabMainHelper;

import static com.example.mank.MainActivity.MainActivityStaticContext;
import static com.example.mank.MainActivity.MainContactListHolder;
import static com.example.mank.MainActivity.contactArrayList;
import static com.example.mank.MainActivity.filterdContactArrayList;
import static com.example.mank.MainActivity.db;
import static com.example.mank.MainActivity.recyclerViewAdapter;
import static com.example.mank.MainActivity.statusForThread;

import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mank.LocalDatabaseFiles.DataContainerClasses.ContactListHolder;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;
import com.example.mank.RecyclerViewClassesFolder.RecyclerViewAdapter;

import java.util.ArrayList;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();

    private LiveData<RecyclerViewAdapter> mText = Transformations.map(mIndex, new Function<Integer, RecyclerViewAdapter>() {
        @Override
        public RecyclerViewAdapter apply(Integer input) {

            Log.d("log-PageViewModel", "Transformations.map || mIndex:" + mIndex.getValue());
            Log.d("log-PageViewModel", "Transformations.map || input:" + input);
            Log.d("log-PageViewModel", "Transformations.map || enter null cond.");
            if (contactArrayList == null) {
                contactArrayList = new ArrayList<>();
                MainContactListHolder = new ContactListHolder(db);
                contactArrayList = MainContactListHolder.MainContactList;
                filterdContactArrayList = (ArrayList<ContactWithMassengerEntity>) contactArrayList.clone();

                synchronized (statusForThread) {
                    statusForThread.setValue(1);
                    Log.d("log-onMassegeArriveFromServer1", "HomePageWithContactActivity.contactArrayList before notifyAll()");
                    statusForThread.notifyAll();
                }
                recyclerViewAdapter = new RecyclerViewAdapter(MainActivityStaticContext, contactArrayList);
            }
            return recyclerViewAdapter;
        }

//        @SuppressLint("NotifyDataSetChanged")
//        public void contactArrayListFilter(String newText, int flag) {
//            if (flag == 0) {
//                Log.d("log-MainActivity", "contactArrayListFilter start with flag 0");
//                recyclerViewAdapter = new RecyclerViewAdapter(MainActivityStaticContext, contactArrayList);
//                recyclerViewAdapter.notifyDataSetChanged();
//                return;
//            }
//            filterdContactArrayList = new ArrayList<ContactWithMassengerEntity>();
//            recyclerViewAdapter = new RecyclerViewAdapter(MainActivityStaticContext, filterdContactArrayList);
//            Log.d("log-MainActivity", "contactArrayListFilter start");
//            for (ContactWithMassengerEntity e : contactArrayList) {
//                if (e.getDisplay_name().toLowerCase().contains(newText.toLowerCase())) {
//                    filterdContactArrayList.add(e);
//                    recyclerViewAdapter.notifyDataSetChanged();
//                }
//            }
//        }

    });

    public void setIndex(int index) {
        mIndex.setValue(index);
        Log.d("log-PageViewModel", "setIndex || mIndex:" + mIndex.getValue());
    }

    public LiveData<RecyclerViewAdapter> getRecyclerViewAdapter() {
        Log.d("log-PageViewModel", "getText || mIndex:" + mIndex.getValue());
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