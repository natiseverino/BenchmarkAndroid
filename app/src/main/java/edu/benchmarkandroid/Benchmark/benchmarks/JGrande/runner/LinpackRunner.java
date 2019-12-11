package edu.benchmarkandroid.Benchmark.benchmarks.JGrande.runner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.runner.linpack.Linpack;


import android.os.Environment;
import android.util.Log;

public class LinpackRunner {

	private String tag="LinpackRunner";
	

	public void execute() {
		Log.d(tag, "Starting runner...");
		FileOutputStream ba=null;
		try {
			ba = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Linpack.txt");
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
		Log.d(tag, "Calling Linpack");
		try{
			Linpack.main(new String[0]);
		} catch(Exception e){
			e.printStackTrace(System.out);
		}
		Log.d(tag, "Ending Linpack");
		System.setOut(ps);
		if (ba!=null) {
			ps1.flush();
			ps1.close();
		}
		Log.d(tag, "Closing runner");
	}

}
