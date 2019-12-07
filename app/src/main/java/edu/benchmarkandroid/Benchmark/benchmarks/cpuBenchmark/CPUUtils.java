package edu.benchmarkandroid.Benchmark.benchmarks.cpuBenchmark;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class CPUUtils {
    private static final String TAG = "CPUUtils";

    public static double readUsage() {

        String dir = "/sdcard/Download/cpu-usage-sample-1.txt";
        String dir2 = "/sdcard/Download/cpu-usage-sample-2.txt";

        File f1 = new File(dir);
        File f2 = new File(dir2);

        String[] toks = readFile(f1);

        long idle1 = Long.parseLong(toks[5]);
        long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);


        toks = readFile(f2);

        long idle2 = Long.parseLong(toks[5]);
        long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

        return (double) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

    }

    private static String[] readFile(File f) {
        RandomAccessFile reader;
        String[] toks = {" "};
        while (toks.length < 12)  // necesito 12 elementos en el arreglo
            if (f.exists()) {
                try {
                    reader = new RandomAccessFile(f, "r");
                    if (f.length() != 0) { //reviso que el tamaño del archivo no sea 0b
                        String load = reader.readLine();
                        if (load != null) {
                            toks = load.split(" ");
                        } else {
                            reader.seek(0); //TODO TEST
                            //throw new FileNotFoundException();
                        }
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        return toks;
    }

}
