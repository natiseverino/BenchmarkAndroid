package edu.benchmarkandroid.service;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.GsonBuilder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.benchmarkandroid.Benchmark.jsonConfig.BenchmarkData;
import edu.benchmarkandroid.Benchmark.jsonConfig.BenchmarkDefinition;
import edu.benchmarkandroid.Benchmark.jsonConfig.Variant;
import edu.benchmarkandroid.utils.BatteryUtils;
import edu.benchmarkandroid.utils.LogGUI;

public class BenchmarkExecutor implements BenchmarkExecutorRunCB {

    private static final String TAG = "BenchmarkExecutor";


    private Context context;

    private List<Variant> variants;
    private String benchClassName;
    private int currentBenchmark;
    private boolean sampling = true;
    private String benchmarkName = "";
    private boolean keepScreenOn = true;
    private Intent actualServiceIntent = null;
    private TextView stateTextView;
    private double neededBatteryLevelNextStep = 0d;
    private String neededBatteryState = "";


    public BenchmarkExecutor(Context context) {
        this.context = context;
    }


    public void setBenchmarkData(final BenchmarkData benchmarkData) {
        final BenchmarkDefinition definition = benchmarkData.getBenchmarkDefinitions().get(0);
        benchmarkName = definition.getBenchmarkClass();
        benchClassName = "edu.benchmarkandroid.Benchmark.benchmarks." + benchmarkName;
        variants = definition.getVariants();
        Collections.sort(variants, new Comparator<Variant>() {
            @Override
            public int compare(Variant o1, Variant o2) {
                List<String> order = benchmarkData.getRunOrder();
                return order.indexOf(o1.getVariantId()) - order.indexOf(o2.getVariantId());
            }
        });
        currentBenchmark = 0;
        this.neededBatteryLevelNextStep = variants.get(0).getEnergyPreconditionSamplingStage().getMinStartBatteryLevel();
        this.neededBatteryState = variants.get(0).getEnergyPreconditionSamplingStage().getRequiredBatteryState();
        String screenState = variants.get(0).getParamsRunStage().getScreenState();
        if (screenState.equalsIgnoreCase("on"))
            keepScreenOn = true;
        else if (screenState.equalsIgnoreCase("off"))
            keepScreenOn = false;

    }

    public boolean hasMoreToExecute() {
        return currentBenchmark < variants.size();
    }

    public void execute() {
        if (checkPreconditions())
            start();
    }

    private boolean checkPreconditions() {
        if (BatteryUtils.getBatteryLevel(context) < neededBatteryLevelNextStep) {
            alertMinBattery();
            BenchmarkExecutorWaitTask waitTask = new BenchmarkExecutorWaitTask(context, this, neededBatteryLevelNextStep, neededBatteryState, true);
            waitTask.execute();
            return false;

        } else if (!neededBatteryState.equalsIgnoreCase(BatteryUtils.getBatteryStatus(context))) {
            alertBatteryState();
            BenchmarkExecutorWaitTask waitTask = new BenchmarkExecutorWaitTask(context, this, neededBatteryLevelNextStep, neededBatteryState, false);
            waitTask.execute();
            return false;
        }

        return true;
    }

    @Override
    public void notifyFinishWaiting() {
        if (checkPreconditions())
            start();
    }


    public void alertMinBattery() {
        if (BatteryUtils.getBatteryLevel(context) < neededBatteryLevelNextStep) {
            LogGUI.log("");
            String msg = "Charge the device until " + (neededBatteryLevelNextStep * 100) + "%";
            Log.d(TAG, "alertMinBattery: " + msg);
            stateTextView.setText(msg);
            LogGUI.log(msg);
        }
    }

    public void alertBatteryState() {
        if (!neededBatteryState.equalsIgnoreCase(BatteryUtils.getBatteryStatus(context))) {
            if (neededBatteryState.equalsIgnoreCase("charging")) {
                LogGUI.log("");

                String msg = "Please connect the device";
                Log.d(TAG, "alertBatteryState: " + msg);
                stateTextView.setText(msg);
                LogGUI.log(msg);
            } else {
                LogGUI.log("");
                String msg = "Please disconnect the device";
                Log.d(TAG, "alertBatteryState: " + msg);
                stateTextView.setText(msg);
                LogGUI.log(msg);

            }
        }

    }

    private void start() {
        if (sampling) {

            // Sampling stage

            stateTextView.setText("Running Sampling");
            LogGUI.log("");
            LogGUI.log("Running Sampling");

            Intent intent = new Intent(context, SamplingIntentService.class);
            actualServiceIntent = intent;
            intent.putExtra("samplingName", benchClassName);
            intent.putExtra("benchmarkName", benchmarkName);
            intent.putExtra("benchmarkVariant", new GsonBuilder().create().toJson(variants.get(currentBenchmark)));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(intent);
            else
                context.startService(intent);
            this.neededBatteryLevelNextStep = variants.get(currentBenchmark).getEnergyPreconditionRunStage().getMinStartBatteryLevel();
            this.neededBatteryState = variants.get(currentBenchmark).getEnergyPreconditionRunStage().getRequiredBatteryState();
            sampling = false;


        } else {

            // Benchmark stage

            stateTextView.setText("Running Benchmark");
            LogGUI.log("");
            LogGUI.log("Running Benchmark");

            Intent intent = new Intent(context, BenchmarkIntentService.class);
            actualServiceIntent = intent;
            intent.putExtra("benchmarkName", benchClassName);
            intent.putExtra("benchmarkVariant", new GsonBuilder().create().toJson(variants.get(currentBenchmark)));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(intent);
            else
                context.startService(intent);

            currentBenchmark++;
            if (hasMoreToExecute()) {
                sampling = true;
                this.neededBatteryLevelNextStep = variants.get(currentBenchmark).getEnergyPreconditionSamplingStage().getMinStartBatteryLevel();
                this.neededBatteryState = variants.get(currentBenchmark).getEnergyPreconditionSamplingStage().getRequiredBatteryState();
                String screenState = variants.get(currentBenchmark).getParamsRunStage().getScreenState();

                if (screenState.equalsIgnoreCase("on"))
                    keepScreenOn = true;
                else if (screenState.equalsIgnoreCase("off"))
                    keepScreenOn = false;
            }


        }
    }


    public void stopBenchmark() {
        if (actualServiceIntent != null)
            context.stopService(actualServiceIntent);
    }


    public boolean isKeepScreenOn() {
        return keepScreenOn;
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        this.keepScreenOn = keepScreenOn;
    }

    public TextView getStateTextView() {
        return stateTextView;
    }

    public void setStateTextView(TextView stateTextView) {
        this.stateTextView = stateTextView;
    }

    public String getNeededBatteryState() {
        return neededBatteryState;
    }

    public double getNeededBatteryLevelNextStep() {
        return neededBatteryLevelNextStep;
    }

}
