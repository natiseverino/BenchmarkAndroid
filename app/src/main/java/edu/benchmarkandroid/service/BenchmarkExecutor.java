package edu.benchmarkandroid.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import edu.benchmarkandroid.Benchmark.jsonConfig.BenchmarkData;
import edu.benchmarkandroid.Benchmark.jsonConfig.BenchmarkDefinition;
import edu.benchmarkandroid.Benchmark.jsonConfig.Variant;
import edu.benchmarkandroid.utils.BatteryUtils;

import com.google.gson.GsonBuilder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BenchmarkExecutor {
    private List<Variant> variants;
    private String benchClassName;
    private int currentBenchmark;
    private boolean sampling = true;
    private double neededBatteryLevelNextStep = 0d;
    private String neededBatteryState = "";
    private String benchmarkName = "";
    private boolean keepScreenOn = true;

    private TextView stateTextView;

    private static final String TAG = "BenchmarkExecutor";

    private Context context;

    public BenchmarkExecutor(Context context) {
        this.context = context;
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

        alertBatteryStatus();

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


        while (!neededBatteryState.equalsIgnoreCase(BatteryUtils.getBatteryStatus(context))) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        if (sampling) {

            // Sampling stage

            stateTextView.setText("Running Sampling");
            Intent intent = new Intent(context, SamplingIntentService.class);
            intent.putExtra("samplingName", benchClassName);
            intent.putExtra("benchmarkName", benchmarkName);
            intent.putExtra("benchmarkVariant", new GsonBuilder().create().toJson(variants.get(currentBenchmark)));
            context.startService(intent);
            this.neededBatteryLevelNextStep = variants.get(currentBenchmark).getEnergyPreconditionRunStage().getMinStartBatteryLevel();
            this.neededBatteryState = variants.get(currentBenchmark).getEnergyPreconditionRunStage().getRequiredBatteryState();
            sampling = false;

            alertBatteryStatus();

        } else {

            // Benchmark stage

            stateTextView.setText("Running Benchmark");
            Intent intent = new Intent(context, BenchmarkIntentService.class);
            intent.putExtra("benchmarkName", benchClassName);
            intent.putExtra("benchmarkVariant", new GsonBuilder().create().toJson(variants.get(currentBenchmark)));
            context.startService(intent);
            currentBenchmark++;
            if (hasMoreToExecute()) {
                sampling = true;
                this.neededBatteryLevelNextStep = variants.get(currentBenchmark).getEnergyPreconditionSamplingStage().getMinStartBatteryLevel();
                this.neededBatteryState = variants.get(currentBenchmark).getEnergyPreconditionSamplingStage().getRequiredBatteryState();

                String screenState = variants.get(0).getParamsRunStage().getScreenState();
                if (screenState.equalsIgnoreCase("on"))
                    keepScreenOn = true;
                else if (screenState.equalsIgnoreCase("off"))
                    keepScreenOn = false;
            }

            alertBatteryStatus();

        }
    }

    public boolean isKeepScreenOn() {
        return keepScreenOn;
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        this.keepScreenOn = keepScreenOn;
    }


    private void alertBatteryStatus(){
        if (!neededBatteryState.equalsIgnoreCase(BatteryUtils.getBatteryStatus(context))) {
            if (neededBatteryState.equalsIgnoreCase("charging")) {
                Log.d(TAG, "execute:  battery status wrong - disconnect the device");
                stateTextView.setText("Please connect the device");
            } else {
                Log.d(TAG, "execute:  battery status wrong - disconnect the device");
                stateTextView.setText("Please disconnect the device");
            }
        }
    }
}
