package com.spasi.android.controller.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.spasi.android.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("ALL")
public class CameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private final String TAG = "CameraActivity";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Camera.PictureCallback mPicture = null;
    private static Camera c = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("Spasi Android | CAMERA");

        mPicture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (pictureFile == null) {
                    Log.d(TAG, "Error creating media file, check storage permissions ");
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file " + e.getMessage());
                }
            }
        };

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // Using icon
        ImageView m = (ImageView) findViewById(R.id.iconMiddleCapture);
        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Real Capture",Toast.LENGTH_LONG).show();
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.captureIcon:
                // Using toolbar
                Toast.makeText(this,"OK",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public boolean checkCameraHardware(Context context) {
        /** Check if this device has a camera */
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
                return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the CameraHelper object. */
    @SuppressWarnings("deprecation")
    public Camera getCameraInstance(){
        try {
            if(null == c)
                c = Camera.open();
        }
        catch (Exception e){
            // CameraHelper is not available (in use or does not exist)
            Log.d(TAG,e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }
}
