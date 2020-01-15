package edu.benchmarkandroid.Benchmark.jsonConfig;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EnergyPreconditionSamplingStage implements Parcelable {

    @SerializedName("requiredBatteryState")
    @Expose
    private String requiredBatteryState = "CHARGING";

    @SerializedName("minStartBatteryLevel")
    @Expose
    private Double minStartBatteryLevel = 0.10;

    protected EnergyPreconditionSamplingStage(Parcel in) {
        requiredBatteryState = in.readString();
        if (in.readByte() == 0) {
            minStartBatteryLevel = null;
        } else {
            minStartBatteryLevel = in.readDouble();
        }
    }

    public static final Creator<EnergyPreconditionSamplingStage> CREATOR = new Creator<EnergyPreconditionSamplingStage>() {
        @Override
        public EnergyPreconditionSamplingStage createFromParcel(Parcel in) {
            return new EnergyPreconditionSamplingStage(in);
        }

        @Override
        public EnergyPreconditionSamplingStage[] newArray(int size) {
            return new EnergyPreconditionSamplingStage[size];
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
    }

    @Override
    public String toString() {
        return "EnergyPreconditionSamplingStage{" +
                "requiredBatteryState='" + requiredBatteryState + '\'' +
                ", minStartBatteryLevel=" + minStartBatteryLevel +
                '}';
    }
}
