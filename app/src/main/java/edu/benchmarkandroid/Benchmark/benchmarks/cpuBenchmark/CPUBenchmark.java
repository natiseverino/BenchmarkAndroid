package edu.benchmarkandroid.Benchmark.benchmarks.cpuBenchmark;

import android.util.Log;

import edu.benchmarkandroid.Benchmark.Benchmark;
import edu.benchmarkandroid.Benchmark.StopCondition;
import edu.benchmarkandroid.Benchmark.Variant;
import edu.benchmarkandroid.service.ProgressUpdater;


public class CPUBenchmark extends Benchmark {

    private static final String EMPTY_PAYLOAD = "empty";
    private static final String TAG = "CPUBenchmark";
    private CPUUserThread[] cpuUser;

    private static final int READING_TIMES = 30;
    private static final int WAITING = 300;


    public CPUBenchmark(Variant variant) {
        super(variant);
    }

    /*
    @Override
    public void runBenchmark(StopCondition stopCondition, ProgressUpdater progressUpdater) {
        int progress = 0;
        while (stopCondition.canContinue() && progress < 40) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progress += 5;
            progressUpdater.update(progress);
        }
        progressUpdater.end(EMPTY_PAYLOAD);
    }

    @Override
    public void runSampling(StopCondition stopCondition, ProgressUpdater progressUpdater) {
        int progress = 0;
        while (stopCondition.canContinue() && progress < 40) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progress += 5;
            progressUpdater.update(progress);
        }
        progressUpdater.end(EMPTY_PAYLOAD);
    }

    */

    private static int cpus = 4;
    private static long sleep;

    private float target = 0.8f;
    private float threshold = 0.02f;

    public void setCPUs(int cpus) {
        this.cpus = cpus;
    }

    @Override
    public void runBenchmark(StopCondition stopCondition, ProgressUpdater progressUpdater) {
        int progress = 0;
        float cpuUsage;

        cpuUser = new CPUUserThread[this.cpus];
        Log.i(TAG, "runBenchmark: sleep: " + sleep);
        for (int i = 0; i < this.cpus; i++) {
            cpuUser[i] = new CPUUserThread();
            cpuUser[i].setSleep(sleep);
            cpuUser[i].start();
        }

        boolean nowStable = true;

        while (stopCondition.canContinue() && progress < 100 /*&& nowStable*/) {
            cpuUsage = cpuUsage();
            Log.d(TAG, "runBenchmark: cpu: " + cpuUsage);
            nowStable = ((-threshold) < (cpuUsage - target)) && ((cpuUsage - target) < (threshold));
            Log.d(TAG, "runBenchmark: nowStable: "+ nowStable);
            progress += 5;
            progressUpdater.update(progress);
        }
        Log.d(TAG, "runBenchmark: END");
        for (int i = 0; i < this.cpus; i++)
            cpuUser[i].kill();

        progressUpdater.end(EMPTY_PAYLOAD);
    }

    public void runSampling(StopCondition stopCondition, ProgressUpdater progressUpdater) { //  CONVERGENCE
        int progress = 0;
        float cpuUsage;
        sleep = 1;

        cpuUser = new CPUUserThread[this.cpus];
        //creates as many cpu consumers as available cores
        for (int i = 0; i < this.cpus; i++) {
            cpuUser[i] = new CPUUserThread();
            cpuUser[i].setSleep(sleep);
            cpuUser[i].start();
        }
        boolean stable = false;
        boolean nowStable;
        float diff;
        long sleepNew;
        while (stopCondition.canContinue() && progress < 100) {
            cpuUsage = cpuUsage();

            diff = cpuUsage / target;
            Log.i(TAG, "runConvergence: CPU Usage: " + cpuUsage +
                    " sleep: " + sleep + " diff: " + diff);
            sleepNew = (long) (sleep * diff);
            nowStable = ((-threshold) < (cpuUsage - target)) && ((cpuUsage - target) < (threshold));

            if ((sleep == sleepNew) && !nowStable) {
                if (diff > 1)
                    sleep++;
                else
                    sleep--;
            } else sleep = sleepNew;

            if (!stable && nowStable) {
                stable = true;
            }
            if (stable && !nowStable) {
                stable = false;
            }

            for (int i = 0; i < this.cpus; i++)
                cpuUser[i].setSleep(sleep);

            progress += 5;
            progressUpdater.update(progress);
        }
        Log.d(TAG, "runConvergence: END");
        for (int i = 0; i < this.cpus; i++)
            cpuUser[i].kill();
        progressUpdater.end(EMPTY_PAYLOAD);
    }


    protected synchronized float cpuUsage() {
        float result = 0;
        for (int i = 0; i < READING_TIMES; i++) {
            float aux = CPUUtils.readUsage();
            Log.d(TAG, "cpuUsage: " + aux);
            if (!Float.isNaN(aux)) {
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

