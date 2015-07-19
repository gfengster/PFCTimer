package com.gfsoft.pfctimer;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PFAlarmReceiver extends WakefulBroadcastReceiver {
    private static final int MINUTE = 1000 * 60; 
	
    private static Activity context;
    
	private static AlarmManager alarmMgr;
    private static PendingIntent alarmIntent;
    private static int count;
    private static int period;
    
    private static int maximum = 0;
    
    @Override
	public void onReceive(Context ctx, Intent intent) {

		final TextView tv = (TextView) context.findViewById(R.id.clock_text);

		tv.setText((count * period) + " - " + ((count + 1)* period) + " mins");

		final ProgressBar pbar = (ProgressBar) context.findViewById(R.id.progress);
		pbar.setProgress((int) (((count + 1) * pbar.getMax()) / maximum));

		Logger.getLogger(getClass().getName()).info(
				"count=" + count + " repeats=" + maximum 
				+ " progress=" + period 
				+ " complete=" + (int) ((count * pbar.getMax()) / MINUTE) + "% at " 
						+ (new Date()).toString());

		final Intent service = new Intent(ctx, PFAlarmService.class);

		service.putExtra("servicetime", count);

		if (count >= maximum) {
			service.putExtra("finish", true);
			tv.setText(((count)* period) + " mins. Done!");
			pbar.setProgress(0);
		}
		
		startWakefulService(ctx, service);

		if (count >= maximum)
			cancelAlarm(context);
		
		count++;
	}

    public void setAlarm(Activity ctx, int interval, int max) {
    	context= ctx;
    	
    	count = 0;
    	period = interval;
    	maximum = max;
    	    	
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, PFAlarmReceiver.class);

        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
      
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
       
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,  
                				0, period * MINUTE, alarmIntent);
        
//        ComponentName receiver = new ComponentName(context, PFBootReceiver.class);
//        PackageManager pm = context.getPackageManager();
//
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);           
    }

    public void cancelAlarm(Activity ctx) {
   	    	
        if (alarmMgr!= null) {
        	count = 0;
        	
        	context.findViewById(R.id.start).setEnabled(true);
        	context.findViewById(R.id.stop).setEnabled(false);
     	
            alarmMgr.cancel(alarmIntent);
            
            TimerActivity.isRunning = false;
            
            context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        
//        ComponentName receiver = new ComponentName(context, PFBootReceiver.class);
//        PackageManager pm = context.getPackageManager();
//
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                PackageManager.DONT_KILL_APP);
    }
}
