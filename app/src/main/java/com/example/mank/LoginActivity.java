package com.example.mank;

import static com.example.mank.MainActivity.API_SERVER_API_KEY;
import static com.example.mank.MainActivity.user_login_id;
import static com.example.mank.configuration.GlobalVariables.URL_MAIN;
import static com.example.mank.configuration.permissionMain.hasPermissions;
import static com.example.mank.configuration.permission_code.CONTACT_STORAGE_PERMISSION;
import static com.example.mank.configuration.permission_code.PERMISSIONS;
import static com.example.mank.configuration.permission_code.PERMISSION_CONTACT_SYNC;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
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

        if (!hasPermissions(this, CONTACT_STORAGE_PERMISSION)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CONTACT_SYNC);
        } else {
            loginRedirect();
        }
    }

    private void registerRedirect() {

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
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                String user_password1 = userPass1InRegister.getText().toString();
                String user_password2 = userPass2InRegister.getText().toString();
                String user_number = userMobileNumberInRegister.getText().toString();
                String user_name = userNameInRegister.getText().toString();
                Log.d("log-signUpButton hit ", user_number + " and " + user_password1);

                if (user_name.length() == 0) {
                    massegeBox.setText("please enter your name");
                    massegeBox.setTextColor(R.color.MassegeBoxWarning);
                    return;
                }
                if (user_name.length() < 2) {
                    massegeBox.setText("user name should be at least 3 character long ");
                    massegeBox.setTextColor(R.color.MassegeBoxWarning);
                    return;
                }
                if (user_number.length() < 10) {
                    massegeBox.setText("please enter valid mobile number");
                    massegeBox.setTextColor(R.color.MassegeBoxWarning);
                    return;
                }
                if (user_password1.length() == 0) {
                    massegeBox.setText("please enter password");
                    massegeBox.setTextColor(R.color.MassegeBoxWarning);
                    return;
                }
                if (!user_password1.equals(user_password2)) {
                    massegeBox.setText("please enter same password");
                    massegeBox.setTextColor(R.color.MassegeBoxWarning);
                    return;
                }
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
                if (user_number.length() < 10) {
                    massegeBox.setText("please enter valid phone number");
                    return;
                }
                if (user_password.length() == 0) {
                    massegeBox.setText("please enter your password");
                    return;
                }

                checkHaveToRegister(user_number, user_password);

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
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(String response) {
                loadingPB.setVisibility(View.GONE);
                try {
                    JSONObject respObj = new JSONObject(response);
                    String status = respObj.getString("status");
                    Log.d("log-response-status", status);

                    switch (status) {
                        case "1":
                            String user_id = (String) respObj.getString("user_id");
                            String displayName ="";
                            String about = "";
                            long ProfileImageVersion = 0;
                            String profileImageBase64 = null;
                            try {
                                displayName = (String) respObj.getString("displayName");
                                about = (String) respObj.getString("about");
                                ProfileImageVersion = Long.parseLong(String.valueOf(respObj.getString("ProfileImageVersion")));
                                profileImageBase64 = (String) respObj.getString("ProfileImage");
                                byte[] profileImageByteArray = Base64.decode(profileImageBase64, Base64.DEFAULT);

                                if (profileImageByteArray.length > 0) {
                                    synchronized (this) {
                                        Bitmap bitmapImage = BitmapFactory.decodeByteArray(profileImageByteArray, 0, profileImageByteArray.length);
                                        Log.d("log-loginActivity", "Saved image of size : " + profileImageByteArray.length + " and resolution : " + bitmapImage.getWidth() + "*" + bitmapImage.getHeight());
                                        saveContactProfileImageToStorage(user_id, bitmapImage);
                                    }
                                }
                            } catch (Exception e) {
                                Log.d("log-LoginActivity ", "expected image not found at login time : " + e.toString());
                            }

                            massegeBox.setTextColor(R.color.MassegeBoxSuccess);
                            massegeBox.setText("Login successful");
                            login(user_number, user_password, user_id, displayName, about, ProfileImageVersion);
                            break;
                        case "2":
                            massegeBox.setTextColor(R.color.MassegeBoxWarning);
                            massegeBox.setText("You have to register with this number, first!!");
                            break;
                        case "0":
                            massegeBox.setTextColor(R.color.MassegeBoxAlert);
                            massegeBox.setText("Wrong password");
                            break;
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

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                Log.d("log-LoginActivity", "apiKey : " + API_SERVER_API_KEY);
                headers.put("api_key", API_SERVER_API_KEY);
                return headers;
            }

        };
        requestQueue.add(request);
    }

    public void login(String user_number, String userPassword, String user_id, String displayName, String about, long ProfileImageVersion) {
        //here we are storing login details to local database
        Log.d("LoginActivity", "login method start");

        long number = Long.parseLong(user_number);
        loginDetailsEntity login_details = new loginDetailsEntity(user_id, userPassword, number, displayName, about);
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
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(String response) {
                loadingPB.setVisibility(View.GONE);
                try {
                    JSONObject respObj = new JSONObject(response);
                    String status = respObj.getString("status");
                    Log.d("log-response-status", status);

                    if (status.equals("1")) {
                        massegeBox.setText("SighUp is successFull! Login with your account");
                        massegeBox.setTextColor(R.color.MassegeBoxSuccess);
//                        loginRedirect();
                    } else if (status.equals("2")) {
                        massegeBox.setText("server error!!! please try again later");
                        massegeBox.setTextColor(R.color.MassegeBoxAlert);
                        Log.d("log-e", "server error ");
                    } else if (status.equals("0")) {
                        massegeBox.setTextColor(R.color.MassegeBoxSuccess);
                        massegeBox.setText("You have already an account with this phone number");
                    } else {
                        massegeBox.setText("enter in else condition");
                        massegeBox.setTextColor(R.color.MassegeBoxAlert);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("log-error", "onResponse: err in try bracet : " + e);
                    massegeBox.setText("enter in catch block");
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingPB.setVisibility(View.GONE);
                massegeBox.setText("Server side error :  " + error);
                massegeBox.setTextColor(R.color.MassegeBoxAlert);
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

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("api_key", API_SERVER_API_KEY);
                return headers;
            }
        };
        requestQueue.add(request);
    }

    private void saveContactProfileImageToStorage(String id, Bitmap bitmapImage) {
        File directory = new File(Environment.getExternalStorageDirectory(), "Android/media/com.massenger.mank.main/Pictures/Profiles");

        // Create the directory if it doesn't exist
        if (!directory.exists()) {
            boolean x = directory.mkdirs();
        }

        // Create the file path
        File imagePath = new File(directory, "" + user_login_id + user_login_id + ".png");

        // Save the bitmap image to the file
        try (OutputStream outputStream = new FileOutputStream(imagePath)) {
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("log-saveImageToInternalStorage", "Image Save failed " + e.toString());
        }
        // Print the absolute path of the saved image
        Log.d("log-saveImageToInternalStorage", "Saved image path: " + imagePath.getAbsolutePath());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CONTACT_SYNC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loginRedirect();
            } else {
                Toast.makeText(LoginActivity.this, "To use Massenger please give Contact and Storage permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}