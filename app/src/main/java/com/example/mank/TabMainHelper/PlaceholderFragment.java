package com.example.mank.TabMainHelper;


import static com.example.mank.MainActivity.ChatsRecyclerView;
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

import com.example.mank.RecyclerViewClassesFolder.RecyclerViewAdapter;
import com.example.mank.databinding.ActivityZTabPageBinding;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private ActivityZTabPageBinding binding;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = ActivityZTabPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ChatsRecyclerView = binding.TCPContactRecyclerView;
        ChatsRecyclerView.setHasFixedSize(true);
        ChatsRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivityStaticContext));
        pageViewModel.getRecyclerViewAdapter().observe(getViewLifecycleOwner(), new Observer<RecyclerViewAdapter>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(@NonNull RecyclerViewAdapter recyclerViewAdapter) {
                ChatsRecyclerView.setAdapter(recyclerViewAdapter);
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