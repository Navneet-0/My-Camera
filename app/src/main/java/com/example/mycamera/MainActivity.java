package com.example.mycamera;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    ImageView image;
    Button saveimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image=findViewById(R.id.image);

        saveimage=(Button)findViewById(R.id.savegallery);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        saveimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToGallery();

            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                boolean pick=true;
                if(pick==true){
                    if(!checkCameraPermission()){
                        requestCameraPermission();


                    }else PickImage();

                }else{
                    if(!checkStoragePermission()){
                        requestStoragePermission();


                    }else PickImage();

                }
            }
        });
    }

    private void PickImage() {
        CropImage.activity().start(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
       requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
    }

    private boolean checkStoragePermission() {
        boolean res2= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return res2;
    }

    private boolean checkCameraPermission() {
        boolean res1= ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        boolean res2= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return res1 && res2;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try{
                    InputStream stream=getContentResolver().openInputStream(resultUri);
                    Bitmap bitmap= BitmapFactory.decodeStream(stream);
                    image.setImageBitmap(bitmap);

                }catch (Exception e){
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void saveToGallery(){
        BitmapDrawable bitmapDrawable=(BitmapDrawable) image.getDrawable();
        Bitmap bitmap=bitmapDrawable.getBitmap();

        FileOutputStream outputStream=null;
        File file= Environment.getExternalStorageDirectory();
        File dir=new File(file.getAbsolutePath()+ "/MyImage");
        dir.mkdir();

        String filename=String.format("%d.png",System.currentTimeMillis());
        File outFile=new File(dir,filename);
        try{
            outputStream=new FileOutputStream(outFile);

        }catch (Exception e){
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);

        try{
            outputStream.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
