package com.longarndu.aboc.mainaboc;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by TOPPEE on 7/23/2017.
 */

public class FacePredictionService extends IntentService {
private final String TAG = "Face";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FacePredictionService(String name) {
        super(name);
    }

    public FacePredictionService(){
        super("");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG,"On handle intent");
        while (true){
            try {
                Thread.sleep(5000);
                double r = Math.random();
                String direction = "";
                if (r>0.5)
                    direction = "left";
                else
                    direction = "right";
                sendDirection(direction);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void sendDirection(String direction) {
        Log.d(TAG,"sent direction (service) "+direction);
        Toast.makeText(this,direction,Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SharedResources.FACE_PREDICTION_INTENT_ACTION);
        intent.putExtra("header","direction");
        intent.putExtra("detail",direction);
        sendBroadcast(intent);
    }

}
