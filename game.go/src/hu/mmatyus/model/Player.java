package hu.mmatyus.model;

public class Player {
  public enum Type {
    HUMAN
    , COMPUTER
  }
  public Type type;
  public Algorithm algo;
  public int param;
  
  Player(Type t, Algorithm a, int p){
    this.type = t;
    this.algo = a;
    this.param = p;
  }
  
  Player(Type t) {
    this.type = t;
    this.algo = null;
    this.param = -1;
  }
}
