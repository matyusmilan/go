package hu.mmatyus.model;

public class Player {
  public enum Type {
    HUMAN
    , COMPUTER
  }
  public final Type type;
  public Algorithm algo;
  public int param;
  
  public Player(Type t, Algorithm a, int p){
    this.type = t;
    this.algo = a;
    this.param = p;
  }
  
  public Player(Type t) {
    this.type = t;
    this.algo = null;
    this.param = -1;
  }
}
