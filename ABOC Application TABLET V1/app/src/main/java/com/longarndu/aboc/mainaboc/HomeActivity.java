/*
* Copyright (C) 2016 Pedro Paulo de Amorim
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.longarndu.aboc.mainaboc;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.longarndu.aboc.mainaboc.R;
import com.folioreader.activity.FolioActivity;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by TOPPEE on 7/23/2017.
 */

public class HomeActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private static final int GALLERY_REQUEST = 102;

    //Initial Text to Speech
    private TextToSpeech tts;
    protected static final int RESULT_SPEECH = 1;


    public static final String[] WRITE_EXTERNAL_STORAGE_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Start Main Speech Recognition-------------------------------------------------------------------
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "command");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "th-TH");

        try {
            startActivityForResult(intent, RESULT_SPEECH);
        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(getApplicationContext(), "Google Speech Recognition is not Working", Toast.LENGTH_SHORT);
            t.show();
        }
        //End Main Speech Recognition-------------------------------------------------------------------

        //Initial Text to Speech
        tts = new TextToSpeech(this, this, "com.google.android.tts");

        //**********************************************************************************************
        findViewById(R.id.btn_assest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeActivity.this, WRITE_EXTERNAL_STORAGE_PERMS, GALLERY_REQUEST);
                } else {
                    openEpub(FolioActivity.EpubSourceType.ASSESTS,"The Silver Chair.epub",0);
                }
            }
        });

        findViewById(R.id.btn_raw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeActivity.this, WRITE_EXTERNAL_STORAGE_PERMS, GALLERY_REQUEST);
                } else {
                    openEpub(FolioActivity.EpubSourceType.RAW,null,R.raw.adventures);
                }
            }
        });

        FloatingActionButton fabEpubOpenControl = (FloatingActionButton) findViewById(R.id.fabEpubOpenControl);
        fabEpubOpenControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start Main Speech Recognition-------------------------------------------------------------------
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "command");
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "th-TH");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(), "Google Speech Recognition is not Working", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });
    }

    //Main Speech Recognition
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    //แสดงผลของ Text ที่พูด
                    String spokenText = text.get(0);
                    Log.i("spokenText",spokenText);

                    if(spokenText.equals("เปิดไฟล์")){
                        Log.i("spokenText","assest epub file");
                        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(HomeActivity.this, WRITE_EXTERNAL_STORAGE_PERMS, GALLERY_REQUEST);
                        } else {
                            openEpub(FolioActivity.EpubSourceType.ASSESTS,"The Silver Chair.epub",0);
                        }
                    } else if(spokenText.equals("เปิดหนังสือ")){
                        Log.i("spokenText","raw epub file");
                        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(HomeActivity.this, WRITE_EXTERNAL_STORAGE_PERMS, GALLERY_REQUEST);
                        } else {
                            openEpub(FolioActivity.EpubSourceType.RAW,null,R.raw.adventures);
                        }
                    }
                }
                break;
            }
        }
    }


    private void openEpub(FolioActivity.EpubSourceType sourceType,String path,int rawID) {
        Intent intent = new Intent(HomeActivity.this, FolioActivity.class);
        if(rawID!=0) {
            intent.putExtra(FolioActivity.INTENT_EPUB_SOURCE_PATH, rawID);
        } else {
            intent.putExtra(FolioActivity.INTENT_EPUB_SOURCE_PATH, path);
        }
        intent.putExtra(FolioActivity.INTENT_EPUB_SOURCE_TYPE, sourceType);
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "Cannot open epub it needs storage access !", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(new Locale("th"));
            tts.speak("ระบบอ่านหนังสือเสียงสำหรับผู้พิการทางสายตา", TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}