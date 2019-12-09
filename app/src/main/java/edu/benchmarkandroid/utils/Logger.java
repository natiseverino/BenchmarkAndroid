package edu.benchmarkandroid.utils;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Logger {

    private static Logger INSTANCE;


    private static final String TAG = "Logger";

    private BufferedWriter bw;

    private int counter = 0;
    private static String fname = "";

    private Logger(String fname) throws FileNotFoundException{
        this.bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fname))));
    }

    public static void init(String fname) {
        Logger.fname = "/sdcard/Download/"+fname;
        Log.d(TAG, "init: "+fname);
    }

    public synchronized static Logger getInstance() throws FileNotFoundException {
        if (INSTANCE == null) INSTANCE = new Logger(fname);
        return INSTANCE;
    }

    public String getFileName() {
        return fname;
    }

    public synchronized void write(String s) {
        try {
            bw.write(Long.toString(System.currentTimeMillis()) + "," + s + "\n");
            counter++;
            if (counter > 10) {
                bw.flush();
                counter = 0;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error adding line", e);
        }
    }

    public synchronized void flush() {
        try {
            bw.flush();
        } catch (IOException e) {
            Log.e(TAG, "Error writting file", e);
        }
    }

    @Override
    public synchronized void finalize() throws Throwable {
        super.finalize();
        this.flush();
        bw.close();
        Log.d(TAG, "finalize logger");
        Logger.fname = "";
        INSTANCE = null;
    }


}