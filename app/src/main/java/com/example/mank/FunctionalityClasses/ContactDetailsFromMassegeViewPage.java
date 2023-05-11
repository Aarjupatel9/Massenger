package com.example.mank.FunctionalityClasses;

import static com.example.mank.MainActivity.Contact_page_opened_id;
import static com.example.mank.MainActivity.socket;
import static com.example.mank.MainActivity.user_login_id;
import static com.example.mank.MainActivity.db;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.room.Room;

import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.R;

import io.socket.emitter.Emitter;

public class ContactDetailsFromMassegeViewPage extends Activity {

    TextView contact_display_name;
    TextView contact_about_details;
    TextView contact_mobile_number;

    private long C_ID;
    private long ContactMobileNumber;
    private String ContactName;

    @Override
    public  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contact_details_from_massege_view_page);

        Intent intent = getIntent();
        C_ID =  intent.getLongExtra("C_ID", -1);
        ContactMobileNumber = intent.getLongExtra("ContactMobileNumber", -2);
        ContactName = intent.getStringExtra("ContactName");
        //we have to fetch data from server
        socket.emit("getContactDetailsForContactDetailsFromMassegeViewPage", user_login_id, Contact_page_opened_id);
        socket.on("getContactDetailsForContactDetailsFromMassegeViewPage_return", onGetContactDetailsForContactDetailsFromMassegeViewPage_return);

        Thread db_work = new Thread(new Runnable() {
            @Override
            public void run() {
                db = Room.databaseBuilder(getApplicationContext(),
                        MainDatabaseClass.class, "MassengerDatabase").fallbackToDestructiveMigration().allowMainThreadQueries().build();

            }
        });

        contact_display_name = (TextView)  findViewById(R.id.contact_display_name);
        contact_about_details = (TextView)  findViewById(R.id.contact_about_details);
        contact_mobile_number = (TextView)  findViewById(R.id.contact_phone_number);

        contact_mobile_number.setText(String.valueOf(ContactMobileNumber));
        contact_display_name.setText(ContactName);

    }


    private final Emitter.Listener onGetContactDetailsForContactDetailsFromMassegeViewPage_return = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("log-onCheckContactOnlineStatus_return", "call: onMassegeReachReceiptFromServer enter");
            try {
                int contact_id = (int) args[0];
                String display_name = (String) args[1];
                String contact_about = (String) args[2];
                Log.d("log-onCheckContactOnlineStatus_return", "contact_id: " + contact_id);
                Log.d("log-onCheckContactOnlineStatus_return", "contact_id: " + contact_about);
                Log.d("log-onCheckContactOnlineStatus_return", "display_name: " + display_name);
                contact_about_details.setText(contact_about);
                contact_display_name.setText(display_name);
//                contact_display_name.setText();
                if(display_name == null){
                    contact_display_name.setText("not set");
                }


            } catch (Exception e) {
                Log.d("log-onCheckContactOnlineStatus_return-exception", "Exception arive : " + e);
            }
        }
    };
}
