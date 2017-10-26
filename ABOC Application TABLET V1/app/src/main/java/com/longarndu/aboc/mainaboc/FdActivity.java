package com.longarndu.aboc.mainaboc;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Core;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import com.longarndu.aboc.mainaboc.R;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import android.speech.tts.TextToSpeech;

/**
 * Created by TOPPEE on 7/23/2017.
 */

public class FdActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, CvCameraViewListener2, OnPageChangeListener, TextToSpeech.OnInitListener {

    private static final String TAG = "MainActivityNavigation";

    //Initial Text to Speech
    private TextToSpeech tts;

    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    private ImageView iv_image;
    private AppCompatSeekBar sb_brightness;
    private Bitmap image;

    //OPEN CV READ PDF
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    private static final Scalar    RED				   = new Scalar(255,0,0,255);
    private static final Scalar    PINK                = new Scalar(255,153,204,255);
    private static final Scalar    LIGHT_BLUE          = new Scalar(95, 204, 245, 255);
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;

    private Mat                    mRgba;
    private Mat                    mGray;
    private File                   mCascadeFile;
    private CascadeClassifier      mJavaDetector;
    private DetectionBasedTracker  mNativeDetector;

    private int                    mDetectorType       = JAVA_DETECTOR;
    private String[]               mDetectorName;

    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;

    private CameraBridgeViewBase   mOpenCvCameraView;

    private Mat                    mResult;
    private int                    learn_frames_l        = 0;
    private int                    learn_frames_r        = 0;
    private int                    learn_frames_m        = 0;
    private int                    positionEL            = 0;
    private int                    numOfEL               = 0;
    private int                    positionER            = 0;
    private int                    numOfER               = 0;
    private int                    positionM             = 0;
    private int                    numOfM                = 0;
    private boolean				   isSmile               = false;
    private int					   smileCount			 = 0;
    private boolean				   isLeft               = false;
    private int					   leftCount			 = 0;
    private boolean				   isRight               = false;
    private int					   RightCount			 = 0;

    private static final int       LEARNING_RATE         = 10;
    private int                    match_valueR;
    private int                    match_valueL;
    private int                    match_valueM;

    // Matching methods
    private static final int       TM_SQDIFF           = 0;
    private static final int       TM_SQDIFF_NORMED    = 1;
    private static final int       TM_CCOEFF           = 2;
    private static final int       TM_CCOEFF_NORMED    = 3;
    private static final int       TM_CCORR            = 4;
    private static final int       TM_CCORR_NORMED     = 5;

    // Mat for templates
    private Mat                    teplateR;
    private Mat                    teplateL;
    private Mat					   teplateM;

    // Classifiers for left-right eyes
    private CascadeClassifier      mCascadeER;
    private CascadeClassifier      mCascadeEL;
    private CascadeClassifier	   mCascadeM;

    // PDF file
    PDFView pdfView  ;
    File pdfFile  = new File("/storage/emulated/0/Download/summary.pdf");
    Button button;
    Button rematch;
    int currentPage = 1;

    //load Cascade classifier ของรูปหน้าทั้งหมดมาจาก XML ของ OpenCV

