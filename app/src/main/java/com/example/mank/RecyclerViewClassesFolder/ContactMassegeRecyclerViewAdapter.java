package com.example.mank.RecyclerViewClassesFolder;


import static com.example.mank.MainActivity.user_login_id;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mank.LocalDatabaseFiles.entities.MassegeEntity;
import com.example.mank.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ContactMassegeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<MassegeEntity> massegeList;

    public ContactMassegeRecyclerViewAdapter(Context context, List<MassegeEntity> massegeList) {
        this.massegeList = massegeList;
        this.context = context;
    }

    public int getItemViewType(int position) {
        MassegeEntity massege = massegeList.get(position);
        if (massege.getSenderId() == user_login_id) {
//            Log.d("log-recyclerview", "getItemViewType: sender id is : "+massege.getSenderId() + " userid: "+user_login_id);
//            Log.d("log-recyclerview", "getItemViewType: ReceiverId is : "+massege.getReceiverId());
//            Log.d("log-recyclerview", "getItemViewType: massege is : "+massege.getMassege());
            return 1;
        } else {
//            Log.d("log-recyclerview", "getItemViewType: else cond. sender id is : "+massege.getSenderId() + " userid: "+user_login_id);
//            Log.d("log-recyclerview", "getItemViewType: else cond. ReceiverId id is : "+massege.getReceiverId());
//            Log.d("log-recyclerview", "getItemViewType: else cond. massege id is : "+massege.getMassegeID());

            return 0;

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.massege_design_row_for_user, parent, false);
            return new ViewHolderUser(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.massege_design_row, parent, false);
            return new ViewHolderContact(view);

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            ViewHolderContact viewHolder = (ViewHolderContact) holder;
            MassegeEntity massege = massegeList.get(position);
            viewHolder.massege_display.setText(massege.getMassege());
            viewHolder.massege_id.setText(String.valueOf(massege.getChat_id()));
            Date date = new Date(massege.getTimeOfSend());
            String formatted_date = new SimpleDateFormat("HH:mm").format(date);
            viewHolder.contact_main_massege_time.setText(formatted_date);
        } else {
            ViewHolderUser viewHolder1 = (ViewHolderUser) holder;
            MassegeEntity massege1 = massegeList.get(position);
            viewHolder1.user_main_massege.setText(massege1.getMassege());
            viewHolder1.user_main_massege_id.setText(String.valueOf(massege1.getChat_id()));
            Date date = new Date(massege1.getTimeOfSend());
            String formatted_date = new SimpleDateFormat("HH:mm").format(date);
            viewHolder1.user_main_massege_time.setText(formatted_date);

            if (massege1.getMassegeStatus() == 1 || massege1.getMassegeStatus() == 0) {
                viewHolder1.user_massege_status_main.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_massege_sent_icon));
            } else if (massege1.getMassegeStatus() == 2) {
                viewHolder1.user_massege_status_main.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_massege_reach_icon));
            } else if (massege1.getMassegeStatus() == 3) {
                viewHolder1.user_massege_status_main.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_massege_read_icon));
            } else if (massege1.getMassegeStatus() == 5) {
                viewHolder1.user_massege_status_main.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_offline_massege_state_icon));
            } else {
                viewHolder1.user_massege_status_main.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_all));
            }
        }
    }

    @Override
    public int getItemCount() {
//        Log.d("log-getItemCount", "getItemCount: size is : " + massegeList.size());
        return massegeList.size();
    }

    public int getItemCountMyOwn() {
        return (massegeList.size() - 1);
    }


    public class ViewHolderContact extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView massege_display;
        public TextView massege_id;
        public TextView contact_main_massege_time;


        public ViewHolderContact(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            massege_display = itemView.findViewById(R.id.contact_main_massege);
            massege_id = itemView.findViewById(R.id.contact_main_massege_id);
            contact_main_massege_time = itemView.findViewById(R.id.contact_main_massege_time);
        }

        @Override
        public void onClick(View view) {
//            int position = this.getAdapterPosition();
//            MassegeEntity massege = massegeList.get(position);
            Log.d("log-clicked", "you clicked on massege " );//+ massege.getMassege());
        }
    }

    public class ViewHolderUser extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView user_main_massege;
        public TextView user_main_massege_id;
        public TextView user_main_massege_time;
        public ImageView user_massege_status_main;
        public ImageView contact_main_massege_time;

        public ViewHolderUser(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            user_main_massege = itemView.findViewById(R.id.user_main_massege);
            user_main_massege_id = itemView.findViewById(R.id.user_main_massege_id);
            user_massege_status_main = itemView.findViewById(R.id.user_massege_status_main);
            user_main_massege_time = itemView.findViewById(R.id.user_main_massege_time);
        }

        @Override
        public void onClick(View view) {
//            int position = this.getAdapterPosition();
//            MassegeEntity massege = massegeList.get(position);
//            Log.d("log-clicked", "you clicked on massege " + massege.getMassege());
        }
    }
}