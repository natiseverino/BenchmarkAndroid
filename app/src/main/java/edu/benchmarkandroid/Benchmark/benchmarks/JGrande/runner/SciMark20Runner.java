//package edu.benchmarkandroid.Benchmark.benchmarks.JGrande.runner;
//
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.PrintStream;
//
//import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.Runner;
//import jnt.scimark2.commandline;
//
//import android.os.Environment;
//import android.util.Log;
//
//public class SciMark20Runner implements Runner {
//
//	private String tag="SciMark20Runner";
//
//	@Override
//	public void execute() {
//		Log.d(tag, "Starting runner...");
//		FileOutputStream ba=null;
//		try {
//			ba = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/sciMark20.txt");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		PrintStream ps=System.out;
//		PrintStream ps1=null;
//		if (ba!=null) {
//			ps1=new PrintStream(ba);
//			System.setOut(ps1);
//		}
//		Log.d(tag, "Calling SciMark20");
//		try{
//			commandline.run(new String[0]);
//		} catch(Exception e){
//			e.printStackTrace(System.out);
//		}
//		Log.d(tag, "Ending SciMark20");
//		System.setOut(ps);
//		if (ba!=null) {
//			ps1.flush();
//			ps1.close();
//		}
//		Log.d(tag, "Closing runner");
//	}
//
//}
