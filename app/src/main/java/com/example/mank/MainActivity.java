package com.example.mank;

import static com.example.mank.ContactMassegeDetailsView.massegeListAdapter;
import static com.example.mank.configuration.GlobalVariables.URL_MAIN;
import static com.example.mank.configuration.permissionMain.hasPermissions;
import static com.example.mank.configuration.permission_code.CAMERA_PERMISSION;
import static com.example.mank.configuration.permission_code.CAMERA_PERMISSION_CODE;
import static com.example.mank.configuration.permission_code.CONTACTS_PERMISSION_CODE;
import static com.example.mank.configuration.permission_code.CONTACT_PERMISSION;
import static com.example.mank.configuration.permission_code.CONTACT_STORAGE_PERMISSION;
import static com.example.mank.configuration.permission_code.NETWORK_PERMISSION;
import static com.example.mank.configuration.permission_code.NETWORK_PERMISSION_CODE;
import static com.example.mank.configuration.permission_code.PERMISSIONS;
import static com.example.mank.configuration.permission_code.PERMISSION_ALL;
import static com.example.mank.configuration.permission_code.PERMISSION_CONTACT_SYNC;
import static com.example.mank.configuration.permission_code.PERMISSION_initContentResolver;
import static com.example.mank.configuration.permission_code.STORAGE_PERMISSION;
import static com.example.mank.configuration.permission_code.STORAGE_PERMISSION_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mank.DatabaseAdapter.ContactListAdapter;
import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.AppDetailsHolder;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.ContactListHolder;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.MassegeHolderForSpecificPurpose;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.contactDetailsHolderForSync;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.userIdEntityHolder;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.AllContactOfUserEntity;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;
import com.example.mank.LocalDatabaseFiles.entities.MassegeEntity;
import com.example.mank.LocalDatabaseFiles.entities.SetupFirstTimeEntity;
import com.example.mank.LoginMenagement.Login;
import com.example.mank.RecyclerViewClassesFolder.RecyclerViewAdapter;
import com.example.mank.TabMainHelper.SectionsPagerAdapter;
import com.example.mank.ThreadPackages.IContactSync;
import com.example.mank.ThreadPackages.MassegePopSoundThread;
import com.example.mank.ThreadPackages.StatusForThread;
import com.example.mank.ThreadPackages.SyncContactDetailsThread;
import com.example.mank.cipher.MyCipher;
import com.example.mank.databinding.ActivityMainBinding;
import com.example.mank.profile.AllSettingsActivity;
import com.example.mank.profile.BgImageSetForContactPage;
import com.example.mank.profile.UserProfileActivity;
import com.example.mank.services.MassegeWorker;
import com.example.mank.services.MyContentObserver;
import com.example.mank.services.MyForegroundService;
import com.example.mank.socket.SocketClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends FragmentActivity {

    public static ContactListAdapter contactListAdapter;
    public static int FinishCode = 0;
    public static String Contact_page_opened_id = "-1";
    public static StatusForThread statusForThread;

    public static String user_login_id;
    public static long UserMobileNumber;
    public static String API_SERVER_API_KEY;
    private boolean toStopAppMainThread = false;

    //global socket variables
    public static SocketClass socketOBJ;
    public static Socket socket;

    public static RecyclerView ChatsRecyclerView;
    public static RecyclerViewAdapter recyclerViewAdapter;
    public static volatile ArrayList<ContactWithMassengerEntity> contactArrayList;
    public static MainDatabaseClass db;

    public static ContactListHolder MainContactListHolder;


    private boolean appOpenFromBackGround = false;
    private boolean EverythingIsOhkInApp = false;
    private SearchView MAPSearchView;
    private ProgressBar mainProgressBar;
    int LAUNCH_LOGIN_ACTIVITY = 1;
    public Intent LoginIntentData;
    private MassegeDao massegeDao;
    public static Context MainActivityStaticContext;
    private ActivityMainBinding binding;

    @Override
    protected void onStart() {
        super.onStart();
//        startBackgroundPractice();

        startNetworkListener();
        if (contactListAdapter != null) {
            contactListAdapter.setContext(this);
        }
        MainActivityStaticContext = this;
        Contact_page_opened_id = "-1";
        FinishCode = 0;
        Log.d("log-Contact_page_opened_id", "onStart: in MainActivity Contact_page_opened_id  is  : " + Contact_page_opened_id);
        if (!appOpenFromBackGround) {
            appOpenFromBackGround = true;
        } else if (EverythingIsOhkInApp) {
            //we will send that we are online massege to server
            Log.d("log-Contact_page_opened_id", "EverythingIsOhkInApp is :  " + EverythingIsOhkInApp);
        }
    }

    private void startBackgroundPractice() {

//        Intent serviceIntent = new Intent(this, MyForegroundService.class);
//        ContextCompat.startForegroundService(this, serviceIntent);
        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        this.startService(serviceIntent);

        // Create a Constraints object to specify conditions for running the task
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(false)
                .build();

// Create a OneTimeWorkRequest for your worker class
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MassegeWorker.class)
                .setConstraints(constraints)
                .build();

// Enqueue the work request
        WorkManager.getInstance().enqueue(workRequest);
    }

