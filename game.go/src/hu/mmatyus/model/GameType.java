package hu.mmatyus.model;

public enum GameType {
  HVH("Human -VS- Human"),
  HVC("Human -VS- Computer"),
  CVH("Computer -VS- Human"),
  CVC("Computer -VS- Computer");
  
  public final String     label;
  GameType(String label){
    this.label = label;
  }
}
