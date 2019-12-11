package edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.prime;

import edu.benchmarkandroid.Benchmark.ParamsRunStage;

public class DHPC_PrimeBench {

	//TODO PARAMETROS

	private int size;

	private int datasizes[]/* = { 10000000, 10000000, 10000000 }*/;

	public void JGFsetsize(int size) {
		this.size = size;
	}

	public void JGFinitialise() {

	}

	public void JGFkernel() {
		for (int i = 2; i <= datasizes[size]; i++) {
			isPrime(i);
		}

	}

	public void JGFvalidate() {
	}

	public void JGFtidyup() {
	}

	public void JGFrun(int size , ParamsRunStage paramsRunStage) {
		this.datasizes = paramsRunStage.getPrime_datasizes();
		JGFsetsize(size);
		JGFinitialise();
		JGFkernel();
		JGFvalidate();
		JGFtidyup();

	}

	public boolean isPrime(int num) {
		boolean prime = true;
		int limit = (int) Math.sqrt(num);

		for (int i = 2; i <= limit; i++) {
			if (num % i == 0) {
				prime = false;
				break;
			}
		}

		return prime;
	}

}