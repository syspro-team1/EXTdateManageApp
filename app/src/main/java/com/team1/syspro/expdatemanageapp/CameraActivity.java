package com.team1.syspro.expdatemanageapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.support.v7.app.ActionBar;

public class CameraActivity extends AppCompatActivity {
    private Camera myCamera;
    private SurfaceHolder.Callback mSurfaceListener =
            new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    myCamera = Camera.open();
                    try{
                        myCamera.setPreviewDisplay(holder);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    Camera.Parameters parameters = myCamera.getParameters();
                    parameters.setPreviewSize(width,height);
                    myCamera.setParameters(parameters);
                    myCamera.startPreview();
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    myCamera.release();
                    myCamera = null;
                }
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        SurfaceView mySurfaceView = (SurfaceView)findViewById(R.id.surface_view);
        SurfaceHolder holder = mySurfaceView.getHolder();
        holder.addCallback(mSurfaceListener);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
}
