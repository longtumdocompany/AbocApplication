package com.example.suttipongk.testaboc;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by TOPPEE on 4/24/2017.
 */

public class ControlLightManager extends AppCompatActivity {

    protected static final int RESULT_SPEECH = 1;
    TextView tvFalado;
    String falado = "";
    String faladotv = "";
    Handler bluetoothIn;

    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address;

    ImageView light = null;
    AlphaAnimation lightIcon = null;

    ImageButton buttonLeft = null;
    ImageButton buttonRight = null;

    AlphaAnimation animLeft = null;
    AlphaAnimation animRight = null;

    private String voiceCommand = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_control_light);

        tvFalado = (TextView) findViewById(R.id.tvFalado);

        light = (ImageView) findViewById (R.id.light);
        buttonLeft = (ImageButton) findViewById (R.id.imageButtonLightLeft);
        final ImageButton micฺButton = (ImageButton) findViewById (R.id.imageButtonLightVoice);
        buttonRight = (ImageButton) findViewById (R.id.imageButtonLightRight);

        //Animation
        animLeft = new AlphaAnimation(1, 0);       // Change alpha from fully visible to invisible
        animRight = new AlphaAnimation(1, 0);
        lightIcon = new AlphaAnimation(1, 0);

        animLeft.setInterpolator(new LinearInterpolator());
        animLeft.setRepeatCount(Animation.INFINITE);                    //Repeat animation
        animLeft.setDuration(1000);

        animRight.setInterpolator(new LinearInterpolator());
        animRight.setRepeatCount(Animation.INFINITE);                    //Repeat animation
        animRight.setDuration(1000);

        lightIcon.setInterpolator(new LinearInterpolator());
        lightIcon.setRepeatCount(Animation.INFINITE);                    //Repeat animation
        lightIcon.setDuration(1000);

        light.startAnimation(lightIcon);                                //start light icon

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                          // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                              //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                                // determine the end-of-line
                    if (endOfLineIndex > 0) {                                                       // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);            // extract string
                        int dataLength = dataInPrint.length();                                      //get length of data received
                    }
                }
            }
        };

        buttonLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animRight.cancel();
                    }
                }, 1000);

                //Start animation
                buttonLeft.startAnimation(animLeft);
            }
        });

        micฺButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lightIcon.cancel();
                    }
                }, 1000);

                //เปิด Popup อ่านเสียง Google Speech Recognition
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "command");
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-EN");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    tvFalado.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(), "Google Speech Recognition is not Working", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        buttonRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animLeft.cancel();
                    }
                }, 1000);

                //Start animation
                buttonRight.startAnimation(animRight);
            }
        });

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
    }

    private void checkBTState() {
        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }

        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //Send Command to Bluetooth
        mConnectedThread.write(voiceCommand+"#");

        //กรณีต้องการจะส่งคำสั่งแบบ Map Command
        Iterator it = SharedResources.getInstance().getComandos().entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            System.out.println(falado.equals(pair.getValue()));
            if(falado.equals(pair.getValue()) == true){
                mConnectedThread.write(pair.getKey().toString());
                falado = "";
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    //แสดงผลของ Text ที่พูด
                    faladotv = text.get(0);

                    //Map Command
                    falado = text.get(0).replaceAll("\\s", "").toLowerCase();

                    //set text to display
                    tvFalado.setText(faladotv);

                    Handler handler = new Handler();

                    //ตรวจสอบคำพูด
                    if(falado.equals("on")){
                        light.startAnimation(lightIcon);
                        voiceCommand = "a";                             //Sent "a" to bluetooth
                        tvFalado.setText("เปิดไฟ");
                    } else if(falado.equals("off")){
                        lightIcon.cancel();
                        voiceCommand = "b";                             //Sent "b" to bluetooth
                        tvFalado.setText("ปิดไฟ");
                    } else if(falado.equals("previous")){
                        lightIcon.cancel();
                        buttonLeft.startAnimation(animLeft);             //Set Animation ปุ่มซ้าย
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                animRight.cancel();                     //Cancel Animation ปุ่มขวา
                            }
                        }, 1000);
                        voiceCommand = "a";                             //Sent "a" to bluetooth
                        tvFalado.setText("เปิดหน้าที่แล้ว");
                    } else if(falado.equals("next")){
                        lightIcon.cancel();
                        buttonRight.startAnimation(animRight);          //Set Animation ปุ่มขวา
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                animLeft.cancel();                      //Cancel Animation ปุ่มซ้าย
                            }
                        }, 1000);
                        voiceCommand = "b";                             //Sent "b" to bluetooth
                        tvFalado.setText("เปิดหน้าถัดไป");
                    } else if(falado.equals("stop")) {                  //หยุดการทำงานทั้งหมด
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                animLeft.cancel();
                                animRight.cancel();
                            }
                        }, 1000);
                        tvFalado.setText("หยุดการทำงาน");
                    }
                }
                break;
            }
        }
    }

    //connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();
            try {
                mmOutStream.write(msgBuffer);               //ส่งข้อมูล
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    public void onPause() {
        super.onPause();
        try {
            btSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
