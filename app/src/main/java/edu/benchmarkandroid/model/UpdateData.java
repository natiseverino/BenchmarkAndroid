package edu.benchmarkandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UpdateData implements Parcelable {

    private int cpu_mhz;
    private int battery_Mah;
    private double minStartBatteryLevel;
    private double currentBatteryLevel;

    public UpdateData(int cpu_mhz, int battery_Mah, double minStartBatteryLevel, double currentBatteryLevel) {
        this.cpu_mhz = cpu_mhz;
        this.battery_Mah = battery_Mah;
        this.minStartBatteryLevel = minStartBatteryLevel;
        this.currentBatteryLevel = currentBatteryLevel;
    }


    protected UpdateData(Parcel in) {
        cpu_mhz = in.readInt();
        battery_Mah = in.readInt();
        minStartBatteryLevel = in.readDouble();
        currentBatteryLevel = in.readDouble();
    }

    public static final Creator<UpdateData> CREATOR = new Creator<UpdateData>() {
        @Override
        public UpdateData createFromParcel(Parcel in) {
            return new UpdateData(in);
        }

        @Override
        public UpdateData[] newArray(int size) {
            return new UpdateData[size];
        }
    };

    public int getCpu_mhz() {
        return cpu_mhz;
    }

    public void setCpu_mhz(int cpu_mhz) {
        this.cpu_mhz = cpu_mhz;
    }

    public int getBattery_Mah() {
        return battery_Mah;
    }

    public void setBattery_Mah(int battery_Mah) {
        this.battery_Mah = battery_Mah;
    }

    public double getMinStartBatteryLevel() {
        return minStartBatteryLevel;
    }

    public void setMinStartBatteryLevel(double minStartBatteryLevel) {
        this.minStartBatteryLevel = minStartBatteryLevel;
    }

    public double getCurrentBatteryLevel() {
        return currentBatteryLevel;
    }

    public void setCurrentBatteryLevel(double currentBatteryLevel) {
        this.currentBatteryLevel = currentBatteryLevel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cpu_mhz);
        dest.writeInt(battery_Mah);
        dest.writeDouble(minStartBatteryLevel);
        dest.writeDouble(currentBatteryLevel);
    }
}
