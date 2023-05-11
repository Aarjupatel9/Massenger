package com.example.mank.RecyclerViewClassesFolder;

import static com.example.mank.MainActivity.ChatsRecyclerView;
import static com.example.mank.MainActivity.Contact_page_opened_id;
import static com.example.mank.MainActivity.db;
import static com.example.mank.MainActivity.setNewMassegeArriveValueToEmpty;
import static com.example.mank.MainActivity.user_login_id;
import static com.google.android.material.internal.ContextUtils.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mank.ContactMassegeDetailsView;
import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;
import com.example.mank.MainActivity;
import com.example.mank.MainActivityClassForContext;
import com.example.mank.R;

import java.util.List;
import java.util.Objects;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final Context context;
    public static List<ContactWithMassengerEntity> contactList;

    public RecyclerViewAdapter(Context context, List<ContactWithMassengerEntity> contactList) {
        this.contactList = contactList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactviewrow, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    View.OnClickListener DpImageClickedFunction(int position) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("log-clicked-image_of-contact", "onClick: position is : " + position);
                RecyclerViewAdapter.ViewHolder holder = (ViewHolder) ChatsRecyclerView.findViewHolderForAdapterPosition(position);
                Log.d("log-clicked-image_of-contact", "onClick: holder at this position is : " + holder);

                String name = holder.Display_Name.getText().toString();
                Log.d("log-clicked-image_of-contact", "onClick: name in holder at this position is : " + name);

                if (holder != null) {
                    Log.d("log-clicked-image_of-contact", "onClick: enter in holder not null condition");
                    holder.Display_Name.setText("jiii");
                    String name1 = holder.Display_Name.getText().toString();
                    Log.d("log-clicked-image_of-contact", "onClick: name in holder at this position is after : " + name1);

//                    Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(position)).setBackgroundColor(Color.RED);
                    MainActivity.recyclerViewAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        ContactWithMassengerEntity contact = contactList.get(position);

        if (contact.getC_ID() == user_login_id) {
            String name = contact.getDisplay_name() + " (self)";
            holder.Display_Name.setText(name);
        } else {
            if (contact.getDisplay_name() == null) {
                holder.Display_Name.setText(String.valueOf(contact.getMobileNumber()));
            } else {
                holder.Display_Name.setText(contact.getDisplay_name());
            }
        }

        holder.LastMassegeOfContact.setText(String.valueOf(contact.getMobileNumber()));
        holder.DPImageButton.setOnClickListener(DpImageClickedFunction(position));
        if (contact.isTouchEffectPass()) {
            holder.constraintLayout.setBackgroundColor(Color.argb(75, 100, 159, 107));
        }

        holder.DPImageButton.setImageDrawable(MainActivityClassForContext.getAppContext().getResources().getDrawable(R.drawable.ic_baseline_person_24));
        if (contact.getNewMassegeArriveValue() == 0) {
            holder.new_massege_arrive_value.setText("");
        } else {
            holder.new_massege_arrive_value.setText(String.valueOf(contact.getNewMassegeArriveValue()));
        }

    }

    @Override
    public int getItemCount() {
//        Log.d("log-getItemCount", "getItemCount: size is : " + contactList.size());
        return contactList.size();
    }

    public int getItemCountMyOwn() {
        if (contactList.size() == 0) {
            return -1;
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView Display_Name;
        public TextView new_massege_arrive_value;
        public TextView LastMassegeOfContact;
        public ImageView DPImageButton;
        public ConstraintLayout constraintLayout;
//        public CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            Display_Name = itemView.findViewById(R.id.Display_Name);
            new_massege_arrive_value = itemView.findViewById(R.id.new_massege_arrive_value);
            LastMassegeOfContact = itemView.findViewById(R.id.LastMassegeOfContact);
            DPImageButton = itemView.findViewById(R.id.DPImageButton);
            constraintLayout = itemView.findViewById(R.id.contactViewRowConstraintLayout);
//            cardView = itemView.findViewById(R.id.contactViewRowCardView);
//            DPImageButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Log.d("log-clicked", "you clicked Contact recyclerView_main");
            int position = this.getAdapterPosition();
            ContactWithMassengerEntity contact = contactList.get(position);
            String name = contact.getDisplay_name();
            long phone = contact.getMobileNumber();
            long C_ID = contact.getC_ID();
            Contact_page_opened_id = C_ID;
            String ContactName = contact.getDisplay_name();

            Toast.makeText(context, "The position is " + String.valueOf(position) +
                    " Name: " + name + ", Phone:" + phone + ", c_ID:"+C_ID, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(context.getApplicationContext(), ContactMassegeDetailsView.class);
            intent.putExtra("C_ID", C_ID);
            intent.putExtra("ContactMobileNumber", phone);
            intent.putExtra("ContactName", ContactName);
            intent.putExtra("RecyclerviewPosition", position);
            //we are saving opened_contactChatView as C_ID
            Log.d("log-opened_contactChatView", "onClick: opened_contactChatView is : " + Contact_page_opened_id);
            context.startActivity(intent);
            setNewMassegeArriveValueToEmpty(position);
        }

        @Override
        public boolean onLongClick(View view) {
            Log.d("log-enter", "getMainSideMenu: enter here");
            PopupMenu popup = new PopupMenu(context, view);
            popup.setGravity(Gravity.CENTER_HORIZONTAL);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.long_press_on_contact_popup_menu, popup.getMenu());
            popup.show();
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @SuppressLint({"NotifyDataSetChanged", "RestrictedApi"})
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = ViewHolder.this.getAdapterPosition();
                    ContactWithMassengerEntity contact = contactList.get(position);
                    long phone = contact.getMobileNumber();
                    long CID = contact.getC_ID();
                    if (item.getItemId() == R.id.LPOCPMDelete) {
                        Toast.makeText(context, "LPOCPMDelete", Toast.LENGTH_SHORT).show();

                        MainActivity.contactArrayList.removeIf(e -> e.getC_ID() == CID);
                        MainActivity.filterdContactArrayList.removeIf(e -> e.getC_ID() == CID);
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MassegeDao massegeDao = db.massegeDao();;
                               int r1= massegeDao.removeChatsFromMassegeTable(CID);
                                int r2 = massegeDao.removeSelfContactFromContactTable(CID);
                            }
                        });
                        t.start();
                        Objects.requireNonNull(getActivity(context)).runOnUiThread(new Runnable() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void run() {
                                MainActivity.recyclerViewAdapter.notifyDataSetChanged();
                            }
                        });

                    } else if (item.getItemId() == R.id.LPOCPMClearChat) {
                        Toast.makeText(context, "LPOCPMClearChat", Toast.LENGTH_SHORT).show();
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MassegeDao massegeDao = db.massegeDao();;
                                int r1= massegeDao.removeChatsFromMassegeTable(CID);
                            }
                        });
                        t.start();
                    }
                    return false;
                }
            });

            return false;
        }

    }
}
