package com.ocvcamprev.castellinc.opencvcamerapreview;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private final static String TAG = "OpenCV:MainActivity";
    private JavaCameraView mOpenCVCameraView;
    private Mat mRgba;
    private Mat mGray;
    private Mat mIntermediateMat;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCVCameraView.enableView();
                } break;
                default: {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set Activity layout for Camera Preview
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        //Load Camera Surface
        mOpenCVCameraView = (JavaCameraView) findViewById(R.id.BackCameraView);
        mOpenCVCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCVCameraView.setCvCameraViewListener(this);

        //Declare Clicking Button parameters
        ImageButton captureButton = (ImageButton)findViewById(R.id.imageButton);
        //final MediaPlayer mp = MediaPlayer.create(this, R.raw.click);

        //Capture Image by Button Click
        captureButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //mp.start(); //Play Capture Sound
                SaveImage ();
            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if (mOpenCVCameraView != null)
            mOpenCVCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCVCameraView != null)
            mOpenCVCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mGray = new Mat();
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
        mIntermediateMat.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

//        Core.flip(inputFrame.rgba(),mRgba,1);
//        Core.flip(inputFrame.gray(),mGray,1);
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        /*int maxValue = 255;
        int blockSize = 5;
        int meanOffset = 4;
        Imgproc.adaptiveThreshold(inputFrame.gray(), mIntermediateMat, maxValue, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, blockSize, meanOffset);
        Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);*/
        return	mRgba;
        //return	mGray;
    }

    public void SaveImage () {
        Mat mIntermediateMat = new Mat();
        Imgproc.cvtColor(mRgba, mIntermediateMat, Imgproc.COLOR_RGBA2BGR, 3);

        //File path = new File(Environment.getExternalStorageDirectory() + "/CVIMG/");
        //path.mkdirs();
        //File file = new File(path, "image.png");


        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).toString() + ".jpg";
        File file = new File(path, fileName);

        Boolean bool = null;
        fileName = file.toString();
        bool = Imgcodecs.imwrite(fileName, mIntermediateMat);

        if (bool == true)
            Log.d(TAG, "SUCCESS writing image to external storage");
        else
            Log.d(TAG, "Fail writing image to external storage");

    }
}

/*//Save Image by Button Click
        File sdRoot = Environment.getExternalStorageDirectory();
        String dir = "/DCIM/CVIMG/";
        String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).toString() + ".jpg";
        File mkDir = new File(sdRoot, dir);
        //mkDir.mkdirs();
        File pictureFile = new File(sdRoot, dir + fileName);
        Imgcodecs.imwrite()

        Imgcodecs.imwrite(sdRoot.toString() + dir + fileName,mRgba);*/