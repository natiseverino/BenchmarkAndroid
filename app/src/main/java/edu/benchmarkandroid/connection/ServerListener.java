package edu.benchmarkandroid.connection;

import edu.benchmarkandroid.Benchmark.jsonConfig.BenchmarkData;

public interface ServerListener {

    void onSuccessUpdateBatteryState();
    void onFailureUpdateBatteryState();

    void onSuccessGetBenchmarks(BenchmarkData benchmarkData);
    void onFailureGetBenchmarks();

    void onSuccessStartBenchmark();
    void onFailureStartBenchmark();

    void onSuccessPostResult();
    void onFailurePostResult();


}
