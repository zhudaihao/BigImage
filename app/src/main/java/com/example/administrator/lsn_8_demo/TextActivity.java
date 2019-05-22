package com.example.administrator.lsn_8_demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

public class TextActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);


        init();

        //init2();


    }



    private void init() {
        MyView myView = findViewById(R.id.myView);

        InputStream is = null;
        try {
            is = getAssets().open("big.png");
            myView.setImage(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private void init2() {
        ImageView iv = findViewById(R.id.iv);
        ImageView iv2 = findViewById(R.id.iv2);
        try {
            InputStream is = getAssets().open("tv.jpg");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is,null,options);

            int w = options.outWidth;
            int h = options.outHeight;


            BitmapRegionDecoder bitmapRegionDecoder = BitmapRegionDecoder.newInstance(is, false);

            options.inPreferredConfig=Bitmap.Config.RGB_565;
            options.inJustDecodeBounds=false;

            Bitmap bitmap =  bitmapRegionDecoder.decodeRegion(new Rect(0,(h/2),w/2,h), options);


            iv.setImageBitmap(bitmap);

            Bitmap bitmap2 = bitmapRegionDecoder.decodeRegion(new Rect(0,(h/2),w/2,h+(h/2)), options);
            iv2.setImageBitmap(bitmap2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
