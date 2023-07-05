package com.example.mank.RecyclerViewClassesFolder;

import static com.example.mank.AllContactOfUserInDeviceView.massegeDao;
import static com.example.mank.MainActivity.Contact_page_opened_id;
import static com.example.mank.MainActivity.contactListAdapter;
import static com.example.mank.MainActivity.user_login_id;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mank.ContactMassegeDetailsView;
import com.example.mank.LocalDatabaseFiles.entities.AllContactOfUserEntity;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;
import com.example.mank.R;
import com.example.mank.RecyclerViewClassesFolder.SearchModel.CourseModel;

import java.util.ArrayList;
import java.util.List;

public class ContactSyncMainRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context context;
    public static List<AllContactOfUserEntity> contactList;

    private ArrayList<CourseModel> courseModelArrayList;

    public void filterList(ArrayList<CourseModel> filterList) {
        // below line is to add our filtered
        // list in our course array list.
        courseModelArrayList = filterList;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    public ContactSyncMainRecyclerViewAdapter(Context context, List<AllContactOfUserEntity> contactList) {
        this.contactList = contactList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == 0 || viewType == 3) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row_for_all_contact_sync_page, parent, false);
            return new ViewHolder(view);
        } else if (viewType == 1) {
            View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.allcontact_sync_devider_label, parent, false);
            return new ViewHolder1(view1);
        } else if (viewType == 2) {
            View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.allcontact_sync_devider_label, parent, false);
            return new ViewHolder1(view1);
        }
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.allcontact_sync_devider_label, parent, false);
        return new ViewHolder1(view1);


    }

    @Override
    public int getItemViewType(int position) {
        AllContactOfUserEntity contact = contactList.get(position);
//        Log.d("log-ContactSyncMainRecyclerViewAdapter", "contact.getCID()  : " + contact.getCID() + " DisplayName: "+contact.getDisplay_name());
        if (contact.getCID().equals("-5")) {
            return 3;
        } else if (contact.getCID().equals("-101")) {
            return 2;
        } else if (contact.getCID().equals("-100")) {//for contact on massenger label
            return 1;
        }
        return 0;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case 0:
                ViewHolder viewHolder = (ViewHolder) holder;
                AllContactOfUserEntity contact = contactList.get(position);
                viewHolder.Display_Name.setText(contact.getDisplayName());
                viewHolder.LastMassegeOfContact.setText(String.valueOf(contact.getMobileNumber()));
                viewHolder.DPImageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.b_user_image));
//                if (contact.getCID() == -1) {
//                    viewHolder.InviteText.setVisibility(View.VISIBLE);
//                }
                break;
            case 3:
                ViewHolder viewHolder3 = (ViewHolder) holder;
                AllContactOfUserEntity contact1 = contactList.get(position);
                viewHolder3.Display_Name.setText(contact1.getDisplayName());
                viewHolder3.LastMassegeOfContact.setText(String.valueOf(contact1.getMobileNumber()));
                viewHolder3.DPImageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.null_user_image));
                viewHolder3.InviteText.setVisibility(View.VISIBLE);
                break;

            case 1:
                ViewHolder1 viewHolder1 = (ViewHolder1) holder;
                viewHolder1.main_label.setText("Contact on Massenger");
                break;
            case 2:
                ViewHolder1 viewHolder2 = (ViewHolder1) holder;
                viewHolder2.main_label.setText("Invite To Massenger");
                break;

            default:
                ViewHolder1 viewHolder0 = (ViewHolder1) holder;
                viewHolder0.main_label.setText("not matched with any type of label");
                break;
        }

    }

    @Override
    public int getItemCount() {
//        Log.d("log-getItemCount", "getItemCount: size is : " + contactList.size());
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView Display_Name;
        public TextView LastMassegeOfContact;
        public ImageView DPImageButton;
        public TextView InviteText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            Display_Name = itemView.findViewById(R.id.Display_Name);
            LastMassegeOfContact = itemView.findViewById(R.id.LastMassegeOfContact);
            DPImageButton = itemView.findViewById(R.id.DPImageButton);
            InviteText = itemView.findViewById(R.id.InviteTextForOtherPeople);

        }

        @Override
        public void onClick(View view) {
            Log.d("log-clicked", "you clicked Contact recyclerView_main");
            int position = this.getAdapterPosition();
            AllContactOfUserEntity contact = contactList.get(position);
            String CID = contact.getCID();
            if (CID.equals("-1")) {
                Toast.makeText(context, "Invite your friend on Massenger", Toast.LENGTH_LONG).show();
            } else {

                long phone = contact.getMobileNumber();
                Contact_page_opened_id = CID;
                String ContactName = contact.getDisplayName();

//                Toast.makeText(context, "The position is " + (position) +
//                        " Name: " + ContactName + ", Phone:" + phone + ", CID:" + CID, Toast.LENGTH_SHORT).show();

                // save contact to contactDetails table
                Thread tx = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ContactWithMassengerEntity y = massegeDao.getContactWith_CID(CID, user_login_id);
                        if(y == null){
                            //if first time then store it to contactDetails table
                            long x = massegeDao.getHighestPriorityRank(user_login_id);
                            ContactWithMassengerEntity new_entity = new ContactWithMassengerEntity(phone, ContactName, CID, x + 1);
                            contactListAdapter.AddContact(new_entity); // adapter add into database as well as reflect into UI
//                            massegeDao.SaveContactDetailsInDatabase(new_entity);
                        }

                    }
                });
                tx.start();

                // staring intent
                Intent intent = new Intent(context.getApplicationContext(), ContactMassegeDetailsView.class);
                intent.putExtra("CID", CID);
                intent.putExtra("ContactMobileNumber", phone);
                intent.putExtra("ContactName", ContactName);
                intent.putExtra("RecyclerviewPosition", position);
                context.startActivity(intent);
                //we are saving opened_contactChatView as CID
//                Log.d("log-opened_contactChatView", "onClick: opened_contactChatView is : " + Contact_page_opened_id);
//            setOpened_contactChatViewToEmpty(position);
            }
        }
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {

        public TextView main_label;
        public TextView small_massege;

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            main_label = itemView.findViewById(R.id.label_boundary_1_main);
//            small_massege = itemView.findViewById(R.id.label_boundary_1_small);

        }
    }

}

