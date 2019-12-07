package edu.benchmarkandroid.Benchmark.benchmarks.JGrande.runner.linpack;

import java.util.Random;

public class Linpack {
  String applet_version = "LinpackJavaV2.1";
  static final int psize = 500;
  double mflops_result;
  double residn_result;
  double time_result;
  double eps_result;
  double total;
  double second_orig = -1.0D;

  final double abs(double paramDouble)
  {
    if (paramDouble >= 0.0D)
      return paramDouble;
    return -paramDouble;
  }

  double second()
  {
    if (this.second_orig == -1.0D)
      this.second_orig = System.currentTimeMillis();
    return (System.currentTimeMillis() - this.second_orig) / 1000.0D;
  }

  public void run_benchmark(int paramInt1, int paramInt2)
  {
    int i = paramInt2 + 1;
    double[][] arrayOfDouble = new double[paramInt2][i];
    double[] arrayOfDouble1 = new double[paramInt2];
    double[] arrayOfDouble2 = new double[paramInt2];
    int[] arrayOfInt = new int[paramInt2];
    double d1 = 0.056D;
    double d2 = 2.0D * (paramInt1 * paramInt1 * paramInt1) / 3.0D + 2.0D * (paramInt1 * paramInt1);
    double d3 = matgen(arrayOfDouble, i, paramInt1, arrayOfDouble1);
    double d6 = second();
    int k = dgefa(arrayOfDouble, i, paramInt1, arrayOfInt);
    dgesl(arrayOfDouble, i, paramInt1, arrayOfInt, arrayOfDouble1, 0);
    this.total = (second() - d6);
    for (int j = 0; j < paramInt1; j++)
      arrayOfDouble2[j] = arrayOfDouble1[j];
    d3 = matgen(arrayOfDouble, i, paramInt1, arrayOfDouble1);
    for (int j = 0; j < paramInt1; j++)
      arrayOfDouble1[j] = (-arrayOfDouble1[j]);
    dmxpy(paramInt1, arrayOfDouble1, paramInt1, i, arrayOfDouble2, arrayOfDouble);
    double d5 = 0.0D;
    double d4 = 0.0D;
    for (int j = 0; j < paramInt1; j++)
    {
      double d7 = arrayOfDouble1[j];
      d7 = arrayOfDouble1[j];
      d5 = d7 >= 0.0D ? d7 : d5 > (d7 >= 0.0D ? d7 : -d7) ? d5 : -d7;
      d7 = arrayOfDouble2[j];
      d7 = arrayOfDouble2[j];
      d4 = d7 >= 0.0D ? d7 : d4 > (d7 >= 0.0D ? d7 : -d7) ? d4 : -d7;
    }
    this.eps_result = epslon(1.0D);
    this.residn_result = (d5 / (paramInt1 * d3 * d4 * this.eps_result));
    this.residn_result += 0.005D;
    this.residn_result = (int)(this.residn_result * 100.0D);
    this.residn_result /= 100.0D;
    this.time_result = this.total;
    this.time_result += 0.005D;
    this.time_result = (int)(this.time_result * 100.0D);
    this.time_result /= 100.0D;
    this.mflops_result = (d2 / (1000000.0D * this.total));
    this.mflops_result += 0.0005D;
    this.mflops_result = (int)(this.mflops_result * 1000.0D);
    this.mflops_result /= 1000.0D;
  }

  final double matgen(double[][] paramArrayOfDouble, int paramInt1, int paramInt2, double[] paramArrayOfDouble1)
  {
    int i = 1325;
    double d = 0.0D;
    Random localRandom = new Random();
    localRandom.setSeed(i);
    for (int j = 0; j < paramInt2; j++)
      for (int k = 0; k < paramInt2; k++)
      {
        paramArrayOfDouble[k][j] = (localRandom.nextDouble() - 0.5D);
        d = paramArrayOfDouble[k][j] > d ? paramArrayOfDouble[k][j] : d;
      }
    for (int j = 0; j < paramInt2; j++)
      paramArrayOfDouble1[j] = 0.0D;
    for (int k = 0; k < paramInt2; k++)
      for (int j = 0; j < paramInt2; j++)
        paramArrayOfDouble1[j] += paramArrayOfDouble[k][j];
    return d;
  }

