package com.example.mank.RecyclerViewClassesFolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mank.R;

import java.util.List;

public class StatusRecyclerViewAdapter extends RecyclerView.Adapter<StatusRecyclerViewAdapter.ViewHolder> {


    public Context context;
    public List<String> ContactStatusList;

    public StatusRecyclerViewAdapter(Context context, List<String> ContactStatusList) {
        this.ContactStatusList = ContactStatusList;
        this.context = context;
    }

    @NonNull
    @Override
    public StatusRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_status_view_row, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull StatusRecyclerViewAdapter.ViewHolder holder, int position) {
        String id = ContactStatusList.get(position);
        holder.SPContactName.setText("status is coming soon...");
        holder.SPStatusPostingTime.setText("");
    }

    @Override
    public int getItemCount() {
        return ContactStatusList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView SPContactName;
        public TextView SPStatusPostingTime;
        public ConstraintLayout SPStatusContactConstraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            SPContactName = itemView.findViewById(R.id.SPContactName);
            SPStatusPostingTime = itemView.findViewById(R.id.SPStatusPostingTime);
            SPStatusContactConstraintLayout = itemView.findViewById(R.id.SPStatusContactConstraintLayout);

        }

        @Override
        public void onClick(View view) {
        }
    }
}
