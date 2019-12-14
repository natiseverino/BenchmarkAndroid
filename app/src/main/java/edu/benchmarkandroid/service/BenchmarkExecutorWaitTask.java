package edu.benchmarkandroid.service;

import android.content.Context;
import android.os.AsyncTask;

import edu.benchmarkandroid.utils.BatteryUtils;

public class BenchmarkExecutorWaitTask extends AsyncTask<Void, Void, Void> {

    private Context context;

    private BenchmarkExecutorRunCB callback;
    private boolean waitForNeededBatteryLevel;
    private double neededBatteryLevel;
    private String neededBatteryState;


    public BenchmarkExecutorWaitTask(Context context, BenchmarkExecutorRunCB callback, double neededBatteryLevel, String neededBatteryState, boolean waitForNeededBatteryLevel) {
        this.context = context;
        this.callback = callback;
        this.neededBatteryLevel = neededBatteryLevel;
        this.neededBatteryState = neededBatteryState;
        this.waitForNeededBatteryLevel = waitForNeededBatteryLevel;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        if (waitForNeededBatteryLevel) {
            while (BatteryUtils.getBatteryLevel(context) < neededBatteryLevel) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            while (!neededBatteryState.equalsIgnoreCase(BatteryUtils.getBatteryStatus(context))) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        callback.notifyFinishWaiting();
    }
}
