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

public class CallsRecyclerViewAdapter extends RecyclerView.Adapter<CallsRecyclerViewAdapter.ViewHolder> {


    public Context context;
    public List<Long> ContactStatusList;

    public CallsRecyclerViewAdapter(Context context, List<Long> ContactStatusList) {
        this.ContactStatusList = ContactStatusList;
        this.context = context;
    }

    @NonNull
    @Override
    public CallsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_status_view_row, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull CallsRecyclerViewAdapter.ViewHolder holder, int position) {
        long id = ContactStatusList.get(position);
        holder.SPContactName.setText("calling is coming soon...");
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
