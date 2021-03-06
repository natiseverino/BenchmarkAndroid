package edu.benchmarkandroid.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;

import edu.benchmarkandroid.Benchmark.BatteryStopCondition;
import edu.benchmarkandroid.Benchmark.Benchmark;
import edu.benchmarkandroid.Benchmark.jsonConfig.Variant;
import edu.benchmarkandroid.utils.Logger;

public class BenchmarkIntentService extends IntentService {

    public static final String PROGRESS_BENCHMARK_ACTION = "progressBenchmark";
    public static final String END_BENCHMARK_ACTION = "endBenchmark";

    private Benchmark benchmark;

    public BenchmarkIntentService() {
        super("BenchmarkIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Benchmark benchmark = null;
        BatteryNotificator batteryNotificator = BatteryNotificator.getInstance();
        try {
            Class<Benchmark> benchmarkClass = (Class<Benchmark>) Class.forName(intent.getStringExtra("benchmarkName"));
            benchmark = benchmarkClass.getConstructor(Variant.class).newInstance(gson.fromJson(intent.getStringExtra("benchmarkVariant"), Variant.class));
            this.benchmark = benchmark;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.init("run-" + benchmark.getVariant().getVariantId() + ".txt");
        Logger logger = null;
        try {
            logger = Logger.getInstance();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ProgressUpdater progressUpdater = new BenchMarckProgressUpdater(
                this,
                PROGRESS_BENCHMARK_ACTION,
                END_BENCHMARK_ACTION,
                benchmark.getVariant().getVariantId(),
                logger);

        benchmark.runBenchmark(
                new BatteryStopCondition(benchmark.getVariant().getEnergyPreconditionRunStage().getMinStartBatteryLevel(), benchmark.getVariant().getEnergyPreconditionRunStage().getMinEndBatteryLevel(), batteryNotificator),
                progressUpdater);

    }

    @Override
    public void onDestroy() {
        if (benchmark != null)
            benchmark.gentleTermination();

        super.onDestroy();
    }
}
