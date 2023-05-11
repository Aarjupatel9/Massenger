package com.example.mank

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.util.Log

class AppStartingViewPage : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_starting_view_page)
        val handler = Handler()
        handler.postDelayed({ finish() }, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("log-StartingTimeOver", "onDestroy: inside after 3 second is over")
        //        finish();
    }
}