package hu.mmatyus.model;

public class Player {
  enum Type {
    HUMAN
    , COMPUTER
  }
  Type type;
  Algorithm algo;
  int param;
}