  final int dgefa(double[][] paramArrayOfDouble, int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    int i1 = 0;
    int n = paramInt2 - 1;
    if (n >= 0)
      for (int j = 0; j < n; j++)
      {
        double[] arrayOfDouble1 = paramArrayOfDouble[j];
        int k = j + 1;
        int m = idamax(paramInt2 - j, arrayOfDouble1, j, 1) + j;
        paramArrayOfInt[j] = m;
        if (arrayOfDouble1[m] != 0.0D)
        {
          if (m != j)
          {
            double d = arrayOfDouble1[m];
            arrayOfDouble1[m] = arrayOfDouble1[j];
            arrayOfDouble1[j] = d;
          }
          double d = -1.0D / arrayOfDouble1[j];
          dscal(paramInt2 - k, d, arrayOfDouble1, k, 1);
          for (int i = k; i < paramInt2; i++)
          {
            double[] arrayOfDouble2 = paramArrayOfDouble[i];
            d = arrayOfDouble2[m];
            if (m != j)
            {
              arrayOfDouble2[m] = arrayOfDouble2[j];
              arrayOfDouble2[j] = d;
            }
            daxpy(paramInt2 - k, d, arrayOfDouble1, k, 1, arrayOfDouble2, k, 1);
          }
        }
        else
        {
          i1 = j;
        }
      }
    paramArrayOfInt[(paramInt2 - 1)] = (paramInt2 - 1);
    if (paramArrayOfDouble[(paramInt2 - 1)][(paramInt2 - 1)] == 0.0D)
      i1 = paramInt2 - 1;
    return i1;
  }

  final void dgesl(double[][] paramArrayOfDouble, int paramInt1, int paramInt2, int[] paramArrayOfInt, double[] paramArrayOfDouble1, int paramInt3)
  {
    int m = paramInt2 - 1;
    int k;
    double d;
    int n;
    int j;
    if (paramInt3 == 0)
    {
      if (m >= 1)
        for (int i = 0; i < m; i++)
        {
          k = paramArrayOfInt[i];
          d = paramArrayOfDouble1[k];
          if (k != i)
          {
            paramArrayOfDouble1[k] = paramArrayOfDouble1[i];
            paramArrayOfDouble1[i] = d;
          }
          n = i + 1;
          daxpy(paramInt2 - n, d, paramArrayOfDouble[i], n, 1, paramArrayOfDouble1, n, 1);
        }
      for (j = 0; j < paramInt2; j++)
      {
        int i = paramInt2 - (j + 1);
        paramArrayOfDouble1[i] /= paramArrayOfDouble[i][i];
        d = -paramArrayOfDouble1[i];
        daxpy(i, d, paramArrayOfDouble[i], 0, 1, paramArrayOfDouble1, 0, 1);
      }
      return;
    }
    for (int i = 0; i < paramInt2; i++)
    {
      d = ddot(i, paramArrayOfDouble[i], 0, 1, paramArrayOfDouble1, 0, 1);
      paramArrayOfDouble1[i] = ((paramArrayOfDouble1[i] - d) / paramArrayOfDouble[i][i]);
    }
    if (m >= 1)
      for (j = 0; j < m; j++)
      {
        int i = paramInt2 - (j + 1);
        n = i + 1;
        paramArrayOfDouble1[i] += ddot(paramInt2 - n, paramArrayOfDouble[i], n, 1, paramArrayOfDouble1, n, 1);
        k = paramArrayOfInt[i];
        if (k == i)
          continue;
        d = paramArrayOfDouble1[k];
        paramArrayOfDouble1[k] = paramArrayOfDouble1[i];
        paramArrayOfDouble1[i] = d;
      }
  }

  final void daxpy(int paramInt1, double paramDouble, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5)
  {
    if ((paramInt1 > 0) && (paramDouble != 0.0D))
    {
      if ((paramInt3 != 1) || (paramInt5 != 1))
      {
        int j = 0;
        int k = 0;
        if (paramInt3 < 0)
          j = (-paramInt1 + 1) * paramInt3;
        if (paramInt5 < 0)
          k = (-paramInt1 + 1) * paramInt5;
        for (int i = 0; i < paramInt1; i++)
        {
          paramArrayOfDouble2[(k + paramInt4)] += paramDouble * paramArrayOfDouble1[(j + paramInt2)];
          j += paramInt3;
          k += paramInt5;
        }
        return;
      }
      for (int i = 0; i < paramInt1; i++)
        paramArrayOfDouble2[(i + paramInt4)] += paramDouble * paramArrayOfDouble1[(i + paramInt2)];
    }
  }

  final double ddot(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5)
  {
    double d = 0.0D;
    if (paramInt1 > 0)
    {
      int i;
      if ((paramInt3 != 1) || (paramInt5 != 1))
      {
        int j = 0;
        int k = 0;
        if (paramInt3 < 0)
          j = (-paramInt1 + 1) * paramInt3;
        if (paramInt5 < 0)
          k = (-paramInt1 + 1) * paramInt5;
        for (i = 0; i < paramInt1; i++)
        {
          d += paramArrayOfDouble1[(j + paramInt2)] * paramArrayOfDouble2[(k + paramInt4)];
          j += paramInt3;
          k += paramInt5;
        }
      }
      else
      {
        for (i = 0; i < paramInt1; i++)
          d += paramArrayOfDouble1[(i + paramInt2)] * paramArrayOfDouble2[(i + paramInt4)];
      }
    }
    return d;
  }

