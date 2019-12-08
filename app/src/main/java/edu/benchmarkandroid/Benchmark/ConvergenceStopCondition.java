package edu.benchmarkandroid.Benchmark;

import android.util.Log;

import edu.benchmarkandroid.service.ThresholdNotificator;

public class ConvergenceStopCondition implements StopCondition {
    private double convergenceThreshold;
    private ThresholdNotificator thresholdNotificator;

    private static final String TAG = "ConvergenceStopConditio";

    public ConvergenceStopCondition(double convergenceThreshold, ThresholdNotificator thresholdNotificator) {
        this.convergenceThreshold = convergenceThreshold;
        this.thresholdNotificator = thresholdNotificator;
    }

    @Override
    public boolean canContinue() {
        double level = thresholdNotificator.getCurrentLevel();
        Log.d(TAG, "canContinue: level: "+ level);
        return convergenceThreshold < level || level < -(convergenceThreshold);
    }

    public void updateLevel(double level){
        this.thresholdNotificator.updateThresholdLevel(level);
    }

}
