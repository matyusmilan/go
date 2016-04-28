package hu.mmatyus.algorithms.uct;

import hu.mmatyus.model.PlayerPolicy;

public class Statistic {
  protected int    amount;
  protected double x;
  protected double xx;

  public void add( double z ) {
    ++amount;
    x += z;
    xx += z*z;
  }

  public int count() {
    return amount;
  }

  public double mean() {
    return x / amount;
  }

  public double score( double nn, PlayerPolicy policy ) {
    if( nn == 0 ) {
      return 0.0;
    }
    double m = mean();
    return m + policy.uctBias * Math.sqrt( Math.log( nn ) / amount * Math.min( 1. / 4, xx / amount - m * m + Math.sqrt( 2 * Math.log( nn ) / ( amount ) ) ) );
  }

  public void multiply( double a ) {
    amount *= a;
    x *= a;
    xx *= a;
  }
}
