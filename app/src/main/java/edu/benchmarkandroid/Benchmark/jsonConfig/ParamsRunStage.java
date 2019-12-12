package edu.benchmarkandroid.Benchmark.jsonConfig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParamsRunStage {

    //CPU
    @SerializedName("cpuLevel")
    @Expose
    private Double cpuLevel = 0.0;

    @SerializedName("screenState")
    @Expose
    private String screenState = "on";

    //JGrande
    @SerializedName("runs")
    @Expose
    private int runs = 2;

    @SerializedName("size")
    @Expose
    private int size = 0;

    //DHPC_FFT
    @SerializedName("FFT_datasizes")
    @Expose
    private int[] FFT_datasizes = {256, 256, 256};

    @SerializedName("n1")
    @Expose
    private int n1 = 64;

    @SerializedName("n2")
    @Expose
    private int n2 = 64;

    @SerializedName("n3")
    @Expose
    private int n3 = 64;

    @SerializedName("iterations")
    @Expose
    private int iterations = 60;

    //DHPC_Sieve
    @SerializedName("m")
    @Expose
    private int m = 100_000;

    @SerializedName("n")
    @Expose
    private int n = 8192;

    @SerializedName("Sieve_datasizes")
    @Expose
    private int[] Sieve_datasizes = {1, 1, 1};

    //DHPC_Hanoi
    @SerializedName("Hanoi_datasizes")
    @Expose
    private int[] Hanoi_datasizes = {28, 28, 28};

    //DHPC_EP
    @SerializedName("EP_n")
    @Expose
    private int EP_n = 16_777_216;

    @SerializedName("X")
    @Expose
    private double x = 271_828_183.0;

    @SerializedName("A")
    @Expose
    private double a = 1_220_703_125.0;

    @SerializedName("EP_datasizes")
    @Expose
    private int[] EP_datasizes = {1, 2, 3};

    //DHPC_Prime
    @SerializedName("Prime_datasizes")
    @Expose
    private int[] Prime_datasizes = {10_000_000, 10_000_000, 10_000_000};


    public int[] getFFT_datasizes() {
        return FFT_datasizes;
    }

    public void setFFT_datasizes(int[] FFT_datasizes) {
        this.FFT_datasizes = FFT_datasizes;
    }

    public int getN1() {
        return n1;
    }

    public void setN1(int n1) {
        this.n1 = n1;
    }

    public int getN2() {
        return n2;
    }

    public void setN2(int n2) {
        this.n2 = n2;
    }

    public int getN3() {
        return n3;
    }

    public void setN3(int n3) {
        this.n3 = n3;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getEP_n() {
        return EP_n;
    }

    public void setEP_n(int EP_n) {
        this.EP_n = EP_n;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public int[] getEP_datasizes() {
        return EP_datasizes;
    }

    public void setEP_datasizes(int[] EP_datasizes) {
        this.EP_datasizes = EP_datasizes;
    }

    public int[] getPrime_datasizes() {
        return Prime_datasizes;
    }

    public void setPrime_datasizes(int[] prime_datasizes) {
        Prime_datasizes = prime_datasizes;
    }

    public int[] getSieve_datasizes() {
        return Sieve_datasizes;
    }

    public void setSieve_datasizes(int[] sieve_datasizes) {
        Sieve_datasizes = sieve_datasizes;
    }

    public int[] getHanoi_datasizes() {
        return Hanoi_datasizes;
    }

    public void setHanoi_datasizes(int[] hanoi_datasizes) {
        Hanoi_datasizes = hanoi_datasizes;
    }

    public Double getCpuLevel() {
        return cpuLevel;
    }

    public void setCpuLevel(Double cpuLevel) {
        this.cpuLevel = cpuLevel;
    }

    public String getScreenState() {
        return screenState;
    }

    public void setScreenState(String screenState) {
        this.screenState = screenState;
    }

    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}

