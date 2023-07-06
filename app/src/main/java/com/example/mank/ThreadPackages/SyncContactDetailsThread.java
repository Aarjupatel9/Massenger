package com.example.mank.ThreadPackages;

import static com.example.mank.MainActivity.db;
import static com.example.mank.MainActivity.user_login_id;
import static com.example.mank.configuration.GlobalVariables.URL_MAIN;
import static com.example.mank.configuration.permissionMain.hasPermissions;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mank.AllContactOfUserInDeviceView;
import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.contactDetailsHolderForSync;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.AllContactOfUserEntity;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;
import com.example.mank.MainActivity;
import com.example.mank.cipher.MyCipher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class SyncContactDetailsThread extends Thread {

    private static final int PERMISSION_ALL = 108;
    private final Context context;
    private int type = 0;
    private AllContactOfUserEntity allContactOfUserEntity;
    private List<AllContactOfUserEntity> allContactOfUserEntityList = new ArrayList<>();
    private final List<AllContactOfUserEntity> connectedContact;
    private List<AllContactOfUserEntity> disConnectedContact;
    private final MassegeDao massegeDao;
    private final MyCipher mc = new MyCipher();

    private final IContactSync callback;

    public SyncContactDetailsThread(Context context, List<AllContactOfUserEntity> connectedContact, List<AllContactOfUserEntity> disConnectedContact, IContactSync callback) {
        this.context = context;
        massegeDao = db.massegeDao();
        this.connectedContact = connectedContact;
        this.disConnectedContact =disConnectedContact;
        this.callback = callback;
    }

    public void setFromWhere(int type) {
        this.type = type;
    }

    Comparator<AllContactOfUserEntity> contactComparator = new Comparator<AllContactOfUserEntity>() {
        @Override
        public int compare(AllContactOfUserEntity contact1, AllContactOfUserEntity contact2) {
            try {
                return contact1.getDisplayName().compareToIgnoreCase(contact2.getDisplayName());
            } catch (Exception e) {
                Log.d("log-AllContactOfUserDeviceView", "Exception in comparator: " + e);
                return 1;
            }
        }
    };

    public void run() {
        synchronized (this) {

            String y = user_login_id;
            JSONArray mainArray = new JSONArray();
            JSONArray ContactDetails = new JSONArray();
            String endpoint = URL_MAIN + "syncContactOfUser";
            RequestQueue requestQueue = Volley.newRequestQueue(context);

            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            Log.d("log-GetUserContactDetailsFromPhone", "getContacts: total contact is " + cursor.getCount());

            if (cursor.getCount() > 0) {
                int counter = 0;
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    number = number.replaceAll("\\s", "");
                    number = number.replaceAll("-", "");
                    number = number.replaceAll("\\)", "");
                    number = number.replaceAll("\\(", "");

                    if (number.length() > 9) {
                        try {
                            if (number.charAt(0) == '+') {
                                number = number.substring(3);
                            }
                            allContactOfUserEntity = new AllContactOfUserEntity(Long.parseLong(number), display_name, "-1");
                        } catch (IndexOutOfBoundsException e) {
                            Log.d("log-GetUserContactDetailsFromPhone", "IndexOutOfBoundsException: for " + number + " || " + e);
                        } catch (Exception e) {
                            Log.d("log-GetUserContactDetailsFromPhone", "Exception: for " + number + " || " + e);
                        }

                        JSONArray jsonParam = new JSONArray();
                        jsonParam.put(mc.encrypt(counter));
                        jsonParam.put(mc.encrypt(display_name));
                        jsonParam.put((number));
                        ContactDetails.put(jsonParam);
                        if (number.equals("1111111111")) {
                            Log.d("log-GetUserContactDetailsFromPhone-D1", "D1 found : " + jsonParam);
                        }
                        allContactOfUserEntityList.add(allContactOfUserEntity);
                    }
                    counter++;
                }
            }

            Set<AllContactOfUserEntity> uniqueContacts = new TreeSet<>(contactComparator);
            uniqueContacts.addAll(allContactOfUserEntityList);
            allContactOfUserEntityList = new ArrayList<>(uniqueContacts);
            Thread tx = new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (this) {
                        for (AllContactOfUserEntity entity :
                                allContactOfUserEntityList) {
                            List<AllContactOfUserEntity> x = massegeDao.getSelectedAllContactOfUserEntity(entity.getMobileNumber(), user_login_id);
                            if (x.size() == 0) {
                                massegeDao.addAllContactOfUserEntity(entity);
                            }
                        }
                    }
                }
            });
            tx.start();

            mainArray.put(y);
            mainArray.put(ContactDetails);
            Log.d("log-AllContactOfUserDeviceView", "sending json array of contact : " + ContactDetails);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, endpoint, mainArray, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
//                        Log.d("log-AllContactOfUserDeviceView", "onResponse: json array list : " + response.toString());
                    Log.d("log-AllContactOfUserDeviceView", "onResponse: response length : " + response.length());
                    JSONObject tmp;

                    boolean isUpdatable;
                    int isUpdatableCount = 0;

                    for (int i = 0; i < response.length(); i++) {
                        isUpdatable = true;
                        try {
                            tmp = response.getJSONObject(i);
                            long tnum = Long.parseLong(tmp.getString("Number"));
                            String tCID = tmp.getString("_id");
                            String name = tmp.getString("Name");
                            Log.d("log-AllContactOfUserDeviceView", "onResponse: id:" + tCID + " name:" + name + " num:" + tnum);
//
                            for (int j = 0; j < connectedContact.size(); j++) {
                                if (connectedContact.get(0).getMobileNumber() == tnum) {
                                    isUpdatable = false;
                                    break;
                                }
                            }
                            if (isUpdatable) {
                                //update CID
                                massegeDao.updateAllContactOfUserEntityCID(tnum, tCID, user_login_id);
//                                        long x = massegeDao.getHighestPriorityRank();
//                                        ContactWithMassengerEntity new_entity = new ContactWithMassengerEntity(tnum, mc.decrypt(tmp.get(1).toString()), tCID, x+1);
//                                        massegeDao.SaveContactDetailsInDatabase(new_entity);
                                isUpdatableCount++;
                            }

                        } catch (JSONException e) {
                            Log.d("log-AllContactOfUserDeviceView", "onResponse: jsonException : " + e);
                        } catch (Exception e) {
                            Log.d("log-AllContactOfUserDeviceView", "onResponse: Simple Exception : " + e);
                        }
                    }

                    Log.d("log-AllContactOfUserDeviceView", "onResponse: isUpdatableCount i s" + isUpdatableCount);

                    callback.execute(1, "sync completed");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("log-AllContactOfUserDeviceView", "onErrorResponse: setChatDetails error: " + error);
//                    Toast.makeText(AllContactOfUserInDeviceView.this, "sync failed , Server side error: " + error, Toast.LENGTH_SHORT).show();
                    callback.execute(0, "sync failed , Server side error : " + error);

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
