package com.team1.syspro.expdatemanageapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.FileOutputStream;

public class CameraActivity extends AppCompatActivity {
    private boolean isStragePermission;
    private Camera m_Camera;
    private SurfaceView m_SurfaceView;
    private SurfaceHolder.Callback m_SurfaceListener =
            new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                        m_Camera = Camera.open();
                        try {
                            m_Camera.setPreviewDisplay(holder);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                        Camera.Parameters parameters = m_Camera.getParameters();
                        //parameters.setRotation(180);

                        int cameraId = 0;
                        android.hardware.Camera.CameraInfo info =
                                new android.hardware.Camera.CameraInfo();
                        android.hardware.Camera.getCameraInfo(cameraId, info);
                        int rotation = getWindowManager().getDefaultDisplay()
                                .getRotation();
                        int degrees = 0;
                        switch (rotation) {
                            case Surface.ROTATION_0:
                                degrees = 0;
                                break;
                            case Surface.ROTATION_90:
                                degrees = 90;
                                break;
                            case Surface.ROTATION_180:
                                degrees = 180;
                                break;
                            case Surface.ROTATION_270:
                                degrees = 270;
                                break;
                        }

                        int result;
                        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            result = (info.orientation + degrees) % 360;
                            result = (360 - result) % 360;  // compensate the mirror
                        } else {  // back-facing
                            result = (info.orientation - degrees + 360) % 360;
                        }
                        m_Camera.setDisplayOrientation(result);
                        parameters.setPreviewSize(height, width);// here w h are reversed

                        m_Camera.setParameters(parameters);
                        m_Camera.startPreview();
                    }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                        m_Camera.release();
                        m_Camera = null;

                }
            };
    // シャッターが押されたときに呼ばれるコールバック
    private Camera.ShutterCallback m_shutterListener = new Camera.ShutterCallback() {
        public void onShutter() {
        }
    };

    // JPEGイメージ生成後に呼ばれるコールバック
    private Camera.PictureCallback m_pictureListener = new Camera.PictureCallback() {
        // データ生成完了
        public void onPictureTaken(byte[] data, Camera camera) {
            // SDカードにJPEGデータを保存する
            if (data != null) {
                FileOutputStream fos = null;
                try {
                    if(isStragePermission) {
                        String filepath = Environment.getExternalStorageDirectory().getPath() + "/camera_test.jpg";
                        fos = new FileOutputStream(filepath);
                        Log.d("my-debug", filepath);
                        fos.write(data);
                        fos.close();
                        // 保存した画像をアンドロイドのデータベースへ登録(一時的な処理)
                        registerDatabase(filepath);
                    }
                } catch (Exception e) {
                    Log.e("my-debug", "message", e);
                }

                // プレビューを再開する
                camera.startPreview();
            }
        }
    };

    // 画面タッチ時のコールバック
    private View.OnTouchListener m_ontouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (m_Camera != null) {
                    // 撮影実行
                    Log.d("my-debug", "camera activity: take Picture call");
                    m_Camera.takePicture(m_shutterListener, null, m_pictureListener);
                }
            }
            return false;
        }
    };

    // アンドロイドのデータベースへ登録する
    private void registerDatabase(String file) {
        ContentValues contentValues = new ContentValues();
        ContentResolver contentResolver = CameraActivity.this.getContentResolver();
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put("_data", file);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Log.d("my-debug", "camera- onCreate");
        isStragePermission =  getIntent().getBooleanExtra("isStoragePermission",false);

        m_SurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        SurfaceHolder holder = m_SurfaceView.getHolder();
        // surfaceholderにsurfacelistenerを設定
        holder.addCallback(m_SurfaceListener);
        View view = findViewById(R.id.content);
        // touch時の挙動を設定
        m_SurfaceView.setOnTouchListener(m_ontouchListener);

    }


    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        //Fragmentの場合はgetContext().getPackageName()
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
}