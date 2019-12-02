package wen.doodledoodle;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    public static int penColor = Color.RED;
    public static int strokeWidth = 10;
    public static int eraserWidth = 10;

    private static final int REQUEST_CODE_SAVE_IMG = 10;
    private static final String TAG = "MainActivity";

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
    }

    // 菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //菜单项
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        DoodleBoard.paint.setXfermode(null);
        DoodleBoard.paint.setStrokeWidth(strokeWidth);

        switch (item.getItemId()) {

            case R.id.pen:
                DoodleBoard.paint.setColor(penColor);
                break;

            case R.id.eraser:
                DoodleBoard.eraser();
                break;

            case R.id.clear:
                DoodleBoard.clear();
                break;

            case R.id.save:
                requestPermission();
                break;

            case R.id.red:
                penColor = Color.RED;
                DoodleBoard.paint.setColor(penColor);
                break;

            case R.id.green:
                penColor = Color.GREEN;
                DoodleBoard.paint.setColor(penColor);
                break;

            case R.id.blue:
                penColor = Color.BLUE;
                DoodleBoard.paint.setColor(penColor);
                break;

            case R.id.width_1:
                strokeWidth = 5;
                DoodleBoard.paint.setStrokeWidth(strokeWidth);
                break;

            case R.id.width_2:
                strokeWidth = 10;
                DoodleBoard.paint.setStrokeWidth(strokeWidth);
                break;

            case R.id.width_3:
                strokeWidth = 15;
                DoodleBoard.paint.setStrokeWidth(strokeWidth);
                break;

            case R.id.eraserWidth_10:
                eraserWidth = 10;
                DoodleBoard.paint.setColor(Color.WHITE);
                DoodleBoard.paint.setStrokeWidth(eraserWidth);
                break;

            case R.id.eraserWidth_40:
                eraserWidth = 40;
                DoodleBoard.paint.setColor(Color.WHITE);
                DoodleBoard.paint.setStrokeWidth(eraserWidth);
                break;

            case R.id.eraserWidth_70:
                eraserWidth = 70;
                DoodleBoard.paint.setColor(Color.WHITE);
                DoodleBoard.paint.setStrokeWidth(eraserWidth);
                break;

            case R.id.eraserWidth_100:
                eraserWidth = 100;
                DoodleBoard.paint.setColor(Color.WHITE);
                DoodleBoard.paint.setStrokeWidth(eraserWidth);
                break;
        }

        return true;
    }

    /**
     * 请求读取sd卡的权限
     */
    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= 23) {

            //读取sd卡的权限
            String[] permissionList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

            if (EasyPermissions.hasPermissions(context, permissionList)) {
                saveImage();
            } else {
                //未同意过,或者说是拒绝了，再次申请权限
                EasyPermissions.requestPermissions(
                        this,  //上下文
                        "保存图片需要读取sd卡的权限", //提示文言
                        REQUEST_CODE_SAVE_IMG, //请求码
                        permissionList //权限列表
                );
            }

        } else {
            saveImage();
        }

    }

    //保存图片
    private void saveImage() {
        boolean isSaveSuccess = ImageUtil.saveImageToGallery(context, DoodleBoard.bitmap);
        if (isSaveSuccess) {
            Toast.makeText(context, "保存图片成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "保存图片失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }

    //授权结果，分发下去
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        //跳转到onPermissionsGranted或者onPermissionsDenied去回调授权结果
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //同意授权
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        Log.i(TAG, "onPermissionsGranted:" + requestCode + ":" + list.size());
        saveImage();
    }

    //拒绝授权
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //打开系统设置，手动授权
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //拒绝授权后，从系统设置了授权后，返回APP进行相应的操作
            Log.i(TAG, "onPermissionsDenied:------>自定义设置授权后返回APP");
            saveImage();
        }
    }

    public void save() {

        // 设置名称
        String filename = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            filename = simpleDateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 将 bitmap 转化为JPG
        if (!filename.equals("")) {
            File file = new File(filename);
            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                if (DoodleBoard.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
                    fos.flush();
                    fos.close();
                    saveImage();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
