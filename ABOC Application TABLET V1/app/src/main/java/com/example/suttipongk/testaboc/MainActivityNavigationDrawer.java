package com.example.suttipongk.testaboc;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import br.liveo.interfaces.OnItemClickListener;
import br.liveo.interfaces.OnPrepareOptionsMenuLiveo;
import br.liveo.model.HelpLiveo;
import br.liveo.navigationliveo.NavigationLiveo;

/**
 * Created by TOPPEE on 7/23/2017.
 */

public class MainActivityNavigationDrawer extends NavigationLiveo implements OnItemClickListener,IFragmentToActivity {

    private HelpLiveo mHelpLiveo;
    private PagerAdapter adapter;

    protected static final int RESULT_SPEECH = 1;

    @Override
    public void onInt(Bundle savedInstanceState) {

        String MY_DISPLAY_NAME = null;
        String MY_GET_EMAIL = null;

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String displayName = myPrefs.getString(MY_DISPLAY_NAME, "");
        String getEmail = myPrefs.getString(MY_GET_EMAIL, "");

        // User Information
        this.userName.setText("");                                  //Name
        this.userEmail.setText(getEmail);                           //E-mail
        this.userPhoto.setImageResource(R.drawable.ic_top);         //รูป Profile
        this.userBackground.setImageResource(R.drawable.ic_user_background_first);

        // Creating items navigation
        mHelpLiveo = new HelpLiveo();
        mHelpLiveo.add(getString(R.string.inbox), R.drawable.ic_inbox_black_24dp, 0);
        mHelpLiveo.addSubHeader(getString(R.string.categories));                                                    //Item subHeader
        mHelpLiveo.add(getString(R.string.speechrecognition), R.drawable.ic_star_black_24dp);
        mHelpLiveo.add(getString(R.string.facedetection), R.drawable.ic_star_black_24dp);
        mHelpLiveo.add(getString(R.string.documents), R.drawable.ic_star_black_24dp);
        mHelpLiveo.add(getString(R.string.scanpaper), R.drawable.ic_star_black_24dp);
        mHelpLiveo.add(getString(R.string.iot), R.drawable.ic_star_black_24dp);
        mHelpLiveo.add(getString(R.string.fall_detection), R.drawable.ic_star_black_24dp);
        mHelpLiveo.add(getString(R.string.chat_room), R.drawable.ic_star_black_24dp);
        mHelpLiveo.add(getString(R.string.sent_mail), R.drawable.ic_send_black_24dp);

        with(this).startingPosition(2)                                                          //Starting position in the list
                .addAllHelpItem(mHelpLiveo.getHelp())
                .colorItemSelected(R.color.nliveo_blue_colorPrimary)
                .footerItem(R.string.settings, R.drawable.ic_settings_black_24dp)
                .setOnClickUser(onClickPhoto)
                .setOnPrepareOptionsMenu(onPrepare)
                .setOnClickFooter(onClickFooter)
                .build();
        int position = this.getCurrentPosition();
        this.setElevationToolBar(position != 2 ? 15 : 0);

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

                    if(spokenText.equals("เปิดอ่านหนังสือ") || spokenText.equals("speech")){
                        Log.i("spokenText","speech recognition");
                        Intent homepage = new Intent(getApplicationContext(),Homepage.class);			//เปิดหน้า Control อ่านหนังสือแบบ HardCopy
                        startActivity(homepage);
                    } else if(spokenText.equals("ใบหน้า") || spokenText.equals("face")){
                        Log.i("spokenText","face recognition");
                        Intent fdActivity = new Intent(getApplicationContext(),FdActivity.class);		//เปิดอ่านหนังสือด้วยใบหน้า
                        startActivity(fdActivity);
                    } else if(spokenText.equals("หนังสือเสียง") || spokenText.equals("voice")){
                        Log.i("spokenText","voice recognition");
                        Intent homeActivity = new Intent(getApplicationContext(),HomeActivity.class);	//เปิดหน้าอ่านหนังสือเสียง
                        startActivity(homeActivity);
                    } else if(spokenText.equals("สแกนหนังสือ") || spokenText.equals("scan")){
                        Log.i("spokenText","scan paper");
                        Intent mainActivityScanPaper = new Intent(getApplicationContext(),MainActivityScanPaper.class);			//เปิด Scan Paper
                        startActivity(mainActivityScanPaper);
                    } else if(spokenText.equals("ไอโอที") || spokenText.equals("IOT")){
                        Log.i("spokenText","iot");
                        Intent androidIOTWebviewActivity = new Intent(getApplicationContext(),AndroidIOTWebviewActivity.class);			//IOT
                        startActivity(androidIOTWebviewActivity);
                    } else if(spokenText.equals("แชทรูม") || spokenText.equals("chat")){
                        Log.i("spokenText","chat room");
                        Intent androidIOTWebviewActivity = new Intent(getApplicationContext(),AndroidIOTWebviewActivity.class);			//Chat Room
                        startActivity(androidIOTWebviewActivity);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        Fragment mFragment;
        FragmentManager mFragmentManager = getSupportFragmentManager();

        switch (position){
            case 2:
                mFragment = new ViewPagerFragmentNavigationDrawer();
                break;

            default:
                mFragment = MainFragmentNavigationDrawer.newInstance(mHelpLiveo.get(position).getName());
                if(mHelpLiveo.get(position).getName().equals("Speech Recognition")){
                    Intent act1 = new Intent(getApplicationContext(),Homepage.class);
                    startActivity(act1);
                } else if(mHelpLiveo.get(position).getName().equals("Face Detection")){
                    Intent act2 = new Intent(getApplicationContext(),FdActivity.class);
                    startActivity(act2);
                } else if(mHelpLiveo.get(position).getName().equals("Voice Recognition")){
                    Intent act3 = new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(act3);
                } else if(mHelpLiveo.get(position).getName().equals("Scan Paper")){
                    Intent act4 = new Intent(getApplicationContext(),MainActivityScanPaper.class);
                    startActivity(act4);
                } else if(mHelpLiveo.get(position).getName().equals("Internet of Things")){
                    Intent act5 = new Intent(getApplicationContext(),AndroidIOTWebviewActivity.class);
                    startActivity(act5);
                } else if(mHelpLiveo.get(position).getName().equals("Fall Detection")){
                    Intent act6 = new Intent(getApplicationContext(),MainActivityAddFirebaseData.class);
                    startActivity(act6);
                }
                break;
        }

        if (mFragment != null){
            mFragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();
        }

        setElevationToolBar(position != 2 ? 15 : 0);
    }

    private OnPrepareOptionsMenuLiveo onPrepare = new OnPrepareOptionsMenuLiveo() {
        @Override
        public void onPrepareOptionsMenu(Menu menu, int position, boolean visible) {
        }
    };

    private View.OnClickListener onClickPhoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "onClickPhoto :D", Toast.LENGTH_SHORT).show();
            closeDrawer();
        }
    };

    private View.OnClickListener onClickFooter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(), MainActivityAddEBook.class));
            closeDrawer();
        }
    };

    @Override
    public void showToast(String msg) {
        Log.i("TEST", "Fragment is not initialized" + msg);
    }

    @Override
    public void communicateToFragment2() {
        TabFragment2 fragment = (TabFragment2) adapter.getFragment(1);
        if (fragment != null) {
            fragment.fragmentCommunication();
        } else {
            Log.i("TEST", "Fragment is not initialized");
        }
    }
}

