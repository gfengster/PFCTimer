package com.gfsoft.pfctimer;

import java.util.logging.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.TextView;

public class TimerActivity extends Activity {
	
	private Logger logger = Logger.getLogger(TimerActivity.class.getName());
	
	private PFAlarmReceiver alarm = new PFAlarmReceiver();
	
	private int interval = 10;
	private int maximum = 6;
	
	private SharedPreferences pref;
	
	private volatile boolean isShowInterval = false;
	private volatile boolean isShowRepeat = false;
	volatile static boolean isRunning = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer);
		
		pref = getSharedPreferences("pftimersetting", MODE_PRIVATE);
		
		interval = pref.getInt("interval", 10);
		maximum = pref.getInt("maximum", 6);
		
		onSettingChanged();
	}
	
	@Override
	public void onBackPressed(){
		alarm.cancelAlarm(this);
		
		isRunning = false;
		
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		super.onBackPressed();
	}
	
	public void start(View v) {
		alarm.setAlarm(this, interval, maximum);
		
		v.setEnabled(false);
		findViewById(R.id.stop).setEnabled(true);
		
		isRunning = true;
		
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	public void stop(View v) {
		alarm.cancelAlarm(this);
		
		v.setEnabled(false);
		findViewById(R.id.start).setEnabled(true);
		
		isRunning = false;
		
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (isRunning || isShowInterval || isShowRepeat)
			return false;
				
		getMenuInflater().inflate(R.menu.timer, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if (isRunning || isShowInterval || isShowRepeat)
			return false;
		
		switch (item.getItemId()) {
			case R.id.action_interval:
				showSetIntervalDiaglog();
				
				break;
			case R.id.action_total:
				showSetRepeatDiaglog();
				break;
			default:
				
		}
			
		return true;
	}
	
	public void setInterval(View v) {
		if (isRunning || isShowInterval || isShowRepeat)
			return;
		
		isShowInterval = true;
		showSetIntervalDiaglog();
		isShowInterval = false;
	}
	
	public void setRepeat(View v) {
		if (isRunning || isShowInterval || isShowRepeat)
			return;
		
		isShowRepeat = true;
		showSetRepeatDiaglog();
		isShowRepeat = false;
	}
	
	private void showSetIntervalDiaglog() {
		final NumberPicker picker = new NumberPicker(this);
		picker.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		picker.setBackground(getResources().getDrawable(R.drawable.pfcbackground));
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(picker);
		builder.setIcon(R.drawable.ic_launcher);
		
		final String[] intervals = getResources().getStringArray(R.array.interval);
        
		builder.setMessage("How long between notice?")
               .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   interval = Integer.parseInt(
                			   picker.getDisplayedValues()[picker.getValue()]);
                	   logger.info("Pick interval: " + interval);
                	   
                	   onSettingChanged();
                   }
               })
               .setNegativeButton("Cancel", null);
              
        picker.setMinValue(0);
        picker.setMaxValue(intervals.length -1);
		picker.setDisplayedValues(intervals);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			for (int i = 0; i < intervals.length; i++) {
				if (intervals[i].equals(String.valueOf(interval))) {
					picker.setValue(i);
					break;
				}
			}
		}

		builder.create().show();
	}
	
	private void showSetRepeatDiaglog() {
		final NumberPicker picker = new NumberPicker(this);
		picker.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		picker.setBackground(getResources().getDrawable(R.drawable.pfcbackground));
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(picker);
		builder.setIcon(R.drawable.ic_launcher);
		
		final String[] repeats = getResources().getStringArray(R.array.repeat);
        
		builder.setMessage("How many notification?")
               .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   maximum = Integer.parseInt(
                			   picker.getDisplayedValues()[picker.getValue()]);
                	   logger.info("Pick repeat: " + maximum);
                	   
                	   onSettingChanged();
                   }
               })
               .setNegativeButton("Cancel", null);
              
        picker.setMinValue(0);
        picker.setMaxValue(repeats.length -1);
        picker.setDisplayedValues(repeats);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
	        for(int i =0; i < repeats.length; i++) {
	        	if (repeats[i].equals(String.valueOf(maximum))) {
	        		picker.setValue(i);
	        		break;
	        	}
	        }
        }
        
        builder.create().show();
	}
	
	private void onSettingChanged() {
		final Editor editor = pref.edit();
 	   	editor.putInt("maximum", maximum);
 	   	editor.putInt("interval", interval);
	   	editor.commit();
 	   	
 	   	((TextView)findViewById(R.id.repeat_text))
				.setText("Repeat: " + maximum);
	   
 	   	((TextView)findViewById(R.id.interval_text))
				.setText("Interval: " + interval + " minutes");
 	   	
 	   	((TextView)findViewById(R.id.period_text))
				.setText("Period: " + (interval * maximum) + " minutes");
	}
}
