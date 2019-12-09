package edu.benchmarkandroid.Benchmark.benchmarks.JGrande.runner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import android.os.Environment;

import edu.benchmarkandroid.Benchmark.Benchmark;
import edu.benchmarkandroid.Benchmark.StopCondition;
//import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.Runner;
import edu.benchmarkandroid.Benchmark.Variant;
import  edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.DHPC_AllSizeA;
import edu.benchmarkandroid.MainActivity;
import edu.benchmarkandroid.service.ProgressUpdater;

public class CBenchmark4Ever extends Benchmark {

    private static final String EMPTY_PAYLOAD = "empty";

    public CBenchmark4Ever(Variant variant) {
        super(variant);
    }

//    @Override
//    public void execute() {
//        FileOutputStream ba=null;
//        try {
//            ba = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/cbench4e.txt");
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        PrintStream ps=System.out;
//        PrintStream ps1=null;
//        if (ba!=null) {
//            ps1=new PrintStream(ba);
//            System.setOut(ps1);
//        }
//        boolean t=true;
//        while(t)
//            DHPC_AllSizeA.main(null);
//        System.setOut(ps);
//        if (ba!=null) {
//            ps1.flush();
//            ps1.close();
//        }
//    }

    @Override
    public void runSampling(StopCondition stopCondition, ProgressUpdater progressUpdater) {
    progressUpdater.end(EMPTY_PAYLOAD);
    }

    @Override
    public void runBenchmark(StopCondition stopCondition, ProgressUpdater progressUpdater) {
        FileOutputStream ba=null;
        try {
            ba = new FileOutputStream(MainActivity.PATH+"cbench4e.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //TODO
        PrintStream ps=System.out;
        PrintStream ps1=null;
        if (ba!=null) {
            ps1=new PrintStream(ba);
            System.setOut(ps1);
        }

        while(stopCondition.canContinue())
            DHPC_AllSizeA.main(null);
        System.setOut(ps);
        if (ba!=null) {
            ps1.flush();
            ps1.close();
        }
    }
}