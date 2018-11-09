package com.jxr202.image_crop;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxr202 on 2017/11/29
 */
public class ImageCropActivity extends Activity implements View.OnClickListener {


    private static final String TAG = "jxr";
    /**
     * 对外的剪裁图片保存路径
     */
    public static final String CROP_IMAGE_PATH = "crop_image_path";
    /**
     * 头像文件
     */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    /**
     * 请求相册
     */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    /**
     * 请求拍照
     **/
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    /**
     * 裁剪返回
     **/
    private static final int CODE_RESULT_REQUEST = 0xa2;
    /**
     * 请求权限
     **/
    private static final int CODE_REQUEST_PERMISSIONS = 0xa3;
    /**
     * 再次请求权限
     **/
    private static final int CODE_REQUEST_SETTING = 0xa4;

    /**
     * 裁剪后图片的宽(X)和高(Y)
     */
    private int outputX, outputY;
    /**
     * 裁剪比例
     */
    private int aspectX, aspectY;
    /**
     * 相机权限
     */
    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /**
     * 没有申请的权限，暂时存放在这
     */
    private List<String> mPermissionList = new ArrayList<>();
    /**
     * 裁剪后的文件
     */
    private File mHeadCacheFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_image_picker);
        setListeners();
        setDialogStyle();
        setPictureParams();
        requestPermissions();
    }

    private void setListeners() {
        findViewById(R.id.takePhoto).setOnClickListener(this);
        findViewById(R.id.choosePhoto).setOnClickListener(this);
        findViewById(R.id.cancelPhoto).setOnClickListener(this);
    }

    private void setDialogStyle() {
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes(); //获得窗体的属性
            lp.y = 20;   //设置Dialog距离底部的距离
            window.setAttributes(lp);   //将属性设置给窗
        }
    }

    private void setPictureParams() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        outputX = bundle.getInt("outputX", 40);
        outputY = bundle.getInt("outputY", 30);
        aspectX = bundle.getInt("aspectX", 4);
        aspectY = bundle.getInt("aspectY", 3);
        Log.i(TAG, "onCreate -> outputX: " + outputX + ", outputY: " + outputY + ", aspectX: " + aspectX + ", aspectY: " + aspectY);
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionList.clear();
            for (String permission : permissions) {
                int permissionValue = ContextCompat.checkSelfPermission(this, permission);
                Log.i(TAG, "permission: " + permission + " is " + permissionValue);
                if (permissionValue != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permission);
                }
            }
            if (mPermissionList.size() > 0) {
                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(this, permissions, CODE_REQUEST_PERMISSIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODE_REQUEST_PERMISSIONS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (int i = 0; i < grantResults.length; i ++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                        if (!shouldShowRequestPermissionRationale(permissions[i])) {
                            showSettingPermissionDialog();
                            return;
                        } else {
                            Toast.makeText(this, R.string.image_crop_6, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_CANCELED, null);
                            finish();
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * 再次警告用户权限框
     */
    private void showSettingPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.image_crop_4)
                .setMessage(R.string.image_crop_5)
                .setPositiveButton(R.string.image_crop_7, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToSetting();
                    }
                })
                .setNegativeButton(R.string.image_crop_3, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ImageCropActivity.this, R.string.image_crop_6, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_CANCELED, null);
                        finish();
                    }
                })
                .setCancelable(false).show();
    }

    /**
     * 从本地相册选取图片作为头像
     */
    private void choseHeadImageFromGallery() {

        // ACTION_GET_CONTENT 让用户自己选择使用哪里的图片，如最近、下载或是相册等等
        /*Intent intentFromGallery = new Intent();
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        intentFromGallery.setType("image*//*");//选择图片*/

        // ACTION_PICK 则是直接使用相册中的图片
        Intent intentPhoto = new Intent(Intent.ACTION_PICK, null);
        intentPhoto.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        // 发起选图请求
        startActivityForResult(intentPhoto, CODE_GALLERY_REQUEST);
    }

    /**
     * 启动手机相机拍摄照片作为头像
     */
    private void choseHeadImageFromCameraCapture() {
        Log.i(TAG, "choseHeadImageFromCameraCapture.. ");
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (hasSdcard()) {  // 判断存储卡是否可用，存储照片文件
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
        }
        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
    }

    /**
     * HeroBand拍照方式
     */
    private void startCameraByHero() {
        File file = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
        //Android N :FileUriExposedException solved
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        Uri uri1 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri1);
        startActivityForResult(intent, CODE_CAMERA_REQUEST);
    }

    /**
     * 跳转到当前应用的设置界面
     */
    private void goToSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, CODE_REQUEST_SETTING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        Log.i(TAG, "onActivityResult -> requestCode: " + requestCode + ", resultCode: " + resultCode + ", intent: " + intent);

        if (requestCode == CODE_REQUEST_SETTING) {  //用户自己打开权限结果
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (String permission : permissions) {
                    int permissionValue = ContextCompat.checkSelfPermission(this, permission);
                    Log.i(TAG, "permission: " + permission + "is " + permissionValue);
                    if (permissionValue != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, R.string.image_crop_6, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_CANCELED, null);
                        finish();
                        return;
                    }
                }
            }
            return; //全部权限都同意了，不作任何操作
        }

        if (resultCode == RESULT_CANCELED) {    // 用户没有进行有效的设置操作，返回
            Toast.makeText(getApplication(), R.string.image_crop_3, Toast.LENGTH_LONG).show();
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
                    intent.putExtra(CROP_IMAGE_PATH, mHeadCacheFile.getAbsolutePath());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            }
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }



    private void createHeadCache() {
        String headCachePath = Environment.getExternalStorageDirectory().getPath() + "/HeadCache";
        File parent = new File(headCachePath);
        if (!parent.exists()) {
            boolean makeParent = parent.mkdirs();
            Log.i(TAG, "makeParent: " + makeParent);
        }
        mHeadCacheFile = new File(parent, "headCache.png");
        if (!mHeadCacheFile.exists()) {
            try {
                boolean makeHeadCache = mHeadCacheFile.createNewFile();
                Log.i(TAG, "makeHeadCache: " + makeHeadCache);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {

        createHeadCache();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);

        Log.i(TAG, "mHeadCacheFile: " + mHeadCacheFile.getAbsolutePath());

        Uri uriPath = Uri.parse("file://" + mHeadCacheFile.getAbsolutePath());
        //将裁剪好的图输出到所建文件中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //注意：此处应设置return-data为false，如果设置为true，是直接返回bitmap格式的数据，耗费内存。设置为false，然后，设置裁剪完之后保存的路径，即：intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath);
        //intent.putExtra("return-data", true);
        intent.putExtra("return-data", false);

        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    /**
     * 当前版本
     * @return v
     */
    public int getTargetSdkVersion() {
        int targetSdkVersion = 0;
        try {
            final PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return targetSdkVersion;
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
//            Toast.makeText(getApplication(), R.string.image_crop_3, Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED, null);
            finish();
        }
    }
}
