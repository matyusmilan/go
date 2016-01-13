package hu.mmatyus.model;

public class BoardEval {
  private Board             board;
  private RandomPlayerBoard rnd;
  private double[]          area; // 0-white, 1-black, 0.5-neutral
  private double            score;

  public BoardEval( Board board, PlayerPolicy policy ) {
    this.board = board;
    area = new double[board.getNumberOfCells()];
    rnd = new RandomPlayerBoard( board.getBoardType(), policy );
    reset();
  }

  public void reset() {
    for( int i = 0; i < area.length; ++i )
      area[i] = 0.5;
    score = board.komi;
  }

  public void eval( int iterations, double threshold ) {
    int i, j;

    for( j = 0; j < area.length; ++j )
      area[j] = 0;
    for( i = 0; i < iterations; ++i ) {
      rnd.copyFrom( board );
      rnd.clearPasses();
      rnd.playRandomGame();
      rnd.calcChineseScore();

      for( j = 0; j < area.length; ++j )
        area[j] += rnd.getArea( j );
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

  public void dump() {
    System.out.println( "estimated score = " + score );
    System.out.println( "area estimatation:" );
    for( int y = 0; y < board.getSize(); ++y ) {
      for( int x = 0; x < board.getSize(); ++x )
        System.out.print( String.format( "%.2f ", area[board.getPos( x, y )] ) );
      System.out.println();
    }
  }
}
