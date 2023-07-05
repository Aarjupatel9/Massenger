package com.example.mank;

import static com.example.mank.MainActivity.Contact_page_opened_id;
import static com.example.mank.MainActivity.MainContactListHolder;
import static com.example.mank.MainActivity.db;
import static com.example.mank.MainActivity.socket;
import static com.example.mank.MainActivity.user_login_id;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.example.mank.DatabaseAdapter.MassegeListAdapter;
import com.example.mank.FunctionalityClasses.ContactDetailsFromMassegeViewPage;
import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.ContactMassegeHolder;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.MassegeEntity;
import com.example.mank.MediaPlayerClasses.DotSound;
import com.example.mank.MediaPlayerClasses.SoundThread;
import com.example.mank.RecyclerViewClassesFolder.ContactMassegeRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.emitter.Emitter;

public class ContactMassegeDetailsView extends Activity {

    public static RecyclerView ContactMassegeRecyclerView;
    public static ContactMassegeRecyclerViewAdapter massegeRecyclerViewAdapter;
    public static ArrayList<MassegeEntity> massegeArrayList;
    public static MassegeListAdapter massegeListAdapter;
    private int lastChatId;
    static int counter = 0;
    private ConstraintLayout CDMVNewUserConstraintLayout;
    private ConstraintLayout CMDVConstraintLayoutMain;
    private EditText massege_field;
    private ImageButton send_massege_button;
    private ImageButton OtherActivityButton;
    private TextView online_status_text_area;
    private String CID;
    private long ContactMobileNumber;
    private String ContactName;
    private TextView Contact_name_of_user;
    public boolean keyboardPass = true;
    private MassegeDao massegeDao;

    private Timer online_status_checker_timer;

