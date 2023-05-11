package com.example.mank;

import static com.example.mank.MainActivity.db;
import static com.example.mank.MainActivity.user_login_id;
import static com.example.mank.configuration.GlobalVariables.URL_MAIN;
import static com.example.mank.configuration.permission_code.CONTACTS_PERMISSION_CODE;
import static com.example.mank.configuration.permission_code.STORAGE_PERMISSION_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.contactDetailsHolderForSync;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.AllContactOfUserEntity;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;
import com.example.mank.RecyclerViewClassesFolder.ContactSyncMainRecyclerViewAdapter;
import com.example.mank.ThreadPackages.GetUserContactDetailsFromPhone;
import com.example.mank.cipher.MyCipher;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class AllContactOfUserInDeviceView extends Activity {
    private ProgressBar loadingPB;
    private static final String url = URL_MAIN;

    private ArrayList<ContactWithMassengerEntity> contactArrayList;
    private ArrayList<ContactWithMassengerEntity> filterdContactArrayList;
    public RecyclerView recyclerView1;
    private ContactSyncMainRecyclerViewAdapter ContactSyncMainRecyclerViewAdapter;

    public GetUserContactDetailsFromPhone getUserContactDetailsFromPhone;

    MyCipher mc = new MyCipher();
    public MassegeDao massegeDao;
    private SearchView ACSPSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("log-AllContactOfUserInDeviceView", "onCreate: enter here");
        setContentView(R.layout.activity_all_contact_of_user_in_device_view);
        loadingPB = findViewById(R.id.idLoadingPB_of_AllContactView);
        recyclerView1 = findViewById(R.id.ContactSyncRecyclerViewMain);
        ACSPSearchView = findViewById(R.id.ACSPSearchView);
        contactArrayList = new ArrayList<>();
        start();
        massegeDao = db.massegeDao();

        ACSPSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("log-MainActivity", "onQueryTextChange newText:" + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("log-MainActivity", "onQueryTextChange newText:" + newText);
//                if (!prevCatchText.equals(newText)) {
//                    prevCatchText = newText;
                if (newText == null || newText.trim().equals("")) {
                    contactArrayListFilter(newText, 0);
                } else {
                    contactArrayListFilter(newText, 1);
                }
//                }
                return false;
            }
        });

    }

    private void start() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            syncContactDetails(db);
        } else {
            askPermission(Manifest.permission.READ_CONTACTS);
        }
    }

    public void FinishThisActivity(View view) {
        this.finish();
    }

    private void askPermission(String Manifest_request) {
        if (Objects.equals(Manifest_request, Manifest.permission.READ_CONTACTS)) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS
            }, CONTACTS_PERMISSION_CODE);
        }
    }

    private List<AllContactOfUserEntity> allContactOfUserEntityList = new ArrayList<>();
    private AllContactOfUserEntity allContactOfUserEntity;

    public void syncContactDetails(MainDatabaseClass db) {

        getUserContactDetailsFromPhone = new GetUserContactDetailsFromPhone(AllContactOfUserInDeviceView.this, db);
        getUserContactDetailsFromPhone.start();


        contactDetailsHolderForSync contactDetailsHolder = new contactDetailsHolderForSync(db);
        List<ContactWithMassengerEntity> contactList = contactDetailsHolder.getData();

        connectedContact = contactList;
        allContact = new ArrayList<>();
        setContentDetailsInView();


        loadingPB.setVisibility(View.VISIBLE);
        Thread t = new Thread(new Runnable() {
            public void run() {
                long y = user_login_id;
                JSONArray mainArray = new JSONArray();
                JSONArray ContactDetails = new JSONArray();
                String endpoint = url + "syncContactOfUser";
                RequestQueue requestQueue = Volley.newRequestQueue(AllContactOfUserInDeviceView.this);

                ContentResolver contentResolver = getContentResolver();
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
                                allContactOfUserEntity = new AllContactOfUserEntity(Long.parseLong(number), display_name, -1);
                            } catch (IndexOutOfBoundsException e) {
                                Log.d("log-GetUserContactDetailsFromPhone", "IndexOutOfBoundsException: for " + number + " || " + e);
                            } catch (Exception e) {
                                Log.d("log-GetUserContactDetailsFromPhone", "Exception: for " + number + " || " + e);
                            }
                            //makeing jsonArray
                            JSONArray jsonParam = new JSONArray();
                            jsonParam.put(mc.encrypt(counter));
                            jsonParam.put(mc.encrypt(display_name));
                            jsonParam.put((number));
                            ContactDetails.put(jsonParam);
                            if (number.equals("1111111111")) {
                                Log.d("log-GetUserContactDetailsFromPhone-D1", "D1 found : " + jsonParam);
                            }
                            allContactOfUserEntityList.add(allContactOfUserEntity);
                            List<AllContactOfUserEntity> x = massegeDao.getSelectedAllContactOfUserEntity(allContactOfUserEntity.getMobileNumber());
                            if (x.size() == 0) {
                                massegeDao.addAllContactOfUserEntity(allContactOfUserEntity);
                            }
                        }
                        counter++;
                    }
                }

                mainArray.put(y);
                mainArray.put(ContactDetails);
                Log.d("log-AllContactOfUserDeviceView", "sending json array of contact : " + ContactDetails);

                allContact = allContactOfUserEntityList;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setContentDetailsInView();
                    }
                });


                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, endpoint, mainArray, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
