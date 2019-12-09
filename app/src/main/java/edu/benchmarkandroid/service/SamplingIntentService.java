package edu.benchmarkandroid.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import edu.benchmarkandroid.Benchmark.Benchmark;
import edu.benchmarkandroid.Benchmark.ConvergenceStopCondition;
import edu.benchmarkandroid.Benchmark.Variant;
import edu.benchmarkandroid.utils.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;

public class SamplingIntentService extends IntentService {

    public static final String PROGRESS_SAMPLING_ACTION = "progressSampling";
    public static final String END_SAMPLING_ACTION = "endSampling";

    public SamplingIntentService() {
        super("SamplingIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Benchmark benchmark = null;
        ThresholdNotificator thresholdNotificator = ThresholdNotificator.getInstance();
        try {
            Class<Benchmark> benchmarkClass = (Class<Benchmark>) Class.forName(intent.getStringExtra("samplingName"));
            benchmark = benchmarkClass.getConstructor(Variant.class).newInstance(gson.fromJson(intent.getStringExtra("benchmarkVariant"), Variant.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.init("sampling-" +benchmark.getVariant().getVariantId()+".txt");
        Logger logger = null;
        try {
            logger = Logger.getInstance();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ProgressUpdater progressUpdater = new SamplingProgressUpdater(this, PROGRESS_SAMPLING_ACTION, END_SAMPLING_ACTION, benchmark.getVariant().getVariantId(), logger);
        benchmark.runSampling(
                new ConvergenceStopCondition(benchmark.getVariant().getParamsSamplingStage().getConvergenceThreshold(), thresholdNotificator),
                progressUpdater);

    }
}
