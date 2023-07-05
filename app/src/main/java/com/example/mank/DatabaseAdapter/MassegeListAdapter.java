package com.example.mank.DatabaseAdapter;

import static com.example.mank.ContactMassegeDetailsView.massegeArrayList;
import static com.example.mank.ContactMassegeDetailsView.massegeRecyclerViewAdapter;
import static com.example.mank.ContactMassegeDetailsView.ContactMassegeRecyclerView;
import static com.example.mank.MainActivity.recyclerViewAdapter;
import static com.example.mank.MainActivity.socket;
import static com.example.mank.MainActivity.user_login_id;
import static com.google.android.material.internal.ContextUtils.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.mank.ContactMassegeDetailsView;
import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity;
import com.example.mank.LocalDatabaseFiles.entities.MassegeEntity;
import com.example.mank.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MassegeListAdapter {

    MassegeDao massegeDao;
    Context context;

    List<MassegeEntity> massegeEntityList;

    public MassegeListAdapter(MainDatabaseClass db) {
        massegeDao = db.massegeDao();
//        data = massegeDao.getContactDetailsFromDatabase();
//        contactList = new ArrayList<>();
//        contactList.addAll(data);
    }

    public void fillMassegeListOfUser(String CID) {

        synchronized (this) {

            if (Objects.equals(CID, user_login_id)) {
                massegeEntityList = massegeDao.getSelfChat(CID, user_login_id);
            } else {
                massegeEntityList = massegeDao.getChat(CID, user_login_id);
            }
            massegeArrayList.addAll(massegeEntityList);
//            updateMassegeToServerWithViewStatus(massegeArrayList);

            //for updating the massegeStatus to 3 for senders massege
            for (MassegeEntity e :
                    massegeArrayList) {
                if (!Objects.equals(e.getSenderId(), user_login_id) && e.getMassegeStatus() != 3) {
                    e.setMassegeStatus(3);
                    recyclerViewAdapterNotifyLocal();
                    Thread tb = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            massegeDao.updateMassegeStatus(e.getSenderId(), e.getReceiverId(), e.getTimeOfSend(), 3, user_login_id);
                        }
                    });
                    tb.start();
                    if (socket != null) {
                        JSONArray returnArray;
                        try {
                            JSONObject massegeOBJ = new JSONObject();
                            massegeOBJ.put("from", e.getSenderId());
                            massegeOBJ.put("to", e.getReceiverId());
                            massegeOBJ.put("massege", e.getMassege());
                            massegeOBJ.put("chatId", e.getChatId());
                            massegeOBJ.put("time", e.getTimeOfSend());
                            massegeOBJ.put("massegeStatus", 3);
                            massegeOBJ.put("massegeStatusL", 1);
                            massegeOBJ.put("ef1", 0);
                            massegeOBJ.put("ef2", 0);
                            returnArray = new JSONArray();
                            returnArray.put(massegeOBJ);
                            socket.emit("massege_reach_read_receipt", 4, user_login_id, returnArray);
                            Log.d("log-MassegeListAdapter-fillMassegeListOfUser", "updating the massegeStatus to 3 for massege : " + e.getMassege());
                        } catch (Exception exception) {
                            Log.d("log-updateMassegeToServerWithViewStatus-exception", exception.toString());
                        }
                    }

                }
            }
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @SuppressLint({"NotifyDataSetChanged", "RestrictedApi"})
    public void addMassege(MassegeEntity newEntity) {
        Log.d("log-MassegeListAdapter", "addMassege method start for " + newEntity.getMassege() + " , " + newEntity.getSenderId());
        Thread tx = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    massegeDao.insertMassegeIntoChat(newEntity);
                } catch (Exception e) {
                    Log.d("log-sql-exception", e.toString());
                }
            }
        });
        tx.start();

        massegeArrayList.add(newEntity);


        try {
            Objects.requireNonNull(getActivity(context)).runOnUiThread(new Runnable() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void run() {
                    Log.d("log-MassegeListAdapter", "recyclerViewAdapterNotifyLocal run start");
                    massegeRecyclerViewAdapter.notifyDataSetChanged();
                    ContactMassegeRecyclerView.scrollToPosition(massegeRecyclerViewAdapter.getItemCountMyOwn());
                }
            });
        } catch (Exception e) {
            Log.d("log-ContactListAdapter", "AddContact Exception : " + e);
        }

