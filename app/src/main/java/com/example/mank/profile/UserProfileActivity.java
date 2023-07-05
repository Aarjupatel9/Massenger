package com.example.mank.profile;


import static com.example.mank.MainActivity.contactListAdapter;
import static com.example.mank.MainActivity.db;
import static com.example.mank.MainActivity.socket;
import static com.example.mank.MainActivity.user_login_id;
import static com.example.mank.configuration.GlobalVariables.URL_MAIN;
import static java.lang.Boolean.TRUE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.holdLoginData;
import com.example.mank.LocalDatabaseFiles.entities.loginDetailsEntity;
import com.example.mank.MainActivity;
import com.example.mank.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class UserProfileActivity extends Activity {

    private String url = URL_MAIN;
    private ApiService apiService;
    private Uri picUri;
    private MassegeDao massegeDao;
    private ArrayList<String> permissionsToRequest;
    private final ArrayList<String> permissionsRejected = new ArrayList<>();
    private final ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private final static int IMAGE_RESULT = 200;
    public ProgressDialog progress;
    private ProgressBar ProfileUploadProgressBar;
    private FloatingActionButton fabCamera, fabUpload;
    private ImageView user_Profile_photo;
    private Bitmap bitmap;
    private EditText user_name_EditField;
    private EditText user_about_in_profile_page;
//    private RadioGroup radioGroupForSelectingPrivacy;
//    private RadioButton radioButton;

    String username, aboutInfo;
    private byte[] imageData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        user_name_EditField = (EditText) findViewById(R.id.user_name_in_profile_page);
        user_about_in_profile_page = (EditText) findViewById(R.id.user_about_in_profile_page);
//        radioGroupForSelectingPrivacy = (RadioGroup) findViewById(R.id.radioGroupForSelectingPrivacy);
//        //by default set to allow to all we will edit this according to database later
//        findViewById(R.id.onlineStatusAllowToAll).setSelected(true);

        holdLoginData hold_LoginData = new holdLoginData();
        loginDetailsEntity dataFromDatabase = hold_LoginData.getData();
        massegeDao = db.massegeDao();

        if (dataFromDatabase != null) {
            if (dataFromDatabase.getDisplayUserName() != null) {
                String userNameLocal = dataFromDatabase.getDisplayUserName().toString();
                String aboutInfolocal = dataFromDatabase.getAbout().toString();

                Log.d("log-oncreate-username", "onCreate: username is " + userNameLocal);
                user_name_EditField.setText(userNameLocal, TextView.BufferType.EDITABLE);
                user_about_in_profile_page.setText(aboutInfolocal);
                username = userNameLocal;
                aboutInfo = aboutInfolocal;
            }
        }

        fabUpload = findViewById(R.id.fabUpload);
        user_Profile_photo = findViewById(R.id.userProfilePhoto);
        setUserImage();

        ProfileUploadProgressBar = findViewById(R.id.ProfileUploadProgressBar);
        initRetrofitClient();

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);

    }
    public void setUserImage() {
        Thread ti = new Thread(new Runnable() {
            @Override
            public void run() {
//                byte[] selfUserImageData = massegeDao.getSelfUserImage(user_login_id, user_login_id);
//                if (selfUserImageData != null) {
//                    Bitmap selfImage = BitmapFactory.decodeByteArray(selfUserImageData, 0, selfUserImageData.length);
//                    Log.d("log-ContactListAdapter", "setUserImage : after fetch image form db : " + selfUserImageData.length);
//                    user_Profile_photo.setImageBitmap(selfImage);
//                }
                String imagePath = "/storage/emulated/0/Android/media/com.massenger.mank.main/Pictures/profiles/" + user_login_id + user_login_id + ".png";
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            user_Profile_photo.setImageBitmap(selfImage);

                        }
                    });
                }
            }
        });
        ti.start();
    }
    private final Emitter.Listener onUpdateUserDisplayName_return = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("log-onUpdateUserDisplayName_return", "call: onUpdateUserDisplayName_return enter ");
            runOnUiThread(new Runnable() {
                public void run() {
                    username = user_name_EditField.getText().toString();
                    Toast.makeText(UserProfileActivity.this, "Display name is updated", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
    private final Emitter.Listener onUpdateUserAboutInfo_return = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("log-onUpdateUserAboutInfo_return", "call: onUpdateUserAboutInfo_return enter ");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    aboutInfo = user_about_in_profile_page.getText().toString();
                    Toast.makeText(getApplicationContext(), "About is updated", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
    private final Emitter.Listener onUpdateUserProfileImage_return = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("log-onChangeUserProfileImage_return", "start");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Image is updated", Toast.LENGTH_SHORT).show();
                    ProfileUploadProgressBar.setVisibility(View.GONE);
                }
            });
        }
    };

    private void initRetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        apiService = new Retrofit.Builder().baseUrl(url).client(client).build().create(ApiService.class);
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
//            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
            outputFileUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    private boolean savePass = false;

    private static final int TARGET_RESOLUTION = 1024;
    private static final int JPEG_QUALITY = 80;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.d("log-UserProfileActivity", "onActivityResult r(resultCode == Activity.RESULT_OK start");
            if (requestCode == IMAGE_RESULT) {
                fabUpload.setVisibility(View.VISIBLE);
                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    Log.d("log-UserProfileActivity", "onActivityResult filepath not null : " + filePath);
                    Thread Ti = new Thread(new Runnable() {
                        @Override
                        public void run() {

//                            bitmap = BitmapFactory.decodeFile(filePath);
                            savePass = true;

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(filePath, options);

                            int imageWidth = options.outWidth;
                            int imageHeight = options.outHeight;

                            int scaleFactor = 1;
                            if (imageWidth > TARGET_RESOLUTION || imageHeight > TARGET_RESOLUTION) {
                                scaleFactor = (int) Math.pow(2, (int) Math.ceil(Math.log(Math.max(imageWidth, imageHeight) / (double) TARGET_RESOLUTION) / Math.log(0.5)));
                            }

                            options.inJustDecodeBounds = false;
                            options.inSampleSize = scaleFactor;

                            bitmap = BitmapFactory.decodeFile(filePath, options);

                            if (bitmap.getWidth() != TARGET_RESOLUTION || bitmap.getHeight() != TARGET_RESOLUTION) {
                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, TARGET_RESOLUTION, TARGET_RESOLUTION, true);
                                bitmap.recycle();
                                bitmap = scaledBitmap;
                            }

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Toast.makeText(UserProfileActivity.this, "image resolution is : " + bitmap.getHeight() + "*" + bitmap.getWidth(), Toast.LENGTH_LONG).show();
                                }
                            });

                            imageData = stream.toByteArray();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int compressedImageLength = imageData.length;

                                    if (compressedImageLength > (200 * 1024)) {
                                        Toast.makeText(UserProfileActivity.this, "image size is two large for store into database", Toast.LENGTH_LONG).show();
                                    } else {
                                        user_Profile_photo.setImageBitmap(bitmap);
                                    }
                                }
                            });

                        }
                    });
                    Ti.start();
                }
            }
        }
    }

    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;
        if (isCamera) {
            return getCaptureImageOutputUri().getPath();
        } else {
            return getPathFromURI(data.getData());
        }
    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        picUri = savedInstanceState.getParcelable("pic_uri");
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
//                    if (!hasPermission(perms)) {
                    permissionsRejected.add(perms);
