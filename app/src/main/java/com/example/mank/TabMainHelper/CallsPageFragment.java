package com.example.mank.TabMainHelper;


import static com.example.mank.MainActivity.MainActivityStaticContext;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mank.RecyclerViewClassesFolder.CallsRecyclerViewAdapter;
import com.example.mank.databinding.ActivityZTabStatusPageBinding;

/**
 * A placeholder fragment containing a simple view.
 */
public class CallsPageFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private CallsPageViewModel callsPageViewModel;
    private ActivityZTabStatusPageBinding binding;

    private RecyclerView recyclerView;
    public static CallsPageFragment newInstance(int index) {
        CallsPageFragment fragment = new CallsPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callsPageViewModel = new ViewModelProvider(this).get(CallsPageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        callsPageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = ActivityZTabStatusPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.TSPRecyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivityStaticContext));
        callsPageViewModel.getCallsRecyclerViewAdapter().observe(getViewLifecycleOwner(), new Observer<CallsRecyclerViewAdapter>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(@NonNull CallsRecyclerViewAdapter recyclerViewAdapter) {
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}