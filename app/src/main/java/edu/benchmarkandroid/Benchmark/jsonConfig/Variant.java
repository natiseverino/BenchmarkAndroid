package edu.benchmarkandroid.Benchmark.jsonConfig;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Variant implements Parcelable {

    @SerializedName("variantId")
    @Expose
    private String variantId = UUID.randomUUID().toString();


    @SerializedName("paramsSamplingStage")
    @Expose
    private ParamsSamplingStage paramsSamplingStage;


    @SerializedName("paramsRunStage")
    @Expose
    private ParamsRunStage paramsRunStage;


    @SerializedName("energyPreconditionSamplingStage")
    @Expose
    private EnergyPreconditionSamplingStage energyPreconditionSamplingStage;


    @SerializedName("energyPreconditionRunStage")
    @Expose
    private EnergyPreconditionRunStage energyPreconditionRunStage;


    protected Variant(Parcel in) {
        variantId = in.readString();
        paramsSamplingStage = in.readParcelable(ParamsSamplingStage.class.getClassLoader());
        paramsRunStage = in.readParcelable(ParamsRunStage.class.getClassLoader());
        energyPreconditionSamplingStage = in.readParcelable(EnergyPreconditionSamplingStage.class.getClassLoader());
        energyPreconditionRunStage = in.readParcelable(EnergyPreconditionRunStage.class.getClassLoader());
    }

    public static final Creator<Variant> CREATOR = new Creator<Variant>() {
        @Override
        public Variant createFromParcel(Parcel in) {
            return new Variant(in);
        }

        @Override
        public Variant[] newArray(int size) {
            return new Variant[size];
        }
    };

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public ParamsSamplingStage getParamsSamplingStage() {
        return paramsSamplingStage;
    }

    public void setParamsSamplingStage(ParamsSamplingStage paramsSamplingStage) {
        this.paramsSamplingStage = paramsSamplingStage;
    }

    public ParamsRunStage getParamsRunStage() {
        return paramsRunStage;
    }

    public void setParamsRunStage(ParamsRunStage paramsRunStage) {
        this.paramsRunStage = paramsRunStage;
    }

    public EnergyPreconditionSamplingStage getEnergyPreconditionSamplingStage() {
        return energyPreconditionSamplingStage;
    }

    public void setEnergyPreconditionSamplingStage(EnergyPreconditionSamplingStage energyPreconditionSamplingStage) {
        this.energyPreconditionSamplingStage = energyPreconditionSamplingStage;
    }

    public EnergyPreconditionRunStage getEnergyPreconditionRunStage() {
        return energyPreconditionRunStage;
    }

    public void setEnergyPreconditionRunStage(EnergyPreconditionRunStage energyPreconditionRunStage) {
        this.energyPreconditionRunStage = energyPreconditionRunStage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(variantId);
        dest.writeParcelable(paramsSamplingStage, flags);
        dest.writeParcelable(paramsRunStage, flags);
        dest.writeParcelable(energyPreconditionSamplingStage, flags);
        dest.writeParcelable(energyPreconditionRunStage, flags);
    }


    @Override
    public String toString() {
        return "Variant{" +
                "variantId='" + variantId + '\'' +
                ", paramsSamplingStage=" + paramsSamplingStage +
                ", paramsRunStage=" + paramsRunStage +
                ", energyPreconditionSamplingStage=" + energyPreconditionSamplingStage +
                ", energyPreconditionRunStage=" + energyPreconditionRunStage +
                '}';
    }
}