//    private void initContentResolver() {
//        ContentResolver contentResolver = getContentResolver();
//        Handler handler = new Handler();
//        MyContentObserver contactsObserver = new MyContentObserver(handler);
//        final String[] PERMISSIONS = {android.Manifest.permission.INTERNET, android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.CHANGE_NETWORK_STATE, android.Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.WRITE_CONTACTS,};
//        if (!hasPermissions(this, PERMISSIONS)) {
//            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_initContentResolver);
//        } else {
//            contentResolver.registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contactsObserver);
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (statusForThread == null) {
            statusForThread = new StatusForThread(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("log-onDestroy", "onDestroy: FinishCode is: " + FinishCode);
        if (FinishCode == 0) {
            Log.d("log-onDestroy", "onDestroy: FinishCode is: " + FinishCode);
            if (EverythingIsOhkInApp) {
                Log.d("log-onDestroy", "onPause EverythingIsOhkInApp: enter here");
            }
            toStopAppMainThread = true;  //for stop appMAinThread
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = Room.databaseBuilder(getApplicationContext(), MainDatabaseClass.class, "MassengerDatabase").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        massegeDao = db.massegeDao();
        API_SERVER_API_KEY = getString(R.string.api_server_api_key);
        verifyLogin(0);
    }

    public void verifyLogin(int code) {
        Login login = new Login();

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
//        if(!hasPermissions(this, STORAGE_PERMISSION)){
//            ActivityCompat.requestPermissions(this, STORAGE_PERMISSION,  STORAGE_PERMISSION_CODE);
//        }
//        if (!hasPermissions(this, CONTACT_PERMISSION)) {
//            ActivityCompat.requestPermissions(this, CONTACT_PERMISSION, CONTACTS_PERMISSION_CODE);
//        }
//        if (!hasPermissions(this, CAMERA_PERMISSION)) {
//            ActivityCompat.requestPermissions(this, CAMERA_PERMISSION, CAMERA_PERMISSION_CODE);
//        }
//        if (!hasPermissions(this, NETWORK_PERMISSION)) {
//            ActivityCompat.requestPermissions(this, NETWORK_PERMISSION, NETWORK_PERMISSION_CODE);
//        }

        if (login.isLogIn(db) == 0) {
            Log.d("log-not logined", "onCreate: not login cond. reached");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LAUNCH_LOGIN_ACTIVITY);
            Log.d("log-FinishCode", "onCreate: FinishCode is: " + FinishCode);
            FinishCode = 2;
        } else {
//            initContentResolver();
            startMain();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("log-MainActivity-onNewIntent", "onNewIntent || start");

        verifyLogin(1);
    }

    private void startMain() {
        db = Room.databaseBuilder(getApplicationContext(), MainDatabaseClass.class, "MassengerDatabase").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        massegeDao = db.massegeDao();
        userIdEntityHolder userIdEntityHolder = new userIdEntityHolder(db);
        user_login_id = userIdEntityHolder.getUserLoginId();
        UserMobileNumber = userIdEntityHolder.getUserMobileNumber();
        contactListAdapter = new ContactListAdapter(db);
        contactListAdapter.setContext(this);
        contactArrayList = contactListAdapter.getContactList();

        saveFireBaseTokenToServer(String.valueOf(user_login_id));
        CreateSocketConnection();

        statusForThread = new StatusForThread(0);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mainProgressBar = binding.MPMainProgressBar;
        MAPSearchView = binding.MAPSearchView;
        mainProgressBar.setVisibility(View.GONE);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.MPViewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.MainTabs;
        tabs.setupWithViewPager(viewPager);

        syncContactAtAppStart();

        EverythingIsOhkInApp = true;
        MAPSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    private void startFirstTimeContactSync() {
        Intent intent = new Intent(this, AllContactOfUserInDeviceView.class);
        startActivityForResult(intent, FirstTimeAppSyncAllContactRequestCode);

    }

    private void startFirstTimeProfileUpdate() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivityForResult(intent, FirstTimeProfileUpdateRequestCode);

    }

    private List<AllContactOfUserEntity> disConnectedContact = new ArrayList<>();
    private List<AllContactOfUserEntity> connectedContact = new ArrayList<>();

    public void syncContactAtAppStart() {
        if (!hasPermissions(this, CONTACT_STORAGE_PERMISSION)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CONTACT_SYNC);
            return;
        }
        Thread tf = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AppDetailsHolder appDetailsHolder = new AppDetailsHolder(db);
                    SetupFirstTimeEntity insertedAtLastEntity = appDetailsHolder.getData();
                    Log.d("log-MainActivity", "Thread tf || lastOpenTime : " + insertedAtLastEntity.getLastOpenTime());
                    if (insertedAtLastEntity.getLastOpenTime() < ((new Date().getTime()) - 300000)) {
                        //app is open after 5 min or more break
                        // do background contact sync
                        Log.d("log-MainActivity", "Thread tf || app is open after 1 min or more break");

//                        Toast.makeText(MainActivity.this, "app is opened after 1 min break or more", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d("log-MainActivity", "Thread tf || exception e: " + e);
                }
                SetupFirstTimeEntity new_entity = new SetupFirstTimeEntity();
                massegeDao.insertLastAppOpenEntity(new_entity);
            }
        });
        tf.start();
        contactDetailsHolderForSync contactDetailsHolder = new contactDetailsHolderForSync(db);
        connectedContact = contactDetailsHolder.getConnectedContact();
        disConnectedContact = contactDetailsHolder.getDisConnectedContact();
        SyncContactDetailsThread scdt = new SyncContactDetailsThread(this, connectedContact, disConnectedContact, new IContactSync() {
            @Override
            public void execute(int status, String massege) {
                Log.d("log-getListOfAllUserContact", "calling getListOfAllUserContact activity");
//                Toast.makeText(MainActivity.this, massege.toString(), Toast.LENGTH_LONG).show();
            }
        });
        scdt.setFromWhere(0);
        scdt.start();

    }

    public static ArrayList<ContactWithMassengerEntity> filteredContactArrayList;

    @SuppressLint("NotifyDataSetChanged")
    public void contactArrayListFilter(String newText, int flag) {
        if (flag == 0) {
            Log.d("log-MainActivity", "contactArrayListFilter start with flag 0");
            contactArrayList.clear();
            contactArrayList.addAll(filteredContactArrayList);
            recyclerViewAdapter.notifyDataSetChanged();
            return;
        }
        Log.d("log-MainActivity", "contactArrayListFilter start");
        contactArrayList.clear();
        contactArrayList.addAll(filteredContactArrayList);
        for (ContactWithMassengerEntity e : filteredContactArrayList) {
            if (!e.getDisplayName().toLowerCase().contains(newText.toLowerCase())) {
                contactArrayList.remove(e);
            }
        }
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private final int FirstTimeAppSyncAllContactRequestCode = 203;
    private final int FirstTimeProfileUpdateRequestCode = 202;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_LOGIN_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                //1101 is code for this permission checking
                this.LoginIntentData = data;
                startMain();
                startFirstTimeProfileUpdate();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("log-onActivityResult", "Activity RESULT_CANCELED");
            }
        } else if (requestCode == FirstTimeProfileUpdateRequestCode) {
            startFirstTimeContactSync();
        } else if (requestCode == FirstTimeAppSyncAllContactRequestCode) {
//            startMain();
        }
    } //onActivityResult


    public void getListOfAllUserContact(View view) {
//        Toast.makeText(this, "you Click all contact details", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, AllContactOfUserInDeviceView.class);
        Log.d("log-getListOfAllUserContact", "calling getListOfAllUserContact activity");
        startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    public static void setNewMassegeArriveValueToEmpty(int position) {
        ContactWithMassengerEntity contactView = contactArrayList.get(position);
        contactView.setNewMassegeArriveValue(0);
        MainActivity.contactArrayList.set(position, contactView);
        MainActivity.recyclerViewAdapter.notifyDataSetChanged();
    }

    public static void FetchDataFromServerAndSaveIntoDB(String CID) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivityStaticContext);
                String endpoint = URL_MAIN + "GetContactDetailsOfUserToSaveLocally";
                JSONArray mainArray = new JSONArray();
                mainArray.put(user_login_id);
                mainArray.put(CID);
                Log.d("log-MainActivity", "mainArray : " + mainArray.toString());
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, endpoint, mainArray, new Response.Listener<JSONArray>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("log-MainActivity", "onResponse: response length : " + response.length());
                        try {
                            Log.d("log-MainActivity", "onResponse: response[0] : " + response.get(0));
                            String CID = String.valueOf(response.get(0));
                            long Number = Long.parseLong(String.valueOf(response.get(1)));
                            String Name = (String) response.get(2);
                            String DisplayName = (String) response.get(5);
                            MassegeDao massegeDao = db.massegeDao();
                            long rank = massegeDao.getHighestPriorityRank(user_login_id);
                            ContactWithMassengerEntity newContact = new ContactWithMassengerEntity(Number, null, CID, rank + 1);
                            if (massegeDao.getContactWith_CID(CID, user_login_id) == null) {
                                massegeDao.SaveContactDetailsInDatabase(newContact);
                                massegeDao.setPriorityRank(CID, massegeDao.getHighestPriorityRank(user_login_id), user_login_id);
                                Log.d("log-MainActivity", "onResponse: newContact saved into with rank :" + (rank + 1));
                                // now we have to add contact into recyclerViewAdapter
                                contactArrayList.add(0, newContact);
                                recyclerViewAdapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("log-AllContactOfUserDeviceView", "onErrorResponse: setChatDetails error: " + error);
                    }
                });
                jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                requestQueue.add(jsonArrayRequest);
            }
        });
        t.start();
    }


    private void CreateSocketConnection() {

        Log.d("log-MainActivity", "CreateSocketConnection: start");
        socketOBJ = new SocketClass(db);
        socket = socketOBJ.getSocket();
        if (socket == null) {
            return;
        }

        socketOBJ.joinRoom(user_login_id);

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                Log.d("log-MainActivity", "onJoinAcknowledgement: join success ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(MainActivity.this, "onJoinAcknowledgement: join success ", Toast.LENGTH_LONG).show();
                    }
                });
                List<MassegeEntity> tmp3, tmp4;
                //we have to get list of all masseges and send them to server at user came online
                MassegeHolderForSpecificPurpose mhsp = new MassegeHolderForSpecificPurpose(db, -1);
                tmp3 = mhsp.getMassegeList();
                try {
                    JSONArray massegeData = new JSONArray();
                    for (int i = 0; i < tmp3.size(); i++) {
                        MassegeEntity tmp1 = tmp3.get(i);
                        try {
                            JSONObject massegeOBJ = new JSONObject();
                            massegeOBJ.put("from", tmp1.getSenderId());
                            massegeOBJ.put("massege", tmp1.getMassege());
                            massegeOBJ.put("to", tmp1.getReceiverId());
                            massegeOBJ.put("chatId", tmp1.getChatId());
                            massegeOBJ.put("time", tmp1.getTimeOfSend());
                            massegeOBJ.put("massegeStatus", 0);
                            massegeOBJ.put("massegeStatusL", 1);
                            massegeOBJ.put("ef1", 1);
                            massegeOBJ.put("ef2", 1);
                            massegeData.put(massegeOBJ);
                        } catch (Exception ex) {
                            Log.d("log-onJoinAcknowledgement", "exception || :" + ex);
                            Log.d("log-onJoinAcknowledgement", "exception || massegeData : " + massegeData);
                            Log.d("log-onJoinAcknowledgement", "exception || tmp3.size() : " + massegeData.length());
                        }
                    }
                    if (tmp3.size() > 0) {

                        socket.emit("send_massege_to_server_from_sender", user_login_id, massegeData);
                        Log.d("log-MainActivity", "onJoinAcknowledgement || massegeData size :" + massegeData);

                    }
                } catch (Exception e) {
                    Log.d("log-MainActivity", "onJoinAcknowledgement || exception :" + e);
                }


                MassegeHolderForSpecificPurpose mhsp1 = new MassegeHolderForSpecificPurpose(db, 0);
                tmp3 = mhsp1.getMassegeList();

                try {
                    JSONArray massegeData = new JSONArray();
                    for (int i = 0; i < tmp3.size(); i++) {
                        MassegeEntity tmp1 = tmp3.get(i);
                        try {
                            JSONObject massegeOBJ = new JSONObject();
                            massegeOBJ.put("from", tmp1.getSenderId());
                            massegeOBJ.put("massege", tmp1.getMassege());
                            massegeOBJ.put("to", tmp1.getReceiverId());
                            massegeOBJ.put("chatId", tmp1.getChatId());
                            massegeOBJ.put("time", tmp1.getTimeOfSend());
                            massegeOBJ.put("massegeStatus", tmp1.getMassegeStatus());
                            massegeOBJ.put("massegeStatusL", 1);
                            massegeOBJ.put("ef1", 1);
                            massegeOBJ.put("ef2", 1);
                            massegeData.put(massegeOBJ);
                        } catch (Exception ex) {
                            Log.d("log-onJoinAcknowledgement", "exception || :" + ex);
                            Log.d("log-onJoinAcknowledgement", "exception || massegeData : " + massegeData);
                            Log.d("log-onJoinAcknowledgement", "exception || tmp3.size() : " + massegeData.length());
                        }
                    }
                    if (tmp3.size() > 0) {
                        socket.emit("send_massege_to_server_from_sender", user_login_id, massegeData);
                        Log.d("log-MainActivity", "onJoinAcknowledgement || massegeData size :" + massegeData);
                    }
                } catch (Exception e) {
                    Log.d("log-MainActivity", "onJoinAcknowledgement || exception :" + e);
                }

                //updating userProfileImages
                JSONArray jsonArray = new JSONArray();
                try {
                    Log.d("log-MainActivity", "updating userProfileImages : " + contactArrayList.size());

                    for (ContactWithMassengerEntity e : contactArrayList) {
                        try {
                            JSONObject tmp = new JSONObject();
                            tmp.put("_id", e.getCID());
                            tmp.put("Number", e.getMobileNumber());
                            tmp.put("ProfileImageVersion", e.getProfileImageVersion());
                            jsonArray.put(tmp);
                        } catch (Exception ex) {
                            Log.d("log-ContactListAdapter-Exception", ex.toString());
                        }
                    }
                    Log.d("log-MainActivity", "profileImage update part : " + jsonArray.toString());
                    socket.emit("updateProfileImages", user_login_id, jsonArray, 1);
                } catch (Exception exception) {
                    Log.d("log-ContactListAdapter-Exception", exception.toString());
                }
            }
        });

        socket.on("new_massege_from_server", onMassegeArriveFromServer);
        socket.on("send_massege_to_server_from_sender_acknowledgement", onMassegeReachAtServerFromCMDV);

        socket.on("massege_reach_read_receipt", onMassegeReachReadReceipt);

        socket.on("updateSingleContactProfileImage", onUpdateSingleContactProfileImage);

        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.connect();
            }
        });
        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("log-MainActivity", "Socket.EVENT_DISCONNECT socket.isActive() : ");
            }
        });
    }

    //socket event listener define here

    //completed
    private final Emitter.Listener onUpdateSingleContactProfileImage = new Emitter.Listener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void call(final Object... args) {
            Log.d("log-onUpdateSingleContactProfileImage", "onUpdateSingleContactProfileImage || start ");
            String userId = String.valueOf(args[0]);
            String id = String.valueOf(args[1]);
            long ProfileImageVersion = Long.parseLong(String.valueOf(args[3]));
            String profileImageBase64;
            try {
                profileImageBase64 = (String) args[2];
                if (profileImageBase64 == null) {

                } else {
                    byte[] profileImageByteArray = Base64.decode(profileImageBase64, Base64.DEFAULT);

                    if (profileImageByteArray.length > 0) {
                        synchronized (this) {
                            Bitmap bitmapImage = BitmapFactory.decodeByteArray(profileImageByteArray, 0, profileImageByteArray.length);
                            Log.d("log-saveImageToInternalStorage", "Saved image of size : " + profileImageByteArray.length + " and resolution : " + bitmapImage.getWidth() + "*" + bitmapImage.getHeight());
                            contactListAdapter.practiceMethod(id, profileImageByteArray);// to update contactList

                            if (saveContactProfileImageToStorage(id, profileImageByteArray)) {
                                massegeDao.updateProfileImageVersion(id, ProfileImageVersion, user_login_id);
                            }
                        }
                    }
                    Log.d("log-onUpdateSingleContactProfileImage", "ProfileImageVersion : " + ProfileImageVersion + " and for cid : " + id + " bytearray : " + Arrays.toString(profileImageByteArray));
                }
            } catch (Exception ex) {
                Log.d("log-onUpdateSingleContactProfileImage-Exception", ex.toString());
            }
        }
    };

    public static boolean saveContactProfileImageToStorage(String CID, byte[] profileImageByteArray) {

//        Bitmap bitmapImage = BitmapFactory.decodeByteArray(profileImageByteArray, 0, profileImageByteArray.length);

        Bitmap bitmapImage = BitmapFactory.decodeByteArray(profileImageByteArray, 0, profileImageByteArray.length);


        File directory = new File(Environment.getExternalStorageDirectory(), "Android/media/com.massenger.mank.main/Pictures/Profiles");
        if (!directory.exists()) {
            boolean success = directory.mkdirs();
            if (!success) {
                Log.d("log-saveByteArrayToInternalStorage", "Failed to create directory");
                return false;
            }
        }

        // Create the file path
        File imagePath = new File(directory, "" + CID + user_login_id + ".png");
        // Save the bitmap image to the file
        try (OutputStream outputStream = new FileOutputStream(imagePath)) {
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            Log.d("log-saveImageToInternalStorage", "Saved image at path: " + imagePath.getAbsolutePath());
            Log.d("log-saveImageToInternalStorage", "Saved image of size : " + bitmapImage.getWidth() + "*" + bitmapImage.getHeight());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("log-saveImageToInternalStorage", "Image Save failed " + e.toString());
        }
        // Print the absolute path of the saved image
        return false;
    }

    private final Emitter.Listener onMassegeReachReadReceipt = new Emitter.Listener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void call(final Object... args) {
            int requestCode = Integer.parseInt(String.valueOf(args[0]));
            Log.d("log-onMassegeReachReadReceipt", "onMassegeReachReadReceipt || start requestCode:" + requestCode);
            if (requestCode == 1) {
                JSONObject data = (JSONObject) args[1];
                try {
                    int viewStatus = Integer.parseInt(String.valueOf(data.get("massegeStatus")));
                    long massege_sent_time = Long.parseLong(String.valueOf(data.get("time")));
                    String sender_id = String.valueOf(data.get("from"));
                    String receiver_id = String.valueOf(data.get("to"));

                    if (Contact_page_opened_id.equals(receiver_id)) {
                        Log.d("log-onMassegeReachReadReceipt", "page is opened obj" + data);
                        massegeListAdapter.updateMassegeStatus(receiver_id, massege_sent_time, viewStatus);
                    }

                    // update view status into database
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int massegeStatus = massegeDao.getMassegeStatus(sender_id, receiver_id, massege_sent_time, user_login_id);
                            if (massegeStatus < viewStatus) {
                                massegeDao.updateMassegeStatus(sender_id, receiver_id, massege_sent_time, viewStatus, user_login_id);
                            }
                        }
                    });
                    t.start();

                    JSONArray x;
                    try {
                        x = new JSONArray();
                        x.put(data);
                        socket.emit("massege_reach_read_receipt_acknowledgement", 1, user_login_id, x);
                        Log.d("log-onMassegeReachReadReceipt", "massege_reach_read_receipt_acknowledgement event emitted");
                    } catch (Exception e) {
                        Log.d("log-onMassegeReachReadReceipt", "Exception || e:" + e);
                    }
                } catch (Exception e) {
                    Log.d("log-MA-onMassegeReachReadReceipt", "Exception || e:" + e);
                }
            } else {
                Log.d("log-MainActivity", "onMassegeReachReadReceipt || request code :" + requestCode);
            }
        }
    };

    //completed
    private final Emitter.Listener onMassegeArriveFromServer = new Emitter.Listener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void call(final Object... args) {
            int acknowledgement_id = (int) args[0];
            int requestCode = -1;
            try {
                requestCode = (int) args[2];
            } catch (Exception e) {
                Log.d("log-exception-in-massege-arrive", "call: Exception is : " + e);
            }
            Log.d("log-onMassegeArriveFromServer3", "onMassegeArriveFromServer || requestCode is : " + requestCode);

            //at send by user imidiate
            if (requestCode == 0) {
                long new_massege_time_of_send = -1;
                try {
                    JSONObject new_massege = (JSONObject) args[1];
                    Log.d("log-onMassegeArriveFromServer3", "args : " + new_massege.toString());

                    String new_massege_sender_id = String.valueOf(new_massege.get("from"));
                    new_massege_time_of_send = (long) new_massege.get("time");
                    ArrayList<ContactWithMassengerEntity> contactArrayList1;
                    contactArrayList1 = contactArrayList;
                    String massege = String.valueOf(new_massege.get("massege"));
                    int viewStatus = 2;
                    MassegeEntity newMassegeEntity1 = new MassegeEntity(new_massege_sender_id, user_login_id, massege, new_massege_time_of_send, viewStatus);

                    Log.d("log-onMassegeArriveFromServer3", "time : " + new_massege_time_of_send);
                    for (int i = 0; i < contactArrayList1.size(); i++) {
                        if (contactArrayList1.get(i).getCID().equals(new_massege_sender_id)) {
                            //means massege arrive from known or saved contacts
                            Log.d("log-onMassegeArriveFromServer3", "massege arrive from known sources : ");
                            if (new_massege_sender_id.equals(Contact_page_opened_id)) {
                                Log.d("log-onMassegeArriveFromServer3", "Contact page is opened");
                                viewStatus = 3;
                                MassegeEntity newMassegeEntity2 = new MassegeEntity(new_massege_sender_id, user_login_id, String.valueOf(new_massege.get("massege")), new_massege_time_of_send, viewStatus);
                                massegeListAdapter.addMassege(newMassegeEntity2, 1);//1 contact page is opened
                                MassegePopSoundThread massegePopSoundThread = new MassegePopSoundThread(MainActivity.this, 0);
                                massegePopSoundThread.start();

                            } else {
                                Log.d("log-onMassegeArriveFromServer3", "Contact page is not opened");
                                ContactWithMassengerEntity contactView = contactArrayList1.get(i);
                                int prev_value = contactView.getNewMassegeArriveValue();
                                contactView.setNewMassegeArriveValue(prev_value + 1);
                                contactView.setLastMassege(massege.toString());
                                MainActivity.contactArrayList.set(i, contactView);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        MainActivity.recyclerViewAdapter.notifyDataSetChanged();
                                        MainActivity.ChatsRecyclerView.scrollToPosition(MainActivity.recyclerViewAdapter.getItemCountMyOwn());
                                    }
                                });
                                MassegePopSoundThread massegePopSoundThread = new MassegePopSoundThread(MainActivity.this, 1);
                                massegePopSoundThread.start();
                                Thread t = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        massegeDao.incrementNewMassegeArriveValue(new_massege_sender_id, user_login_id);
                                    }
                                });
                                t.start();

                                Thread massegeInsertIntoDatabase = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MassegeDao massegeDao = db.massegeDao();
                                        try {
                                            massegeDao.insertMassegeIntoChat(newMassegeEntity1);
                                            Log.d("log-onMassegeArriveFromServer3", "massege is inserted into database successfully");
                                        } catch (Exception e) {
                                            showPopUpMessage(e.toString());
                                            Log.d("log-sql-exception", e.toString() + " for massege:" + newMassegeEntity1.getMassege() + ", s_id:" + newMassegeEntity1.getSenderId() + ", r_id:" + newMassegeEntity1.getReceiverId() + ", time:" + newMassegeEntity1.getTimeOfSend() + ", status:" + newMassegeEntity1.getMassegeStatus());
                                            updateMassegeStatusFromException(new_massege);
                                        }
                                    }
                                });
                                massegeInsertIntoDatabase.start();
                            }
                        } else {
                            //means massege arrive from unknown or new contact
                            Log.d("log-onMassegeArriveFromServer3", "massege arrive from known sources : ");
                        }
                    }
                    Thread checkContactSavedInDB = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ContactWithMassengerEntity x = massegeDao.getContactWith_CID(newMassegeEntity1.getSenderId(), user_login_id);
                            if (x == null) {
                                Log.d("log-onMassegeArriveFromServer3", "setPriorityRankThread1");
                                FetchDataFromServerAndSaveIntoDB(newMassegeEntity1.getSenderId());
                            } else {
                                Thread setPriorityRankThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        long HighestPriority = massegeDao.getHighestPriorityRank(user_login_id);
                                        massegeDao.setPriorityRank(newMassegeEntity1.getSenderId(), HighestPriority + 1, user_login_id);
                                        if (contactListAdapter != null) {
                                            contactListAdapter.updatePositionOfContact(newMassegeEntity1.getSenderId(), MainActivity.this);
                                        }
                                    }
                                });
                                setPriorityRankThread.start();
                            }
                        }
                    });
                    checkContactSavedInDB.start();


                    JSONArray jsonArray = new JSONArray();
                    try {
                        JSONObject tmpOBJ = new JSONObject();
                        tmpOBJ.put("to", newMassegeEntity1.getReceiverId());
                        tmpOBJ.put("from", new_massege_sender_id);
                        tmpOBJ.put("time", new_massege_time_of_send);
                        tmpOBJ.put("massegeStatus", viewStatus);
                        jsonArray.put(tmpOBJ);
                        if (!Objects.equals(new_massege_sender_id, user_login_id)) {
                            socket.emit("massege_reach_read_receipt", 3, user_login_id, jsonArray);// ViewStatus 2 means at contact's database and 3 means read by contact
                        }
                        Log.d("log-onMassegeArriveFromServer3", "massege_reach_read_receipt_acknowledgement socket emit :" + jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.d("log-onMassegeArriveFromServer-Exception", "call: error while parsing data : " + e);
                }

            } else {

                showPopUpMessage("onMassegeArriveFromServer unHandled requestCode arrive : " + requestCode);
//                Toast.makeText(MainActivity.this, "onMassegeArriveFromServer unHandled requestCode arrive : "+requestCode, Toast.LENGTH_LONG).show();
                Log.d("log-onMassegeArriveFromServer", "onMassegeArriveFromServer unHandled requestCode arrive : " + requestCode);
            }
        }
    };


    //check if status is updatable or not and update
    private void updateMassegeStatusFromException(JSONObject new_massege) {
        try {
            int massegeStatus = (int) new_massege.get("massegeStatus");
            String s = String.valueOf(new_massege.get("from"));
            String r = String.valueOf(new_massege.get("to"));
            long t = (long) new_massege.get("time");
            int vs = massegeDao.getMassegeStatus(s, r, t, user_login_id);
            if (vs < massegeStatus) {
                massegeDao.updateMassegeStatus(s, r, t, massegeStatus, user_login_id);
            }
        } catch (Exception e) {
            Log.d("log-updateMassegeStatusFromException-function", e.toString());
        }
    }

    //completed
    private final Emitter.Listener onMassegeReachAtServerFromCMDV = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject new_massege;
            try {
                new_massege = (JSONObject) args[1];
                long time = Long.parseLong(String.valueOf(new_massege.get("time")));
                String sender_id = String.valueOf(new_massege.get("from"));
                String receiver_id = String.valueOf(new_massege.get("to"));

                //massegeListHolder update required;
                int massegeStatus = massegeDao.getMassegeStatus(sender_id, receiver_id, time, user_login_id);
                if (massegeStatus < 1) {
                    massegeDao.updateMassegeStatus(sender_id, receiver_id, time, 1, user_login_id);
                }
                Log.d("log-onMassegeReachAtServerFromCMDV", new_massege.toString());
            } catch (Exception e) {
                Log.d("log-onMassegeReachAtServerFromCMDV", "Exception || e:" + e);
            }
        }
    };


    private void showPopUpMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popUpView = getLayoutInflater().inflate(R.layout.popup_message, null);
        builder.setView(popUpView);
        TextView messageTextView = popUpView.findViewById(R.id.messageTextView);
        Button closeButton = popUpView.findViewById(R.id.closeButton);
        messageTextView.setText(message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = builder.create();
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

    }


    public void getMainSideMenu(View view) {
        Log.d("log-enter", "getMainSideMenu: enter here");
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main_popup, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.MainMenuSetting) {
                    Intent intent = new Intent(MainActivity.this, AllSettingsActivity.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.MainMenuBG) {
                    Intent intent = new Intent(MainActivity.this, BgImageSetForContactPage.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.MainMenuExtra) {
                    Toast.makeText(MainActivity.this, "coming soon...", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }


    private void saveFireBaseTokenToServer(String user_login_id) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("log-saveFireBaseTokenToServer", "Fetching FCM registration token failed", task.getException());
                    return;
                }
                // Get new FCM registration token
                String token = task.getResult();
                // Log and toast
                Log.d("log-saveFireBaseTokenToServer", "token : " + token);
//                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();

                //store token to server with emailId
                String endpoint = URL_MAIN + "SaveFireBaseTokenToServer";
                Log.d("log-endpoint", endpoint);
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                StringRequest request = new StringRequest(Request.Method.POST, endpoint, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject respObj = new JSONObject(response);
                            String status = respObj.getString("status");
                            Log.d("log-saveFireBaseTokenToServer-response", "status : " + status);

                            if (status.equals("1")) {
                                Log.d("log-saveFireBaseTokenToServer-response", "token saved successfully");
                            } else if (status.equals("2")) {
                                Log.d("log-saveFireBaseTokenToServer-response", "token not saved");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("log-error", "onResponse: err in try bracket : " + e);
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Server side error :  " + error, Toast.LENGTH_SHORT).show();
                        Log.d("volley-error-saveFireBaseTokenToServer", "Server side error : " + error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {

                        MyCipher mc = new MyCipher();

                        Map<String, String> params = new HashMap<String, String>();
                        params.put("user_login_id", mc.encrypt(user_login_id));
                        params.put("tokenFCM", mc.encrypt(token));
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("api_key", API_SERVER_API_KEY);
                        return headers;
                    }
                };
                requestQueue.add(request);
            }
        });
    }

    private void startNetworkListener() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(ConnectivityManager.class);
        connectivityManager.requestNetwork(networkRequest, networkCallback);
    }

    private final NetworkRequest networkRequest = new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build();
    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            if (socket != null) {
                if (socket.isActive()) {
                    Log.d("log-ConnectivityManager.NetworkCallback", "onAvailable || if cond isActive : true");
//                    socket.connect();
                } else {
                    CreateSocketConnection();
                    Log.d("log-ConnectivityManager.NetworkCallback", "onAvailable || if cond isActive : false");
                }
            } else {
                Log.d("log-ConnectivityManager.NetworkCallback", "onAvailable || else cond.");
                CreateSocketConnection();
            }
            // send massege which is not sent due to  internet connection
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            Log.d("log-ConnectivityManager.NetworkCallback", "onLost");
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            final boolean unMetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
            Log.d("log-ConnectivityManager.NetworkCallback", "onCapabilitiesChanged unMetered:" + unMetered);
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Camera Permission needed to use the massenger", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission needed to use the massenger", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_ALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(MainActivity.this, "To use Massenger please give all permissions", Toast.LENGTH_SHORT).show();
//                initContentResolver();
//                this.finish();
            }

        } else if (requestCode == PERMISSION_CONTACT_SYNC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                syncContactAtAppStart();
            } else {
                Toast.makeText(MainActivity.this, "To use Massenger please give Contact and Storage permission", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PERMISSION_initContentResolver) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                initContentResolver();
            } else {
                Toast.makeText(MainActivity.this, "To use Massenger you must give the Contact Read and Write permission, please restart the app", Toast.LENGTH_SHORT).show();
//                initContentResolver();
            }
        }
    }

}