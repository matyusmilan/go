package hu.mmatyus.algorithms.tt;

public class TranspositionTableEntry {
  public enum Type {
    EXACT_VALUE
    , LOWERBOUND
    , UPPERBOUND
  }
  private Type type;
  private int bestMove;
  private int evaluation;
  private int depth;
  
  TranspositionTableEntry(Type type, int bestMove, int evaluation, int depth){
    this.type = type;
    this.bestMove = bestMove;
    this.evaluation = evaluation;
    this.depth = depth;
  }
  public Type getType() {
    return type;
  }
  public void setType( Type type ) {
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
