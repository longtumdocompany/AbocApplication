package com.example.suttipongk.testaboc;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by TOPPEE on 4/24/2017.
 */

public class Homepage extends AppCompatActivity implements TextToSpeech.OnInitListener {

	//Initial Text to Speech
	private TextToSpeech tts;

	protected static final int RESULT_SPEECH = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_control);

		//Initial Text to Speech
		tts = new TextToSpeech(this, this, "com.google.android.tts");

		ImageButton buttonselectbluetooth = (ImageButton) findViewById (R.id.buttonselectbluetooth);
		buttonselectbluetooth.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent bluetoothManager = new Intent(getApplicationContext(),DeviceListActivity.class);			//เปิด Bluetooth
				bluetoothManager.putExtra("PAGE", 0);
				startActivity(bluetoothManager);
			}
		});

		ImageButton buttoncontrolpage = (ImageButton) findViewById (R.id.buttoncontrolpage);					//เปิดอ่านหนังสือ
		buttoncontrolpage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent readingBookManager = new Intent(getApplicationContext(),DeviceListActivity.class);
				readingBookManager.putExtra("PAGE", 1);
				startActivity(readingBookManager);
			}
		});

		ImageButton buttoncontrollight = (ImageButton) findViewById (R.id.buttoncontrollight);					//แสงสว่าง
		buttoncontrollight.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent controlLightManager = new Intent(getApplicationContext(),DeviceListActivity.class);
				controlLightManager.putExtra("PAGE", 2);
				startActivity(controlLightManager);
			}
		});

		ImageButton buttonhowtopage = (ImageButton) findViewById (R.id.buttonhowtopage);						//วิดีโอ
		buttonhowtopage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent videoManager = new Intent(getApplicationContext(),VideoManager.class);
				startActivity(videoManager);
			}
		});

		FloatingActionButton fabSpeechControl = (FloatingActionButton) findViewById(R.id.fabSpeechControl);
		fabSpeechControl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				//Main Speech Recognition-------------------------------------------------------------------
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

					if(spokenText.equals("บลูทูธ") || spokenText.equals("bluetooth")){
						Log.i("spokenText","bluetooth");
						Intent bluetoothPage = new Intent(getApplicationContext(),DeviceListActivity.class);
						startActivity(bluetoothPage);
					} else if(spokenText.equals("อ่านหนังสือ") || spokenText.equals("read")){
						Log.i("spokenText","read");
						Intent readPage = new Intent(getApplicationContext(),DeviceListActivity.class);
						startActivity(readPage);
					} else if(spokenText.equals("แสงสว่าง") || spokenText.equals("light")){
						Log.i("spokenText","light");
						Intent lightPage = new Intent(getApplicationContext(),DeviceListActivity.class);
						startActivity(lightPage);
					} else if(spokenText.equals("วิดีโอ") || spokenText.equals("video")){
						Log.i("spokenText","video");
						Intent videoManager = new Intent(getApplicationContext(),VideoManager.class);
						startActivity(videoManager);
					}
				}
				break;
			}
		}
	}

	//Initial Text to Speech
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			tts.setLanguage(new Locale("th"));
			tts.speak("ระบบควบคุมเครื่องเปิดอ่านหนังสือด้วยเสียง", TextToSpeech.QUEUE_FLUSH, null);
		}
	}
}
