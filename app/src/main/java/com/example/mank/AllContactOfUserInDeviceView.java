package com.example.mank;

import static com.example.mank.MainActivity.db;
import static com.example.mank.configuration.permission_code.CONTACTS_PERMISSION_CODE;
import static com.example.mank.configuration.permission_code.STORAGE_PERMISSION_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.contactDetailsHolderForSync;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.AllContactOfUserEntity;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;
import com.example.mank.RecyclerViewClassesFolder.ContactSyncMainRecyclerViewAdapter;
import com.example.mank.ThreadPackages.GetUserContactDetailsFromPhone;
import com.example.mank.ThreadPackages.IContactSync;
import com.example.mank.ThreadPackages.SyncContactDetailsThread;
import com.example.mank.cipher.MyCipher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class AllContactOfUserInDeviceView extends Activity {
    private ProgressBar loadingPB;

    private ArrayList<AllContactOfUserEntity> contactArrayList;
    private ArrayList<AllContactOfUserEntity> filteredContactArrayList;
    public RecyclerView recyclerView1;
    private ContactSyncMainRecyclerViewAdapter ContactSyncMainRecyclerViewAdapter;

    public GetUserContactDetailsFromPhone getUserContactDetailsFromPhone;

    MyCipher mc = new MyCipher();
    public static MassegeDao massegeDao;
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
    private List<AllContactOfUserEntity> disConnectedContact = new ArrayList<>();
    private List<AllContactOfUserEntity> connectedContact = new ArrayList<>();
    private List<AllContactOfUserEntity> allContactOfUser = new ArrayList<>();
    private AllContactOfUserEntity allContactOfUserEntity;

    public void syncContactDetails(MainDatabaseClass db) {

        getUserContactDetailsFromPhone = new GetUserContactDetailsFromPhone(AllContactOfUserInDeviceView.this, db);
        getUserContactDetailsFromPhone.start();

        //fetch existing data from database and display it
        contactDetailsHolderForSync contactDetailsHolder = new contactDetailsHolderForSync(db);
        connectedContact = contactDetailsHolder.getConnectedContact();
        disConnectedContact = contactDetailsHolder.getDisConnectedContact();
        allContactOfUser = contactDetailsHolder.getAllContact();
        setContentDetailsInView();

        syncContactListToServer(connectedContact , disConnectedContact);
//        Thread t = new Thread(new Runnable() {
//            public void run() {
//                synchronized (this) {
//                    String y = user_login_id;
//                    JSONArray mainArray = new JSONArray();
//                    JSONArray ContactDetails = new JSONArray();
//                    String endpoint = URL_MAIN + "syncContactOfUser";
//                    RequestQueue requestQueue = Volley.newRequestQueue(AllContactOfUserInDeviceView.this);
//
//                    ContentResolver contentResolver = getContentResolver();
//                    Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//
//                    Cursor cursor = contentResolver.query(uri, null, null, null, null);
//                    Log.d("log-GetUserContactDetailsFromPhone", "getContacts: total contact is " + cursor.getCount());
//
//                    if (cursor.getCount() > 0) {
//                        int counter = 0;
//                        while (cursor.moveToNext()) {
//                            @SuppressLint("Range") String display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                            @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                            number = number.replaceAll("\\s", "");
//                            number = number.replaceAll("-", "");
//                            number = number.replaceAll("\\)", "");
//                            number = number.replaceAll("\\(", "");
//
//                            if (number.length() > 9) {
//                                try {
//                                    if (number.charAt(0) == '+') {
//                                        number = number.substring(3);
//                                    }
//                                    allContactOfUserEntity = new AllContactOfUserEntity(Long.parseLong(number), display_name, "-1");
//                                } catch (IndexOutOfBoundsException e) {
//                                    Log.d("log-GetUserContactDetailsFromPhone", "IndexOutOfBoundsException: for " + number + " || " + e);
//                                } catch (Exception e) {
//                                    Log.d("log-GetUserContactDetailsFromPhone", "Exception: for " + number + " || " + e);
//                                }
//                                //makeing jsonArray
//                                JSONArray jsonParam = new JSONArray();
//                                jsonParam.put(mc.encrypt(counter));
//                                jsonParam.put(mc.encrypt(display_name));
//                                jsonParam.put((number));
//                                ContactDetails.put(jsonParam);
//                                if (number.equals("1111111111")) {
//                                    Log.d("log-GetUserContactDetailsFromPhone-D1", "D1 found : " + jsonParam);
//                                }
//                                allContactOfUserEntityList.add(allContactOfUserEntity);
//                            }
//                            counter++;
//                        }
//                    }
//
//                    Set<AllContactOfUserEntity> uniqueContacts = new TreeSet<>(contactComparator);
//                    uniqueContacts.addAll(allContactOfUserEntityList);
//                    allContactOfUserEntityList = new ArrayList<>(uniqueContacts);
//                    Thread tx = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            synchronized (this) {
//                                for (AllContactOfUserEntity entity :
//                                        allContactOfUserEntityList) {
//                                    List<AllContactOfUserEntity> x = massegeDao.getSelectedAllContactOfUserEntity(entity.getMobileNumber());
//                                    if (x.size() == 0) {
//                                        massegeDao.addAllContactOfUserEntity(entity);
//                                    }
//                                }
//                            }
//                        }
//                    });
//                    tx.start();
//
//                    mainArray.put(y);
//                    mainArray.put(ContactDetails);
//                    Log.d("log-AllContactOfUserDeviceView", "sending json array of contact : " + ContactDetails);
//
//                    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, endpoint, mainArray, new Response.Listener<JSONArray>() {
//                        @Override
//                        public void onResponse(JSONArray response) {
////                        Log.d("log-AllContactOfUserDeviceView", "onResponse: json array list : " + response.toString());
//                            Log.d("log-AllContactOfUserDeviceView", "onResponse: response length : " + response.length());
//                            JSONObject tmp;
//
//                            boolean isUpdatable;
//                            int isUpdatableCount = 0;
//
//                            for (int i = 0; i < response.length(); i++) {
//                                isUpdatable = true;
//                                try {
//                                    tmp = response.getJSONObject(i);
//                                    long tnum = Long.parseLong(tmp.getString("Number"));
//                                    String tCID = tmp.getString("_id");
//                                    String name = tmp.getString("Name");
//                                    Log.d("log-AllContactOfUserDeviceView", "onResponse: id:" + tCID + " name:" + name + " num:" + tnum);
////                                    Log.d("log-AllContactOfUserDeviceView", "onResponse: id:" + mc.decrypt(tmp.get(0).toString()) + " name:" + mc.decrypt(tmp.get(1).toString()) + " num:" + mc.decrypt(tmp.get(2).toString()));
//                                    //updating C_ID of contact who connected with massenger
//
//                                    for (int j = 0; j < connectedContact.size(); j++) {
//                                        if (connectedContact.get(0).getMobileNumber() == tnum) {
//                                            isUpdatable = false;
//                                            break;
//                                        }
//                                    }
//                                    if (isUpdatable) {
//                                        //update CID
//                                        massegeDao.updateAllContactOfUserEntityCID(tnum, tCID);
////                                        long x = massegeDao.getHighestPriorityRank();
////                                        ContactWithMassengerEntity new_entity = new ContactWithMassengerEntity(tnum, mc.decrypt(tmp.get(1).toString()), tCID, x+1);
////                                        massegeDao.SaveContactDetailsInDatabase(new_entity);
//                                        isUpdatableCount++;
//                                    }
//
//                                } catch (JSONException e) {
//                                    Log.d("log-AllContactOfUserDeviceView", "onResponse: jsonException : " + e);
//                                } catch (Exception e) {
//                                    Log.d("log-AllContactOfUserDeviceView", "onResponse: Simple Exception : " + e);
//                                }
//                            }
//
//                            Log.d("log-AllContactOfUserDeviceView", "onResponse: isUpdatableCount i s" + isUpdatableCount);
//
//
//
//
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.d("log-AllContactOfUserDeviceView", "onErrorResponse: setChatDetails error: " + error);
//                            loadingPB.setVisibility(View.GONE);
//                            Toast.makeText(AllContactOfUserInDeviceView.this, "sync failed , Server side error: " + error, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
//                            0,
//                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//                    requestQueue.add(jsonArrayRequest);
//                }
//            }
//        });
//        t.start();

    }

    private void syncContactListToServer( List<AllContactOfUserEntity> connectedContact,  List<AllContactOfUserEntity> disConnectedContact) {
        loadingPB.setVisibility(View.VISIBLE);
        SyncContactDetailsThread scdt = new SyncContactDetailsThread(this, connectedContact, disConnectedContact, new IContactSync() {
            @Override
            public void execute(int status, String massege) {
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(AllContactOfUserInDeviceView.this, massege.toString(),Toast.LENGTH_LONG).show();
                if(status == 1) {
                    syncContactListToServerCallBack();
                }
            }
        });
        scdt.setFromWhere(1);
        scdt.start();
    }
    public void syncContactListToServerCallBack(){
        synchronized (this) {
            contactDetailsHolderForSync newContactDetailsHolder = new contactDetailsHolderForSync(db);
            connectedContact = newContactDetailsHolder.getConnectedContact();
            disConnectedContact = newContactDetailsHolder.getDisConnectedContact();
            for (AllContactOfUserEntity e : connectedContact
            ) {
                Log.d("log-AllContactOfUserDeviceView", "onResponse: connected contact is :" + e.getDisplayName() + ", " + e.getCID() + " , " + e.getMobileNumber());
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentDetailsInView();
                }
            });
        }
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
    Comparator<ContactWithMassengerEntity> contactComparator2 = new Comparator<ContactWithMassengerEntity>() {
        @Override
        public int compare(ContactWithMassengerEntity contact1, ContactWithMassengerEntity contact2) {
            try {
                return contact1.getDisplayName().compareToIgnoreCase(contact2.getDisplayName());
            } catch (Exception e) {
                Log.d("log-AllContactOfUserDeviceView", "Exception in comparator2: " + e);
                return 1;
            }
        }
    };


    @SuppressLint("NotifyDataSetChanged")
    private void setContentDetailsInView() {
        synchronized (this) {

            Log.d("log-AllContactOfUserDeviceView", "setContentDetailsInView: enter");
            Log.d("log-AllContactOfUserDeviceView", "before sorting and remove duplicates disConnectedContact.size() = " + disConnectedContact.size());

// Create a TreeSet with the custom comparator to store the sorted, unique contacts
            Set<AllContactOfUserEntity> uniqueContacts = new TreeSet<>(contactComparator);
// Add all the contacts to the TreeSet, which will automatically remove duplicates
            uniqueContacts.addAll(disConnectedContact);
// Convert the TreeSet back to a List and assign it to disConnectedContact variable
            disConnectedContact = new ArrayList<>(uniqueContacts);

            Set<AllContactOfUserEntity> uniqueContacts2 = new TreeSet<>(contactComparator);
            uniqueContacts2.addAll(connectedContact);
            connectedContact = new ArrayList<>(uniqueContacts2);

            Log.d("log-AllContactOfUserDeviceView", "after sorting and remove duplicates disConnectedContact.size() = " + disConnectedContact.size());
            Log.d("log-AllContactOfUserDeviceView", "setContentDetailsInView: enter");


            contactArrayList = new ArrayList<>();
            ContactSyncMainRecyclerViewAdapter = new ContactSyncMainRecyclerViewAdapter(AllContactOfUserInDeviceView.this, contactArrayList);
            recyclerView1.setHasFixedSize(true);
            recyclerView1.setLayoutManager(new LinearLayoutManager(this));
            recyclerView1.setAdapter(ContactSyncMainRecyclerViewAdapter);

            // first we have to remove all object in contactArrayList
            contactArrayList.clear();

            //after this we set lable
            int tmp_number = 0000000000;
            AllContactOfUserEntity new_label_entity = new AllContactOfUserEntity((long) tmp_number, "boundary_100", "-100");
            contactArrayList.add(new_label_entity);
            ContactSyncMainRecyclerViewAdapter.notifyDataSetChanged();

            //set contact have massenger
            contactArrayList.addAll(connectedContact);
            ContactSyncMainRecyclerViewAdapter.notifyDataSetChanged();
            Log.d("log-AllContactOfUserDeviceView", "setContentDetailsInView: after add connectedContact");

            //after this we set lable
            AllContactOfUserEntity new_label_entity1 = new AllContactOfUserEntity((long) tmp_number, "boundary_101", "-101");
            contactArrayList.add(new_label_entity1);
            ContactSyncMainRecyclerViewAdapter.notifyDataSetChanged();

            //add all disConnected array
            contactArrayList.addAll(disConnectedContact);
            ContactSyncMainRecyclerViewAdapter.notifyDataSetChanged();

            filteredContactArrayList = (ArrayList<AllContactOfUserEntity>) contactArrayList.clone();

            Log.d("log-AllContactOfUserDeviceView", "setContentDetailsInView: after clone of filteredContactArrayList");
            recyclerView1.scrollToPosition(0);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void contactArrayListFilter(String newText, int flag) {
        if (flag == 0) {
            Log.d("log-MainActivity", "contactArrayListFilter start with flag 0");
            contactArrayList.clear();
            contactArrayList.addAll(filteredContactArrayList);
            ContactSyncMainRecyclerViewAdapter.notifyDataSetChanged();
            return;
        }
        Log.d("log-MainActivity", "contactArrayListFilter start");
        contactArrayList.clear();
        contactArrayList.addAll(filteredContactArrayList);
        for (AllContactOfUserEntity e : filteredContactArrayList) {
            if (!e.getDisplayName().toLowerCase().contains(newText.toLowerCase())) {
                if (!Objects.equals(e.getCID(), "-100") || !Objects.equals(e.getCID(), "-101")) {
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