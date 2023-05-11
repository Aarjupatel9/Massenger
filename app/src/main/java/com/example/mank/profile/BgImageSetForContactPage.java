package com.example.mank.profile;

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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.example.mank.R;

import java.io.File;
import java.io.OutputStream;
import java.util.Objects;

public class BgImageSetForContactPage extends Activity {


    private final static int IMAGE_RESULT = 230;

    private ImageView bgImageView;
    private ProgressBar progressBar;
    private Bitmap bitmap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bg_image_set_for_contact_page);
        bgImageView = findViewById(R.id.bgImageView);
        progressBar = findViewById(R.id.bgProgressBar);
        progressBar.setVisibility(View.GONE);

    }


    public void bgSelectImageButtonOnClick(View view) {
        Log.d("log-BgImageSetForContactPage", "ProfilePhotoOnClick method before startActivityForResult");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, IMAGE_RESULT);
    }

    public void bgSaveButtonOnClick(View view) {
        if (!blockSaveButton) {
            Log.d("log-BgImageSetForContactPage", "bgSaveButtonOnClick blockSaveButton" + blockSaveButton);
            if (passForSave) {
                progressBar.setVisibility(View.VISIBLE);
                blockSaveButton = true;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream fos;
                        try {
                            ContentResolver contentResolver = getApplicationContext().getContentResolver();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "BgContactMassegePage" + ".png");
                            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Massenger" + File.separator + "bg");
                            Uri bgImgUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                            fos = contentResolver.openOutputStream(bgImgUri);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            Log.d("log-BgImageSetForContactPage", "onActivityResult copyFile successful");
                            Objects.requireNonNull(fos);
                            Toast.makeText(BgImageSetForContactPage.this, "background image set successfully", Toast.LENGTH_SHORT).show();
                            blockSaveButton = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("log-BgImageSetForContactPage-Exception", "onActivityResult copyFile Exception" + e);
                            Toast.makeText(BgImageSetForContactPage.this, "problem while save image", Toast.LENGTH_SHORT).show();
                            blockSaveButton = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                });
                t.start();

            } else {
                Toast.makeText(this, "please select Image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "please wait while we save background", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean passForSave = false;
    private boolean blockSaveButton = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.d("log-BgImageSetForContactPage", "onActivityResult r(resultCode == Activity.RESULT_OK start");
            if (requestCode == IMAGE_RESULT) {
                Log.d("log-BgImageSetForContactPage", "onActivityResult requestCode == IMAGE_RESULT start");
                Log.d("log-BgImageSetForContactPage", "onActivityResult requestCode == IMAGE_RESULT data" + data.getData());
                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    Log.d("log-BgImageSetForContactPage", "onActivityResult filepath not null : " + filePath);
                    bitmap = BitmapFactory.decodeFile(filePath);
                    bgImageView.setImageBitmap(bitmap);
                    passForSave = true;
                }
            }
        }
    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
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

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


}
