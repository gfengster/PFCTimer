package com.gfsoft.pfctimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PFBootReceiver extends BroadcastReceiver {
	//private final PFAlarmReceiver alarm = new PFAlarmReceiver();
    
	@Override
    public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                  
			//alarm.setAlarm(context, 1000, 6);
        }
    }
}
