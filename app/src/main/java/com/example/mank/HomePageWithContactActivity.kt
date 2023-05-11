package com.example.mank

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room.databaseBuilder
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.ContactListHolder
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.MassegeHolderForSpecificPurpose
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.userIdEntityHolder
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass
import com.example.mank.LocalDatabaseFiles.entities.ContactWithMassengerEntity
import com.example.mank.LocalDatabaseFiles.entities.MassegeEntity
import com.example.mank.LoginMenagement.Login
import com.example.mank.RecyclerViewClassesFolder.CallsRecyclerViewAdapter
import com.example.mank.RecyclerViewClassesFolder.RecyclerViewAdapter
import com.example.mank.RecyclerViewClassesFolder.StatusRecyclerViewAdapter
import com.example.mank.ThreadPackages.AppMainThread
import com.example.mank.ThreadPackages.MassegePopSoundThread
import com.example.mank.ThreadPackages.StatusForThread
import com.example.mank.ThreadPackages.onMassegeArriveThread1
import com.example.mank.cipher.MyCipher
import com.example.mank.configuration.GlobalVariables
import com.example.mank.configuration.permissionMain
import com.example.mank.configuration.permission_code
import com.example.mank.databinding.ActivityHomePageWithContactBinding
import com.example.mank.profile.SettingsOptionPage
import com.example.mank.socket.SocketClass
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class HomePageWithContactActivity : Activity() {
    private var PAGE_NUMBER = 0
    private val MAX_PAGE = 2
    private val x1 = 0f
    private val x2 = 0f
    private val y1 = 0f
    private val y2 = 0f
    var appMainThread: AppMainThread? = null
    var toStopAppMainThread = false
    var x: Int = 0
    var MassegeEntityList: List<MassegeEntity>? = null
    private val PERMISSION_ALL = 1
    var PERMISSIONS = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.CHANGE_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS
    )
    private var appOpenFromBackGround = false
    private var EverythingIsOhkInApp = false
    private var MainProgressBar: ProgressBar? = null
    private var ChatsTabHighLighter: TextView? = null
    private var CallsTabHighLighter: TextView? = null
    private var StatusTabHighLighter: TextView? = null
    private var HPFloatingButton: ImageButton? = null
    var LAUNCH_LOGIN_ACTIVITY = 1
    var LoginIntentData: Intent? = null
    private var massegeDao: MassegeDao? = null
    override fun onStart() {
        super.onStart()
        startNetworkListener()
        HomePageWithContactActivityStaticContext = this
        Contact_page_opened_id = -1
        FinishCode = 0
        Log.d(
            "log-Contact_page_opened_id",
            "onStart: in HomePageWithContactActivity Contact_page_opened_id  is  : " + Contact_page_opened_id
        )
        if (!appOpenFromBackGround) {
            appOpenFromBackGround = true
        } else if (EverythingIsOhkInApp) {
            //we will send that we are online massege to server
            Log.d("log-Contact_page_opened_id", "EverythingIsOhkInApp is :  $EverythingIsOhkInApp")
        }
    }

    var statusForThread: StatusForThread? = null
    override fun onResume() {
        super.onResume()
        if (statusForThread == null) {
            statusForThread = StatusForThread(0)
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("log-onDestroy", "onDestroy: FinishCode is: " + FinishCode)
        if (FinishCode == 0) {
            Log.d("log-onDestroy", "onDestroy: FinishCode is: " + FinishCode)
            if (EverythingIsOhkInApp) {
                Log.d("log-onDestroy", "onPause EverythingIsOhkInApp: enter here")
            }
            toStopAppMainThread = true //for stop appMAinThread
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = databaseBuilder(
            applicationContext, MainDatabaseClass::class.java, "MassengerDatabase"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
        massegeDao = db!!.massegeDao()
        val login = Login()
        if (!permissionMain.hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        }
        if (login.isLogIn(db) == 0) {
            Log.d("log-not logined", "onCreate: not login cond. reached")
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, LAUNCH_LOGIN_ACTIVITY)
            Log.d("log-FinishCode", "onCreate: FinishCode is: " + FinishCode)
            FinishCode = 2
        } else {
            startMain()
        }
    }

    private val binding: ActivityHomePageWithContactBinding? = null
    private var recyclerView: RecyclerView? = null;
    private fun startMain() {
        val userIdEntityHolder = userIdEntityHolder(db)
        user_login_id = userIdEntityHolder.userLoginId
        UserMobileNumber = userIdEntityHolder.userMobileNumber
        saveFireBaseTokenToServer(user_login_id.toString())
        statusForThread = StatusForThread(0)
        setContentView(R.layout.activity_home_page_with_contact)
        MainProgressBar = findViewById(R.id.MainProgressBar)
        MainProgressBar?.setVisibility(View.GONE)
        HPFloatingButton = findViewById(R.id.HPFloatingButton)
        ChatsTabHighLighter = findViewById(R.id.ChatsTabHighLighter)
        StatusTabHighLighter = findViewById(R.id.StatusTabHighLighter)
        CallsTabHighLighter = findViewById(R.id.CallsTabHighLighter)
        recyclerView = findViewById(R.id.ContactRecyclerView)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        CreateSocketConnection()
        //        startSwipeEventListener();
        EverythingIsOhkInApp = true

        //by default
        SetChatsView(View(this))


        //other stuff
        appMainThread = AppMainThread(toStopAppMainThread)
        appMainThread!!.start()
    }

    private val FirstTimeAppSyncAllContactRequestCode = 202
    private fun startMainFirstTime() {
        val userIdEntityHolder = userIdEntityHolder(db)
        user_login_id = userIdEntityHolder.userLoginId
        saveFireBaseTokenToServer(user_login_id.toString())
        statusForThread = StatusForThread(0)
        val intent = Intent(this, AllContactOfUserInDeviceView::class.java)
        startActivityForResult(intent, FirstTimeAppSyncAllContactRequestCode)

//        setContentView(R.layout.activity_home_page_with_contact);
//        MainProgressBar = findViewById(R.id.MainProgressBar);
//        MainProgressBar.setVisibility(View.GONE);
//        HPFloatingButton = findViewById(R.id.HPFloatingButton);
//
//        ChatsTabHighLighter = findViewById(R.id.ChatsTabHighLighter);
//        StatusTabHighLighter = findViewById(R.id.StatusTabHighLighter);
//        CallsTabHighLighter = findViewById(R.id.CallsTabHighLighter);
//
//        recyclerView = findViewById(R.id.ContactRecyclerView);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        CreateSocketConnection();
//        EverythingIsOhkInApp = true;
//        SetChatsView(new View(this));
//
//        appMainThread = new AppMainThread(toStopAppMainThread);
//        appMainThread.start();
    }

    fun setPageView() {
        if (PAGE_NUMBER == 0) {
            SetChatsView(View(this))
        } else if (PAGE_NUMBER == 1) {
            SetStatusView(View(this))
        } else if (PAGE_NUMBER == 2) {
            SetCallsView(View(this))
        }
    }

    private val MainHeaderColorHaxValue = "#147E53"
    private val MainHighLighterColorHaxValue = "#0DAAF3"
    fun setHighLighterColor() {
        if (PAGE_NUMBER == 0) {
            ChatsTabHighLighter!!.setBackgroundColor(Color.parseColor(MainHighLighterColorHaxValue))
            StatusTabHighLighter!!.setBackgroundColor(Color.parseColor(MainHeaderColorHaxValue))
            CallsTabHighLighter!!.setBackgroundColor(Color.parseColor(MainHeaderColorHaxValue))
        } else if (PAGE_NUMBER == 1) {
            ChatsTabHighLighter!!.setBackgroundColor(Color.parseColor(MainHeaderColorHaxValue))
            StatusTabHighLighter!!.setBackgroundColor(Color.parseColor(MainHighLighterColorHaxValue))
            CallsTabHighLighter!!.setBackgroundColor(Color.parseColor(MainHeaderColorHaxValue))
        } else if (PAGE_NUMBER == 2) {
            ChatsTabHighLighter!!.setBackgroundColor(Color.parseColor(MainHeaderColorHaxValue))
            StatusTabHighLighter!!.setBackgroundColor(Color.parseColor(MainHeaderColorHaxValue))
            CallsTabHighLighter!!.setBackgroundColor(Color.parseColor(MainHighLighterColorHaxValue))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LAUNCH_LOGIN_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                //1101 is code for this permission checking
                LoginIntentData = data
                startMainFirstTime()
            }
            if (resultCode == RESULT_CANCELED) {
                Log.d("log-onActivityResult", "Activity RESULT_CANCELED")
            }
        } else if (requestCode == FirstTimeAppSyncAllContactRequestCode) {
            startMain()
        }
    } //onActivityResult

    fun SetChatsView(view: View?) {
        Log.d("log-HomePageWithContactActivity", "SetChatsView- reach here ")
        PAGE_NUMBER = 0
        setHighLighterColor()
        HPFloatingButton!!.visibility = View.VISIBLE
        if (contactArrayList == null) {
            contactArrayList = ArrayList()
            MainContactListHolder = ContactListHolder(db)
            contactArrayList = MainContactListHolder!!.MainContactList
//            synchronized(statusForThread!!) {
//                statusForThread!!.value = 1
//                Log.d(
//                    "log-onMassegeArriveFromServer1",
//                    "HomePageWithContactActivity.contactArrayList before notifyAll()"
//                )
//                statusForThread.notify()
//            }
//            val condition = statusForThread.newCondition()
//
//            statusForThread.withLock {           // like synchronized(lock)
//                condition.await()     // like wait()
//                condition.signal()    // like notify()
//                condition.signalAll() // like notifyAll()
//            }

            recyclerViewAdapter = RecyclerViewAdapter(this, contactArrayList)
        }
        recyclerView!!.adapter = recyclerViewAdapter
    }

    fun SetStatusView(view: View?) {
        PAGE_NUMBER = 1
        setHighLighterColor()
        HPFloatingButton!!.visibility = View.VISIBLE
        val contactStatusList = ArrayList<Long>()
        Log.d("log-HomePageWithContactActivity", "SetStatusView- reach here ")
        contactStatusList.add(user_login_id)
        val statusRecyclerViewAdapter = StatusRecyclerViewAdapter(this, contactStatusList)
        recyclerView!!.adapter = statusRecyclerViewAdapter
    }

    fun SetCallsView(view: View?) {
        PAGE_NUMBER = 2
        setHighLighterColor()
        HPFloatingButton!!.visibility = View.GONE
        val contactCallList = ArrayList<Long>()
        Log.d("log-HomePageWithContactActivity", "SetStatusView- reach here ")
        contactCallList.add(user_login_id)
        val callsRecyclerViewAdapter = CallsRecyclerViewAdapter(this, contactCallList)
        recyclerView!!.adapter = callsRecyclerViewAdapter
    }

    fun getListOfAllUserContact(view: View?) {
//        Toast.makeText(this, "you Click all contact details", Toast.LENGTH_SHORT).show();
        val intent =
            Intent(this@HomePageWithContactActivity, AllContactOfUserInDeviceView::class.java)
        Log.d("log-getListOfAllUserContact", "calling getListOfAllUserContact activity")
        startActivity(intent)
    }

    // socket area
    private fun CreateSocketConnection() {
        Log.d("log-login-check method", "CreateSocketConnection: enter in logIned check  method")
        socketOBJ = SocketClass()
        socket = socketOBJ!!.socket
        socketOBJ!!.joinRoom(user_login_id)
        val socket = socket
        if (socket != null) {

            socket.on("join_acknowledgement", onJoinAcknowledgement)
            socket.on("new_msg", onNewMessage)
            socket.on("massege_sent_to_user", onMassegeSentToUser)
            socket.on("massege_seen_by_user", onMassegeSeenByUser)
            socket.on("massege_not_sent_to_user", onMassegeNotSentToUser)

            //massege handling from server
            socket.on("new_massege_from_server", onMassegeArriveFromServer)
            socket.on(
                "massege_have_to_sent_at_comeTOOnline_to_server_acknowledgement",
                onMHTSACAcknowledgement
            )
            socket.on("massege_reach_receipt_from_server", onMassegeReachReceiptFromServer)
            socket.on("send_massege_to_server_from_CMDV_acknowledgement", onMassegeReachAtServer)
            socket.on(Socket.EVENT_CONNECT_ERROR, Emitter.Listener { socket.connect() })
            socket.on(Socket.EVENT_CONNECT, Emitter.Listener {
                Log.d(
                    "log-HomePageWithContactActivity",
                    "Socket.EVENT_CONNECT socket.isActive() : "
                )
            })
            socket.on(Socket.EVENT_DISCONNECT, Emitter.Listener {
                Log.d(
                    "log-HomePageWithContactActivity",
                    "Socket.EVENT_DISCONNECT socket.isActive() : "
                )
            })
        }
    }

    //socket event listener define here
    private val onMassegeReachReceiptFromServer = Emitter.Listener {
        Log.d(
            "log-onMassegeReachReceiptFromServer", "call: onMassegeReachReceiptFromServer enter"
        )
    }
    private val onJoinAcknowledgement = Emitter.Listener {
        Log.d("log-socket-massege", "onJoinAcknowledgement: join success ")
        //we have to get list of all masseges and send them to server at user came online
        val mhsp = MassegeHolderForSpecificPurpose(db, 1)
        MassegeEntityList = mhsp.massegeList
        try {
            val massegeData = JSONObject()
            val MassegeEntityList = MassegeEntityList
            if (MassegeEntityList != null) {
                for (i in MassegeEntityList.indices) {
                    val tmp1 = MassegeEntityList.get(i)
                    try {
                        val tmp2 = JSONObject()
                        tmp2.put("user_login_id", user_login_id)
                        tmp2.put("user_massege", tmp1.massege)
                        tmp2.put("C_ID", tmp1.receiverId)
                        tmp2.put("Chat_id", tmp1.chat_id)
                        massegeData.put(i.toString(), tmp2)
                    } catch (ex: Exception) {
                        Log.d("log-onJoinAcknowledgement-exception", "exception is : $ex")
                        Log.d("log-onJoinAcknowledgement-exception", "massegeData : $massegeData")
                        Log.d(
                            "log-onJoinAcknowledgement-exception",
                            "MassegeEntityList.size() : " + MassegeEntityList.size
                        )
                    }
                }
            }
            // socket.emit("massege_have_to_sent_at_comeTOOnline_to_server", massegeData, MassegeEntityList.size(), user_login_id);
        } catch (e: Exception) {
            Log.d("log-onJoinAcknowledgement-exception", "exception is : $e")
        }
    }
    private val onMHTSACAcknowledgement = Emitter.Listener { args ->
        Log.d("log-socket-onMHTSACAcknowledgement", "call: onMHTSACAcknowledgement enter ")
        val status = args[0] as Int
        if (status == 1) {

            // we have to change chat status from 5 to 0; and update into ui  if chat_box is open
            for (i in MassegeEntityList!!.indices) {
                val chat_id = MassegeEntityList!![i].chat_id
                massegeDao!!.updateMassegeStatus(chat_id, 0)
            }
        }
    }
    private val onMassegeArriveFromServer = Emitter.Listener { args ->
        Log.d("log-socket-onMassegeArriveFromServer", "call: new massage is comes ")
        val acknowledgement_id = args[0] as Int
        Log.d("log-socket-onMassegeArriveFromServer-args", acknowledgement_id.toString())
        var requestCode = -1
        try {
            requestCode = args[2] as Int
        } catch (e: Exception) {
            Log.d("log-exception-in-massege-arrive", "call: Exception is : $e")
        }
        Log.d("log-requestCode", "call: before conditions requestCode is : $requestCode")

        //at send by user imidiate
        if (requestCode == 3) {
            var new_massege_time_of_send: Long = -1
            try {
                val new_massege = args[1] as JSONObject
                Log.d("log-onMassegeArriveFromServer3", "args$new_massege")
                val new_massege_sender_id = new_massege["sender_id"] as Int
                new_massege_time_of_send = new_massege["time_of_send"] as Long
                Log.d(
                    "log-onMassegeArriveFromServer3",
                    "new_massege_time_of_send is: $new_massege_time_of_send"
                )
                val contactArrayList1: ArrayList<ContactWithMassengerEntity>?
                contactArrayList1 = contactArrayList
                val newMassegeEntity1 = MassegeEntity(
                    new_massege_sender_id.toLong(),
                    user_login_id,
                    new_massege["user_massege"] as String,
                    new_massege_time_of_send,
                    1
                )
                for (i in contactArrayList1!!.indices) {
                    if (contactArrayList1[i].c_ID == new_massege_sender_id.toLong()) {
                        if (new_massege_sender_id.toLong() == Contact_page_opened_id) {
                            Log.d(
                                "log-onMassegeArriveFromServer3",
                                "else cond. start contact page is not opened"
                            )
                            runOnUiThread {
                                if (massegeDao!!.getMassegeByTimeOfSend(
                                        newMassegeEntity1.senderId,
                                        newMassegeEntity1.getTimeOfSend()
                                    ) == null
                                ) {
                                    val massegePopSoundThread =
                                        MassegePopSoundThread(this@HomePageWithContactActivity, 0)
                                    massegePopSoundThread.start()
                                    ContactMassegeDetailsView.massegeArrayList.add(newMassegeEntity1)
                                    ContactMassegeDetailsView.massegeRecyclerViewAdapter.notifyDataSetChanged()
                                    ContactMassegeDetailsView.massege_recyclerView.scrollToPosition(
                                        ContactMassegeDetailsView.massegeRecyclerViewAdapter.itemCountMyOwn
                                    )
                                }
                            }
                        } else {
                            Log.d(
                                "log-onMassegeArriveFromServer3",
                                "else cond. start contact page is not opened"
                            )
                            runOnUiThread {
                                val massegePopSoundThread =
                                    MassegePopSoundThread(this@HomePageWithContactActivity, 1)
                                massegePopSoundThread.start()
                                val t = Thread {
                                    massegeDao!!.incrementNewMassegeArriveValue(
                                        new_massege_sender_id.toLong()
                                    )
                                }
                                t.start()
                                val contactView = contactArrayList1[i]
                                val prev_value = contactView.newMassegeArriveValue
                                contactView.newMassegeArriveValue = prev_value + 1
                                contactArrayList!![i] = contactView
                                recyclerViewAdapter!!.notifyDataSetChanged()
                                recyclerView!!.scrollToPosition(
                                    recyclerViewAdapter!!.itemCountMyOwn
                                )
                            }
                        }
                        val massegeInsertIntoDatabase = Thread {
                            val massegeDao = db!!.massegeDao()
                            val x = massegeDao.getMassegeByTimeOfSend(
                                newMassegeEntity1.senderId, newMassegeEntity1.getTimeOfSend()
                            )
                            if (x == null) {
                                massegeDao.insertMassegeIntoChat(newMassegeEntity1)
                                Log.d(
                                    "log-onMassegeArriveFromServer3",
                                    "massege is inserted into database successfully"
                                )
                            } else {
                                Log.d("log-onMassegeArriveFromServer3", "X: $x")
                                Log.d("log-onMassegeArriveFromServer3", "X: " + x.massegeID)
                            }
                        }
                        massegeInsertIntoDatabase.start()
                    }
                }
                val checkContactSavedInDB = Thread {
                    val x = massegeDao!!.getContactWith_CID(newMassegeEntity1.senderId)
                    if (x == null) {
                        Log.d("log-onMassegeArriveFromServer3", "setPriorityRankThread1")
                        FetchDataFromServerAndSaveIntoDB(newMassegeEntity1.senderId)
                    } else {
                        val setPriorityRankThread = Thread {
                            val HighestPriority = massegeDao!!.highestPriorityRank
                            massegeDao!!.setPriorityRank(
                                newMassegeEntity1.senderId, HighestPriority + 1
                            )
                            MainContactListHolder!!.updatePositionOfContact(
                                newMassegeEntity1.senderId, this@HomePageWithContactActivity
                            )
                        }
                        setPriorityRankThread.start()
                    }
                }
                checkContactSavedInDB.start()
                try {
                    val tmpOBJ = JSONObject()
                    tmpOBJ.put("massege_sent_time", new_massege_time_of_send)
                    tmpOBJ.put("user_login_id", user_login_id)
                    socket!!.emit("new_massege_from_server_acknowledgement3", tmpOBJ)
                    Log.d(
                        "log-onMassegeArriveFromServer3",
                        "new_massege_from_server_acknowledgement3 socket emit sent"
                    )
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                Log.d(
                    "log-onMassegeArriveFromServer-Exception", "call: error while parsing data : $e"
                )
                try {
                    val tmpOBJ = JSONObject()
                    tmpOBJ.put("massege_sent_time", new_massege_time_of_send)
                    tmpOBJ.put("user_login_id", user_login_id)
                    socket!!.emit("new_massege_from_server_acknowledgement3", tmpOBJ)
                } catch (ex: JSONException) {
                    ex.printStackTrace()
                }
            }
        } else if (requestCode == 1) {
            //at staring the app
            val OnMassegeArriveThread1: Thread =
                onMassegeArriveThread1(this@HomePageWithContactActivity, statusForThread, *args)
            OnMassegeArriveThread1.start()
        } else if (requestCode == 2) {
            Log.d("log-requestCode", "call: requestCode is : $requestCode")
        } else {
            Log.d("log-requestCode", "call: requestCode enter in else condition : ")
            Log.d("log-requestCode", "call: requestCode is : $requestCode")
        }
    }
    private val onNewMessage = Emitter.Listener { args ->
        Log.d("log-socket-massege", "call: new massage is comes ")
        val acknowledgement_id = args[0] as Int
        val new_massege = args[1] as String
        Log.d("log-socket-massege args  : ", acknowledgement_id.toString())
        Log.d("log-socket-massege args  : ", new_massege)
        try {
            val statusObj = JSONObject()
            statusObj.put("status", 1)
            statusObj.put("acknowledgement_id", acknowledgement_id)
            socket!!.emit("new_massege_acknowledgement", statusObj)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    private val onMassegeReachAtServer = Emitter.Listener { args ->
        val new_massege: JSONObject
        try {
            new_massege = args[1] as JSONObject
            val chat_id = new_massege["Chat_id"] as Int
            Log.d("log-socket-onMassegeReachAtServer-args", new_massege.toString())
            Log.d(
                "log-socket-onMassegeReachAtServer-args massege from : ",
                new_massege["Chat_id"].toString()
            )
            Log.d(
                "log-socket-onMassegeReachAtServer-args massege is  :",
                new_massege["massege_status"].toString()
            )
        } catch (e: Exception) {
        }
        Log.d("log-socket-massege", "call: massege reach at server acknowledgement arrive")
    }
    private val onMassegeNotSentToUser =
        Emitter.Listener { Log.d("log-socket-massege", "call: massage not sent to user ") }
    private val onMassegeSentToUser =
        Emitter.Listener { Log.d("log-socket-massege", "call: massage is sent to user ") }
    private val onMassegeSeenByUser =
        Emitter.Listener { Log.d("log-socket-massege", "call: massage is seen by user ") }

    private fun saveFireBaseTokenToServer(user_login_id: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(
                    "log-saveFireBaseTokenToServer",
                    "Fetching FCM registration token failed",
                    task.exception
                )
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            // Log and toast
            Log.d("log-saveFireBaseTokenToServer", "token : $token")
            //                        Toast.makeText(HomePageWithContactActivity.this, token, Toast.LENGTH_SHORT).show();

            //store token to server with emailId
            val endpoint = GlobalVariables.URL_MAIN + "SaveFireBaseTokenToServer"
            Log.d("log-endpoint", endpoint)
            val requestQueue = Volley.newRequestQueue(this@HomePageWithContactActivity)
            val request: StringRequest = object :
                StringRequest(Method.POST, endpoint, Response.Listener<String?> { response ->
                    try {
                        val respObj = JSONObject(response)
                        val status = respObj.getString("status")
                        Log.d("log-saveFireBaseTokenToServer-response", "status : $status")
                        if (status == "1") {
                            Log.d(
                                "log-saveFireBaseTokenToServer-response",
                                "token saved successfully"
                            )
                        } else if (status == "2") {
                            Log.d("log-saveFireBaseTokenToServer-response", "token not saved")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.d("log-error", "onResponse: err in try bracket : $e")
                    }
                }, Response.ErrorListener { error ->
                    Toast.makeText(
                        this@HomePageWithContactActivity,
                        "Server side error :  $error",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "volley-error-saveFireBaseTokenToServer", "Server side error : $error"
                    )
                }) {
                override fun getParams(): Map<String, String>? {
                    val mc = MyCipher()
                    val params: MutableMap<String, String> = HashMap()
                    params["user_login_id"] = mc.encrypt(user_login_id)
                    params["tokenFCM"] = mc.encrypt(token)
                    return params
                }
            }
            requestQueue.add(request)
        })
    }

    fun getMainSideMenu(view: View?) {
        Log.d("log-enter", "getMainSideMenu: enter here")
        val intent = Intent(this, SettingsOptionPage::class.java)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        )
        if (requestCode == permission_code.CAMERA_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@HomePageWithContactActivity,
                    "Camera Permission Granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@HomePageWithContactActivity, "Camera Permission Denied", Toast.LENGTH_SHORT
                ).show()
            }
        } else if (requestCode == permission_code.STORAGE_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@HomePageWithContactActivity,
                    "Storage Permission Granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@HomePageWithContactActivity,
                    "Storage Permission Denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (requestCode == 1101) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@HomePageWithContactActivity,
                    "Contact Permission Granted",
                    Toast.LENGTH_SHORT
                ).show()
                //                SyncContactDetailsFirstTime();
            } else {
//                Toast.makeText(HomePageWithContactActivity.this, "ContactPermission Denied", Toast.LENGTH_SHORT).show();
                Toast.makeText(
                    this@HomePageWithContactActivity,
                    "To Use Our App YOu must Give the Contact Permission and manual ync Contact Later",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startNetworkListener() {
        val connectivityManager = getSystemService(
            ConnectivityManager::class.java
        ) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    private val networkRequest =
        NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build()
    private val networkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d("log-ConnectivityManager.NetworkCallback", "onAvailable")

            // send massege which is not sent due to  internet connection
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d("log-ConnectivityManager.NetworkCallback", "onLost")
        }

        override fun onCapabilitiesChanged(
            network: Network, networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val unMetered =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            Log.d(
                "log-ConnectivityManager.NetworkCallback",
                "onCapabilitiesChanged unMetered:$unMetered"
            )
        }
    }

    companion object {
        var FinishCode = 0
        const val MIN_DISTANCE = 150
        const val MAX_DISTANCE_Y = 80

        @JvmField
        var Contact_page_opened_id: Long = -1

        //Globle socket variables
        var socketOBJ: SocketClass? = null

        @JvmField
        var socket: Socket? = null

        @JvmField
        var user_login_id: Long = 0
        var UserMobileNumber: Long = 0
        var recyclerView: RecyclerView? = null
        var recyclerViewAdapter: RecyclerViewAdapter? = null
        var contactArrayList: ArrayList<ContactWithMassengerEntity>? = null

        @JvmField
        var db: MainDatabaseClass? = null
        var MainContactListHolder: ContactListHolder? = null
        var HomePageWithContactActivityStaticContext: Context? = null

        @SuppressLint("NotifyDataSetChanged")
        fun setOpened_contactChatViewToEmpty(position: Int) {
            val contactView = contactArrayList!![position]
            contactView.newMassegeArriveValue = 0
            contactArrayList!![position] = contactView
            recyclerViewAdapter!!.notifyDataSetChanged()
        }

        fun FetchDataFromServerAndSaveIntoDB(CID: Long) {
            val t = Thread {
                val requestQueue = Volley.newRequestQueue(
                    HomePageWithContactActivityStaticContext
                )
                val endpoint = GlobalVariables.URL_MAIN + "GetContactDetailsOfUserToSaveLocally"
                val mainArray = JSONArray()
                mainArray.put(user_login_id)
                mainArray.put(CID)
                Log.d("log-HomePageWithContactActivity", "mainArray : $mainArray")
                val jsonArrayRequest =
                    JsonArrayRequest(Request.Method.POST, endpoint, mainArray, { response ->
                        Log.d(
                            "log-HomePageWithContactActivity",
                            "onResponse: response length : " + response.length()
                        )
                        try {
                            Log.d(
                                "log-HomePageWithContactActivity",
                                "onResponse: response[0] : " + response[0]
                            )
                            val CID = response[0].toString().toLong()
                            val Number = response[1].toString().toLong()
                            val Name = response[2] as String
                            val DisplayName = response[5] as String
                            val massegeDao = db!!.massegeDao()
                            val rank = massegeDao.highestPriorityRank
                            val newContact = ContactWithMassengerEntity(Number, null, CID, rank + 1)
                            if (massegeDao.getContactWith_CID(CID) == null) {
                                massegeDao.SaveContactDetailsInDatabase(newContact)
                                Log.d(
                                    "log-HomePageWithContactActivity",
                                    "onResponse: newContact saved into with rank :" + (rank + 1)
                                )
                                // now we have to add contact into recyclerViewAdapter
                                contactArrayList!!.add(newContact)
                                recyclerViewAdapter!!.notifyDataSetChanged()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }) { error ->
                        Log.d(
                            "log-AllContactOfUserDeviceView",
                            "onErrorResponse: setChatDetails error: $error"
                        )
                    }
                jsonArrayRequest.retryPolicy = DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
                requestQueue.add(jsonArrayRequest)
            }
            t.start()
        }

        val onSocketConnect = Emitter.Listener {
            Log.d(
                "log-HomePageWithContactActivity-onSocketConnect",
                "Socket connect event id is: " + socket!!.id()
            )
        }
        val onSocketDisConnect = Emitter.Listener {
            Log.d(
                "log-HomePageWithContactActivity-onSocketDisConnect",
                "Socket DisConnect event id is: " + socket!!.id()
            )
        }
    }
}