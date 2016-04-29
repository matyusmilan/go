package com.mmatyus.go.algorithms.transposition;

public class TranspositionTableEntry {

  private TranspositionType type;
  private int               bestMove;
  private int               evaluation;
  private int               depth;

  TranspositionTableEntry( TranspositionType type, int bestMove, int evaluation, int depth ) {
    this.type = type;
    this.bestMove = bestMove;
    this.evaluation = evaluation;
    this.depth = depth;
  }

  public TranspositionType getType() {
    return type;
  }

  public void setType( TranspositionType type ) {
    this.type = type;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth( int depth ) {
    this.depth = depth;
  }

  public int getBestMove() {
    return bestMove;
  }

  public void setBestMove( int bestMove ) {
    this.bestMove = bestMove;
  }

  public int getEvaluation() {
    return evaluation;
  }

  public void setEvaluation( int evaluation ) {
    this.evaluation = evaluation;
  }

}
