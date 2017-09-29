package com.example.suttipongk.testaboc;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

/**
 * Created by TOPPEE on 4/24/2017.
 */

public class VideoManager extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_page);

        //เรียก Video หลังจากเปิด Page
        VideoView view = (VideoView)findViewById(R.id.videoView);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.smartvdo;
        view.setVideoURI(Uri.parse(path));
        view.start();
    }

}
