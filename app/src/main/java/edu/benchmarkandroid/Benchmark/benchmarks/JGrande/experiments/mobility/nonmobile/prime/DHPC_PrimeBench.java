package edu.benchmarkandroid.Benchmark.benchmarks.JGrande.experiments.mobility.nonmobile.prime;

public class DHPC_PrimeBench {

	private int size;

	private int datasizes[] = { 10000000, 10000000, 10000000 };

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

	public void JGFrun(int size) {

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