package com.team1.syspro.expdatemanageapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private int REQUEST_CODE_PERMISSIONS= 0x11;
    private boolean isCameraPermission;
    private boolean isStragePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sendButton = findViewById(R.id.goListButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Intent intent = new Intent(getApplication(),ProductManageActivity.class);
                                              startActivity(intent);
                                          }
                                      }
        );

        Button tempButton = findViewById(R.id.TempButton);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCameraPermission = false;
                isStragePermission = false;
                if (Build.VERSION.SDK_INT >= 23) {
                    // camera permission
                    if ((PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                            (PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                        Log.d("my-debug", "API >= 23, request camera and storage permission .");
                        requestPermissions();
                    }else{
                        isCameraPermission = true;
                        isStragePermission = true;
                        Log.d("my-debug", "API >= 23, camera and storage permissions: GRANTED.");
                        Intent intent = new Intent(getApplication(), CameraActivity.class);
                        intent.putExtra("isStoragePermission",isStragePermission);
                        startActivity(intent);
                    }
                } else {
                    isCameraPermission = true;
                    isStragePermission = true;
                    //startSetting();
                    Log.d("my-debug", "API <= 23 .");
                    //API 23以下なので許可なく起動
                    Intent intent = new Intent(getApplication(), CameraActivity.class);
                    intent.putExtra("isStoragePermission",isStragePermission);
                    startActivity(intent);
                }
            }
        });
    }


    void requestPermissions() {
        // 権限を要求する
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                REQUEST_CODE_PERMISSIONS);
    }

    // userがバーミッションをどうしたかが帰ってくる
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.length == 2) {
            for (int i=0;i<2;i++){
                if(permissions[i].equals(Manifest.permission.CAMERA)){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        Log.d("my-debug","Request Camera Permission : DENIED");
                        new AlertDialog.Builder(this)
                                .setTitle("パーミッション取得エラー")
                                .setMessage("カメラは起動しません")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // サンプルのため、今回はもう一度操作をはさんでいますが
                                        // ここでrequestCameraPermissionメソッドの実行でもよい
                                    }
                                })
                                .create()
                                .show();
                    }else{
                        isCameraPermission = true;
                    }
                }
                if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        Log.d("my-debug","Request Storage Permission : DENIED");
                        new AlertDialog.Builder(this)
                                .setTitle("パーミッション取得エラー")
                                .setMessage("ストレージの書き込みはできません")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // サンプルのため、今回はもう一度操作をはさんでいますが
                                        // ここでrequestCameraPermissionメソッドの実行でもよい
                                    }
                                })
                                .create()
                                .show();
                    }else{
                        isStragePermission = true;
                    }
                }
            }
            // camera permissionがない場合はCameraActivityを起動しない．
            if(isCameraPermission) {
                Intent intent = new Intent(getApplication(), CameraActivity.class);
                intent.putExtra("isStoragePermission",isStragePermission);
                startActivity(intent);
            }
            //startSetting();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
