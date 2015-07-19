package com.gfsoft.pfctimer;

import java.util.logging.Logger;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

public class PFAlarmService extends IntentService {
	
    public PFAlarmService() {
        super(PFAlarmService.class.getSimpleName());
    }
   
    @Override
    protected void onHandleIntent(Intent intent) {
    	final int t = intent.getIntExtra("servicetime", 1);
    	final boolean isFinish = intent.getBooleanExtra("finish", false);
        
        Logger.getLogger(getClass().getName()).info("service time " + t);
    	
        final Vibrator viberator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        viberator.vibrate(1000);
        
        final Uri uri;
        if (isFinish) {
        	uri = Uri.parse("android.resource://" + getPackageName()
        			+ "/" + R.raw.fallbackring);
        } else {
        	uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        
    	final Ringtone ringtone 
			= RingtoneManager.getRingtone(getApplicationContext(), uri);
    	
    	ringtone.play();
        
        PFAlarmReceiver.completeWakefulIntent(intent);
    }
}