	/*
	Example
	-Frontal Face
	stump 24x24, 20x20gentle, 20x20tree
	-Profile Face (20x20)
	*/

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            Log.e(TAG, "status : "+status);
            Log.e(TAG, "LoaderCallbackInterface.SUCCESS : "+LoaderCallbackInterface.SUCCESS);
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.face);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        // --------------------------------- load Right eye classificator -----------------------------------
                        InputStream iser = getResources().openRawResource(R.raw.haarcascade_righteye_2splits);
                        File cascadeDirER = getDir("cascadeER", Context.MODE_PRIVATE);
                        File cascadeFileER = new File(cascadeDirER, "haarcascade_eye_right.xml");
                        FileOutputStream oser = new FileOutputStream(cascadeFileER);

                        byte[] bufferER = new byte[4096];
                        int bytesReadER;
                        while ((bytesReadER = iser.read(bufferER)) != -1) {
                            oser.write(bufferER, 0, bytesReadER);
                        }
                        iser.close();
                        oser.close();

                        // --------------------------------- load left eye classificator ------------------------------------
                        InputStream isel = getResources().openRawResource(R.raw.haarcascade_lefteye_2splits);
                        File cascadeDirEL = getDir("cascadeEL", Context.MODE_PRIVATE);
                        File cascadeFileEL = new File(cascadeDirEL, "haarcascade_eye_left.xml");
                        FileOutputStream osel = new FileOutputStream(cascadeFileEL);

                        byte[] bufferEL = new byte[4096];
                        int bytesReadEL;
                        while ((bytesReadEL = isel.read(bufferEL)) != -1) {
                            osel.write(bufferEL, 0, bytesReadEL);
                        }
                        isel.close();
                        osel.close();
                        Log.d(TAG, "Before mount");

                        // --------------------------------- load mount classificator ------------------------------------
                        InputStream ism = getResources().openRawResource(R.raw.mouth);
                        File cascadeDirM = getDir("cascadeM", Context.MODE_PRIVATE);
                        File cascadeFileM = new File(cascadeDirM, "haarcascade_mcs_mount.xml");
                        FileOutputStream osm = new FileOutputStream(cascadeFileM);
                        Log.d(TAG, "After Connect file");
                        byte[] bufferM = new byte[4096];
                        int bytesReadM;
                        Log.d(TAG, "Before read input stream");
                        while ((bytesReadM = ism.read(bufferM)) != -1) {
                            osm.write(bufferM, 0, bytesReadM);
                        }
                        ism.close();
                        osm.close();
                        Log.d(TAG, "After mount");

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        mCascadeER = new CascadeClassifier(cascadeFileER.getAbsolutePath());
                        mCascadeEL = new CascadeClassifier(cascadeFileEL.getAbsolutePath());
                        mCascadeM = new CascadeClassifier(cascadeFileM.getAbsolutePath());
                        Log.d(TAG, "After new mCascadeM");

                        if(mCascadeM.empty())
                            Log.e("empty", "True");
                        else
                            Log.e("empty", "False");

                        if(mCascadeEL.empty())
                            Log.e("empty", "True");
                        else
                            Log.e("empty", "False");

                        if(mCascadeER.empty())
                            Log.e("empty", "True");
                        else
                            Log.e("empty", "False");

                        if(mJavaDetector.empty())
                            Log.e("empty", "True");
                        else
                            Log.e("empty", "False");


                        if (mJavaDetector.empty()|| mCascadeER.empty() || mCascadeEL.empty() || mCascadeM.empty() ) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                            mCascadeER=null;
                            mCascadeEL=null;
                            mCascadeM=null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        //mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

                        if(mNativeDetector == null)
                            Log.e("Detector", "1: Null");
                        else
                            Log.e("Detector", "1: not null");

                        cascadeDir.delete();

                        cascadeFileER.delete();
                        cascadeDirER.delete();

                        cascadeFileEL.delete();
                        cascadeDirEL.delete();

                        cascadeFileM.delete();
                        cascadeDirM.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.setCameraIndex(mOpenCvCameraView.CAMERA_ID_FRONT);
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                    Log.e(TAG, "status : "+status);
                } break;
            }
        }
    };

    public FdActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("detection_based_tracker");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Initial Text to Speech
        tts = new TextToSpeech(this, this, "com.google.android.tts");

        final Activity ac = this;
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.face_detect_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);

        //*******************************************เลือก PDF แล้วกำหนดไว้ที่หน้าแรก*******************************************
        button = (Button)findViewById(R.id.chooseButton);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.w("zoom", "zoom :"+pdfView.getZoom());
                new FileChooser(ac).setFileListener(new FileSelectedListener() {

                    public void fileSelected(File file) {
                        pdfFile = file;
                        Log.w("DIR", "choosed : "+pdfFile.getName());
                        currentPage = 1;
                        display(pdfFile,currentPage);
                    }
                }).showDialog();
            }
        });

        //*******************************************Rematching คือ Set ทุกอย่างที่กำหนดไว้ให้ Learn เป็น 0*******************************************
        rematch = (Button)findViewById(R.id.rematch);
        rematch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                learn_frames_l = 0;
                learn_frames_m = 0;
                learn_frames_r = 0;
            }
        });
        pdfView = (PDFView)findViewById(R.id.pdfView);
        //display(new File("/storage/emulated/0/Download/summary.pdf"),1);
        display();

    }

    //*******************************************Camera Disable*******************************************
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    //*******************************************Load OPENCV Library*******************************************
    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    //*******************************************ใส่ Seekbar เพื่อเพิ่มคุณภาพของรูป ตรงนี้ไม่มีในระบบ*******************************************
    private Bitmap increaseBrightness(Bitmap bitmap, int value){
        Mat src = new Mat(bitmap.getHeight(),bitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap,src);
        src.convertTo(src,-1,1,value);
        Bitmap result = Bitmap.createBitmap(src.cols(),src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src,result);
        return result;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Bitmap edited = increaseBrightness(image,progress);
        iv_image.setImageBitmap(edited);
    }

    //****************************************************************************************************************

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onPageChanged(int page, int pageCount) {}


    //*******************************************Overide Camera***************************************************
    @Override
    public void onCameraViewStarted(int i, int i1) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    //**********************************************************************OVERRIDE MATCH EYE WITH TEMPLATE***********************************************************************
    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        try{
            mRgba = inputFrame.rgba();
            mGray = inputFrame.gray();
            Log.i("mRgb", "get input frame");
            Log.d("Mat", "cols : "+mGray.cols());
            Log.d("Mat", "rows : "+mGray.rows());
            Log.d("Mat", "width : "+mGray.width());
            Log.d("Mat", "height : "+mGray.height());

            //*******************************************เริ่มต้น get RGBA/GRAY****************************************************
            if (mAbsoluteFaceSize == 0) {
                int height = mGray.rows();
                if (Math.round(height * mRelativeFaceSize) > 0) {
                    mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
                }
                if(mNativeDetector == null)
                    Log.e("Detector", "2: Null");
                else
                    Log.e("Detector", "2: not null");
                mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
            }

            //*******************************************สร้างกรอบล้อมรอบหน้ากับปาก***************************************************
            MatOfRect faces = new MatOfRect();
            MatOfRect mounts = new MatOfRect();         //mount

            mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());

            Rect biggestFace = null;
            Rect[] facesArray = faces.toArray();

            //*******************************************นับจำนวนจุดบนใบหน้า********************************************************
            if(facesArray.length != 0){
                biggestFace = facesArray[0].clone();
                for (int i = 0; i < facesArray.length; i++){
                    if(facesArray[i].size().height > biggestFace.size().height){
                        biggestFace = facesArray[i].clone();
                    }
                }

                //*******************************************ความสูงของรูปหน้ามากกว่า 100****************************************************
                if(biggestFace.size().height > 100){
                    Log.d("RectArray", biggestFace.toString());
                    Core.rectangle(mRgba, biggestFace.tl(), biggestFace.br(), FACE_RECT_COLOR, 3);

                    //split it แบ่งพื้นที่
                    Rect eyearea_right = new Rect(biggestFace.x +biggestFace.width/16,(int)(biggestFace.y + (biggestFace.height/3.5)),(biggestFace.width - 2*biggestFace.width/16)/2,(int)( biggestFace.height/4.0));
                    Rect eyearea_left = new Rect(biggestFace.x +biggestFace.width/16 +(biggestFace.width - 2*biggestFace.width/16)/2,(int)(biggestFace.y + (biggestFace.height/3.5)),(biggestFace.width - 2*biggestFace.width/16)/2,(int)( biggestFace.height/4.0));
                    Rect mouthArea = new Rect((biggestFace.x+biggestFace.width/6) ,(int)(biggestFace.y + (biggestFace.height*2/3)),(biggestFace.width*4/6),(int)( biggestFace.height/3));

                    //*******************************************สร้างกรอบ new Rect(x, y, width, height)*******************************************
                    // draw the area - mGray is working grayscale mat, if you want to see area in rgb preview, change mGray to mRgba
                    Core.rectangle(mRgba,eyearea_left.tl(),eyearea_left.br() , FACE_RECT_COLOR, 2);
                    Core.rectangle(mRgba,eyearea_right.tl(),eyearea_right.br() , FACE_RECT_COLOR, 2);
                    Core.rectangle(mRgba,mouthArea.tl(),mouthArea .br() , LIGHT_BLUE, 2);

                    //********************************************************จับจุดที่ปาก***********************************************************
                    Rect toothArea = new Rect((biggestFace.x+biggestFace.width/3) ,(int)(biggestFace.y + (biggestFace.height*2/3)),(biggestFace.width/3),(int)( biggestFace.height/3));
                    Core.rectangle(mRgba, toothArea.tl(), toothArea.br(), PINK, 3);
                    Mat toothMat= mGray.submat(toothArea).clone();

                    Point centerOfToothTL = new Point(toothArea.tl().x+(toothArea.width/2)-(toothArea.width/16), toothArea.tl().y+(toothArea.height/2)-(toothArea.height/16));
                    Point centerOfToothBR = new Point(toothArea.br().x-(toothArea.width/2)+(toothArea.width/16), toothArea.br().y-(toothArea.height/2)+(toothArea.height/16));
                    Rect toothCenter = new Rect(centerOfToothTL, centerOfToothBR);
                    Mat toothCenterMat= mGray.submat(toothCenter).clone();
                    Core.rectangle(mRgba, centerOfToothTL,centerOfToothBR , RED);

                    Log.d("tooth", "size:"+toothArea.height+"*"+toothArea.width);
                    Log.d("tooth", ">"+toothMat.dump());
                    int unmask = 0;
                    int avgLight = 0;

                    Core.rectangle(mRgba, centerOfToothTL,centerOfToothBR , RED);
                    for(int i = 0 ; i < toothCenterMat.height();i++){
                        for(int j = 0; j < toothCenterMat.width();j++){
                            Scalar tmpColor = new Scalar(0,0,0);
                            if(toothCenterMat.get(i, j)[0] > 100){
                                Core.circle(mRgba, new Point(toothCenter.x+j, toothCenter.y+i), 2, tmpColor, 1);
                                unmask++;
                            }
                        }
                    }

                    //*********************************************ฟัน******************************************************
                    int numOfTooth = toothCenterMat.height()*toothCenterMat.width();
                    Log.d("unmask", ""+unmask+"   "+numOfTooth+"    "+((double)unmask/(double)numOfTooth));

                    //*******************************************จับการยิ้ม****************************************************
                    if((double)unmask/(double)numOfTooth > 0.2){
                        if(isSmile && smileCount >= 5){
                            Core.rectangle(mRgba, new Point(50, 50), new Point(500, 500), LIGHT_BLUE,10);
                            smileCount++;
                        }else{
                            isSmile = true;
                            smileCount++;
                        }
                    }else{
                        isSmile = false;
                        smileCount = 0;
                    }

                    //*******************************************ขนาดของตา**************************************************
                    int eyeSize = (int)(biggestFace.size().height/14);

                    //**********************************************ตาขวา*****************************************************
                    if(learn_frames_r<LEARNING_RATE ){
                        teplateR = getEyeTemplate(mCascadeER,eyearea_right,eyeSize);
                        if(teplateR.width() != 0)
                            learn_frames_r++;
                        Log.d("learn frame", "Right : "+learn_frames_r);
                    }else if(numOfER < 5){
                        match_valueR = match_eye(eyearea_right,teplateR, 1, LIGHT_BLUE);
                        positionER = ((numOfER*positionER)+match_valueR)/(++numOfER);
                        Log.d("position", "Position of ER : "+positionER);
                        Log.d("eye width", "Right : "+eyearea_right.width);
                    }else{
                        match_valueR = match_eye(eyearea_right,teplateR,1,FACE_RECT_COLOR);         //Or hardcode method you needs eg TM_SQDIFF_NORMED
                        Log.d("match value", "Right eye: "+match_valueR);
                    }

                    //*********************************************ตาซ้าย****************************************************
                    if(learn_frames_l<LEARNING_RATE ){
                        teplateL = getEyeTemplate(mCascadeEL,eyearea_left,eyeSize);
                        if(teplateL.width() != 0)
                            learn_frames_l++;
                        Log.d("learn frame", "Left : "+learn_frames_l);
                    }else if(numOfEL < 5){
                        match_valueL = match_eye(eyearea_left,teplateL, 1, LIGHT_BLUE);
                        positionEL = ((numOfEL*positionEL)+match_valueL)/(++numOfEL);
                        Log.d("position", "Position of EL : "+positionEL);
                        Log.d("eye width", "Left : "+eyearea_left.width);
                    }else{
                        match_valueL = match_eye(eyearea_left,teplateL,1,FACE_RECT_COLOR);          //Or hardcode method you needs eg TM_SQDIFF_NORMED
                        Log.d("match value", "left eye: "+match_valueL);
                    }

                    //*********************************************ปาก****************************************************
                    if(learn_frames_m<LEARNING_RATE ){
                        teplateM = getMountTemplate(mCascadeM,mouthArea);
                        if(teplateM.width() != 0)
                            learn_frames_m++;
                        Log.d("learn frame", "mount : "+learn_frames_m);
                    }else if(numOfM < 5){
                        match_valueM = match_eye(mouthArea,teplateM, 1, LIGHT_BLUE);
                        positionM = ((numOfM*positionM)+match_valueM)/(++numOfM);
                        Log.d("position", "Position of M : "+positionM);
                        Log.d("mount width", "mount : "+mouthArea.width);
                    }else{
                        match_valueM = match_eye(mouthArea,teplateM,1,FACE_RECT_COLOR);             //Or hardcode method you needs eg TM_SQDIFF_NORMED
                        Log.d("match value", "mount: "+match_valueM);
                    }

                    //************************คำนวณตำแหน่งของปาก***************************
                    int centerOfMouthX = match_valueM;
                    int centerOfToothX = toothCenter.x+(toothCenter.width/2);
                    int diffOfX = centerOfToothX-centerOfMouthX;
                    Log.w("move", "tooth - mouth in X:  "+diffOfX+"    "+centerOfToothX+ "    "+centerOfMouthX);

                    //*********************เรียกการอ่าน PDF จากการขยับปาก*********************
                    if(diffOfX >= 100){
                        Log.w("move", "Left    "+diffOfX);
                        Log.w("move", "isLeft : "+isLeft + "leftCount :"+leftCount);

                        if(isLeft && leftCount == 5){
                            Log.w("move", "--------------left----------------"+pdfFile.getName());
                            display(pdfFile, ++currentPage);        //Page ก่อนหน้า
                            leftCount++;
                        }else if(isLeft){
                            leftCount++;
                        }else{
                            leftCount++;
                            RightCount = 0;
                            isLeft = true;
                            isRight = false;
                        }
                    }else if(diffOfX <= 100){
                        Log.w("move", "Right   "+diffOfX);
                        if(isRight && RightCount == 5){
                            Log.w("move", "--------------Right---------------"+pdfFile.getName());
                            display(pdfFile, --currentPage);        //Page ถัดไป
                            RightCount++;
                        }else if(isRight){
                            RightCount++;
                        }else{
                            RightCount++;
                            leftCount = 0;
                            isRight = true;
                            isLeft = false;
                        }
                    }else{      //ถ้าหน้าตรง ปากนิ่ง ให้ Set Boolean เป็น False แล้ว Int เป็น 0 ให้หมด
                        RightCount = 0;
                        leftCount = 0;
                        isRight = false;
                        isLeft = false;
                    }
                }
            }
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
        return mRgba;
    }

    //**********************************************************************MATCH EYE WITH TEMPLATE***********************************************************************
    private int  match_eye(Rect area, Mat mTemplate,int type, Scalar color){
        Point matchLoc;
        Mat mROI = mGray.submat(area);
        int result_cols =  mROI.cols() - mTemplate.cols() + 1;
        int result_rows = mROI.rows() - mTemplate.rows() + 1;
        //Check for bad template size
        if(mTemplate.cols()==0 ||mTemplate.rows()==0){
            return 0;
        }
        mResult = new Mat(result_cols, result_rows, CvType.CV_8U);
        try{
            switch (type){
                case TM_SQDIFF:
                    Imgproc.matchTemplate(mROI, mTemplate, mResult, Imgproc.TM_SQDIFF) ;
                    break;
                case TM_SQDIFF_NORMED:
                    Imgproc.matchTemplate(mROI, mTemplate, mResult, Imgproc.TM_SQDIFF_NORMED) ;
                    break;
                case TM_CCOEFF:
                    Imgproc.matchTemplate(mROI, mTemplate, mResult, Imgproc.TM_CCOEFF) ;
                    break;
                case TM_CCOEFF_NORMED:
                    Imgproc.matchTemplate(mROI, mTemplate, mResult, Imgproc.TM_CCOEFF_NORMED) ;
                    break;
                case TM_CCORR:
                    Imgproc.matchTemplate(mROI, mTemplate, mResult, Imgproc.TM_CCORR) ;
                    break;
                case TM_CCORR_NORMED:
                    Imgproc.matchTemplate(mROI, mTemplate, mResult, Imgproc.TM_CCORR_NORMED) ;
                    break;
            }
        } catch(Exception ex){
            Log.e("Matching", ex.toString());
        }
        Core.MinMaxLocResult mmres =  Core.minMaxLoc(mResult);
        // there is difference in matching methods - best match is max/min value
        if(type == TM_SQDIFF || type == TM_SQDIFF_NORMED) {
            matchLoc = mmres.minLoc;
        }
        else {
            matchLoc = mmres.maxLoc;
        }

        Point  matchLoc_tx = new Point(matchLoc.x+area.x,matchLoc.y+area.y);
        Point  matchLoc_ty = new Point(matchLoc.x + mTemplate.cols() + area.x , matchLoc.y + mTemplate.rows()+area.y );

        Core.rectangle(mRgba, matchLoc_tx,matchLoc_ty, color);
        return (int)(matchLoc.x+(mTemplate.width()/2));
    }

    private Mat  getEyeTemplate(CascadeClassifier clasificator, Rect area,int size){
        Mat template = new Mat();
        Log.d("Template :","width"+template.width());
        Mat mROI = mGray.submat(area);
        MatOfRect eyes = new MatOfRect();
        Point iris = new Point();
        Rect eye_template = new Rect();
        clasificator.detectMultiScale(mROI, eyes, 1.15, 2,Objdetect.CASCADE_FIND_BIGGEST_OBJECT|Objdetect.CASCADE_SCALE_IMAGE, new Size(30,30),new Size());
        Rect[] eyesArray = eyes.toArray();

        for (int i = 0; i < eyesArray.length; i++){
            Rect e = eyesArray[i];
            e.x = area.x + e.x;
            e.y = area.y + e.y;
            Rect eye_only_rectangle = new Rect((int)e.tl().x,(int)( e.tl().y + e.height*0.4),(int)e.width,(int)(e.height*0.6));

            // reduce ROI
            mROI = mGray.submat(eye_only_rectangle);
            Mat vyrez = mRgba.submat(eye_only_rectangle);
            Core.MinMaxLocResult mmG = Core.minMaxLoc(mROI);                            // find the darkness point
            Core.circle(vyrez, mmG.minLoc,2, new Scalar(255, 255, 255, 255),2);         // draw point to visualise pupil
            iris.x = mmG.minLoc.x + eye_only_rectangle.x;
            iris.y = mmG.minLoc.y + eye_only_rectangle.y;
            eye_template = new Rect((int)iris.x-size/2,(int)iris.y-size/2 ,size,size);
            Core.rectangle(mRgba,eye_template.tl(),eye_template.br(),new Scalar(255, 0, 0, 255), 2);
            template = (mGray.submat(eye_template)).clone();    // copy area to template
            return template;
        }

        return template;
    }

    private Mat  getMountTemplate(CascadeClassifier clasificator, Rect area){
        Mat template = new Mat();
        Log.d("Template :","width"+template.width());
        Mat mROI = mGray.submat(area);
        MatOfRect eyes = new MatOfRect();
        clasificator.detectMultiScale(mROI, eyes, 1.15, 2,Objdetect.CASCADE_FIND_BIGGEST_OBJECT|Objdetect.CASCADE_SCALE_IMAGE, new Size(30,30),new Size());
        Rect[] eyesArray = eyes.toArray();
        for (int i = 0; i < eyesArray.length; i++){
            Rect e = eyesArray[i];
            e.x = area.x + e.x;
            e.y = area.y + e.y;
            Core.rectangle(mRgba, e.tl(), e.br(), RED, 3);
            return mGray.submat(e).clone();
        }
        return template;
    }

    //**********************************************************************Option***********************************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    //*******************************************************************setMinFaceSize*******************************************************************
    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }

    private void setDetectorType(int type) {
        if (mDetectorType != type) {
            mDetectorType = type;
            if (type == NATIVE_DETECTOR) {
                Log.i(TAG, "Detection Based Tracker enabled");
                mNativeDetector.start();
            } else {
                Log.i(TAG, "Cascade detector enabled");
                mNativeDetector.stop();
            }
        }
    }

    //***********************************************************************PDF READER***********************************************************************
    private void display(File pdfFile,int page) {
        Log.w("page", ""+pdfFile.getName()+"  "+page);
        //pdfFile = new File("/storage/emulated/0/Download/summary.pdf");
        Log.w("DIR", "is dir : "+pdfFile.isDirectory());
        Log.w("DIR", "is file : "+pdfFile.isFile());
        Log.w("DIR", "can read : "+pdfFile.canRead());
        Log.w("zoom", "zoom :"+pdfView.getZoom());
        pdfView.recycle();
        pdfView.fromFile(pdfFile).defaultPage(page).onPageChange(this).load();
        Log.w("zoom", "zoom :"+pdfView.getZoom());
        pdfView.zoomTo((float)2.38);
        Log.w("zoom", "zoom :"+pdfView.getZoom());
    }

    private void display() {
        //pdfFile = new File("/storage/emulated/0/Download/summary.pdf");
        pdfView.fromAsset("summary.pdf").defaultPage(1).onPageChange(this).load();
    }

    private void changePage(File pdfFile,int page) {
        page = pdfView.getCurrentPage()+page;
        Log.w("page", ""+page);
        //pdfFile = new File("/storage/emulated/0/Download/summary.pdf");
        Log.w("zoom", "zoom :"+pdfView.getZoom());
        pdfView.zoomTo((float)2.38);
        pdfView.fromFile(pdfFile).defaultPage(page).onPageChange(this).load();
        Log.w("zoom", "zoom :"+pdfView.getZoom());
        pdfView.zoomTo((float)2.38);
        Log.w("zoom", "zoom :"+pdfView.getZoom());
    }

    //***********************************************************************Initial Text to Speech***********************************************************************
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(new Locale("th"));
            tts.speak("ระบบอ่านหนังสือผ่านใบหน้าสำหรับผู้พิการทางร่างกาย", TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
