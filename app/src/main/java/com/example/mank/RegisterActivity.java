package com.example.mank;

import static com.example.mank.configuration.GlobalVariables.URL_MAIN;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mank.cipher.MyCipher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    //    private static final String url = "http://192.168.43.48:10000/";
    private static final String url = URL_MAIN;


    EditText nameOfUserRegister;
    EditText userPhoneRegister;
    EditText userPassword1;
    EditText userPassword2;
    Button register;
    private ProgressBar loadingPB;
    MyCipher mc = new MyCipher();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("log-not logined", "onCreate: inside Oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeractivity_main);

        nameOfUserRegister = findViewById(R.id.nameOfUserRegister);
        userPhoneRegister = findViewById(R.id.userPhoneRegister);
        userPassword1 = findViewById((R.id.userPassword1));
        userPassword2 = findViewById((R.id.userPassword2));
        register = findViewById(R.id.register_button);
        loadingPB = findViewById(R.id.LoadingPBOfLoginPage);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_password1 = userPassword1.getText().toString();
                String user_password2 = userPassword2.getText().toString();
                String user_name = nameOfUserRegister.getText().toString();
                String user_number = userPhoneRegister.getText().toString();
                if (user_name.equals("") || user_name.length() < 2) {
                    Toast.makeText(RegisterActivity.this, "user name can not be empty Or less than 2 Charcator please enter appropriate user name", Toast.LENGTH_SHORT).show();
                } else if (user_number.length() != 10) {
                    Toast.makeText(RegisterActivity.this, "Please enter proper phone number", Toast.LENGTH_SHORT).show();
                } else if (user_password1.equals(user_password2)) {

                    Log.d("log-loginbutton hit ", user_number + " and " + user_password1);
                    if (!user_password1.equals("")) {
                        Register(user_number, user_password1, user_name);
                    } else {
                        Toast.makeText(RegisterActivity.this, "password can not be empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Please enter both password same", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void Register(String user_number, String user_password, String user_name) {
        loadingPB.setVisibility(View.VISIBLE);
        // creating a new variable for our request queue
        String endpoint = url + "RegisterNewUser";
        Log.d("log-e", endpoint);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, endpoint, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Login succsesfull enjoy it!", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject respObj = new JSONObject(response);
                    String status = respObj.getString("status");
                    Log.d("log-response-status", status);

                    if (status.equals("1")) {
                        Toast.makeText(RegisterActivity.this, "Now You Can Login With Your User Acoount... Thanks For Signup In Massenger", Toast.LENGTH_LONG).show();
                        finish();
                    } else if (status.equals("2")) {
                        Toast.makeText(RegisterActivity.this, "server error!!! please try again later", Toast.LENGTH_SHORT).show();
                        Log.d("log-e", "server error ");
                    } else if (status.equals("0")) {
                        Toast.makeText(RegisterActivity.this, "Wrong Password!!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("log-error", "onResponse: err in try bracet : " + e);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Server side error :  " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("number", mc.encrypt(user_number));
                params.put("password", mc.encrypt(user_password));
                params.put("name", mc.encrypt(user_name));

                return params;
            }
        };
        requestQueue.add(request);
    }
}