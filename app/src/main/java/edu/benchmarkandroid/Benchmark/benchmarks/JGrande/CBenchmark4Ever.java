package edu.benchmarkandroid.Benchmark.benchmarks.JGrande;


import edu.benchmarkandroid.Benchmark.Benchmark;
import edu.benchmarkandroid.Benchmark.StopCondition;
import edu.benchmarkandroid.Benchmark.Variant;
import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.DHPC_AllSizeA;
import edu.benchmarkandroid.service.ProgressUpdater;

public class CBenchmark4Ever extends Benchmark {

    public CBenchmark4Ever(Variant variant) {
        super(variant);
    }

    @Override
    public void runSampling(StopCondition stopCondition, ProgressUpdater progressUpdater) {
        progressUpdater.end();
    }

    @Override
    public void runBenchmark(StopCondition stopCondition, ProgressUpdater progressUpdater) {

        while (stopCondition.canContinue())
            DHPC_AllSizeA.run(progressUpdater, getVariant().getParamsRunStage());

        progressUpdater.end();
    }


}