package edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.sieve;

import edu.benchmarkandroid.Benchmark.ParamsRunStage;
import edu.benchmarkandroid.service.ProgressUpdater;

public class DHPC_SieveBench {

	private int size;

	//TODO PARAMETROS
	private int datasizes[] = { 1, 1, 1 };
	private int m; //100000
	private int n; //8192

	public void JGFsetsize(int size) {
		this.size = size;
	}

	public void JGFinitialise() {
	}

	public void JGFkernel() {
		sieve(m, n);
	}

	public void JGFvalidate() {

	}

	public void JGFtidyup() {

	}

	public void JGFrun(int size, ParamsRunStage paramsRunStage) {

		this.datasizes = paramsRunStage.getSieve_datasizes();
		this.m = paramsRunStage.getM();
		this.n = paramsRunStage.getN();

		JGFsetsize(size);
		JGFinitialise();
		JGFkernel();
		JGFvalidate();
		JGFtidyup();
	}

	void sieve(int m, int n) {
		int i, k, ci;
		int count, size;
		int prime = 0;
		long N_Prime, L_Prime;
		boolean flags[] = new boolean[m];
		long iter, j;
		int ptr = 0;

		size = m - 1;

		N_Prime = 0L;
		L_Prime = 0L;

		j = 0;
		for (iter = 1; iter <= n; iter++) {
			count = 0;

			for (i = 0; i <= size; i++)
				flags[ptr + i] = true;

			ci = 0;
			for (i = 0; i <= size; i++) {
				if (flags[ptr + i]) {
					count++;
					prime = i + i + 3;
					for (k = i + prime; k <= size; k += prime) {
						ci++;
						flags[ptr + k] = false;
					}

				}
			}

			j = j + count;
		}

		N_Prime = j / n;
		L_Prime = prime;

	}

}