//                    }
                }
                if (permissionsRejected.size() > 0) {

                    if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                        showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                    }
                                });
                        return;
                    }

                }
                break;
        }

    }

    @SuppressLint("IntentReset")
    public void ProfilePhotoOnClick(View view) {
        Log.d("log-UserProfileActivity", "ProfilePhotoOnClick method start");
        fabUpload.setVisibility(View.GONE);
        fabUpload.setEnabled(TRUE);
        Log.d("log-UserProfileActivity", "ProfilePhotoOnClick method before startActivityForResult");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, IMAGE_RESULT);
    }


    public void FabUploadOnClick(View view) {
        ProfileUploadProgressBar.setVisibility(View.VISIBLE);
        if (imageData != null) {
            if (socket != null) {
                contactListAdapter.updateSelfUserImage(imageData);
                socket.on("updateUserProfileImage_return", onUpdateUserProfileImage_return);
                socket.emit("updateUserProfileImage", user_login_id, imageData);

                saveImageToInternalStorage(bitmap);
            } else {
                Toast.makeText(getApplicationContext(), "profile photo can not be update right now, please try again after soe time", Toast.LENGTH_SHORT).show();
                ProfileUploadProgressBar.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Bitmap is null. Try again", Toast.LENGTH_SHORT).show();
            ProfileUploadProgressBar.setVisibility(View.GONE);
        }
    }

    public void UpdateUserProfileDetails(View view) {
        String set_display_name = user_name_EditField.getText().toString();
        String set_about_name = user_about_in_profile_page.getText().toString();

        if (!set_display_name.equals(username)) {
            if (socket != null) {
                int success_status = massegeDao.updateDisplayUserName(set_display_name, user_login_id);
                Log.d("log-success_status", "UpdateUserDisplayName: status is " + success_status);
                socket.on("updateUserDisplayName_return", onUpdateUserDisplayName_return);
                socket.emit("updateUserDisplayName", user_login_id, set_display_name);

            } else {
                Toast.makeText(getApplicationContext(), "username can not be update right now, please try again after soe time", Toast.LENGTH_SHORT).show();
                ProfileUploadProgressBar.setVisibility(View.GONE);
            }
        }
        if (!set_about_name.equals(aboutInfo)) {
            if (set_about_name.equals("")) {
                Toast.makeText(this, "About Can not be empty", Toast.LENGTH_SHORT).show();
            } else {
                if (socket != null) {
                    socket.on("updateUserAboutInfo_return", onUpdateUserAboutInfo_return);
                    socket.emit("updateUserAboutInfo", user_login_id, set_about_name);
                    int success_status = massegeDao.updateAboutUserName(set_about_name, user_login_id);
                    Log.d("log-success_status", "UpdateUserAboutInfo: status is " + success_status);
                } else {
                    Toast.makeText(getApplicationContext(), "about status can not be update right now, please try again after soe time", Toast.LENGTH_SHORT).show();
                    ProfileUploadProgressBar.setVisibility(View.GONE);
                }
            }
        }
    }

//    public void UpdateUserOnlineStatusPrivacy(View view) {
//        int selectedId = radioGroupForSelectingPrivacy.getCheckedRadioButtonId();
//
//        // find the radiobutton by returned id
//        radioButton = (RadioButton) findViewById(selectedId);
//        Log.d("log-radio", "radio select : " + radioButton.getText());
////        int success_status = MassegeDao.updateAboutUserName(set_about_name, user_login_id);
////        Log.d("log-success_status", "UpdateUserDisplayName: status is " + success_status);
//    }

    public void userProfilePhotoLabelOnClick(View view) {
        Log.d("log-UserProfileActivity", "userProfilePhotoLabelOnClick method start");
        writeFileOnInternalStorage(this, "try.txt", "testing");
        Log.d("log-UserProfileActivity", "userProfilePhotoLabelOnClick method end");
    }

    public void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody) {
        File file = new File(this.getFilesDir(), "try.txt");
        Log.d("log-UserProfileActivity", "writeFileOnInternalStorage method before if cond. dir:" + file);
        try (FileOutputStream fos = mcoContext.openFileOutput(sFileName, Context.MODE_PRIVATE)) {
            fos.write(sBody.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("log-UserProfileActivity-IOException", "writeFileOnInternalStorage method IOException:" + e);
        }

//        try {
//            Log.d("log-UserProfileActivity", "writeFileOnInternalStorage method try enter");
//            File gpxfile = new File(dir, sFileName);
//            FileWriter writer = new FileWriter(gpxfile);
//            writer.append(sBody);
//            writer.flush();
//            writer.close();
//            Log.d("log-UserProfileActivity", "writeFileOnInternalStorage method before try end");
//        } catch (Exception e){
//            Log.d("log-UserProfileActivity-Exception", "userProfilePhotoLabelOnClick method Exception:"+e);
//            e.printStackTrace();
//        }
    }

    private void saveImageToInternalStorage(Bitmap bitmapImage) {
        // Get the application-specific directory path
//        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profiles");
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

//    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
//            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
//                // Callback is invoked after the user selects a media item or closes the
//                // photo picker.
//                if (uri != null) {
//                    Log.d("PhotoPicker", "Selected URI: " + uri);
//                } else {
//                    Log.d("PhotoPicker", "No media selected");
//                }
//            });



    public void FinishAUPPActivity(View view) {
        this.finish();
    }
}
