package edu.benchmarkandroid.Benchmark.jsonConfig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParamsSamplingStage {

    @SerializedName("convergenceThreshold")
    @Expose
    private Double convergenceThreshold = 0.01;

    public Double getConvergenceThreshold() {
        return convergenceThreshold;
    }

    public void setConvergenceThreshold(Double convergenceThreshold) {
        this.convergenceThreshold = convergenceThreshold;
    }

}
