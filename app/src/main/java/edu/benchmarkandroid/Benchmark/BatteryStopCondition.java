package edu.benchmarkandroid.Benchmark;

import edu.benchmarkandroid.service.BatteryNotificator;

public class BatteryStopCondition implements StopCondition {
    private double startBatteryLevel;
    private double endBatteryLevel;
    private BatteryNotificator batteryNotificator;
    private Comp comparator;


    public BatteryStopCondition(double startBatteryLevel, double endBatteryLevel, BatteryNotificator batteryNotificator) {
        this.startBatteryLevel = startBatteryLevel;
        this.endBatteryLevel = endBatteryLevel;
        this.batteryNotificator = batteryNotificator;

        if (this.startBatteryLevel >= this.endBatteryLevel) // discharging
            this.comparator = new Comp() {
                @Override
                public boolean compare(double end, double level) {
                    return end < level;
                }
            };
        else //charging
            this.comparator = new Comp() {
                @Override
                public boolean compare(double end, double level) {
                    return end > level;
                }
            };

    }

    @Override
    public boolean canContinue() {
        return comparator.compare(this.endBatteryLevel, batteryNotificator.getCurrentLevel());
    }


    private interface Comp {
        boolean compare(double end, double level);
    }

}
