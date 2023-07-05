package com.example.mank.ThreadPackages;

import static com.example.mank.MainActivity.user_login_id;
import static com.example.mank.configuration.GlobalVariables.URL_MAIN;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.ContactImageHolderForSync;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.contactDetailsHolderForSync;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.AllContactOfUserEntity;
import com.example.mank.MainActivity;
import com.example.mank.cipher.MyCipher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class UpdateContactImageThread extends Thread {
    MainActivity parent;
    MainDatabaseClass db;
    MassegeDao massegeDao;
    MyCipher mc = new MyCipher();
    JSONArray ContactDetails;
    private List<AllContactOfUserEntity> ConnectedContactList = new ArrayList<>();


    public UpdateContactImageThread(MainActivity h, MainDatabaseClass db) {
        this.parent = h;
        this.db = db;
        massegeDao = db.massegeDao();

    }

    Comparator<AllContactOfUserEntity> contactComparator = new Comparator<AllContactOfUserEntity>() {
        @Override
        public int compare(AllContactOfUserEntity contact1, AllContactOfUserEntity contact2) {
            try {
                return contact1.getDisplayName().compareToIgnoreCase(contact2.getDisplayName());
            } catch (Exception e) {
                Log.d("log-SyncContactDetailsThread", "Exception in comparator: " + e);
                return 1;
            }
        }
    };

    public void run() {
        synchronized (this) {
            String y = user_login_id;
            JSONArray mainArray = new JSONArray();
            ContactDetails = new JSONArray();
            String endpoint = URL_MAIN + "updateContactProfileImage";
            RequestQueue requestQueue = Volley.newRequestQueue(parent);

            ContactImageHolderForSync contactImageHolderForSync = new ContactImageHolderForSync(db);
            ConnectedContactList = contactImageHolderForSync.getImageOfConnectedContact();

            for (AllContactOfUserEntity e :
                    ConnectedContactList) {
                JSONArray jsonParam = new JSONArray();
                jsonParam.put(e.getCID());
                jsonParam.put(e.getMobileNumber());
                jsonParam.put(e.getImageVersion());
                ContactDetails.put(jsonParam);
            }

            mainArray.put(y);
            mainArray.put(ContactDetails);
            Log.d("log-SyncContactDetailsThread", "sending json array of contact : " + ContactDetails);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, endpoint, mainArray, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
//                        Log.d("log-SyncContactDetailsThread", "onResponse: json array list : " + response.toString());
                    Log.d("log-SyncContactDetailsThread", "onResponse: response length : " + response.length());
                    JSONObject tmp;

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            tmp = response.getJSONObject(i);
                            long tnum = Long.parseLong(tmp.getString("Number"));
                            String tCID = tmp.getString("_id");
                            String name = tmp.getString("Name");
                            boolean toUpdate = tmp.getBoolean("toUpdate");
//                            byte[] imageData =  tmp.getString("imageData");

                            Log.d("log-SyncContactDetailsThread", "onResponse: id:" + tCID + " name:" + name + " num:" + tnum);
                            massegeDao.updateAllContactOfUserEntityCID(tnum, tCID, user_login_id);


                        } catch (JSONException e) {
                            Log.d("log-SyncContactDetailsThread", "onResponse: jsonException : " + e);
                        } catch (Exception e) {
                            Log.d("log-SyncContactDetailsThread", "onResponse: Simple Exception : " + e);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("log-SyncContactDetailsThread", "onErrorResponse: setChatDetails error: " + error);
                    Toast.makeText(parent, "background contact Sync failed , Server side error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(jsonArrayRequest);
        }
    }


}
