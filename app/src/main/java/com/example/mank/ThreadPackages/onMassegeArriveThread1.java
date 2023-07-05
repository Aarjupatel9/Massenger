package com.example.mank.ThreadPackages;

import static com.example.mank.ContactMassegeDetailsView.massegeArrayList;
import static com.example.mank.ContactMassegeDetailsView.massegeRecyclerViewAdapter;
import static com.example.mank.ContactMassegeDetailsView.ContactMassegeRecyclerView;
import static com.example.mank.MainActivity.Contact_page_opened_id;
import static com.example.mank.MainActivity.FetchDataFromServerAndSaveIntoDB;
import static com.example.mank.MainActivity.MainActivityStaticContext;
import static com.example.mank.MainActivity.MainContactListHolder;
import static com.example.mank.MainActivity.db;
import static com.example.mank.MainActivity.socket;
import static com.example.mank.MainActivity.user_login_id;
import static com.google.android.material.internal.ContextUtils.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.example.mank.MainActivity;
import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;
import com.example.mank.LocalDatabaseFiles.entities.MassegeEntity;
import com.example.mank.MediaPlayerClasses.DotSound;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class onMassegeArriveThread1 extends Thread {

    private Object[] args;
    private int requestCode;
    private Context Context;
    private StatusForThread statusForThread;
    private MassegeDao massegeDao;

    public onMassegeArriveThread1(Context Context, StatusForThread statusForThread, Object... args) {
        this.args = args;
        requestCode = (int) args[2];
        this.Context = Context;
        this.statusForThread = statusForThread;
        massegeDao = db.massegeDao();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void run() {
        synchronized (statusForThread) {
            try {
                JSONArray result = (JSONArray) args[1];
                JSONArray returnArray = new JSONArray();
                JSONObject tmp2;
                while (statusForThread.getValue() == 0) {
                    Log.d("log-onMassegeArriveFromServer1", "MainActivity.contactArrayList == null");
                    statusForThread.wait();
                }
//                Log.d("log-onMassegeArriveFromServer1", "MainActivity.contactArrayList != null");
                ArrayList<ContactWithMassengerEntity> contactArrayList1;
                contactArrayList1 = MainActivity.contactArrayList;
                for (int i = 0; i < result.length(); i++) {
                    JSONObject tmp = (JSONObject) result.get(i);
                    tmp2 = new JSONObject();
                    tmp2.put("massege_number", tmp.get("massege_number"));
                    tmp2.put("sender_id", tmp.get("sender_id"));
                    tmp2.put("receiver_id", tmp.get("receiver_id"));
                    returnArray.put(tmp2);
                    if (Contact_page_opened_id.equals(tmp.get("sender_id"))) {
                        Log.d("log-onMassegeArriveFromServer1-result", "call: page is opened whose massege is arrived");
                    }
                    //now we have to insert massege into database
                    String MassegeId = String.valueOf(tmp.get("massege_number"));
                    String sender_id = String.valueOf(tmp.get("sender_id"));
                    long time_of_sent = Long.parseLong(String.valueOf(tmp.get("massege_sent_time")));
                    String massege = String.valueOf(tmp.get("massage"));
                    MassegeEntity new_massege = new MassegeEntity(sender_id, user_login_id, massege, time_of_sent, 1);

                    Thread massegeInsertIntoDatabase = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MassegeEntity x = massegeDao.getMassegeByTimeOfSend(new_massege.getSenderId(),new_massege.getTimeOfSend(), user_login_id);
                            if (x == null) {
                                try {
                                    massegeDao.insertMassegeIntoChat(new_massege);
                                    Log.d("log-onMassegeArriveFromServer1", "massege is inserted into database successfully");
                                } catch (Exception e) {
                                    Log.d("log-sql-exception", e.toString());
                                }
                            }
                        }
                    });
                    massegeInsertIntoDatabase.start();
                    Thread checkContactSavedInDB = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ContactWithMassengerEntity x = massegeDao.getContactWith_CID(new_massege.getSenderId(), user_login_id);
                            if (x == null) {
                                Log.d("log-onMassegeArriveFromServer3", "setPriorityRankThread1");
                                FetchDataFromServerAndSaveIntoDB(new_massege.getSenderId());
                            } else {
                                Thread setPriorityRankThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        long HighestPriority = massegeDao.getHighestPriorityRank(user_login_id);
                                        massegeDao.setPriorityRank(new_massege.getSenderId(), HighestPriority + 1, user_login_id);
                                        MainContactListHolder.updatePositionOfContact(new_massege.getSenderId(), MainActivityStaticContext);

                                    }
                                });
                                setPriorityRankThread.start();
                            }
                        }
                    });
                    checkContactSavedInDB.start();


//                    Log.d("log-onMassegeArriveFromServer1", "contactArrayList1.size():" + contactArrayList1.size());
                    for (int j = 0; j < contactArrayList1.size(); ++j) {
                        if (contactArrayList1.get(j).getCID().equals(sender_id)) {
//                            Log.d("log-onMassegeArriveFromServer1", "enter in if cond. sender_id is:" + sender_id +" and contactArrayList1.get(i).getC_ID():"+contactArrayList1.get(i).getC_ID());
                            if (sender_id.equals(Contact_page_opened_id)) {
//                                Log.d("log-onMassegeArriveFromServer1", "enter in if cond.");
                                Objects.requireNonNull(getActivity(Context)).runOnUiThread(new Runnable() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void run() {
                                        Thread massegePopSound = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                DotSound ma = new DotSound(Context, 0);
                                                ma.massegePopPlay();
                                            }
                                        });
                                        massegePopSound.start();
                                        massegeArrayList.add(new_massege);
                                        massegeRecyclerViewAdapter.notifyDataSetChanged();
                                        ContactMassegeRecyclerView.scrollToPosition(massegeRecyclerViewAdapter.getItemCountMyOwn());
                                    }
                                });
                            } else {
                                int index = j;
//                                Log.d("log-onMassegeArriveFromServer1", "enter in else cond. index is:" + index);
                                Objects.requireNonNull(getActivity(Context)).runOnUiThread(new Runnable() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void run() {
                                        Thread massegePopSound = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                DotSound ma = new DotSound(Context, 1);
                                                ma.massegePopPlay();
                                            }
                                        });
                                        massegePopSound.start();

                                        Thread t = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MassegeDao massegeDao = db.massegeDao();
                                                massegeDao.incrementNewMassegeArriveValue(sender_id, user_login_id);
                                            }
                                        });
                                        t.start();
//                                        Log.d("log-onMassegeArriveFromServer1", "enter in else cond. ");
                                        ContactWithMassengerEntity contactView = contactArrayList1.get(index);
                                        int prev_value = contactView.getNewMassegeArriveValue();
                                        contactView.setNewMassegeArriveValue(prev_value + 1);
                                        MainActivity.contactArrayList.set(index, contactView);
                                        MainActivity.recyclerViewAdapter.notifyDataSetChanged();
                                        MainActivity.ChatsRecyclerView.scrollToPosition(MainActivity.recyclerViewAdapter.getItemCountMyOwn());
                                    }
                                });
                            }
                        } else {
                            Log.d("log-onMassegeArriveFromServer1", "massege arrive from who not in contact list");
                        }
                    }


                }
                JSONObject tmpOBJ = new JSONObject();
                tmpOBJ.put("returnArray", returnArray);
                tmpOBJ.put("user_login_id", user_login_id);
                socket.emit("new_massege_from_server_acknowledgement", tmpOBJ);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("log-result-exception", "" + e);
            }
        }
    }
}
