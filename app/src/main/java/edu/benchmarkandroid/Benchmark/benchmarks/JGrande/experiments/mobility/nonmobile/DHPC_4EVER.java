/*package edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile;

import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.Statistics;
import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.ep.DHPC_EPBench;
import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.fft.DHPC_FFTBench;
import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.hanoi.DHPC_HanoiBench;
import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.prime.DHPC_PrimeBench;
import edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.sieve.DHPC_SieveBench;

public class DHPC_4EVER {

    public static void main(String argv[]) {
        while(true){
            int runs = 1;
            int size = 0;
            double[] CPUSnapshots = new double[runs];

            //ApplicationContext appContext = new FileSystemXmlApplicationContext(
            //		"configuration/nonmobile-test.xml");
            try{
                for (int i = 0; i < runs; i++) {
                    long l = System.currentTimeMillis();

                    DHPC_FFTBench ffb = new DHPC_FFTBench();
                    ffb.JGFrun(size);
                    CPUSnapshots[i] = System.currentTimeMillis() - l;
                }
                System.out.println("DHPC_FFTBench\t" + Statistics.average(CPUSnapshots)
                        + "\t" + Statistics.standardDeviation(CPUSnapshots));
            } catch (Throwable e) {
                System.out.println("Error executing DHPC_FFTBench");
                e.printStackTrace(System.out);
            }

            try{
                for (int i = 0; i < runs; i++) {
                    long l = System.currentTimeMillis();
                    DHPC_SieveBench sb = new DHPC_SieveBench();
                    sb.JGFrun(size);
                    CPUSnapshots[i] = System.currentTimeMillis() - l;
                }
                System.out.println("DHPC_SieveBench\t"
                        + Statistics.average(CPUSnapshots) + "\t"
                        + Statistics.standardDeviation(CPUSnapshots));
            } catch (Throwable e) {
                System.out.println("Error executing DHPC_SieveBench");
                e.printStackTrace(System.out);
            }

            try{
                for (int i = 0; i < runs; i++) {
                    long l = System.currentTimeMillis();
                    DHPC_HanoiBench hb = new DHPC_HanoiBench();
                    hb.JGFrun(size);
                    CPUSnapshots[i] = System.currentTimeMillis() - l;
                }
                System.out.println("DHPC_HanoiBench\t"
                        + Statistics.average(CPUSnapshots) + "\t"
                        + Statistics.standardDeviation(CPUSnapshots));
            } catch (Throwable e) {
                System.out.println("Error executing DHPC_HanoiBench");
                e.printStackTrace(System.out);
            }


            try{
                for (int i = 0; i < runs; i++) {
                    long l = System.currentTimeMillis();
                    DHPC_EPBench eb = new DHPC_EPBench();
                    eb.JGFrun(size);
                    CPUSnapshots[i] = System.currentTimeMillis() - l;
                }
                System.out.println("DHPC_EPBench\t" + Statistics.average(CPUSnapshots)
                        + "\t" + Statistics.standardDeviation(CPUSnapshots));
            } catch (Throwable e) {
                System.out.println("Error executing DHPC_EPBench");
                e.printStackTrace(System.out);
            }


            try{
                for (int i = 0; i < runs; i++) {
                    long l = System.currentTimeMillis();
                    DHPC_PrimeBench kb =new DHPC_PrimeBench();
                    kb.JGFrun(size);
                    CPUSnapshots[i] = System.currentTimeMillis() - l;
                }
                System.out.println("DHPC_PrimeBench\t"
                        + Statistics.average(CPUSnapshots) + "\t"
                        + Statistics.standardDeviation(CPUSnapshots));
            } catch (Throwable e) {
                System.out.println("Error executing DHPC_PrimeBench");
                e.printStackTrace(System.out);
            }

        }
    }
}*/