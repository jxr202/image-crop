package com.jxr.imagecropexample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.jxr202.image_crop.ImageCropActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "jxr";

    @BindView(R.id.avatar)
    ImageView avatar;

    @BindView(R.id.button)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    /**
     * 进入选取头像界面
     */
    private void startToImagePicker() {

        Bundle bundle = new Bundle();
        bundle.putInt("outputX", 80);
        bundle.putInt("outputY", 160);
        bundle.putInt("aspectX", 1);
        bundle.putInt("aspectY", 2);
        Intent intent = new Intent("android.intent.action.ImageCrop");
        intent.putExtra("bundle", bundle);

        startActivityForResult(intent, 111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Bundle extras = data.getExtras();
            Log.i(TAG, "extras: " + extras);
            if (extras != null) {
                Log.i(TAG, "头像选取成功");
                String imagePath = data.getStringExtra(ImageCropActivity.CROP_IMAGE_PATH);
                Log.i(TAG, "imagePath: " + imagePath);
                if (!TextUtils.isEmpty(imagePath)) {
                    Log.i(TAG, "获取头像路径成功");
                    Bitmap pathBitmap = BitmapFactory.decodeFile(imagePath);
                    avatar.setImageBitmap(pathBitmap);
                }
            }
        } else {
            Log.i(TAG, "头像选取失败");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.button)
    public void onViewClicked() {

        startToImagePicker();

    }

}
