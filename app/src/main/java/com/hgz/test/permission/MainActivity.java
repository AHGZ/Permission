package com.hgz.test.permission;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String CAMERA_PERMISSION= Manifest.permission.CAMERA;
    private static final int CAMERA_REQUESTCODE = 0X100;
    private static final int GO_TO_SETTING_REQUEST_CODE = 0X101;
    private static final String TAG = "permission_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openCamera(View view) {
        //检测是否授权
        if (ContextCompat.checkSelfPermission(this,CAMERA_PERMISSION)== PackageManager.PERMISSION_GRANTED){
            startCamera();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_REQUESTCODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUESTCODE:
                if (permissions[0].equals(CAMERA_PERMISSION)){
                    if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                        Log.e(TAG, "===========权限回调---用户同意了");
                        startCamera();
                    }else{
                        Log.e(TAG, "===========权限回调---用户拒绝了");
                        // 如 果系统不再解释权限，我们去解释权限 （用户勾选了不再询问，系统就不会再解释权限了）
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,CAMERA_PERMISSION)){
                            //返回true 因为系统刚刚有权限弹窗，所以不用解释了，直接告诉用户如何开启权限
                            Log.e(TAG,"=========== shouldShowRequestPermissionRationale 返回值为 true");
                            showTipGoSetting();
                        }else{
                            //返回false ，用户勾选了  不再询问，之后系统也不会再弹出系统权限弹框，所以我们自己弹框解释
                            Log.e(TAG,"=========== shouldShowRequestPermissionRationale 返回值为 false");
                            showTipExplainPermission();
                        }
                    }
                }
                break;
        }
    }
    //对话框 -- 给用户解释需要的权限
    private void showTipExplainPermission() {
        new AlertDialog.Builder(this)
                .setTitle("说明")
                .setMessage("需要设置权限来进行拍照")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //告诉用户怎么去打开权限
                        showTipGoSetting();
                    }
                })
                .setNegativeButton("取消",null)
                .show();
    }
    //对话框 -- 告诉用户怎么去打开权限
    private void showTipGoSetting() {
        new AlertDialog.Builder(this)
                .setTitle("需要打开相机权限")
                .setMessage("在设置-权限中去打开相机权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //告诉用户怎么去打开权限
                        goToSetting();
                    }
                })
                .setNegativeButton("取消",null)
                .show();
    }
    //跳转到设置权限页面
    private void goToSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent,GO_TO_SETTING_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case GO_TO_SETTING_REQUEST_CODE:
                if (ContextCompat.checkSelfPermission(this,CAMERA_PERMISSION)==PackageManager.PERMISSION_GRANTED){
                    Log.e(TAG, "===========设置页面返回之后-再次检查权限---用户已经拥有相机这个权限了");
                    startCamera();
                }else{
                    Log.e(TAG, "===========设置页面返回之后-再次检查权限---用户没有开启这个权限，在这不用再去请求权限了");
                }
                break;
        }
    }
    // 打开相机
    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,0);
    }
}
