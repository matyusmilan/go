package com.mmatyus.go.model;

public class PlayerPolicy {
  public int     iterations    = 300000;
  public double  uctBias       = 0.6;
  public double  fpu           = Double.MAX_VALUE; // first play urgency
  public double  passThreshold = 0.005;
  public boolean useUCBPrior   = true;
  public double  forceOfPrior  = 1.0;
  public boolean preferKill    = true;

  public PlayerPolicy( int amountOfIteration ) {
    this.iterations = amountOfIteration;
  }
}