//                        Log.d("log-AllContactOfUserDeviceView", "onResponse: json array list : " + response.toString());
                        Toast.makeText(AllContactOfUserInDeviceView.this, "Response arrived", Toast.LENGTH_LONG).show();
                        Log.d("log-AllContactOfUserDeviceView", "onResponse: response length : " + response.length());
                        JSONArray tmp;

                        boolean isStorebal;
                        int isStorebalCount = 0;

                        for (int i = 0; i < response.length(); i++) {
                            isStorebal = true;
                            try {
                                tmp = response.getJSONArray(i);
                                long tnum = Long.parseLong(mc.decrypt(tmp.get(2).toString()));
                                long tc_id = Long.parseLong(mc.decrypt(tmp.get(0).toString()));
                                Log.d("log-AllContactOfUserDeviceView", "onResponse: id:" + tmp.get(0) + " name:" + tmp.get(1) + " num:" + tmp.get(2));
                                Log.d("log-AllContactOfUserDeviceView", "onResponse: id:" + mc.decrypt(tmp.get(0).toString()) + " name:" + mc.decrypt(tmp.get(1).toString()) + " num:" + mc.decrypt(tmp.get(2).toString()));

                                //updating C_ID of contact who connected with massenger

                                for (int j = 0; j < contactList.size(); j++) {
                                    if (contactList.get(0).getMobileNumber() == tnum) {
                                        isStorebal = false;
                                        break;
                                    }
                                }
                                if (isStorebal) {
                                    //store
                                    massegeDao.updateAllContactOfUserEntityC_ID(tnum, tc_id);
                                    long x = massegeDao.getHighestPriorityRank();
                                    ContactWithMassengerEntity new_entity = new ContactWithMassengerEntity(tnum, mc.decrypt(tmp.get(1).toString()), tc_id);
                                    massegeDao.SaveContactDetailsInDatabase(new_entity);
                                    isStorebalCount++;
                                }


                            } catch (JSONException e) {
                                Log.d("log-AllContactOfUserDeviceView", "onResponse: jsonException : " + e);
                            } catch (Exception e) {
                                Log.d("log-AllContactOfUserDeviceView", "onResponse: Simple Exception : " + e);
                            }
                        }
                        Log.d("log-AllContactOfUserDeviceView", "onResponse: isStorebalCount i s" + isStorebalCount);
                        contactDetailsHolderForSync newContactDetailsHolder = new contactDetailsHolderForSync(db);
                        List<ContactWithMassengerEntity> MainContactList = newContactDetailsHolder.getData();

                        connectedContact = MainContactList;
                        allContact = allContactOfUserEntityList;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setContentDetailsInView();
                            }
                        });
                        loadingPB.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("log-AllContactOfUserDeviceView", "onErrorResponse: setChatDetails error: " + error);
                        loadingPB.setVisibility(View.GONE);
                        Toast.makeText(AllContactOfUserInDeviceView.this, "sync failed Duo to  Server side error :  " + error, Toast.LENGTH_SHORT).show();
                    }
                });
                jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                requestQueue.add(jsonArrayRequest);

            }
        });
        t.start();

    }

    Comparator<AllContactOfUserEntity> contactComparator = new Comparator<AllContactOfUserEntity>() {
        @Override
        public int compare(AllContactOfUserEntity contact1, AllContactOfUserEntity contact2) {
            try {
                return contact1.getDisplay_name().compareToIgnoreCase(contact2.getDisplay_name());
            } catch (Exception e) {
                Log.d("log-AllContactOfUserDeviceView", "Exception in comparator: " + e);
                return 1;
            }
        }
    };
    Comparator<ContactWithMassengerEntity> contactComparator2 = new Comparator<ContactWithMassengerEntity>() {
        @Override
        public int compare(ContactWithMassengerEntity contact1, ContactWithMassengerEntity contact2) {
            try {
                return contact1.getDisplay_name().compareToIgnoreCase(contact2.getDisplay_name());
            } catch (Exception e) {
                Log.d("log-AllContactOfUserDeviceView", "Exception in comparator2: " + e);
                return 1;
            }
        }
    };

    private List<ContactWithMassengerEntity> connectedContact;
    private List<AllContactOfUserEntity> allContact;

    @SuppressLint("NotifyDataSetChanged")
    private void setContentDetailsInView() {

        synchronized (this) {

            Log.d("log-AllContactOfUserDeviceView", "setContentDetailsInView: enter");
            Log.d("log-AllContactOfUserDeviceView", "before sorting and remove duplicates allContact.size() = " + allContact.size());

// Create a TreeSet with the custom comparator to store the sorted, unique contacts
            Set<AllContactOfUserEntity> uniqueContacts = new TreeSet<>(contactComparator);
// Add all the contacts to the TreeSet, which will automatically remove duplicates
            uniqueContacts.addAll(allContact);
// Convert the TreeSet back to a List and assign it to allContact variable
            allContact = new ArrayList<>(uniqueContacts);
            Set<ContactWithMassengerEntity> uniqueContacts2 = new TreeSet<>(contactComparator2);
// Add all the contacts to the TreeSet, which will automatically remove duplicates
            uniqueContacts2.addAll(connectedContact);
// Convert the TreeSet back to a List and assign it to allContact variable
            connectedContact = new ArrayList<>(uniqueContacts2);

            Log.d("log-AllContactOfUserDeviceView", "after sorting and remove duplicates allContact.size() = " + allContact.size());
            Log.d("log-AllContactOfUserDeviceView", "setContentDetailsInView: enter");


            contactArrayList = new ArrayList<>();
            ContactSyncMainRecyclerViewAdapter = new ContactSyncMainRecyclerViewAdapter(AllContactOfUserInDeviceView.this, contactArrayList);
            recyclerView1.setHasFixedSize(true);
            recyclerView1.setLayoutManager(new LinearLayoutManager(this));
            recyclerView1.setAdapter(ContactSyncMainRecyclerViewAdapter);

            //after this we set lable
            int tmp_number = 0000000000;
            ContactWithMassengerEntity new_label_entity = new ContactWithMassengerEntity((long) tmp_number, "boundary_100", -100);
            contactArrayList.add(new_label_entity);
            ContactSyncMainRecyclerViewAdapter.notifyDataSetChanged();

            //set contact have massenger
            contactArrayList.addAll(connectedContact);
            ContactSyncMainRecyclerViewAdapter.notifyDataSetChanged();
            Log.d("log-AllContactOfUserDeviceView", "setContentDetailsInView: after add connectedContact");


            //after this we set lable
            ContactWithMassengerEntity new_label_entity1 = new ContactWithMassengerEntity((long) tmp_number, "boundary_101", -101);
            contactArrayList.add(new_label_entity1);
            ContactSyncMainRecyclerViewAdapter.notifyDataSetChanged();


            //now we set all rest contact
            Thread t = new Thread(new Runnable() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void run() {
                    boolean pass;
                    for (int i = 0; i < allContact.size(); i++) {
                        pass = true;
                        for (int k = 0; k < connectedContact.size(); k++) {
                            if (allContact.get(i).getMobileNumber().equals(connectedContact.get(k).getMobileNumber())) {
                                pass = false;
                            }
                        }
                        if (pass) {
                            ContactWithMassengerEntity new_entity = new ContactWithMassengerEntity(allContact.get(i).getMobileNumber(), allContact.get(i).getDisplay_name(), allContact.get(i).getC_ID());
                            contactArrayList.add(new_entity);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ContactSyncMainRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    });
                    filterdContactArrayList = (ArrayList<ContactWithMassengerEntity>) contactArrayList.clone();
                }
            });
            t.start();

            Log.d("log-AllContactOfUserDeviceView", "setContentDetailsInView: after add noConnectedContact");
//        ContactSyncMainRecyclerViewAdapter.notifyDataSetChanged();
            recyclerView1.scrollToPosition(0);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void contactArrayListFilter(String newText, int flag) {
        if (flag == 0) {
            Log.d("log-MainActivity", "contactArrayListFilter start with flag 0");
            contactArrayList.clear();
            contactArrayList.addAll(filterdContactArrayList);
            ContactSyncMainRecyclerViewAdapter.notifyDataSetChanged();
            return;
        }
        Log.d("log-MainActivity", "contactArrayListFilter start");
        contactArrayList.clear();
        contactArrayList.addAll(filterdContactArrayList);
        for (ContactWithMassengerEntity e : filterdContactArrayList) {
            if (!e.getDisplay_name().toLowerCase().contains(newText.toLowerCase())) {
                if (e.getC_ID() != -100 || e.getC_ID() != -101) {
                    contactArrayList.remove(e);
                }
            }
        }
        ContactSyncMainRecyclerViewAdapter.notifyDataSetChanged();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == CONTACTS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                start();
            } else {
                Toast.makeText(this, "permission required", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length <= 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(AllContactOfUserInDeviceView.this, "Permission Required", Toast.LENGTH_SHORT).show();
            }
        }
    }

}