  final void dscal(int paramInt1, double paramDouble, double[] paramArrayOfDouble, int paramInt2, int paramInt3)
  {
    if (paramInt1 > 0)
    {
      if (paramInt3 != 1)
      {
        int j = paramInt1 * paramInt3;
        int i = 0;
        while (i < j)
        {
          paramArrayOfDouble[(i + paramInt2)] *= paramDouble;
          i += paramInt3;
        }
        return;
      }
      for (int i = 0; i < paramInt1; i++)
        paramArrayOfDouble[(i + paramInt2)] *= paramDouble;
    }
  }

  final int idamax(int paramInt1, double[] paramArrayOfDouble, int paramInt2, int paramInt3)
  {
    int k = 0;
    if (paramInt1 < 1)
    {
      k = -1;
    }
    else if (paramInt1 == 1)
    {
      k = 0;
    }
    else
    {
      double d1;
      int i;
      double d2;
      if (paramInt3 != 1)
      {
        d1 = paramArrayOfDouble[paramInt2] < 0.0D ? -paramArrayOfDouble[paramInt2] : paramArrayOfDouble[paramInt2];
        int j = 1 + paramInt3;
        for (i = 0; i < paramInt1; i++)
        {
          d2 = paramArrayOfDouble[(j + paramInt2)] < 0.0D ? -paramArrayOfDouble[(j + paramInt2)] : paramArrayOfDouble[(j + paramInt2)];
          if (d2 > d1)
          {
            k = i;
            d1 = d2;
          }
          j += paramInt3;
        }
      }
      else
      {
        k = 0;
        d1 = paramArrayOfDouble[paramInt2] < 0.0D ? -paramArrayOfDouble[paramInt2] : paramArrayOfDouble[paramInt2];
        for (i = 0; i < paramInt1; i++)
        {
          d2 = paramArrayOfDouble[(i + paramInt2)] < 0.0D ? -paramArrayOfDouble[(i + paramInt2)] : paramArrayOfDouble[(i + paramInt2)];
          if (d2 <= d1)
            continue;
          k = i;
          d1 = d2;
        }
      }
    }
    return k;
  }

  final double epslon(double paramDouble)
  {
    double d1 = 1.333333333333333D;
    double d4 = 0.0D;
    while (d4 == 0.0D)
    {
      double d2 = d1 - 1.0D;
      double d3 = d2 + d2 + d2;
      double d5 = d3 - 1.0D;
      d4 = d5 >= 0.0D ? d5 : -d5;
    }
    return d4 * (paramDouble >= 0.0D ? paramDouble : -paramDouble);
  }

  final void dmxpy(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, double[][] paramArrayOfDouble)
  {
    for (int i = 0; i < paramInt2; i++)
      for (int j = 0; j < paramInt1; j++)
        paramArrayOfDouble1[j] += paramArrayOfDouble2[i] * paramArrayOfDouble[i][j];
  }

  public Linpack() {
	  int times = 30;
	  double[] results = new double[times];
	  for (int i=0; i<times+1; i++) {
		  System.out.println("...running benchmark (" + 500 + "x" + 500 + ")");
		  run_benchmark(500, 1000);
		  System.out.println("this.mflops_result: " + this.mflops_result);
		  System.out.println("this.residn_result: " + this.residn_result);
		  System.out.println("this.time_result: " + this.time_result);    		  
		  System.out.println("this.eps_result: " + this.eps_result);
		  if (i>0) {
			  results[i-1] = this.mflops_result;
		  }
	  }
	  double avg = calcAvg(results);
	  double stdev = calcStanDev(results);
	  System.out.println("Avg. mflops: " + avg);
	  System.out.println("Std. dev mflops: " + stdev);
	  System.out.println("Std. dev/Avg.: " + stdev/avg);
  }
  
  public double calcAvg(double[] s) {
	  int n = s.length;
	  double total = 0;
	  for (int i = 0; i < n; i++) {
		  total += s[i];
	  }
	  return total/(double)n;
  }

  double calcStanDev(double[] s) {
	  return Math.pow(calcVariance(s), 0.5);
  }

  public double calcVariance(double[] s) {
	  int n = s.length;
	  double total = 0;
	  double sTotal = 0;
	  double scalar = 1/(double)(n-1);
	  for (int i = 0; i < n; i++) {
		  total += s[i];
		  sTotal += Math.pow(s[i], 2);
	  }
	  return (scalar*(sTotal - (Math.pow(total, 2)/n)));
  }
  
  public static void main(String[] args) {
	  new Linpack();
  }
}