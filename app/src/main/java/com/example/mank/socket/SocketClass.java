package com.example.mank.socket;

import static com.example.mank.MainActivity.API_SERVER_API_KEY;
import static com.example.mank.configuration.GlobalVariables.SOCKET_URL;
import static java.util.Collections.singletonMap;

import android.util.Log;

import com.example.mank.LocalDatabaseFiles.DataContainerClasses.holdLoginData;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.loginDetailsEntity;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketClass {

    private Socket socket;

    public SocketClass(MainDatabaseClass db) {
        holdLoginData hold_LoginData = new holdLoginData();
        loginDetailsEntity userDetails = hold_LoginData.getData();

        if (userDetails != null) {
            String[] arr = {API_SERVER_API_KEY, userDetails.UID};
            IO.Options options = IO.Options.builder()
                    .setAuth(singletonMap("token", API_SERVER_API_KEY+userDetails.UID))
                    .setPath("/socket.io/")
                    .build();
            try {
                socket = IO.socket(SOCKET_URL, options);
            } catch (URISyntaxException e) {
                Log.d("log-HomePageWithContactActivity-SocketClass", "Exception error connecting to socket: " + e);
            }
            socket.connect();
        }
        }

        public Socket getSocket () {
            return socket;
        }

        public void joinRoom (String user_login_id){
            if (socket != null) {
                socket.emit("join", user_login_id);
            }
        }
    }
