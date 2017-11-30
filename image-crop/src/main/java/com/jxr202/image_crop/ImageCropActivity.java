package com.jxr202.image_crop;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;

/**
 * Created by jxr202 on 2017/11/29
 */
public class ImageCropActivity extends Activity implements View.OnClickListener{


    private static final String TAG = "jxr";
    /** 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    /** 请求相册 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    /** 请求拍照 **/
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    /** 裁剪返回 **/
    private static final int CODE_RESULT_REQUEST = 0xa2;
    /** 裁剪后图片的宽(X)和高(Y) */
    private int outputX, outputY;
    /** 裁剪比例 */
    private int aspectX, aspectY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_image_picker);

        findViewById(R.id.takePhoto).setOnClickListener(this);
        findViewById(R.id.choosePhoto).setOnClickListener(this);
        findViewById(R.id.cancelPhoto).setOnClickListener(this);

        Window window = getWindow();

        if (window != null) {

            window.setGravity(Gravity.BOTTOM);
            //获得窗体的属性
            WindowManager.LayoutParams lp = window.getAttributes();
            //设置Dialog距离底部的距离
            lp.y = 20;
            //将属性设置给窗
            window.setAttributes(lp);
        }

        Bundle bundle = getIntent().getBundleExtra("bundle");

        outputX = bundle.getInt("outputX", 40);
        outputY = bundle.getInt("outputY", 30);
        aspectX = bundle.getInt("aspectX", 4);
        aspectY = bundle.getInt("aspectY", 3);

        Log.i(TAG, "onCreate -> outputX: " + outputX + ", outputY: " + outputY + ", aspectX: " + aspectX + ", aspectY: " + aspectY);
    }

    /**
     * 从本地相册选取图片作为头像
     */
    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent();
        // 设置文件类型
        intentFromGallery.setType("image/*");//选择图片
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        //你需要使用系统提供的startActivityForResult(Intent intent,int requestCode)方法打开新的Activity
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }

    /**
     * 启动手机相机拍摄照片作为头像
     */
    private void choseHeadImageFromCameraCapture() {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 判断存储卡是否可用，存储照片文件
        if (hasSdcard()) {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
        }

        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        Log.i(TAG, "onActivityResult -> requestCode: " + requestCode + ", resultCode: " + resultCode + ", intent: " + intent);

        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {//取消
            Toast.makeText(getApplication(), "取消", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED, intent);
            finish();
            return;
        }

        switch (requestCode) {

            case CODE_GALLERY_REQUEST: {
                //本地相机，裁剪
                cropRawPhoto(intent.getData());
                break;
            }
            case CODE_CAMERA_REQUEST: {
                //照相
                if (hasSdcard()) {
                    File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                    cropRawPhoto(Uri.fromFile(tempFile));
                } else {
                    Toast.makeText(getApplication(), "没有SDCard!", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case CODE_RESULT_REQUEST: {
                //裁剪完成
                if (intent != null) {
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            }
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setDataAndType(uri, "image/*");

        //把裁剪的数据填入里面

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }


    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {

        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.takePhoto) {
            choseHeadImageFromCameraCapture();
        } else if (i == R.id.choosePhoto) {
            choseHeadImageFromGallery();
        } else if (i == R.id.cancelPhoto) {
            Toast.makeText(getApplication(), "取消", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED, null);
            finish();
        }
    }
}
