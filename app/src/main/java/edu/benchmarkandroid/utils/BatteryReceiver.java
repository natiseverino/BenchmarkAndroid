package edu.benchmarkandroid.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class BatteryReceiver extends BroadcastReceiver {
	 //TODO Comparar con baterryInfoReceiver en MainActivity y ver que hacer
	
	private static String CHARGING="Charging";
	private static String DISCHARGING="Discharging";
	private Logger log;
	
	public BatteryReceiver(Logger log){
		this.log=log;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		StringBuffer st=new StringBuffer();
		st.append(System.currentTimeMillis());
		st.append(',');
		String status=null;
		if(intent.getExtras().getInt(BatteryManager.EXTRA_STATUS)== BatteryManager.BATTERY_STATUS_CHARGING)
			status=CHARGING;
		if(intent.getExtras().getInt(BatteryManager.EXTRA_STATUS)== BatteryManager.BATTERY_STATUS_DISCHARGING)
			status=DISCHARGING;
		st.append(status);
		st.append(',');
		st.append(intent.getExtras().get(BatteryManager.EXTRA_LEVEL));
		log.write(st.toString());
	}

}
