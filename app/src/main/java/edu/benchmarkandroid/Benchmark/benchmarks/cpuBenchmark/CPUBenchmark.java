package edu.benchmarkandroid.Benchmark.benchmarks.cpuBenchmark;

import android.util.Log;

import edu.benchmarkandroid.Benchmark.Benchmark;
import edu.benchmarkandroid.Benchmark.StopCondition;
import edu.benchmarkandroid.Benchmark.Variant;
import edu.benchmarkandroid.service.ProgressUpdater;
import edu.benchmarkandroid.service.ThresholdNotificator;
import edu.benchmarkandroid.utils.CPUUtils;


public class CPUBenchmark extends Benchmark {

    private static final String EMPTY_PAYLOAD = "empty";
    private static final String TAG = "CPUBenchmark";
    private CPUUserThread[] cpuUser;

    private static final int READING_TIMES = 30;
    private static final int WAITING = 300;


    public CPUBenchmark(Variant variant) {
        super(variant);
    }

    private static int cpus = Runtime.getRuntime().availableProcessors();
    private static long sleep;

    private double target = getVariant().getParamsRunStage().getCpuLevel();
    private double threshold = getVariant().getParamsSamplingStage().getConvergenceThreshold();

    public void setCPUs(int cpus) {
        this.cpus = cpus;
    }

    @Override
    public void runBenchmark(StopCondition stopCondition, ProgressUpdater progressUpdater) {
        String progress;
        double cpuUsage;

        Log.i(TAG, "runBenchmark: sleep: " + sleep);

        cpuUser = new CPUUserThread[this.cpus];
        for (int i = 0; i < this.cpus; i++) {
            cpuUser[i] = new CPUUserThread();
            cpuUser[i].setSleep(sleep);
            cpuUser[i].start();
        }

        boolean stable = false;
        boolean nowStable;
        double diff;
        long sleepNew;

        Log.d(TAG, "runBenchmark: target: "+ target+ "threshold: "+ threshold);
        while (stopCondition.canContinue() && stable) {
            cpuUsage = cpuUsage();

            diff = cpuUsage / target;
            sleepNew = (long) (sleep * diff);

            Log.i(TAG, "runBenchmark: CPU Usage: " + cpuUsage +
                    " sleep: " + sleep + " diff: " + diff);


            nowStable = ((-threshold) < (cpuUsage - target)) && ((cpuUsage - target) < (threshold));

            if ((sleep == sleepNew) && !nowStable) {
                if (diff > 1)
                    sleep++;
                else
                    sleep--;
            } else
                sleep = sleepNew;

            for (int i = 0; i < this.cpus; i++)
                cpuUser[i].setSleep(sleep);


            if (nowStable) {
                Log.d(TAG, "runBenchmark: CPU Usage: " + cpuUsage + " nowStable: " + nowStable);
            }
            else {
                Log.d(TAG, "runBenchmark: no esta estable");
            }
            progress = "CPUUsage: "+ cpuUsage+ "Sleep: "+sleep;
            progressUpdater.update(progress);
        }
        Log.d(TAG, "runBenchmark: END");

        for (int i = 0; i < this.cpus; i++)
            cpuUser[i].kill();

        progressUpdater.end(EMPTY_PAYLOAD);
    }


    public void runSampling(StopCondition stopCondition, ProgressUpdater progressUpdater) { //  CONVERGENCE
        int iterations = 0;
        String progress;
        double cpuUsage;
        sleep = 1;

        cpuUser = new CPUUserThread[this.cpus];
        //creates as many cpu consumers as available cores
        for (int i = 0; i < this.cpus; i++) {
            cpuUser[i] = new CPUUserThread();
            cpuUser[i].setSleep(sleep);
            cpuUser[i].start();
        }

        boolean nowStable;
        double diff;
        long sleepNew;

        Log.d(TAG, "runConvergence: target: "+ target+ " threshold: "+ threshold);

        ThresholdNotificator thresholdNotificator = ThresholdNotificator.getInstance();

        while (stopCondition.canContinue() || iterations < 10) { //var "progress" to avoid early convergence
            cpuUsage = cpuUsage();

            diff = cpuUsage / target;
            sleepNew = (long) (sleep * diff);

            Log.i(TAG, "runConvergence: CPU Usage: " + cpuUsage +
                    " sleep: " + sleep + " diff: " + diff);

            thresholdNotificator.updateThresholdLevel(cpuUsage - target);

            if ((sleep == sleepNew) && stopCondition.canContinue()) { //canContinue checks if is not stable yet
                if (diff > 1)
                    sleep++;
                else
                    sleep--;
            } else
                sleep = sleepNew;


            for (int i = 0; i < this.cpus; i++)
                cpuUser[i].setSleep(sleep);

            iterations += 1;
            progress = "CPUUsage: "+ cpuUsage+ "Sleep: "+sleep;
            progressUpdater.update(progress);
        }

        Log.d(TAG, "runConvergence: END");

        for (int i = 0; i < this.cpus; i++)
            cpuUser[i].kill();

        progressUpdater.end(EMPTY_PAYLOAD);
    }


    protected synchronized double cpuUsage() {
        double result = 0;
        for (int i = 0; i < READING_TIMES; i++) {
            double aux = CPUUtils.readUsage();
            if (!Double.isNaN(aux)) {
                result += aux;
            }
            try {
                wait(WAITING);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result / READING_TIMES;
    }

}

