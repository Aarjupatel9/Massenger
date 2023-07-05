package com.example.mank.profile;

import static com.example.mank.MainActivity.user_login_id;
import static java.lang.Boolean.TRUE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.example.mank.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class BgImageSetForContactPage extends Activity {

    private final static int IMAGE_RESULT = 230;
    private ImageView bgImageView;
    private ProgressBar progressBar;
    private Bitmap bitmap;
    private byte[] imageData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bg_image_set_for_contact_page);
        bgImageView = findViewById(R.id.bgImageView);
        progressBar = findViewById(R.id.ABGSProgressBar);
        progressBar.setVisibility(View.GONE);
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bgImageView.setImageBitmap(selfImage);
                        }
                    });
                }
            }
        });
        ti.start();
    }

    @SuppressLint("IntentReset")
    public void bgSelectImageButtonOnClick(View view) {
        Log.d("log-BgImageSetForContactPage", "bgSelectImageButtonOnClick method start");

        Log.d("log-BgImageSetForContactPage", "bgSelectImageButtonOnClick method before startActivityForResult");

        @SuppressLint("IntentReset") Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, IMAGE_RESULT);

    }

    public void bgSaveButtonOnClick(View view) {
        if (!blockSaveButton) {
            Log.d("log-BgImageSetForContactPage", "bgSaveButtonOnClick blockSaveButton" + blockSaveButton);
            if (passForSave) {
//                Bitmap
                progressBar.setVisibility(View.VISIBLE);
                Thread ts = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveImageToInternalStorage(bitmap);
                    }
                });
                ts.start();

            } else {
                Toast.makeText(this, "please select Image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "please wait while we save background", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToInternalStorage(Bitmap bitmapImage) {
        // Get the application-specific directory path
        File directory = new File(Environment.getExternalStorageDirectory(), "Android/media/com.massenger.mank.main/bg/bgImages");

        if (!directory.exists()) {
            boolean x = directory.mkdirs();
            if (!x) {
                Log.d("log-saveImageToInternalStorage", "Saved failed dir creation failed");
                return;
            }
        }

        // Create the file path
        File imagePath = new File(directory, user_login_id + ".png");

        // Save the bitmap image to the file
        try (OutputStream outputStream = new FileOutputStream(imagePath)) {
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            Log.d("log-saveImageToInternalStorage", "Saved image path: " + imagePath.getAbsolutePath());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BgImageSetForContactPage.this, "background image is updated", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("log-saveImageToInternalStorage", "Image Save failed " + e.toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BgImageSetForContactPage.this, "background image is update failed", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }


    private boolean passForSave = false;
    private boolean blockSaveButton = false;
    private static final int TARGET_RESOLUTION = 1024;
    private static final int JPEG_QUALITY = 100;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.d("log-BgImageSetForContactPage", "onActivityResult r(resultCode == Activity.RESULT_OK start");
            if (requestCode == IMAGE_RESULT) {

                progressBar.setVisibility(View.VISIBLE);
                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    Log.d("log-UserProfileActivity", "onActivityResult filepath not null : " + filePath);
                    Thread Ti = new Thread(new Runnable() {
                        @Override
                        public void run() {

//                            bitmap = BitmapFactory.decodeFile(filePath);
                            passForSave = true;
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(filePath, options);
                            int imageWidth = options.outWidth;
                            int imageHeight = options.outHeight;
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                            int TARGET_RESOLUTION_X = displayMetrics.widthPixels;
                            int TARGET_RESOLUTION_Y = displayMetrics.heightPixels;

                            int scaleFactor = 1;
                            if (imageWidth > TARGET_RESOLUTION_X || imageHeight > TARGET_RESOLUTION_Y) {
                                int scaleX = imageWidth / TARGET_RESOLUTION_X;
                                int scaleY = imageHeight / TARGET_RESOLUTION_Y;
                                scaleFactor = Math.max(scaleX, scaleY);
                            }


                            options.inJustDecodeBounds = false;
                            options.inSampleSize = scaleFactor;

                            bitmap = BitmapFactory.decodeFile(filePath, options);

                            if (bitmap.getWidth() != TARGET_RESOLUTION_X || bitmap.getHeight() != TARGET_RESOLUTION_Y) {
                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, TARGET_RESOLUTION_X, TARGET_RESOLUTION_Y, true);
                                bitmap.recycle();
                                bitmap = scaledBitmap;
                            }

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, JPEG_QUALITY, stream);

                            imageData = stream.toByteArray();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    bgImageView.setImageBitmap(bitmap);
                                    progressBar.setVisibility(View.GONE);
                                }
                            });

                        }
                    });
                    Ti.start();
                }

            }
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

    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;
        if (isCamera) {
            return getCaptureImageOutputUri().getPath();
        } else {
            return getPathFromURI(data.getData());
        }
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


}
