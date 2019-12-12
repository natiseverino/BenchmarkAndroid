package edu.benchmarkandroid.Benchmark.jsonConfig;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParamsRunStage {

    //CPU
    @SerializedName("cpuLevel")
    @Expose
    private Double cpuLevel;
    @SerializedName("screenState")
    @Expose
    private String screenState;

    //JGrande
    @SerializedName("runs")
    @Expose
    private int runs;
    @SerializedName("size")
    @Expose
    private int size;

    //DHPC_FFT
    @SerializedName("FFT_datasizes")
    @Expose
    private int[] FFT_datasizes;
    @SerializedName("n1")
    @Expose
    private int n1;
    @SerializedName("n2")
    @Expose
    private int n2;
    @SerializedName("n3")
    @Expose
    private int n3;
    @SerializedName("iterations")
    @Expose
    private int iterations;

    //DHPC_Sieve
    @SerializedName("m")
    @Expose
    private int m;
    @SerializedName("n")
    @Expose
    private int n;
    @SerializedName("Sieve_datasizes")
    @Expose
    private int[] Sieve_datasizes;

    //DHPC_Hanoi
    @SerializedName("Hanoi_datasizes")
    @Expose
    private int[] Hanoi_datasizes;

    //DHPC_EP
    @SerializedName("EP_n")
    @Expose
    private int EP_n;
    @SerializedName("X")
    @Expose
    private  double x;
    @SerializedName("A")
    @Expose
    private double a;
    @SerializedName("EP_datasizes")
    @Expose
    private int[] EP_datasizes;

    //DHPC_Prime
    @SerializedName("Prime_datasizes")
    @Expose
    private int[] Prime_datasizes;


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

