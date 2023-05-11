package com.example.mank.profile;


import static com.example.mank.MainActivity.MainActivityStaticContext;
import static com.example.mank.MainActivity.db;
import static com.example.mank.MainActivity.socket;
import static com.example.mank.MainActivity.user_login_id;
import static com.example.mank.configuration.GlobalVariables.URL_MAIN;
import static java.lang.Boolean.TRUE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
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

import com.example.mank.FunctionalityClasses.MyImageClass;
import com.example.mank.LocalDatabaseFiles.DAoFiles.MassegeDao;
import com.example.mank.LocalDatabaseFiles.DataContainerClasses.holdLoginData;
import com.example.mank.LocalDatabaseFiles.entities.loginDetailsEntity;
import com.example.mank.MainActivity;
import com.example.mank.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class ProfileUploadActivity extends AppCompatActivity {

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
    private TextView userProfilePhotoLabel;
    private EditText user_name_EditField;
    private EditText user_about_in_profile_page;
    private RadioGroup radioGroupForSelectingPrivacy;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        socket.on("updateUserDisplayName_return", onUpdateUserDisplayName_return);
        socket.on("updateUserAboutInfo_return", onUpdateUserAboutInfo_return);

        user_name_EditField = (EditText) findViewById(R.id.user_name_in_profile_page);
        user_about_in_profile_page = (EditText) findViewById(R.id.user_about_in_profile_page);
        radioGroupForSelectingPrivacy = (RadioGroup) findViewById(R.id.radioGroupForSelectingPrivacy);
        //by default set to allow to all we will edit this according to database later
        findViewById(R.id.onlineStatusAllowToAll).setSelected(true);

        holdLoginData hold_LoginData = new holdLoginData();
        loginDetailsEntity dataFromDatabase = hold_LoginData.getData();
        massegeDao = db.massegeDao();


        if (dataFromDatabase != null) {
            if (dataFromDatabase.getDisplayUserName() != null) {
                String userName = dataFromDatabase.getDisplayUserName().toString();
                Log.d("log-oncreate-username", "onCreate: username is " + userName);
                user_name_EditField.setText(userName, TextView.BufferType.EDITABLE);
            }
        }

        fabUpload = findViewById(R.id.fabUpload);
        userProfilePhotoLabel = findViewById(R.id.userProfilePhotoLabel);
        user_Profile_photo = findViewById(R.id.userProfilePhoto);

        File f = new File(MainActivityStaticContext.getFilesDir(), "bgImage.jpg");
        try {
            String filePath = f.getPath();
            bitmap = BitmapFactory.decodeFile(filePath);
            user_Profile_photo.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.d("log-ProfileUploadActivity-Exception", "onActivityResult setImage Exception" + e);
        }

        ProfileUploadProgressBar = findViewById(R.id.ProfileUploadProgressBar);
        initRetrofitClient();
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);

    }

    private final Emitter.Listener onUpdateUserDisplayName_return = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("log-onUpdateUserDisplayName_return", "call: onUpdateUserDisplayName_return enter ");
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ProfileUploadActivity.this, "Display name is updated", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "About is updated", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.d("log-ProfileUploadActivity", "onActivityResult r(resultCode == Activity.RESULT_OK start");
            if (requestCode == IMAGE_RESULT) {
                Log.d("log-ProfileUploadActivity", "onActivityResult requestCode == IMAGE_RESULT start");
                Log.d("log-ProfileUploadActivity", "onActivityResult requestCode == IMAGE_RESULT data" + data.getData());
                fabUpload.setVisibility(View.VISIBLE);
                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    Log.d("log-ProfileUploadActivity", "onActivityResult filepath not null : " + filePath);
                    bitmap = BitmapFactory.decodeFile(filePath);
                    user_Profile_photo.setImageBitmap(bitmap);
                    savePass = true;

////                    File imagepath = new File(this.getFilesDir(), "try.txt");
//                    File f = new File(HomePageWithContactActivityStaticContext.getFilesDir(), "bgImage.jpg");
//                    try {
//                        copyFile(new File(getRealPathFromURI(data.getData())), f);
//                        Log.d("log-ProfileUploadActivity", "onActivityResult copyFile successful");
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Log.d("log-ProfileUploadActivity-IOException", "onActivityResult copyFile IOException" + e);
//                  }


                }
            }
        }
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }
        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }

    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = {MediaStore.Video.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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

    private void multipartImageUpload() {

//        try {

//            ContentResolver contentResolver = getContentResolver();
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Massenger" + File.separator + "user" + File.separator + "profileImage");
//
////            Uri photoUri = Uri.withAppendedPath(
////                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
////                    cursor.getString(idColumnIndex));
//            Uri photoUri = Uri.withAppendedPath(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    String.valueOf(contentValues));
//
//            final double[] latLong;
//            photoUri = MediaStore.setRequireOriginal(photoUri);
//            InputStream stream = getContentResolver().openInputStream(photoUri);
//            if (stream != null) {
//                ExifInterface exifInterface = new ExifInterface(stream);
//                double[] returnedLatLong = exifInterface.getLatLong();
//
//                // If lat/long is null, fall back to the coordinates (0, 0).
//                latLong = returnedLatLong != null ? returnedLatLong : new double[2];
//
//                // Don't reuse the stream associated with
//                // the instance of "ExifInterface".
//                stream.close();
//            } else {
//                // Failed to load the stream, so return the coordinates (0, 0).
//                latLong = new double[2];
//            }


        List<MyImageClass> imageList = new ArrayList<MyImageClass>();

        Uri collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;


        String[] projection = new String[]{
                Images.Media._ID,
                Images.Media.DISPLAY_NAME,
                Images.Media.MIME_TYPE,
        };
        String selection = Images.Media.DISPLAY_NAME +
                " == BgContactMassegePage.png";
        String[] selectionArgs = {Images.Media.SIZE + " <= 10000000",
        };
        String sortOrder = MediaStore.Images.Media.DISPLAY_NAME + " ASC";

            try {
        Log.d("log-multipartImageUpload", "inner try block start");
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
        // Cache column indices.
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        int nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
        int durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
        int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
        int typeColumn = cursor.getColumnIndexOrThrow(Images.Media.MIME_TYPE);
        int pathColumn = cursor.getColumnIndexOrThrow(Images.Media.RELATIVE_PATH);
        Log.d("log-multipartImageUpload", "while loop start");
        while (cursor.moveToNext()) {
            // Get values of columns for a given video.
            long id = cursor.getLong(idColumn);
            String name = cursor.getString(nameColumn);
            String path = cursor.getString(pathColumn);
            String type = cursor.getString(typeColumn);
            Log.d("log-multipartImageUpload", "image id:" + id + ", name:" + name + ", type:" + type + ", path" + path);
            Uri contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            imageList.add(new MyImageClass(contentUri, name, path, type, id));
        }

            } catch (Exception e) {
                e.printStackTrace();
                ProfileUploadProgressBar.setVisibility(View.GONE);
                Log.d("log-multipartImageUpload", "Exception:" + e);
            }


//        } catch (Exception e) {
//            e.printStackTrace();
//            ProfileUploadProgressBar.setVisibility(View.GONE);
//            Log.d("log-multipartImageUpload", "Exception:" + e);
//        }


        // disable dismiss by tapping outside of the dialog
//        fabUpload.setEnabled(FALSE);
//        ProfileUploadProgressBar.setVisibility(View.VISIBLE);
//        try {
//            File filesDir = getApplicationContext().getFilesDir();
//            File file = new File(filesDir, "image" + ".png");
//
//            OutputStream os;
//            try {
//                os = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
//                os.flush();
//                os.close();
//            } catch (Exception e) {
//                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
//            }
//
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
//            byte[] bitmapdata = bos.toByteArray();
//
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(bitmapdata);
//            fos.flush();
//            fos.close();
//
//            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
//            MultipartBody.Part body = MultipartBody.Part.createFormData("myFile", file.getName(), reqFile);
//            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "myFile");
//
//            Call<ResponseBody> req = apiService.postImage(body, name);
//            req.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    progress.dismiss();
//                    Log.e("Upload", String.valueOf(response.body()));
//                    if (response.code() == 200) {
//                        ProfileUploadProgressBar.setVisibility(View.GONE);
//                        fabUpload.setVisibility(View.GONE);
//                        Toast.makeText(getApplicationContext(), "Image Uploaded successfully", Toast.LENGTH_LONG).show();
////                        textView.setText("Uploaded Successfully!");
////                        textView.setTextColor(Color.BLUE);
//                    } else {
//                        Toast.makeText(getApplicationContext(), response.code() + " ", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    ProfileUploadProgressBar.setVisibility(View.GONE);
////                    textView.setText("Uploaded Failed!");
////                    textView.setTextColor(Color.RED);
//                    fabUpload.setEnabled(TRUE);
//                    Toast.makeText(getApplicationContext(), "Failed to Upload , Please Try Again", Toast.LENGTH_SHORT).show();
//                    t.printStackTrace();
//                    Log.e("ERROR", t.toString());
//                }
//            });
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @SuppressLint("IntentReset")
    public void ProfilePhotoOnClick(View view) {
        Log.d("log-ProfileUploadActivity", "ProfilePhotoOnClick method start");
        fabUpload.setVisibility(View.GONE);
        fabUpload.setEnabled(TRUE);
        Log.d("log-ProfileUploadActivity", "ProfilePhotoOnClick method before startActivityForResult");
//        startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);

//        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, IMAGE_RESULT);


    }


    public void FabUploadOnClick(View view) {
        ProfileUploadProgressBar.setVisibility(View.VISIBLE);
        if (bitmap != null) {
            multipartImageUpload();
        } else {
            Toast.makeText(getApplicationContext(), "Bitmap is null. Try again", Toast.LENGTH_SHORT).show();
            ProfileUploadProgressBar.setVisibility(View.GONE);
        }

//        if (savePass) {
//            Thread t = new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                    OutputStream fos;
//                    try {
//                        ContentResolver contentResolver = getContentResolver();
//                        ContentValues contentValues = new ContentValues();
//                        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "BgContactMassegePage" + ".png");
//                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
////                        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.getRootDirectory().getPath() + File.separator + "Massenger" + File.separator + "bg");
//                        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Massenger" + File.separator + "user" + File.separator + "profileImage");
//                        Uri bgImgUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//                        fos = contentResolver.openOutputStream(bgImgUri);
//                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                        Log.d("log-ProfileUploadActivity", "onActivityResult copyFile successful");
//                        Objects.requireNonNull(fos);
//                        Toast.makeText(ProfileUploadActivity.this, "save successful locally", Toast.LENGTH_SHORT).show();
//                        savePass = false;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.d("log-ProfileUploadActivity-Exception", "onActivityResult copyFile Exception" + e);
//                        Toast.makeText(ProfileUploadActivity.this, "save failed to locally", Toast.LENGTH_SHORT).show();
//                        savePass = false;
//                    }
//
//                }
//            });
//            t.start();
//        }
//

    }

    public void UpdateUserDisplayName(View view) {
        String set_display_name = user_name_EditField.getText().toString();
        int success_status = massegeDao.updateDisplayUserName(set_display_name, user_login_id);
        Log.d("log-success_status", "UpdateUserDisplayName: status is " + success_status);
        socket.emit("updateUserDisplayName", user_login_id, set_display_name);
    }

    public void UpdateUserAboutInfo(View view) {
        String set_about_name = user_about_in_profile_page.getText().toString();
        if (set_about_name.equals("")) {
            Toast.makeText(this, "About Can not be empty", Toast.LENGTH_SHORT).show();
        } else {
//        int success_status = MassegeDao.updateAboutUserName(set_about_name, user_login_id);
//        Log.d("log-success_status", "UpdateUserDisplayName: status is " + success_status);
            socket.emit("updateUserAboutInfo", user_login_id, set_about_name);
        }
    }

    public void UpdateUserOnlineStatusPrivacy(View view) {
        int selectedId = radioGroupForSelectingPrivacy.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        radioButton = (RadioButton) findViewById(selectedId);
        Log.d("log-radio", "radio select : " + radioButton.getText());
//        int success_status = MassegeDao.updateAboutUserName(set_about_name, user_login_id);
//        Log.d("log-success_status", "UpdateUserDisplayName: status is " + success_status);
    }

    public void userProfilePhotoLabelOnClick(View view) {
        Log.d("log-ProfileUploadActivity", "userProfilePhotoLabelOnClick method start");
        writeFileOnInternalStorage(this, "try.txt", "testing");
        Log.d("log-ProfileUploadActivity", "userProfilePhotoLabelOnClick method end");
    }

    public void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody) {
        File file = new File(this.getFilesDir(), "try.txt");
        Log.d("log-ProfileUploadActivity", "writeFileOnInternalStorage method before if cond. dir:" + file);
        try (FileOutputStream fos = mcoContext.openFileOutput(sFileName, Context.MODE_PRIVATE)) {
            fos.write(sBody.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("log-ProfileUploadActivity-IOException", "writeFileOnInternalStorage method IOException:" + e);
        }

//        try {
//            Log.d("log-ProfileUploadActivity", "writeFileOnInternalStorage method try enter");
//            File gpxfile = new File(dir, sFileName);
//            FileWriter writer = new FileWriter(gpxfile);
//            writer.append(sBody);
//            writer.flush();
//            writer.close();
//            Log.d("log-ProfileUploadActivity", "writeFileOnInternalStorage method before try end");
//        } catch (Exception e){
//            Log.d("log-ProfileUploadActivity-Exception", "userProfilePhotoLabelOnClick method Exception:"+e);
//            e.printStackTrace();
//        }
    }

    public void button2OnClick(View view) {
//        pickMedia.launch(new PickVisualMediaRequest.Builder()
//                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
//                .build());


    }

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    public void LogOutFromApp(View view) {
        massegeDao.LogOutFromAppForThisUser(user_login_id);

        Intent mStartActivity = new Intent(ProfileUploadActivity.this, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(ProfileUploadActivity.this, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager mgr = (AlarmManager)ProfileUploadActivity.this.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, mPendingIntent);
//        System.exit(0);
        Runtime.getRuntime().exit(0);
        System.exit(0);
        this.finish();
    }
}
