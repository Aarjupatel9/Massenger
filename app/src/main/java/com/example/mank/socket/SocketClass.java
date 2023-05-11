package com.example.mank.socket;

import static com.example.mank.configuration.GlobalVariables.SOCKET_URL;
import static java.util.Collections.singletonMap;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketClass {

    private Socket socket;


    public SocketClass() {
        IO.Options options = IO.Options.builder()
                .setAuth(singletonMap("token", "abcd"))
                .setPath("/socket.io/")
                .build();
        try {
            socket = IO.socket(SOCKET_URL, options);
        } catch (URISyntaxException e) {
            Log.d("log-HomePageWithContactActivity-SocketClass", "Exception error connecting to socket: "+e);
        }
        socket.connect();
    }

    public Socket getSocket() {
        return socket;
    }

    public void joinRoom(long user_login_id) {
        socket.emit("join", user_login_id);
    }
}