//        recyclerViewAdapterNotifyLocal();
//        ContactMassegeRecyclerView.scrollToPosition(massegeRecyclerViewAdapter.getItemCountMyOwn());

        Log.d("log-MassegeListAdapter", "addMassege method end");

    }

    @SuppressLint({"NotifyDataSetChanged", "RestrictedApi"})
    public void addMassege(MassegeEntity newEntity, int flag) {
        Log.d("log-MassegeListAdapter", "addMassege method start for " + newEntity.getMassege() + " , " + newEntity.getSenderId());
        if (flag == 1) {
            Thread tx = new Thread(new Runnable() {
                @Override
                public void run() {

                    if (massegeDao.getMassegeByTimeOfSend(newEntity.getSenderId(), newEntity.getTimeOfSend(), user_login_id) == null) {
                        Log.d("log-onMassegeArriveFromServer3", "Contact page is opened inside runnable inside if");
                        massegeArrayList.add(newEntity);
                        try {
                            Objects.requireNonNull(getActivity(context)).runOnUiThread(new Runnable() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void run() {
                                    Log.d("log-MassegeListAdapter", "recyclerViewAdapterNotifyLocal run start");
                                    massegeRecyclerViewAdapter.notifyDataSetChanged();
                                    ContactMassegeRecyclerView.scrollToPosition(massegeRecyclerViewAdapter.getItemCountMyOwn());
                                }
                            });
                        } catch (Exception e) {
                            Log.d("log-ContactListAdapter", "AddContact Exception : " + e);
                        }

                        try {
                            massegeDao.insertMassegeIntoChat(newEntity);
                        } catch (Exception e) {
                            Log.d("log-sql-exception", e.toString());
                        }
                    }

                }
            });
            tx.start();
        }
        Log.d("log-MassegeListAdapter", "addMassege method end");
    }

    public void updateMassegeStatus(String receiverId, long time, int viewStatus) {
        ArrayList<MassegeEntity> x = ContactMassegeDetailsView.massegeArrayList;
        for (int j = x.size() - 1; j >= 0; j--) {
            if (x.get(j).getTimeOfSend() == time && receiverId.equals(x.get(j).getReceiverId())) {
                x.get(j).setMassegeStatus(viewStatus);
            }
        }
        recyclerViewAdapterNotifyLocal();
    }

//    //run at start of the CMDV
//    public void updateMassegeToServerWithViewStatus(ArrayList<MassegeEntity> massegeArrayList) {
//        for (MassegeEntity me : massegeArrayList) {
//            if (!me.getSenderId().equals(user_login_id)) {
//                if (me.getMassegeStatus() != 2) {
//                    Thread ums = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            massegeDao.updateMassegeStatus(me.getSenderId(), me.getReceiverId(), me.getTimeOfSend(), 3);
//                        }
//                    });
//                    ums.start();
//                    if (socket != null) {
//                        JSONArray returnArray;
//                        try {
//                            JSONObject massegeOBJ = new JSONObject();
//                            massegeOBJ.put("from", me.getSenderId());
//                            massegeOBJ.put("to", me.getReceiverId());
//                            massegeOBJ.put("massege", me.getMassege());
//                            massegeOBJ.put("chatId", me.getChatId());
//                            massegeOBJ.put("time", me.getTimeOfSend());
//                            massegeOBJ.put("massegeStatus", 3);
//                            massegeOBJ.put("massegeStatusL", 1);
//                            massegeOBJ.put("ef1", 0);
//                            massegeOBJ.put("ef2", 0);
//                            returnArray = new JSONArray();
//                            returnArray.put(massegeOBJ);
//                            socket.emit("massege_reach_read_receipt", 4, user_login_id, me);
//                        } catch (Exception e) {
//                            Log.d("log-updateMassegeToServerWithViewStatus-exception", e.toString());
//                        }
//                    }
//                }
//            }
//        }
//    }

    @SuppressLint("RestrictedApi")
    private void recyclerViewAdapterNotifyLocal() {
        try {
            Objects.requireNonNull(getActivity(context)).runOnUiThread(new Runnable() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void run() {
                    if (massegeRecyclerViewAdapter != null) {
                        Log.d("log-MassegeListAdapter", "recyclerViewAdapterNotifyLocal run start");
                        massegeRecyclerViewAdapter.notifyDataSetChanged();
                    }
                }
            });
        } catch (Exception e) {
            Log.d("log-ContactListAdapter", "AddContact Exception : " + e);
        }
    }
}
