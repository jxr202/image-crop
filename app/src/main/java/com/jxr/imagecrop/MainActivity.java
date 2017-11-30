package com.jxr.imagecrop;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.jxr202.image_crop.ImageCropActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.avatar)
    ImageView avatar;

    private static final String TAG = "jxr";
    @BindView(R.id.button)
    Button button;

    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //initImagePicker();
        //ButterKnife.bind(this);
    }

    /**
     * 显示框框
     */
    private void initImagePicker() {
        Log.i(TAG, "initImagePicker..");
        //填充对话框的布局
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_image_picker, null);

        ButterKnife.bind(this, dialogView);

        /*takePhoto = ButterKnife.findById(dialogView, R.id.takePhoto);
        choosePhoto = ButterKnife.findById(dialogView, R.id.choosePhoto);
        cancelPhoto = ButterKnife.findById(dialogView, R.id.cancelPhoto);*/
        //初始化Dialog
        mDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        //将布局设置给Dialog
        mDialog.setContentView(dialogView);
        //获取当前Activity所在的窗体
        Window dialogWindow = mDialog.getWindow();
        //设置Dialog从窗体底部弹出
        if (dialogWindow != null) {

            dialogWindow.setGravity(Gravity.BOTTOM);
            //获得窗体的属性
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            //设置Dialog距离底部的距离
            lp.y = 20;
            //将属性设置给窗体
            dialogWindow.setAttributes(lp);
        }
    }

    private void showImagePicker() {

        if (mDialog != null) {
            mDialog.show();
        }
    }

    private void hideImagePicker() {

        if (mDialog != null) {
            mDialog.show();
        }
    }

    /**
     * 进入选取头像界面
     *
     * @param type 类型： 0：拍照， 1：从相册选择
     */
    private void startToImagePicker(int type) {

        hideImagePicker();

        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putInt("outputX", 80);
        bundle.putInt("outputY", 160);
        bundle.putInt("aspectX", 1);
        bundle.putInt("aspectY", 2);
        Intent intent = new Intent(this, ImageCropActivity.class);
        intent.putExtra("bundle", bundle);

        startActivityForResult(intent, 111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Log.i(TAG, "头像选取成功");
                Bitmap photo = extras.getParcelable("data");
                avatar.setImageBitmap(photo);
            }
        } else {
            Log.i(TAG, "头像选取失败");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.button)
    public void onViewClicked() {

        startToImagePicker(1);

    }


    /*@OnClick({R.id.button, R.id.takePhoto, R.id.choosePhoto, R.id.cancelPhoto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button: {
                showImagePicker();
                break;
            }
            case R.id.takePhoto: {
                startToImagePicker(0);
                break;
            }
            case R.id.choosePhoto: {
                startToImagePicker(1);
                break;
            }
            case R.id.cancelPhoto: {
                hideImagePicker();
                break;
            }
        }
    }*/

}
