# image-crop

# 头像获取库

# 1.init：

①.在Project中的build.gradle添加：

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }	//添加这一句
		}
	}
	
②.在app中的build.gradle添加：

	dependencies {
	        compile 'com.github.jxr202:image-crop:v1.0.3'	//添加这一句.
	}
	
	
# 2.用法 :

    /**
     * 进入选取头像界面
     */
    private void startToImagePicker() {
        
        Bundle bundle = new Bundle();
        bundle.putInt("outputX", 80);   //宽度
        bundle.putInt("outputY", 160);  //高度
        bundle.putInt("aspectX", 1);    //比例
        bundle.putInt("aspectY", 2);    //比例
        Intent intent = new Intent("android.intent.action.ImageCrop");
        intent.putExtra("bundle", bundle);

        startActivityForResult(intent, 111);
    }
	
# 3.回调：
	注意，你必须要对data是否为空做判断，data为null即没有选择图片直接点了返回

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
	
	
	
	