    private final Emitter.Listener onCheckContactOnlineStatus_return = new Emitter.Listener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void call(final Object... args) {
            try {
                int contact_id = (int) args[0];
                int online_status = (int) args[1];
                long last_online_time = (long) args[2];
                String privacy = (String) args[3];

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (online_status == 1) {
                            online_status_text_area.setText("online");
                        } else if (online_status == 0) {
//                            Log.d("log-onCheckContactOnlineStatus_return", "call: enter in offline cond.");
                            if (privacy.equals("private")) {
                                //if user don't want to show their last seen
                                online_status_text_area.setText("");
                            } else {
                                Date date = new Date(last_online_time);
                                //here we have to implement some function
                                Date current_date = new Date();
                                if (current_date.getYear() == date.getYear() && current_date.getMonth() == date.getMonth() && current_date.getDate() == date.getDate()) {
//                                    Log.d("log-onCheckContactOnlineStatus_return", "date: " + date);
                                    String formatted = new SimpleDateFormat("HH:mm").format(date);
//                                    Log.d("log-onCheckContactOnlineStatus_return", "formatted: " + formatted);
                                    online_status_text_area.setText("last seen at " + formatted);
                                } else if ((current_date.getYear() == date.getYear()) && (current_date.getMonth() == date.getMonth()) && (current_date.getDate() == date.getDate() + 1)) {
//                                    Log.d("log-onCheckContactOnlineStatus_return", "enter in else  if cond.");
//                                    Log.d("log-onCheckContactOnlineStatus_return", "date: " + date);
                                    String formatted = new SimpleDateFormat("HH:mm").format(date);
//                                    Log.d("log-onCheckContactOnlineStatus_return", "formatted: " + formatted);
                                    online_status_text_area.setText("last seen yesterday at  " + formatted);
                                } else {

                                    String formatted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                                    String cur_formatted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(current_date);
                                    online_status_text_area.setText("last seen at " + formatted);
                                }
                            }
                        } else {
                            Log.d("log-onCheckContactOnlineStatus_return", "call: enter in other cond.");
                        }
                    }
                });

            } catch (Exception e) {
                Log.d("log-onCheckContactOnlineStatus_return-exception", e.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        CID = intent.getStringExtra("CID");
        if (CID == null) {
            CID = "";
        }
        ContactMobileNumber = intent.getLongExtra("ContactMobileNumber", -2);
        ContactName = intent.getStringExtra("ContactName");

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        massegeListAdapter = new MassegeListAdapter(db);

        Log.d("log-ContactMassegeDetailsView-onCreate", "contact_id:" + CID + ", user_login_id:" + user_login_id);
        setContentView(R.layout.activity_contact_massege_details_view);

        online_status_checker_timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                socket.emit("CheckContactOnlineStatus", user_login_id, Contact_page_opened_id);
            }
        };

        online_status_checker_timer.scheduleAtFixedRate(timerTask, 1, 1000);
        online_status_text_area = findViewById(R.id.Contact_last_come_in_app_status);
        Contact_name_of_user = findViewById(R.id.Contact_name_of_user);
        CDMVNewUserConstraintLayout = findViewById(R.id.CDMVNewUserConstraintLayout);
        if (ContactName == null) {
            CDMVNewUserConstraintLayout.setVisibility(View.VISIBLE);
            Contact_name_of_user.setText(String.valueOf(ContactMobileNumber));
        } else {
            Contact_name_of_user.setText(ContactName);
        }

        massege_field = (EditText) findViewById(R.id.write_massege);
        OtherActivityButton = (ImageButton) findViewById(R.id.OtherActivityButton);
        send_massege_button = (ImageButton) findViewById(R.id.send_massege_button);
        send_massege_button.setClickable(false);
        ContactMassegeRecyclerView = findViewById(R.id.ContactMassegeRecyclerView);
        CMDVConstraintLayoutMain = findViewById(R.id.CMDVConstraintLayoutMain);

        setLocationButtonColor(true);

        socket.on("CheckContactOnlineStatus_return", onCheckContactOnlineStatus_return);
        massegeDao = db.massegeDao();
        setAllMassege(CID);
        setLastChatId();
        setNewMassegeArriveValue(CID);

        massege_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!massege_field.getText().toString().equals("")) {
                    if (massege_field.getText().toString().trim().isBlank()) {
                        send_massege_button.setClickable(false);
                    } else {
                        send_massege_button.setClickable(true);
                    }
                } else {
                    send_massege_button.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        final View activityRootView = findViewById(R.id.contact_massege_details_view_root);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(ContactMassegeDetailsView.this, 200)) { // if more than 200 dp, it's probably a keyboard...
                    if (keyboardPass) {
                        keyboardPass = false;
                        Log.d("log-addOnGlobalLayoutListener", "keyboard is visible");
                        ContactMassegeRecyclerView.scrollToPosition(massegeRecyclerViewAdapter.getItemCountMyOwn());
                    }
                } else {
                    if (!keyboardPass) {
                        keyboardPass = true;
                        Log.d("log-addOnGlobalLayoutListener", "keyboard is not visible");
                    }
                }
            }
        });
        setBackgroundImage();
    }

    public void setBackgroundImage() {
        Thread ti = new Thread(new Runnable() {
            @Override
            public void run() {

                String imagePath = "/storage/emulated/0/Android/media/com.massenger.mank.main/bg/bgImages/" + user_login_id + ".png";
                byte[] byteArray = null;
                try {
                    File imageFile = new File(imagePath);
                    FileInputStream fis = new FileInputStream(imageFile);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }
                    fis.close();
                    bos.close();
                    byteArray = bos.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (byteArray != null) {
                    Bitmap selfImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    Log.d("log-ContactListAdapter", "setUserImage : after fetch image form file system : " + byteArray.length);
                    BitmapDrawable drawable = new BitmapDrawable(getResources(), selfImage);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (CMDVConstraintLayoutMain != null) {
                                CMDVConstraintLayoutMain.setBackground(drawable);
                            } else {
                                Log.d("log-ContactMassegeDetailsView", "ContactMassegeRecyclerView is null");
                            }
                        }
                    });
                }
            }
        });
        ti.start();
    }


    public void addNewUserIntoContact(View view) {

        Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        contactIntent
                .putExtra(ContactsContract.Intents.Insert.NAME, "")
                .putExtra(ContactsContract.Intents.Insert.PHONE, String.valueOf(ContactMobileNumber));

        startActivityForResult(contactIntent, 106);


    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        online_status_checker.interrupt();
        online_status_checker_timer.cancel();

    }

    public void getTestingSite(View view) {
        Log.d("log-enter", "getTestingSite: enter here");
//        online_status_text_area.setText("fkjffjf");

    }

    private void setNewMassegeArriveValue(String cId) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                massegeDao.updateNewMassegeArriveValue(cId, 0, user_login_id);
            }
        });
        t.start();
    }


    private void setLastChatId() {

        lastChatId = massegeDao.getLastInsertedMassege(user_login_id) + 1;
        Log.d("log-lastchatid", "setLastChatId: last chat is : " + lastChatId);
    }

    private void setAllMassege(String CID) {

        massegeArrayList = new ArrayList<>();
        massegeListAdapter.setContext(this);
        massegeListAdapter.fillMassegeListOfUser(CID);

        ContactMassegeRecyclerView.setHasFixedSize(true);
        ContactMassegeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        massegeRecyclerViewAdapter = new ContactMassegeRecyclerViewAdapter(this, massegeArrayList);
        ContactMassegeRecyclerView.setAdapter(massegeRecyclerViewAdapter);
//        Log.d("log-list-size", "setAllMassege: list size is :" + massegeRecyclerViewAdapter.getItemCountMyOwn());
        ContactMassegeRecyclerView.scrollToPosition(massegeRecyclerViewAdapter.getItemCountMyOwn());

    }

    int LAUNCH_ContactDetailsFromMassegeViewPage_ACTIVITY = 1;

    public void getContactDetailsOfUser(View view) {
        Log.d("log-getContactDetailsOfUser", "getContactDetailsOfUser: enter here");
        Intent intent = new Intent(this, ContactDetailsFromMassegeViewPage.class);
        intent.putExtra("ContactMobileNumber", ContactMobileNumber);
        intent.putExtra("CID", CID);
        intent.putExtra("ContactName", ContactName);
        startActivityForResult(intent, LAUNCH_ContactDetailsFromMassegeViewPage_ACTIVITY);
//        Toast.makeText(this, "you Clicked in User name", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("log-onActivityResult", "onActivityResult: activity finished with code " + requestCode);

        if (requestCode == 106) {
            if (data != null) {
                Log.d("log-onActivityResult", "onActivityResult: " + resultCode + " | " + data.getData().toString());
            }
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Added Contact", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled Added Contact", Toast.LENGTH_SHORT).show();
            }

        }
    }


    @SuppressLint("NotifyDataSetChanged")
    public void SendMassege(View view) {
        counter++;
        String user_massege = massege_field.getText().toString().trim();
        Log.d("log-SendMassege", "user_massege is : " + user_massege);
        Log.d("log-SendMassege", "user_login_id is : " + user_login_id);

        massege_field.setText("");
        int massege_status = -1;
        Date current_date = new Date();

        Thread setPriorityRankThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long HighestPriority = massegeDao.getHighestPriorityRank(user_massege);
                massegeDao.setPriorityRank(CID, HighestPriority + 1, user_massege);
                if (MainContactListHolder != null) {
                    MainContactListHolder.updatePositionOfContact(CID, ContactMassegeDetailsView.this);
                }
            }
        });
        setPriorityRankThread.start();

        Log.d("log-try", "Send_massege_of_user: internet connection is : " + check_internet_connectivity());
        if (check_internet_connectivity()) {
            if (Objects.equals(user_login_id, Contact_page_opened_id)) {
                massege_status = 3;
            } else {
                massege_status = 0;
            }
            MassegeEntity new_massege = new MassegeEntity(user_login_id, CID, user_massege, current_date.getTime(), massege_status);
            //now wwe have to store it into database
            //notify adapter for add massege into recycler view
            massegeListAdapter.addMassege(new_massege);
            SoundThread ma = new SoundThread(ContactMassegeDetailsView.this, 0);
            ma.start();

            try {
                JSONObject massegeOBJ = new JSONObject();
                massegeOBJ.put("from", user_login_id);
                massegeOBJ.put("to", CID);
                massegeOBJ.put("massege", user_massege);
                massegeOBJ.put("chatId", lastChatId);
                massegeOBJ.put("time", current_date.getTime());
                massegeOBJ.put("massegeStatus", massege_status);
                massegeOBJ.put("massegeStatusL", 1);
                massegeOBJ.put("ef1", 1);
                massegeOBJ.put("ef2", 1);

                lastChatId++;

                JSONArray massegeArray = new JSONArray();
                massegeArray.put(massegeOBJ);

                socket.emit("send_massege_to_server_from_sender", user_login_id, massegeArray);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            MassegeEntity new_massege = new MassegeEntity(user_login_id, CID, user_massege, current_date.getTime(), massege_status);
            massegeListAdapter.addMassege(new_massege);
        }
    }


    public boolean check_internet_connectivity() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected());
    }

    private boolean toogelColor = false;

    public void OtherActivityButtonOnCLick(View view) {
        Log.d("log-ContactMassegeDetailsView", "OtherActivityButtonOnCLick || start");
        askPermissionToShareLocation();
    }

    private boolean LocationSharingEnable = false;

    public void askPermissionToShareLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Start sharing your Location with " + ContactName);
        builder.setPositiveButton("Start sharing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Log.d("log-ContactMassegeDetailsView", "");
                if (!LocationSharingEnable) {
                    //enable location sharing
                    startLocationShareWithContact();
                    LocationSharingEnable = true;
                }
                setLocationButtonColor(false);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (LocationSharingEnable) {
                    startLocationShareWithContact();
                    LocationSharingEnable = false;
                    stopLocationShareWithContact();
                }
                dialog.dismiss();
                setLocationButtonColor(true);
            }
        });
        builder.show();
    }

    private void startLocationShareWithContact() {

    }

    private void stopLocationShareWithContact() {

    }

    private void setLocationButtonColor(boolean value) {
        final ContextThemeWrapper wrapper;
        if (value) {
            wrapper = new ContextThemeWrapper(this, R.style.LocationButtonDefaultScene);
        } else {
            wrapper = new ContextThemeWrapper(this, R.style.LocationButtonUpdatedScene);
        }
        final Drawable drawable = VectorDrawableCompat.create(getResources(), R.drawable.baseline_location_on_24, wrapper.getTheme());
        OtherActivityButton.setImageDrawable(drawable);
    }
}