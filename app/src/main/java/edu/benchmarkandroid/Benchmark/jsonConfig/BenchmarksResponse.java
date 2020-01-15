package edu.benchmarkandroid.Benchmark.jsonConfig;

import android.os.Parcel;
import android.os.Parcelable;

public class BenchmarksResponse implements Parcelable {

    Boolean message;
    BenchmarkData benchmarkData;

    public BenchmarksResponse() {
    }

    protected BenchmarksResponse(Parcel in) {
        byte tmpMessage = in.readByte();
        message = tmpMessage == 0 ? null : tmpMessage == 1;
        benchmarkData = in.readParcelable(BenchmarkData.class.getClassLoader());
    }

    public static final Creator<BenchmarksResponse> CREATOR = new Creator<BenchmarksResponse>() {
        @Override
        public BenchmarksResponse createFromParcel(Parcel in) {
            return new BenchmarksResponse(in);
        }

        @Override
        public BenchmarksResponse[] newArray(int size) {
            return new BenchmarksResponse[size];
        }
    };

    public Boolean getMessage() {
        return message;
    }

    public void setMessage(Boolean message) {
        this.message = message;
    }

    public BenchmarkData getBenchmarkData() {
        return benchmarkData;
    }

    public void setBenchmarkData(BenchmarkData benchmarkData) {
        this.benchmarkData = benchmarkData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (message == null ? 0 : message ? 1 : 2));
        dest.writeParcelable(benchmarkData, flags);
    }

    @Override
    public String toString() {
        return "BenchmarksResponse{" +
                "message=" + message +
                ", benchmarkData=" + benchmarkData +
                '}';
    }
}
