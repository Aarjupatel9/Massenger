package com.example.mank;

import static com.example.mank.configuration.GlobalVariables.URL_MAIN;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.MainDatabaseClass;
import com.example.mank.LocalDatabaseFiles.entities.loginDetailsEntity;
import com.example.mank.cipher.MyCipher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {
    //    private static final String url = "http://192.168.43.48:10000/";
    private static final String url = URL_MAIN;

    MyCipher mc = new MyCipher();
    EditText userPasswordInLogin, userMobileNumberInLogin, userNameInRegister, userMobileNumberInRegister, userPass1InRegister, userPass2InRegister;
    Button login, signUp;
    private ProgressBar loadingPB;
    private TextView massegeBox, registerRedirectLink, loginRedirectLink;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("log-onDestroy", "onDestroy: in LoginActivity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("log-not logined", "onCreate: inside Oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page_master);


        massegeBox = findViewById(R.id.massegeBoxInLogin);
        loadingPB = findViewById(R.id.LoadingPBOfLoginPage);

        loginRedirect();
    }

    private void registerRedirect() {

        massegeBox.setText("registerRedirect() start");
        ViewGroup includeLayout = findViewById(R.id.includeInLoginMaster);
        View newLayout = LayoutInflater.from(this).inflate(R.layout.activity_login_page_register, null);
        includeLayout.removeAllViews();
        includeLayout.addView(newLayout);


        loginRedirectLink = findViewById(R.id.loginRedirectLink);
        signUp = findViewById(R.id.cirRegisterButton);

        userNameInRegister = findViewById(R.id.userNameInRegister);
        userMobileNumberInRegister = findViewById((R.id.userMobileNumberInRegister));
        userPass1InRegister = findViewById(R.id.userPass1InRegister);
        userPass2InRegister = findViewById((R.id.userPass2InRegister));

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_password1 = userPass1InRegister.getText().toString();
                String user_password2 = userPass2InRegister.getText().toString();
                String user_number = userMobileNumberInRegister.getText().toString();
                String user_name = userNameInRegister.getText().toString();
                Log.d("log-signUpButton hit ", user_number + " and " + user_password1);

                if(user_name.length() == 0){
                    massegeBox.setText("please enter your name");
                    return;
                }
                if(user_name.length() < 2){
                    massegeBox.setText("user name should be at least 3 character long ");
                    return;
                }
                if (user_number.length() < 10) {
                    massegeBox.setText("please enter valid mobile number");
                    return;
                }
                if(user_password1.length() == 0){
                    massegeBox.setText("please enter password");
                    return;
                }
                if (!user_password1.equals(user_password2)) {
                    massegeBox.setText("please enter same password");
                    return;
                }
                massegeBox.setText("please wait while we signUp you in Massenger");
                loadingPB.setVisibility(View.VISIBLE);
                SignUp(user_number, user_password1, user_name);
            }
        });
        loginRedirectLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginRedirect();
            }
        });


    }

    private void loginRedirect() {
        massegeBox.setText("loginRedirect() start");
        ViewGroup includeLayout = findViewById(R.id.includeInLoginMaster);
        View newLayout = LayoutInflater.from(this).inflate(R.layout.activity_login_page_login, null);
        includeLayout.removeAllViews();
        includeLayout.addView(newLayout);

        login = findViewById(R.id.cirLoginButton);
        registerRedirectLink = findViewById(R.id.registerRedirectLink);

        userMobileNumberInLogin = findViewById(R.id.userMobileNumberInLogin);
        userPasswordInLogin = findViewById((R.id.userPasswordInLogin));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_password = userPasswordInLogin.getText().toString();
                String user_number = userMobileNumberInLogin.getText().toString();
                Log.d("log-loginButton hit ", user_number + " and " + user_password);
                massegeBox.setText("login button hit : " + user_number + " and " + user_password);
                if (user_number.length() < 10) {
                    massegeBox.setText("please enter valid phone number");
                    return;
                }
                if(user_password.length() == 0){
                    massegeBox.setText("please enter your password");
                    return;
                }

                checkHaveToRegister(user_number, user_password);
//              checkHaveToRegisterInDatabase(user_number, user_password);

            }
        });

        registerRedirectLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerRedirect();
            }
        });

    }

    private void checkHaveToRegister(String user_number, String user_password) {
        loadingPB.setVisibility(View.VISIBLE);
        // creating a new variable for our request queue
        String endpoint = url + "checkHaveToRegister";
        Log.d("log-e", endpoint);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, endpoint, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingPB.setVisibility(View.GONE);
                try {
                    JSONObject respObj = new JSONObject(response);
                    String status = respObj.getString("status");
                    Log.d("log-response-status", status);

                    if (status.equals("1")) {
                        String user_id = (String)respObj.getString("user_id");
                        Log.d("log-user-id", String.valueOf(user_id));
                        login(user_number, user_password, user_id);
                    } else if (status.equals("2")) {
                        Toast.makeText(LoginActivity.this, "You have to register , Thre is No account with this phone number", Toast.LENGTH_LONG).show();
                    } else if (status.equals("0")) {
                        Toast.makeText(LoginActivity.this, "Wrong Password!!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(LoginActivity.this, "Server side error :  " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("number", mc.encrypt(user_number));
                params.put("password", mc.encrypt(user_password));
                return params;
            }
        };
        requestQueue.add(request);
    }

    public void login(String user_number, String user_password, String user_id) {
        //here we are storing login details to local database
        Log.d("LoginActivity", "login method start");

        long number = Long.parseLong(user_number);
        loginDetailsEntity login_details = new loginDetailsEntity(user_id, user_password, number, null);
        Log.d("log-reached", "before db initialize : " + login_details.getMobileNumber() + " qnd " + login_details.getPassword());
        MainDatabaseClass db = Room.databaseBuilder(getApplicationContext(),
                MainDatabaseClass.class, "MassengerDatabase").allowMainThreadQueries().build();

        Log.d("log-reached", "after db initialize");
        MassegeDao massegeDao = db.massegeDao();
        massegeDao.SaveLoginDetailsInDatabase(login_details);
        Log.d("log-login details saved", "after saved Details");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", user_id);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
    public void ResetPassword(View view) {
        Log.d("log-register", "ResetPassword : in method enter");
        setContentView(R.layout.activity_app_reset_password);
    }

    private void SignUp(String user_number, String user_password, String user_name) {
        loadingPB.setVisibility(View.VISIBLE);
        // creating a new variable for our request queue
        String endpoint = url + "RegisterNewUser";
        Log.d("log-e", endpoint);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, endpoint, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingPB.setVisibility(View.GONE);
                try {
                    JSONObject respObj = new JSONObject(response);
                    String status = respObj.getString("status");
                    Log.d("log-response-status", status);

                    if (status.equals("1")) {
                        massegeBox.setText("SighUp is successFull! Login with your account");
//                        loginRedirect();
                    } else if (status.equals("2")) {
                        massegeBox.setText("server error!!! please try again later");
                        Log.d("log-e", "server error ");
                    } else if (status.equals("0")) {
                        massegeBox.setText("You have already an account with this phone number");
                    } else  {
                        massegeBox.setText("enter in else condition");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("log-error", "onResponse: err in try bracet : " + e);
                    massegeBox.setText("enter in catch block");
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingPB.setVisibility(View.GONE);
                massegeBox.setText("Server side error :  " + error);
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