package com.example.suttipongk.testaboc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by TOPPEE on 7/29/2017.
 */

public class QrCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Read QR Code
        readQRCodeImage();
    }

    //Read Image to show
    public void readQRCodeImage(){

        File picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String fileIDPath = picturePath + "//" + "Aboc" + "//" + "Aboc_01";
        File imgFile = new File(fileIDPath);
        imgFile.mkdirs();
        fileIDPath = fileIDPath + "//qrCodeNumber.jpg";
        String fileImageSCPath = fileIDPath;

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView myImage = (ImageView) findViewById(R.id.qrCode);
            myImage.setImageBitmap(myBitmap);
        }
    }
}
