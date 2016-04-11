package hu.mmatyus;

import hu.mmatyus.algorithms.UCT_Robot;
import hu.mmatyus.algorithms.Zobrist;
import hu.mmatyus.gui.BoardDisplay;
import hu.mmatyus.model.Board;
import hu.mmatyus.model.BoardEval;
import hu.mmatyus.model.BoardType;
import hu.mmatyus.model.PlayerPolicy;
import hu.mmatyus.model.Robot;

public class Play {
  public static final int       AMOUNT_OF_HANDICAP = 0;
  public static final String    TITLE              = "{\u03C9} GOmega";
  public static final int       COMPUTER           = Board.BLACK;
  public static final double    KOMI               = 6.5;

  public static void main( String[] args ) throws Exception {
    final BoardType boardType = BoardType.getBySize(Integer.valueOf(args[0]));
    
    final Board board = new Board( boardType, AMOUNT_OF_HANDICAP, KOMI );
    final BoardDisplay display = new BoardDisplay( board, TITLE );
    final BoardEval eval = new BoardEval( board, new PlayerPolicy() );
    final Robot computer = new UCT_Robot( new PlayerPolicy() );
    // final Robot computer = new NegaMaxRobot( 3 );
    final Zobrist zobrist = new Zobrist(boardType);

    display.setComputerColor( COMPUTER );


    if( board.getNextPlayer() == COMPUTER ) {
      board.move( computer.move( board ) );
      display.update();
      System.out.println(zobrist.getZobristHash( board ));
    }

    display.setListener( new BoardDisplay.Listener() {
      @Override public void onCellClick( int pos ) {
        //System.out.println( pos );
        if( board.isLegalMove( pos ) ) {
          board.move( pos );
          if( !board.isGameOver() && COMPUTER != Board.EMPTY ){
            display.update();
            System.out.println(zobrist.getZobristHash( board ));
            board.move( computer.move( board ) );
          }
          if( board.isGameOver() && board.getPassNum() == 2 ) {
            eval.eval( 10000, 0.4 );
            eval.dump();
            display.setEval( eval );
          } else {
            display.update();
          }
        }
      }
    } );
  }
}
