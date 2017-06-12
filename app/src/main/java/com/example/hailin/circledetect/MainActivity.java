package com.example.hailin.circledetect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.CV_HOUGH_GRADIENT;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {


    private Mat                    mRgba;
    private Mat mGray;

    private CameraBridgeViewBase mOpenCvCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OpenCVLoader.initDebug();

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.cameraView);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setMaxFrameSize(640,480);
        mOpenCvCameraView.enableView();
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }


    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        /// Reduce the noise so we avoid false circle detection
        Imgproc.GaussianBlur( mGray, mGray, new Size(5,5), 2, 2 );

        Mat circles = new Mat();

        /// Apply the Hough Transform to find the circles
        Imgproc.HoughCircles( mGray, circles, CV_HOUGH_GRADIENT, 1, mGray.rows()/20, 100, 60, 20, 100 );

        Log.d("circle","circle detect num"+circles.cols());

        /// Draw the circles detected
        for( int i = 0; i < circles.cols(); i++ )
        {
            double x[] = circles.get(0,i);
            Imgproc.circle(mRgba,new Point(x[0],x[1]),(int)x[2],new Scalar(255,0,0));
        }
        return mRgba;
    }


}
