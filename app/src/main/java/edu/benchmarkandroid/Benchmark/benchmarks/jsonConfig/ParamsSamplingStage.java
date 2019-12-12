package edu.benchmarkandroid.Benchmark.benchmarks.jsonConfig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParamsSamplingStage {

    @SerializedName("convergenceThreshold")
    @Expose
    private Double convergenceThreshold;

    public Double getConvergenceThreshold() {
        if (convergenceThreshold != null)
            return convergenceThreshold;
        else
            return 0.0;
    }

    public void setConvergenceThreshold(Double convergenceThreshold) {
        this.convergenceThreshold = convergenceThreshold;
    }

}
