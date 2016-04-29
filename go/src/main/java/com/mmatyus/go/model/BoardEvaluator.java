package com.mmatyus.go.model;

public class BoardEvaluator {
  private Board             board;
  private RandomPlayerBoard rnd;
  private double[]          area; // 0-white, 1-black, 0.5-neutral
  private double            score;

  public BoardEvaluator( Board board, PlayerPolicy policy ) {
    this.board = board;
    area = new double[board.getNumberOfCells()];
    rnd = new RandomPlayerBoard( board.boardType, policy );
    reset();
  }

  public void reset() {
    for( int i = 0; i < area.length; ++i )
      area[i] = 0.5;
    score = board.komi;
  }

  public void eval( int iterations, double threshold ) {
    int i, j;

    int REPORT_SIZE = iterations / 10;
    if( REPORT_SIZE == 0 )
      REPORT_SIZE = 1;

    for( j = 0; j < area.length; ++j )
      area[j] = 0;
    for( i = 0; i < iterations; ++i ) {
      if( 0 == i % REPORT_SIZE )
        System.err.println( "BoardEvaluator: " + i + "/" + iterations );
      rnd.board = board.clone();
      rnd.board.clearPasses();
      rnd.playRandomGame();
      rnd.board.calcChineseScore();

      for( j = 0; j < area.length; ++j )
        area[j] += rnd.board.getArea( j );
    }

    score = -board.getKomi();
    for( j = 0; j < area.length; ++j ) {
      area[j] /= iterations;
      if( area[j] < threshold ) {
        score--;
      } else if( area[j] > 1 - threshold ) {
        score++;
      }
    }
  }

  public double getArea( int pos ) {
    return area[pos];
  }

  public double getScore() {
    return score;
  }

  public void dump() {
    System.out.println( "estimated score = " + score );
    System.out.println( "area estimatation:" );
    for( int y = 0; y < board.sideLength; ++y ) {
      for( int x = 0; x < board.sideLength; ++x )
        System.out.print( String.format( "%.2f ", area[board.getPos( x, y )] ) );
      System.out.println();
    }
  }
}
