package edu.benchmarkandroid.Benchmark.jsonConfig;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EnergyPreconditionRunStage implements Parcelable {

    @SerializedName("requiredBatteryState")
    @Expose
    private String requiredBatteryState = "DISCHARGING";

    @SerializedName("minStartBatteryLevel")
    @Expose
    private Double minStartBatteryLevel = 1d;

    @SerializedName("minEndBatteryLevel")
    @Expose
    private Double minEndBatteryLevel = 0.01;


    protected EnergyPreconditionRunStage(Parcel in) {
        requiredBatteryState = in.readString();
        if (in.readByte() == 0) {
            minStartBatteryLevel = null;
        } else {
            minStartBatteryLevel = in.readDouble();
        }
        if (in.readByte() == 0) {
            minEndBatteryLevel = null;
        } else {
            minEndBatteryLevel = in.readDouble();
        }
    }

    public static final Creator<EnergyPreconditionRunStage> CREATOR = new Creator<EnergyPreconditionRunStage>() {
        @Override
        public EnergyPreconditionRunStage createFromParcel(Parcel in) {
            return new EnergyPreconditionRunStage(in);
        }

        @Override
        public EnergyPreconditionRunStage[] newArray(int size) {
            return new EnergyPreconditionRunStage[size];
        }
    };

    public String getRequiredBatteryState() {
        return requiredBatteryState;
    }

    public void setRequiredBatteryState(String requiredBatteryState) {
        this.requiredBatteryState = requiredBatteryState;
    }

    public Double getMinStartBatteryLevel() {
        return minStartBatteryLevel;
    }

    public void setMinStartBatteryLevel(Double minStartBatteryLevel) {
        this.minStartBatteryLevel = minStartBatteryLevel;
    }

    public Double getMinEndBatteryLevel() {
        if (minEndBatteryLevel == 0.0)
            minEndBatteryLevel = 0.01;
        return minEndBatteryLevel;
    }

    public void setMinEndBatteryLevel(Double minEndBatteryLevel) {
        this.minEndBatteryLevel = minEndBatteryLevel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(requiredBatteryState);
        if (minStartBatteryLevel == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(minStartBatteryLevel);
        }
        if (minEndBatteryLevel == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(minEndBatteryLevel);
        }
    }

    @Override
    public String toString() {
        return "EnergyPreconditionRunStage{" +
                "requiredBatteryState='" + requiredBatteryState + '\'' +
                ", minStartBatteryLevel=" + minStartBatteryLevel +
                ", minEndBatteryLevel=" + minEndBatteryLevel +
                '}';
    }
}