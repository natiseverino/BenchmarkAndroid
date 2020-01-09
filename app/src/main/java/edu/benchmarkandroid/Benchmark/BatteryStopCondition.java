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
        //TODO curva de bateria de carga (si el start el menor al end es una curva de carga)
        return batteryMinLevel <= batteryNotificator.getCurrentLevel();
    }


}
