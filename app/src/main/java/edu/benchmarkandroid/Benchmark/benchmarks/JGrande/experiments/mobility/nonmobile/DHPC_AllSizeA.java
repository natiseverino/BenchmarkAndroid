package edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile;

import edu.benchmarkandroid.Benchmark.StopCondition;
import edu.benchmarkandroid.service.ProgressUpdater;
import edu.benchmarkandroid.Benchmark.jsonConfig.ParamsRunStage;

import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.Statistics;
import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.ep.DHPC_EPBench;
import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.fft.DHPC_FFTBench;
import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.hanoi.DHPC_HanoiBench;
import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.prime.DHPC_PrimeBench;
import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.sieve.DHPC_SieveBench;

/**
 * ep NAS Embarrasingly Parallel benchmark (Class S problem size) fib Calculate
 * the 40th Fibonacci benchmark fft NAS Fast Fourier Transform benchmark (Class
 * S problem size) hanoi Solve the Tower of Hanoi with 25 disks sieve Sieve of
 * Erasthosthenes with an array size of 10000
 */
public class DHPC_AllSizeA {

    public static void run(ProgressUpdater progressUpdater, ParamsRunStage paramsRunStage, StopCondition stopCondition) {


        int runs = paramsRunStage.getRuns();
        int size = paramsRunStage.getSize();

        double[] CPUSnapshots = new double[runs];

        if(!stopCondition.canContinue()) return;

        try{
            for (int i = 0; i < runs; i++) {
                long l = System.currentTimeMillis();

                DHPC_FFTBench ffb = new DHPC_FFTBench();
                ffb.JGFrun(size, progressUpdater, paramsRunStage);
                CPUSnapshots[i] = System.currentTimeMillis() - l;
            }
            progressUpdater.update("DHPC_FFTBench\t" + Statistics.average(CPUSnapshots)
                    + "\t" + Statistics.standardDeviation(CPUSnapshots));
        } catch (Throwable e) {
            progressUpdater.update("Error executing DHPC_FFTBench");
            //e.printStackTrace(System.out);
        }

        if(!stopCondition.canContinue()) return;

        try{
            for (int i = 0; i < runs; i++) {
                long l = System.currentTimeMillis();
                DHPC_SieveBench sb = new DHPC_SieveBench();
                sb.JGFrun(size, paramsRunStage);
                CPUSnapshots[i] = System.currentTimeMillis() - l;
            }
            progressUpdater.update("DHPC_SieveBench\t"
                    + Statistics.average(CPUSnapshots) + "\t"
                    + Statistics.standardDeviation(CPUSnapshots));
        } catch (Throwable e) {
            progressUpdater.update("Error executing DHPC_SieveBench");
            //e.printStackTrace(System.out);
        }

        if(!stopCondition.canContinue()) return;

        try{
            for (int i = 0; i < runs; i++) {
                long l = System.currentTimeMillis();
                DHPC_HanoiBench hb = new DHPC_HanoiBench();
                hb.JGFrun(size, progressUpdater, paramsRunStage);
                CPUSnapshots[i] = System.currentTimeMillis() - l;
            }
            progressUpdater.update("DHPC_HanoiBench\t"
                    + Statistics.average(CPUSnapshots) + "\t"
                    + Statistics.standardDeviation(CPUSnapshots));
        } catch (Throwable e) {
            progressUpdater.update("Error executing DHPC_HanoiBench");
            //e.printStackTrace(System.out);
        }

        if(!stopCondition.canContinue()) return;

        try{
            for (int i = 0; i < runs; i++) {
                long l = System.currentTimeMillis();
                DHPC_EPBench eb = new DHPC_EPBench();
                eb.JGFrun(size, progressUpdater, paramsRunStage);
                CPUSnapshots[i] = System.currentTimeMillis() - l;
            }
            progressUpdater.update("DHPC_EPBench\t" + Statistics.average(CPUSnapshots)
                    + "\t" + Statistics.standardDeviation(CPUSnapshots));
        } catch (Throwable e) {
            progressUpdater.update("Error executing DHPC_EPBench");
            //e.printStackTrace(System.out);
        }

        if(!stopCondition.canContinue()) return;

        try{
            for (int i = 0; i < runs; i++) {
                long l = System.currentTimeMillis();
                DHPC_PrimeBench kb =new DHPC_PrimeBench();
                kb.JGFrun(size, paramsRunStage);
                CPUSnapshots[i] = System.currentTimeMillis() - l;
            }
            progressUpdater.update("DHPC_PrimeBench\t"
                    + Statistics.average(CPUSnapshots) + "\t"
                    + Statistics.standardDeviation(CPUSnapshots));
        } catch (Throwable e) {
            progressUpdater.update("Error executing DHPC_PrimeBench");
            //e.printStackTrace(System.out);
        }

    }
}