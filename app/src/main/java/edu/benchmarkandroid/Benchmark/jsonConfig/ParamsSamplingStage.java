package edu.benchmarkandroid.Benchmark.jsonConfig;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParamsSamplingStage implements Parcelable {

    @SerializedName("convergenceThreshold")
    @Expose
    private Double convergenceThreshold = 0.01;

    protected ParamsSamplingStage(Parcel in) {
        if (in.readByte() == 0) {
            convergenceThreshold = null;
        } else {
            convergenceThreshold = in.readDouble();
        }
    }

    public static final Creator<ParamsSamplingStage> CREATOR = new Creator<ParamsSamplingStage>() {
        @Override
        public ParamsSamplingStage createFromParcel(Parcel in) {
            return new ParamsSamplingStage(in);
        }

        @Override
        public ParamsSamplingStage[] newArray(int size) {
            return new ParamsSamplingStage[size];
        }
    };

    public Double getConvergenceThreshold() {
        return convergenceThreshold;
    }

    public void setConvergenceThreshold(Double convergenceThreshold) {
        this.convergenceThreshold = convergenceThreshold;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (convergenceThreshold == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(convergenceThreshold);
        }
    }
}
