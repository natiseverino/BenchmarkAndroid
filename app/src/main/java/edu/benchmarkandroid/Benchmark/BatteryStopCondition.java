package edu.benchmarkandroid.Benchmark;

import edu.benchmarkandroid.service.BatteryNotificator;

public class BatteryStopCondition implements StopCondition {
    private double batteryMinLevel;
    private BatteryNotificator batteryNotificator;

    public BatteryStopCondition(double batteryMinLevel, BatteryNotificator batteryNotificator) {
        this.batteryMinLevel = batteryMinLevel;
        this.batteryNotificator = batteryNotificator;
    }

    @Override
    public boolean canContinue() {
        return batteryMinLevel <= batteryNotificator.getCurrentLevel();
    }
}
