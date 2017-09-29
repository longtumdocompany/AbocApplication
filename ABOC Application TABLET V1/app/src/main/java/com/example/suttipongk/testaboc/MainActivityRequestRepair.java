package com.example.suttipongk.testaboc;

import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.suttipongk.util.FirebaseNotificationUtil;
import com.example.suttipongk.util.QRCodeUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class MainActivityRequestRepair extends AppCompatActivity implements AdapterView.OnItemClickListener {

	protected static final int RESULT_SPEECH = 1;

	String queueNo = "";

	public static final String[] titles = new String[] { "Speech Recognition",
			"Face Recognition", "Voice Recognition", "Scan Paper" , "Google Cloud Messaging"};

	public static final String[] descriptions = new String[] {
			"Voice synthesis between reading device and Tablet does not work.",		//ระบบสังเคราะห์เสียงระหว่างเครื่องเปิดอ่านหนังสือและ Tablet ไม่ทำงาน
			"The system can not detect the face on the tablet.",					//ระบบไม่สามารถตรวจจับใบหน้าผ่านเครื่อง Tablet ได้
			"Audio books can not be read aloud.",									//ระบบหนังสือเสียงไม่สามารถอ่านออกเสียงได้
			"The system can not turn on the camera.",								//ระบบไม่สามารถเปิดกล้องได้
			"The system can not send a repair message to the ABOC manufacturer."	//ระบบไม่สามารถส่งข้อความแจ้งซ่อมไปที่ผู้ผลิต ABOC ได้
		};

	public static final Integer[] images = {
			R.drawable.arduinoiconrepair,
			R.drawable.cameraiconrepair,
			R.drawable.voiceiconrepair,
			R.drawable.scaniconrepair,
			R.drawable.messageiconrepair
	};

	List<RowItem> rowItems;
	ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_request_repair);

		rowItems = new ArrayList<RowItem>();
		for (int i = 0; i < titles.length; i++) {
			RowItem item = new RowItem(images[i], titles[i], descriptions[i]);
			rowItems.add(item);
		}

		listView = (ListView) findViewById(R.id.list);
		CustomListViewAdapter adapter = new CustomListViewAdapter(this, R.layout.activity_layout_request_customer_service, rowItems);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);

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

		FloatingActionButton fabRepairControl = (FloatingActionButton) findViewById(R.id.fabRepairControl);
		fabRepairControl.setOnClickListener(new View.OnClickListener() {
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

					if(spokenText.equals("หนึ่ง") || spokenText.equals("one")){
						queueNo = "A0001";
					} else if(spokenText.equals("สอง") || spokenText.equals("two")){
						queueNo = "B0001";
					} else if(spokenText.equals("สาม") || spokenText.equals("three")){
						queueNo = "C0001";
					} else if(spokenText.equals("สี่") || spokenText.equals("four")){
						queueNo = "D0001";
					} else if(spokenText.equals("ห้า") || spokenText.equals("five")){
						queueNo = "E0001";
					}

					//notificationDetail(queueNo);
					//QRCodeGenerator(queueNo);
				}
				break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		if(position == 1){
			queueNo = "A0001";
			//Voice synthesis between reading device and Tablet does not work.
		} else if(position == 2){
			queueNo = "B0001";
			//The system can not detect the face on the tablet.
		} else if(position == 3){
			queueNo = "C0001";
			//Audio books can not be read aloud.
		} else if(position == 4){
			queueNo = "D0001";
			//The system can not turn on the camera.
		} else if(position == 5){
			queueNo = "E0001";
			//The system can not turn on the camera.
		}

		//notificationDetail(queueNo);
		//QRCodeGenerator(queueNo);
	}

	//PUSH NOTIFICATION
	public void notificationDetail(String queueNo){

		//getTokenId*******************************************************************
		Log.d("Firebase", "token "+ FirebaseInstanceId.getInstance().getToken());
		String tokenId = FirebaseInstanceId.getInstance().getToken();
		//*****************************************************************************
		//String tokenId = "dnmBnd3mEc4:APA91bFUi4Zo393ElmWKDDxorxqbyUEthuysAgG8ALMVCaN1SfCoFuWuG8NtoV85D93yRRopxv4GO8fKVSrZ4wsUAlkkkEnSSipGD0uv3fhRheiMGPG8hrs6noHjprKUAuSBX3HrifCI";
		String authKeyFcm = "AAAAQ_8KJaM:APA91bGITIJYqDA3lNC8pTBh0_ml2UvO_RCpcKXYX55Cc9ytKNW1Hjh1THUqswi70x_kYZ8vBrr3B5jBxIn9A6QEeuxVWW3kMqpZZ9ab2G98LO6Q7XoHKzO01Kz1E263Y8IAkSaQIjfM";     //Server Key
		String apiUrlFcm = "https://fcm.googleapis.com/fcm/send";       //URL Service

		String reqNumber = "Aboc_01";
		String nameAndSurname = "Suttipong Kullawattana";
		String tel = "083-053-0649";
		String email = "suttipong.kull@gmail.com";
		String office = "Bangkok";

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date currentDate = new Date();
		String currentDateDay = dateFormat.format(currentDate);
		String date = currentDateDay;

		//ส่ง FCM ไปที่สำนักงานขายของ ABOC
		FirebaseNotificationUtil firebaseNotificationUtil = new FirebaseNotificationUtil();           //>>>> ส่ง Notification ไปที่เครื่อง User
		//firebaseNotificationUtil.pushFCMNotification(reqNumber, tokenId, authKeyFcm, apiUrlFcm, queueNo, nameAndSurname, tel, email, office, date);

	}

	//#QR CODE
	public void QRCodeGenerator(String queueNo){
		try {

			File picturePath = null;
			picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			String filePrint = picturePath + "//" + "ConfigurationFile" + "//" +"qrCodeGenerate.jpg";

			// Create the ByteMatrix for the QR-Code
			Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

			QRCodeWriter qrCodeWriter = new QRCodeWriter();					//Lib zxing-2.1jar class QRCodeWriter

			//getting unique id for device
			String machineId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

			BitMatrix byteMatrix = qrCodeWriter.encode(machineId+","+"Aboc_01"+","+queueNo, BarcodeFormat.QR_CODE, 500, 500, hintMap);

			try {
				QRCodeUtil.writeToFile(byteMatrix, "PNG", filePrint);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (WriterException e) {
			e.printStackTrace();
		}
	}
}