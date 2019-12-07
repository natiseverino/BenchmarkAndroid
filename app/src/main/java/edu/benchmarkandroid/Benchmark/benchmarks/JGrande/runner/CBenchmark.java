package edu.benchmarkandroid.Benchmark.benchmarks.JGrande.runner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import android.os.Environment;

import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.Runner;
import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.DHPC_AllSizeA;


public class CBenchmark implements Runner {

	@Override
	public void execute() {
		FileOutputStream ba=null;
		try {
			ba = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/cbench.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintStream ps=System.out;
		PrintStream ps1=null;
		if (ba!=null) {
			ps1=new PrintStream(ba);
			System.setOut(ps1);
		}
		DHPC_AllSizeA.main(null);
		System.setOut(ps);
		if (ba!=null) {
			ps1.flush();
			ps1.close();
		}
	}

}
