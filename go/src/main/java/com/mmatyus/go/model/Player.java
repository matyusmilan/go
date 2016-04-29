package com.mmatyus.go.model;

public class Player {
  public final PlayerType      playerType;
  public final Algorithm algo;
  public final int       param;

  public Player( PlayerType t, Algorithm a, int p ) {
    this.playerType = t;
    this.algo = a;
    this.param = p;
  }

  public Player( PlayerType t ) {
    this.playerType = t;
    this.algo = null;
    this.param = -1;
  }
}
