package com.example.mank;

import static com.example.mank.ContactMassegeDetailsView.massegeArrayList;
import static com.example.mank.ContactMassegeDetailsView.massegeRecyclerViewAdapter;
import static com.example.mank.ContactMassegeDetailsView.massege_recyclerView;
import static com.example.mank.configuration.GlobalVariables.URL_MAIN;
import static com.example.mank.configuration.permissionMain.hasPermissions;
import static com.example.mank.configuration.permission_code.CAMERA_PERMISSION_CODE;
import static com.example.mank.configuration.permission_code.STORAGE_PERMISSION_CODE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.ContactListHolder;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.MassegeHolderForSpecificPurpose;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.userIdEntityHolder;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;
import com.example.mank.LocalDatabaseFiles.entities.MassegeEntity;
import com.example.mank.LoginMenagement.Login;
import com.example.mank.RecyclerViewClassesFolder.RecyclerViewAdapter;
import com.example.mank.TabMainHelper.SectionsPagerAdapter;
import com.example.mank.ThreadPackages.MassegePopSoundThread;
import com.example.mank.ThreadPackages.StatusForThread;
import com.example.mank.ThreadPackages.onMassegeArriveThread1;
import com.example.mank.cipher.MyCipher;
import com.example.mank.databinding.ActivityMainBinding;
import com.example.mank.profile.BgImageSetForContactPage;
import com.example.mank.profile.ProfileUploadActivity;
import com.example.mank.socket.SocketClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends FragmentActivity {

    public static int FinishCode = 0;
    public static long Contact_page_opened_id = -1;
    boolean toStopAppMainThread = false;

    public int x;
    //Globle socket variables
    public static SocketClass socketOBJ;
    public static Socket socket;

    public static long user_login_id;
    public static long UserMobileNumber;
    public static RecyclerView ChatsRecyclerView;
    private SearchView MAPSearchView;
    public static RecyclerViewAdapter recyclerViewAdapter;
    public static ArrayList<ContactWithMassengerEntity> contactArrayList;
    public static MainDatabaseClass db;

    public static ContactListHolder MainContactListHolder;

    private final int PERMISSION_ALL = 1;
    private final String[] PERMISSIONS = {android.Manifest.permission.INTERNET, android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.CHANGE_NETWORK_STATE, android.Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.WRITE_CONTACTS,};
    private boolean appOpenFromBackGround = false;
    private boolean EverythingIsOhkInApp = false;
    private ProgressBar mainProgressBar;
    int LAUNCH_LOGIN_ACTIVITY = 1;
    public Intent LoginIntentData;
    private MassegeDao massegeDao;
    public static Context MainActivityStaticContext;
    private ActivityMainBinding binding;

    @Override
    protected void onStart() {
        super.onStart();
        startNetworkListener();
        MainActivityStaticContext = this;
        Contact_page_opened_id = -1;
        FinishCode = 0;
        Log.d("log-Contact_page_opened_id", "onStart: in MainActivity Contact_page_opened_id  is  : " + Contact_page_opened_id);
        if (!appOpenFromBackGround) {
            appOpenFromBackGround = true;
        } else if (EverythingIsOhkInApp) {
            //we will send that we are online massege to server
            Log.d("log-Contact_page_opened_id", "EverythingIsOhkInApp is :  " + EverythingIsOhkInApp);
        }
    }
    public static StatusForThread statusForThread;
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

        Login login = new Login();

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        if (login.isLogIn(db) == 0) {
            Log.d("log-not logined", "onCreate: not login cond. reached");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LAUNCH_LOGIN_ACTIVITY);
            Log.d("log-FinishCode", "onCreate: FinishCode is: " + FinishCode);
            FinishCode = 2;
        } else {
            startMain();
        }

    }
    private void startMain() {
        userIdEntityHolder userIdEntityHolder = new userIdEntityHolder(db);
        user_login_id = userIdEntityHolder.getUserLoginId();
        UserMobileNumber = userIdEntityHolder.getUserMobileNumber();
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
    public static ArrayList<ContactWithMassengerEntity> filterdContactArrayList;
    @SuppressLint("NotifyDataSetChanged")
    public void contactArrayListFilter(String newText, int flag) {
        if (flag == 0) {
            Log.d("log-MainActivity", "contactArrayListFilter start with flag 0");
            contactArrayList.clear();
            contactArrayList.addAll(filterdContactArrayList);
            recyclerViewAdapter.notifyDataSetChanged();
            return;
        }
        Log.d("log-MainActivity", "contactArrayListFilter start");
        contactArrayList.clear();
        contactArrayList.addAll(filterdContactArrayList);
        for (ContactWithMassengerEntity e : filterdContactArrayList) {
            if (!e.getDisplay_name().toLowerCase().contains(newText.toLowerCase())) {
                contactArrayList.remove(e);
            }
        }
        recyclerViewAdapter.notifyDataSetChanged();
    }
    private final int FirstTimeAppSyncAllContactRequestCode = 202;
    private void startMainFirstTime() {
        userIdEntityHolder userIdEntityHolder = new userIdEntityHolder(db);
        user_login_id = userIdEntityHolder.getUserLoginId();
        saveFireBaseTokenToServer(String.valueOf(user_login_id));
        statusForThread = new StatusForThread(0);

        Intent intent = new Intent(this, AllContactOfUserInDeviceView.class);
        startActivityForResult(intent, FirstTimeAppSyncAllContactRequestCode);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_LOGIN_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                //1101 is code for this permission checking
                this.LoginIntentData = data;
                startMainFirstTime();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("log-onActivityResult", "Activity RESULT_CANCELED");
            }
        } else if (requestCode == FirstTimeAppSyncAllContactRequestCode) {
            startMain();
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
    public static void FetchDataFromServerAndSaveIntoDB(long CID) {
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
                            long CID = Long.parseLong(String.valueOf(response.get(0)));
                            long Number = Long.parseLong(String.valueOf(response.get(1)));
                            String Name = (String) response.get(2);
                            String DisplayName = (String) response.get(5);
                            MassegeDao massegeDao = db.massegeDao();
                            long rank = massegeDao.getHighestPriorityRank();
                            ContactWithMassengerEntity newContact = new ContactWithMassengerEntity(Number, null, CID, rank + 1);
                            if (massegeDao.getContactWith_CID(CID) == null) {
                                massegeDao.SaveContactDetailsInDatabase(newContact);
                                massegeDao.setPriorityRank(CID, massegeDao.getHighestPriorityRank());
                                Log.d("log-MainActivity", "onResponse: newContact saved into with rank :" + (rank + 1));
                                // now we have to add contact into recyclerViewAdapter
                                contactArrayList.add(0,newContact);
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
        socketOBJ = new SocketClass();
        socket = socketOBJ.getSocket();
        socketOBJ.joinRoom(user_login_id);

        socket.on("join_acknowledgement", onJoinAcknowledgement);

        socket.on("massege_sent_to_user", onMassegeSentToUser);
        socket.on("massege_seen_by_user", onMassegeSeenByUser);
        socket.on("massege_not_sent_to_user", onMassegeNotSentToUser);
        socket.on("massege_reach_receipt_from_server", onMassegeReachReceiptFromServer);

        socket.on("massege_sent_when_user_come_to_online_acknowledgement", onMSWUCTOAcknowledgement);
        socket.on("new_massege_from_server", onMassegeArriveFromServer);
        socket.on("send_massege_to_server_from_CMDV_acknowledgement", onMassegeReachAtServerFromCMDV);

        socket.on("massege_number_from_server", onMassegeNumberFromServerArrive);


        socket.on("massege_reach_read_receipt", onMassegeReachReadReceipt);

        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.connect();
            }
        });
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("log-MainActivity", "Socket.EVENT_CONNECT socket.isActive() : ");
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
    private final Emitter.Listener onMassegeNumberFromServerArrive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            int requestCode = Integer.parseInt(String.valueOf(args[0]));
            Log.d("log-MA", "onMassegeNumberFromServerArrive || start requestCode:"+requestCode);
            if (requestCode == 1) {
                JSONArray tmp = (JSONArray) args[2];
                try {
                    JSONObject data = (JSONObject) tmp.get(0);
                    long sender_id = Long.parseLong(String.valueOf(data.get("sender_id")));
                    long receiver_id = Long.parseLong(String.valueOf(data.get("receiver_id")));
                    long chat_id = Long.parseLong(String.valueOf(data.get("chat_id")));
                    long massege_number = Long.parseLong(String.valueOf(data.get("massege_number")));
                    int rowAffected = massegeDao.updateMassegeNumber(chat_id, massege_number);
                    Log.d("log-MA", "onMassegeNumberFromServerArrive || massege:" + massege_number + " rowAffected:" + rowAffected);
                } catch (Exception e) {
                    Log.d("log-MA", "Exception || e:" + e);
                }
            } else if (requestCode == 2) {
                JSONArray tmp = (JSONArray) args[2];
                try {
                    for (int i = 0; i < tmp.length(); i++) {
                        JSONObject data = (JSONObject) tmp.get(i);
                        long sender_id = Long.parseLong(String.valueOf(data.get("sender_id")));
                        long receiver_id = Long.parseLong(String.valueOf(data.get("receiver_id")));
                        long chat_id = Long.parseLong(String.valueOf(data.get("chat_id")));
                        long massege_number = Long.parseLong(String.valueOf(data.get("massege_number")));
                        int rowAffected = massegeDao.updateMassegeNumber(chat_id, massege_number);

                        Log.d("log-MA", "onMassegeNumberFromServerArrive || massege:" + massege_number + " rowAffected:" + rowAffected);
                    }
                } catch (Exception e) {
                    Log.d("log-MA", "Exception || e:" + e);
                }
            }
        }
    };
    private final Emitter.Listener onMassegeReachReadReceipt = new Emitter.Listener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void call(final Object... args) {
            int requestCode = Integer.parseInt(String.valueOf(args[0]));
            Log.d("log-MainActivity", "onMassegeReachReadReceipt || start requestCode:" + requestCode);
            if (requestCode == 1) {
                int viewStatus = Integer.parseInt(String.valueOf(args[1]));
                JSONObject data = (JSONObject) args[2];
                try {
                    long massege_sent_time = Long.parseLong(String.valueOf(data.get("massege_sent_time")));
                    long sender_id = Long.parseLong(String.valueOf(data.get("sender_id")));
                    long receiver_id = Long.parseLong(String.valueOf(data.get("receiver_id")));
                    Log.d("log-onMassegeReachReadReceipt-result", "page is opened obj"+data);
                    if (Contact_page_opened_id == receiver_id) {
                        ArrayList<MassegeEntity> x = ContactMassegeDetailsView.massegeArrayList;
                        for (int j = x.size() - 1; j >= 0; j--) {
                            if (x.get(j).getTimeOfSend() == massege_sent_time && x.get(j).getReceiverId() == receiver_id) {
                                x.get(j).setMassegeStatus(viewStatus);
                            }
                        }
                        ContactMassegeDetailsView.massegeRecyclerViewAdapter.notifyDataSetChanged();
                    }
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            massegeDao.updateMassegeStatus(sender_id, receiver_id, massege_sent_time, viewStatus);
                        }
                    });
                    t.start();


                } catch (Exception e) {
                    Log.d("log-MA-onMassegeReachReadReceipt", "Exception || e:" + e);
                }

            } else if (requestCode == 2) {
                int viewStatus = Integer.parseInt(String.valueOf(args[1]));
                JSONArray result = (JSONArray) args[2];
                try {
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject tmp = (JSONObject) result.get(i);
                        long CHAT_ID = Long.parseLong(String.valueOf(tmp.get("chat_id")));
                        long MassegeID = Long.parseLong(String.valueOf(tmp.get("massege_number")));
                        if (Contact_page_opened_id == Long.parseLong(String.valueOf(tmp.get("receiver_id")))) {
                            Log.d("log-onMassegeReachReadReceipt-result", "call: page is opened whose massege is arrived");
                            ArrayList<MassegeEntity> x = ContactMassegeDetailsView.massegeArrayList;
                            for (int j = x.size() - 1; j >= 0; j--) {
                                if (x.get(j).getChat_id() == CHAT_ID) {
                                    x.get(j).setMassegeStatus(viewStatus);
                                }
                            }
                            ContactMassegeDetailsView.massegeRecyclerViewAdapter.notifyDataSetChanged();
                        }
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                massegeDao.updateMassegeStatus(MassegeID, CHAT_ID, viewStatus);
                            }
                        });
                        t.start();
                    }
                } catch (Exception e) {
                    Log.d("log-MainActivity", "onMassegeReachReadReceipt || Exception:" + e);
                }
            } else if (requestCode == 3) {
                long userId = Long.parseLong(String.valueOf(args[1]));
                JSONArray result = (JSONArray) args[2];
                try {
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject tmp = (JSONObject) result.get(i);
                        long CHAT_ID = Long.parseLong(String.valueOf(tmp.get("chat_id")));
                        long MassegeID = Long.parseLong(String.valueOf(tmp.get("massege_number")));
                        int viewStatus = Integer.parseInt(String.valueOf(tmp.get("View_Status")));
                        Log.d("log-MA-reciept", "JSONObjet:" + tmp.toString());
                        if (Contact_page_opened_id == Long.parseLong(String.valueOf(tmp.get("receiver_id")))) {
                            Log.d("log-onMassegeReachReadReceipt-result", "call: page is opened whose massege is arrived");
                            ArrayList<MassegeEntity> x = ContactMassegeDetailsView.massegeArrayList;
                            for (int j = x.size() - 1; j >= 0; j--) {
                                if (x.get(j).getChat_id() == CHAT_ID) {
                                    x.get(j).setMassegeStatus(Integer.parseInt((String) tmp.get("View_Status")));
                                }
                            }
                            ContactMassegeDetailsView.massegeRecyclerViewAdapter.notifyDataSetChanged();
                        }
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("log-MA-reciept", "thread is running");
                                int r = massegeDao.updateMassegeStatus(MassegeID, CHAT_ID, viewStatus);
                                Log.d("log-MA-reciept", "updateMassegeStatus r:" + r);

                            }
                        });t.start();
                    }
                    socket.emit("massege_reach_read_receipt_acknowledgement",user_login_id,requestCode,result );
                } catch (Exception e) {
                    Log.d("log-MainActivity", "onMassegeReachReadReceipt || Exception:" + e);
                }
            } else {
                Log.d("log-MainActivity", "onMassegeReachReadReceipt || request code :" + requestCode);
            }
        }
    };
    private final Emitter.Listener onJoinAcknowledgement = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("log-MainActivity", "onJoinAcknowledgement: join success ");

            List<MassegeEntity> tmp3, tmp4;
            //we have to get list of all masseges and send them to server at user came online
            MassegeHolderForSpecificPurpose mhsp = new MassegeHolderForSpecificPurpose(db, 1);
            tmp3 = mhsp.getMassegeList();
            try {
                JSONArray massegeData = new JSONArray();
                for (int i = 0; i < tmp3.size(); i++) {
                    MassegeEntity tmp1 = tmp3.get(i);
                    try {
                        JSONObject massegeOBJ = new JSONObject();
                        massegeOBJ.put("sender_id", tmp1.getSenderId());
                        massegeOBJ.put("user_massege", tmp1.getMassege());
                        massegeOBJ.put("C_ID", tmp1.getReceiverId());
                        massegeOBJ.put("Chat_id", tmp1.getChat_id());
                        massegeOBJ.put("time_of_send", tmp1.getTimeOfSend());
                        massegeOBJ.put("massege_status", 0);
                        massegeData.put(massegeOBJ);
                    } catch (Exception ex) {
                        Log.d("log-onJoinAcknowledgement", "exception || :" + ex);
                        Log.d("log-onJoinAcknowledgement", "exception || massegeData : " + massegeData);
                        Log.d("log-onJoinAcknowledgement", "exception || tmp3.size() : " + massegeData.length());
                    }
                }
                if (tmp3.size() > 0) {
                    socket.emit("massege_sent_when_user_come_to_online", user_login_id, massegeData, massegeData.length());
                    Log.d("log-MainActivity", "onJoinAcknowledgement || massegeData size :" + massegeData.length());
                }
            } catch (Exception e) {
                Log.d("log-MainActivity", "onJoinAcknowledgement || exception :" + e);
            }


            //we have to maintain consistancy of MassegeId in database
            MassegeHolderForSpecificPurpose mhsp2 = new MassegeHolderForSpecificPurpose(db, 2);
            tmp4 = mhsp2.getMassegeList();
            try {
                JSONArray massegeData = new JSONArray();
                for (int i = 0; i < tmp4.size(); i++) {
                    MassegeEntity tmp1 = tmp4.get(i);
                    try {
                        JSONObject massegeOBJ = new JSONObject();
                        massegeOBJ.put("sender_id", tmp1.getSenderId());
                        massegeOBJ.put("C_ID", tmp1.getReceiverId());
                        massegeOBJ.put("Chat_id", tmp1.getChat_id());
                        massegeData.put(massegeOBJ);
                    } catch (Exception ex) {
                        Log.d("log-onJoinAcknowledgement", "exception || :" + ex);
                    }
                }
                if (tmp4.size() > 0) {
                    socket.emit("massege_number_fetch", user_login_id, massegeData, massegeData.length());
                    Log.d("log-MainActivity", "onJoinAcknowledgement || massegeData size :" + massegeData.length());
                }
            } catch (Exception e) {
                Log.d("log-MainActivity", "onJoinAcknowledgement || exception :" + e);
            }

        }
    };
    private final Emitter.Listener onMSWUCTOAcknowledgement = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("log-MainActivity", "onMHTSACAcknowledgement || start ");
            long userId = Long.parseLong(String.valueOf(args[0]));
            JSONArray massegeReturnData = (JSONArray) args[1];

            for (int i = 0; i < massegeReturnData.length(); i++) {
                try {
                    long CHAT_ID = Long.parseLong(String.valueOf(massegeReturnData.get(i)));
                    Log.d("log-MainActivity", "onMHTSACAcknowledgement || data[" + i + "] = " + massegeReturnData.get(i) + " in long format : " + CHAT_ID);
                    // updating the massege_status in dataBase
                    massegeDao.updateMassegeStatus(CHAT_ID, 1);

                } catch (Exception e) {
                    Log.d("log-MainActivity", "onMHTSACAcknowledgement || exception :" + e);
                }
            }

        }
    };
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
            Log.d("log-MainActivity", "onMassegeArriveFromServer || requestCode is : " + requestCode);
            Log.d("log-MainActivity", "onMassegeArriveFromServer || args:" + String.valueOf(acknowledgement_id));

            //at send by user imidiate
            if (requestCode == 3) {
                long new_massege_time_of_send = -1;
                try {
                    JSONObject new_massege = (JSONObject) args[1];
                    Log.d("log-onMassegeArriveFromServer3", "args" + new_massege.toString());
                    long new_massege_sender_id = Long.parseLong(String.valueOf(new_massege.get("sender_id")));
                    new_massege_time_of_send = (long) new_massege.get("time_of_send");
                    ArrayList<ContactWithMassengerEntity> contactArrayList1;
                    contactArrayList1 = MainActivity.contactArrayList;

                    int viewStatus = 2;
                    MassegeEntity newMassegeEntity1 = new MassegeEntity(new_massege_sender_id, user_login_id, String.valueOf(new_massege.get("user_massege")), new_massege_time_of_send, 1);
                    for (int i = 0; i < contactArrayList1.size(); i++) {
                        if (contactArrayList1.get(i).getC_ID() == new_massege_sender_id) {
                            if (new_massege_sender_id == Contact_page_opened_id) {
                                Log.d("log-onMassegeArriveFromServer3", "Contact page is not opened");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (massegeDao.getMassegeByTimeOfSend(newMassegeEntity1.getSenderId(), newMassegeEntity1.getTimeOfSend()) == null) {

                                            MassegePopSoundThread massegePopSoundThread = new MassegePopSoundThread(MainActivity.this, 0);
                                            massegePopSoundThread.start();

                                            massegeArrayList.add(newMassegeEntity1);
                                            massegeRecyclerViewAdapter.notifyDataSetChanged();
                                            massege_recyclerView.scrollToPosition(massegeRecyclerViewAdapter.getItemCountMyOwn());
                                        }
                                    }
                                });
                                viewStatus=3;
                            } else {
                                Log.d("log-onMassegeArriveFromServer3", "contact page is not opened");
                                int index = i;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        MassegePopSoundThread massegePopSoundThread = new MassegePopSoundThread(MainActivity.this, 1);
                                        massegePopSoundThread.start();
                                        Thread t = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                massegeDao.incrementNewMassegeArriveValue(new_massege_sender_id);
                                            }
                                        });
                                        t.start();

                                        ContactWithMassengerEntity contactView = contactArrayList1.get(index);
                                        int prev_value = contactView.getNewMassegeArriveValue();
                                        contactView.setNewMassegeArriveValue(prev_value + 1);
                                        MainActivity.contactArrayList.set(index, contactView);
                                        MainActivity.recyclerViewAdapter.notifyDataSetChanged();
                                        MainActivity.ChatsRecyclerView.scrollToPosition(MainActivity.recyclerViewAdapter.getItemCountMyOwn());
                                    }
                                });
                            }

                            Thread massegeInsertIntoDatabase = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    MassegeDao massegeDao = db.massegeDao();
                                    MassegeEntity x = massegeDao.getMassegeByTimeOfSend(newMassegeEntity1.getSenderId(), newMassegeEntity1.getTimeOfSend());
                                    if (x == null) {
                                        massegeDao.insertMassegeIntoChat(newMassegeEntity1);
                                        Log.d("log-onMassegeArriveFromServer3", "massege is inserted into database successfully");
                                    } else {
                                        Log.d("log-onMassegeArriveFromServer3", "X: " + x);
                                        Log.d("log-onMassegeArriveFromServer3", "X: " + x.getMassegeID());
                                    }
                                }
                            });
                            massegeInsertIntoDatabase.start();
                        }
                    }
                    Thread checkContactSavedInDB = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ContactWithMassengerEntity x = massegeDao.getContactWith_CID(newMassegeEntity1.getSenderId());
                            if (x == null) {
                                Log.d("log-onMassegeArriveFromServer3", "setPriorityRankThread1");
                                FetchDataFromServerAndSaveIntoDB(newMassegeEntity1.getSenderId());
                            } else {
                                Thread setPriorityRankThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        long HighestPriority = massegeDao.getHighestPriorityRank();
                                        massegeDao.setPriorityRank(newMassegeEntity1.getSenderId(), HighestPriority + 1);
                                        MainContactListHolder.updatePositionOfContact(newMassegeEntity1.getSenderId(), MainActivity.this);
                                    }
                                });
                                setPriorityRankThread.start();
                            }
                        }
                    });
                    checkContactSavedInDB.start();

                    try {
                        JSONObject tmpOBJ = new JSONObject();
                        tmpOBJ.put("receiver_id", newMassegeEntity1.getReceiverId());
                        tmpOBJ.put("sender_id", new_massege_sender_id);
                        tmpOBJ.put("massege_sent_time", new_massege_time_of_send);
                        tmpOBJ.put("View_Status", viewStatus);
                        socket.emit("massege_reach_read_receipt_acknowledgement",3,user_login_id, tmpOBJ);
                        Log.d("log-onMassegeArriveFromServer3", "massege_reach_read_receipt_acknowledgement socket emit :"+tmpOBJ);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.d("log-onMassegeArriveFromServer-Exception", "call: error while parsing data : " + e);
                }

            } else if (requestCode == 1) {
                Thread OnMassegeArriveThread1 = new onMassegeArriveThread1(MainActivity.this, statusForThread, args);
                OnMassegeArriveThread1.start();

            } else if (requestCode == 2) {
                Log.d("log-requestCode", "call: requestCode is : " + requestCode);
            } else {
                Log.d("log-requestCode", "call: requestCode enter in else condition : ");
            }
        }
    };

    private final Emitter.Listener onMassegeReachAtServerFromCMDV = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject new_massege;
            try {
                new_massege = (JSONObject) args[1];
                long CHAT_ID = Long.parseLong(String.valueOf(new_massege.get("Chat_id")));
                massegeDao.updateMassegeStatus(CHAT_ID, 1);
                Log.d("log-onMassegeReachAtServer-args", new_massege.toString());
            } catch (Exception e) {
                Log.d("log-MA-onMassegeReachAtServerFromCMDV", "Exception || e:" + e);

            }

        }
    };

    private final Emitter.Listener onMassegeReachReceiptFromServer = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("log-onMassegeReachReceiptFromServer", "call: onMassegeReachReceiptFromServer enter");
        }
    };

    private final Emitter.Listener onMassegeNotSentToUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("log-socket-massege", "call: massage not sent to user ");
        }
    };
    private final Emitter.Listener onMassegeSentToUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("log-socket-massege", "call: massage is sent to user ");
        }
    };
    private final Emitter.Listener onMassegeSeenByUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("log-socket-massege", "call: massage is seen by user ");
        }
    };


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
                    Intent intent = new Intent(MainActivity.this, ProfileUploadActivity.class);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Contact Permission Granted", Toast.LENGTH_SHORT).show();
//                SyncContactDetailsFirstTime();
            } else {
//                Toast.makeText(MainActivity.this, "ContactPermission Denied", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "To Use Our App YOu must Give the Contact Permission and manual ync Contact Later", Toast.LENGTH_SHORT).show();
            }
        }
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


}