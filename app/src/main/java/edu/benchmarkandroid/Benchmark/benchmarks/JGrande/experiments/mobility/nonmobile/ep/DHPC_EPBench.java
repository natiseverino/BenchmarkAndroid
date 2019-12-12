package edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.ep;

import edu.benchmarkandroid.Benchmark.jsonConfig.ParamsRunStage;
import edu.benchmarkandroid.service.ProgressUpdater;

public class DHPC_EPBench {

	private static final String TAG = "DHPC_EPBench";

	private ProgressUpdater progressUpdater;

	static int n /*= 16777216*/; // Number of random numbers to generate

	static double X /*= 271828183.0*/; // Inital seed

	static double A /*= 1220703125.0*/; // Multiplier

	static int KS;

	static double R23, R46, T23, T46;

	private int size;

	private int datasizes[] /*= { 1, 2, 3 }*/; // Only a single size (Class S) is

	// supported

	public void JGFsetsize(int size) {
		this.size = size;
	}

	public void JGFinitialise() {
	}

	public void JGFkernel() {
		ep();
	}

	public void JGFvalidate() {
	}

	public void JGFtidyup() {
	}

	public void JGFrun(int size, ProgressUpdater progressUpdater, ParamsRunStage paramsRunStage) {

		this.progressUpdater = progressUpdater;
		n = paramsRunStage.getEP_n();
		X = paramsRunStage.getX();
		A = paramsRunStage.getA();
		datasizes = paramsRunStage.getEP_datasizes();

		JGFsetsize(size);
		JGFinitialise();
		JGFkernel();
		JGFvalidate();
		JGFtidyup();
	}

	public void ep() {
		// Generate Gaussian random number pairs in accordance
		// with NAS specifications

		double x, y, t, xy;
		int l;
		double xx = 0.0f;
		double yy = 0.0f;
		int Q[] = new int[10];
		double Xsum = 0.0f;
		double Ysum = 0.0f;

		for (int i = 0; i < 10; i++)
			Q[i] = 0;

		for (int j = 0; j < n; j++) {
			x = 2.0 * next() - 1.0;
			y = 2.0 * next() - 1.0;
			t = x * x + y * y;
			if (t <= 1.0) {
				xy = (double) Math.sqrt((-2.0 * Math.log(t)) / t);
				xx = x * xy;
				Xsum += xx;
				yy = y * xy;
				Ysum += yy;
				l = (int) Math.floor(Math.max(Math.abs(xx), Math.abs(yy)));
				Q[l]++;
			}
		}
		progressUpdater.update("Sum X:" + Xsum);
		progressUpdater.update("Sum Y:" + Ysum);
		for (l = 0; l < 10; l++)
			progressUpdater.update("Q[" + l + "]:" + Q[l]);
	}

	double next() {
		double T1, T2, T3, T4;
		double A1;
		double A2;
		double X1;
		double X2;
		double Z;
		int i, j;

		if (KS == 0) {
			R23 = 1.0;
			R46 = 1.0;
			T23 = 1.0;
			T46 = 1.0;

			for (i = 1; i <= 23; i++) {
				R23 = 0.50 * R23;
				T23 = 2.0 * T23;
			}
			for (i = 1; i <= 46; i++) {
				R46 = 0.50 * R46;
				T46 = 2.0 * T46;
			}
			KS = 1;
		}

		/* Break A into two parts such that A = 2^23 * A1 + A2 and set X = N. */

		T1 = R23 * A;
		j = (int) T1;
		A1 = j;
		A2 = A - T23 * A1;

		/*
		 * Break X into two parts such that X = 2^23 * X1 + X2, compute Z = A1 *
		 * X2 + A2 * X1 (mod 2^23), and then X = 2^23 * Z + A2 * X2 (mod 2^46).
		 */

		T1 = R23 * X;
		j = (int) T1;
		X1 = j;
		X2 = X - T23 * X1;
		T1 = A1 * X2 + A2 * X1;

		j = (int) (R23 * T1);
		T2 = j;
		Z = T1 - T23 * T2;
		T3 = T23 * Z + A2 * X2;
		j = (int) (R46 * T3);
		T4 = j;
		X = T3 - T46 * T4;
		return (R46 * X);
	}

}
