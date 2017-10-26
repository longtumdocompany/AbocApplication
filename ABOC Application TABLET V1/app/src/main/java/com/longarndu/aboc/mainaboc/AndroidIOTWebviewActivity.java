package com.longarndu.aboc.mainaboc;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.longarndu.aboc.mainaboc.R;

import java.util.Locale;

/**
 * Created by TOPPEE on 9/11/2017.
 */

public class AndroidIOTWebviewActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    /** Called when the activity is first created. */

    private TextToSpeech tts;
    protected static final int RESULT_SPEECH = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_iot);
        
        WebView mainWebView = (WebView) findViewById(R.id.mainWebView);
        
        WebSettings webSettings = mainWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        
        mainWebView.setWebViewClient(new MyCustomWebViewClient());
        mainWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        //Freeboard.io
        mainWebView.loadUrl("https://freeboard.io/board/khsgbT");

        //Initial Text to Speech
        tts = new TextToSpeech(this, this, "com.google.android.tts");
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(new Locale("th"));
            tts.speak("ระบบติดต่ออุปกรณ์อิเล็กทรอนิกส์หรือไอโอที", TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private class MyCustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}