package com.example.mank.ThreadPackages;

import static com.example.mank.configuration.GlobalVariables.URL_MAIN;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.contactDetailsHolderForSync;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;
import com.example.mank.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncContactDetailsThread extends Thread {
    MainActivity parent;
    MainDatabaseClass db;
    Cursor cursor;
    MassegeDao massegeDao;

    public SyncContactDetailsThread(MainActivity h, MainDatabaseClass db, Cursor cursor) {
        this.parent = h;
        this.db = db;
        this.cursor = cursor;
        massegeDao = db.massegeDao();

    }

    public void run() {
        //Do your background thing here
        Log.d("log-", "syncContactDetails: enter here");
        JSONArray ContactDetails = new JSONArray();
        String endpoint = URL_MAIN + "syncContactOfUser";
        RequestQueue requestQueue = Volley.newRequestQueue(parent);
        Log.d("log-HomePageWithContactActivity", "syncContactDetails: enter here endpoint is : " + endpoint);
//                loadingPB.setVisibility(View.VISIBLE);
        Log.d("log-HomePageWithContactActivity", "syncContactDetails:  before request initialize");
        StringRequest request = new StringRequest(Request.Method.POST, endpoint, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                contactDetailsHolderForSync contactDetailsHolder = new contactDetailsHolderForSync(db);
                List<ContactWithMassengerEntity> contactList = contactDetailsHolder.getData();

                int contactList_size = contactList.size();
                Log.d("log-sync contact-database", "  setChatDetails Database Response: " + contactList_size);
                Log.d("log-response-in-syncContact", response);
                try {
                    JSONObject respObj = new JSONObject(response);
                    String mainString = respObj.getString("ja");
                    int isOn_number = Integer.parseInt(respObj.getString("isOnnumber"));
                    Log.d("log-allcoantact sync response", "onResponse: isOnnumber is : " + isOn_number);
                    JSONArray responseArray = new JSONArray(mainString);
                    int l = responseArray.length();
                    for (int i = 0; i < isOn_number; i++) {
                        String RowOfArray = responseArray.getString(i);
                        JSONObject RowObject = new JSONObject(RowOfArray);
                        Log.d("log-setChatDetails-Response", "  setChatDetails onResponse C_ID is: " + RowObject.getString("C_ID"));
                        Log.d("log-setChatDetails-Response", "  setChatDetails onResponse number is : " + RowObject.getString("number"));
                        Log.d("log-setChatDetails-Response", "  setChatDetails onResponse nme is : : " + RowObject.getString("name"));

                        if (contactList_size == 0) {
                            //now we have to save all details in contact table
                            Log.d("log-sync contact-database", "  we have to save all contact with C_ID :" + Integer.parseInt(RowObject.getString("C_ID")));
                            ContactWithMassengerEntity newEntity = new ContactWithMassengerEntity(Long.valueOf(RowObject.getString("number")), RowObject.getString("name"), Integer.parseInt(RowObject.getString("C_ID")));
                            massegeDao.SaveContactDetailsInDatabase(newEntity);
                        } else {
                            //we have to check whether user is all ready connected or not
                            //if not then save details into chat
                            boolean already = false;
                            for (int j = 0; j < contactList_size; j++) {
                                ContactWithMassengerEntity contactEntity = contactList.get(j);
                                if (contactEntity.getMobileNumber().equals(Long.valueOf(RowObject.getString("number")))) {
                                    already = true;
                                }
//                                Log.d("log-sync contact-database", "  setChatDetails Database Response: " + contactEntity.getMobileNumber());
                            }
                            if (!already) {
                                ContactWithMassengerEntity newEntity = new ContactWithMassengerEntity(Long.valueOf(RowObject.getString("number")), RowObject.getString("name"), Integer.parseInt(RowObject.getString("C_ID")));
                                massegeDao.SaveContactDetailsInDatabase(newEntity);
                            }
                        }
                    }
                    //now we have delete of those contact who delete their account
                    for (int j = 0; j < contactList.size(); j++) {
                        boolean already = false;
                        ContactWithMassengerEntity contactEntity = contactList.get(j);
                        for (int i = 0; i < isOn_number; i++) {
                            String RowOfArray = responseArray.getString(i);
                            JSONObject RowObject = new JSONObject(RowOfArray);
                            if (contactEntity.getMobileNumber().equals(Long.valueOf(RowObject.getString("number")))) {
                                already = true;
                            }
                        }
                        if (!already) {
                            massegeDao.deleteContactDetailsInDatabase(contactEntity.getMobileNumber());
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(parent, "application error! , please try again after some time", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
//                    loadingPB.setVisibility(View.GONE);
                }
//                parent.SetChatsView(new View(parent));
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Log.d("log-setChatDetails-Response", "  setChatDetails error: " + error);
//                        loadingPB.setVisibility(View.GONE);
                Toast.makeText(parent, "sync failed Duo to  Server side error :  " + error, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();
                Log.d("log-contact", "getContacts: enterd and number is " + cursor.getCount());

                if (cursor.getCount() > 0) {
                    int counter = 0;
                    while (cursor.moveToNext()) {
                        @SuppressLint("Range") String display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        number = number.replaceAll("\\s", "");
                        number = number.replaceAll("-", "");
                        number = number.replaceAll("\\)", "");
                        number = number.replaceAll("\\(", "");

                        JSONObject jsonParam = new JSONObject();
                        try {
                            //Add string params
                            jsonParam.put("id", counter);
                            jsonParam.put("name", display_name);
                            jsonParam.put("number", number);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ContactDetails.put(jsonParam);
                        counter++;
                    }
                }
                Log.d("log-contact", "json array list : " + ContactDetails);
                params.put("ContactDetails", ContactDetails.toString());
                return params;
            }
        };
        requestQueue.add(request);


    }


}
