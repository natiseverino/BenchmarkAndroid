package edu.benchmarkandroid.Benchmark;


import edu.benchmarkandroid.service.ProgressUpdater;

public abstract class Benchmark {
    private Variant variant;

    public Benchmark(Variant variant) {
        this.variant = variant;
    }

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    public String getId() {
        return variant.getVariantId();
    }

    public abstract void runSampling(StopCondition stopCondition, ProgressUpdater progressUpdater);

    public abstract void runBenchmark(StopCondition stopCondition, ProgressUpdater progressUpdater);
}
