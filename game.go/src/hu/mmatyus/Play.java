package hu.mmatyus;

import hu.mmatyus.algorithms.UCT_Player;
import hu.mmatyus.gui.BoardDisplay;
import hu.mmatyus.model.Board;
import hu.mmatyus.model.BoardType;
import hu.mmatyus.model.Player;
import hu.mmatyus.model.PlayerPolicy;

public class Play {
  public static final int       AMOUNT_OF_HANDICAP = 0;
  public static final BoardType BOARD_TYPE         = BoardType.SMALL;
  public static final String    TITLE              = "Title of board";
  public static final int       COMPUTER           = Board.BLACK;
  public static final int       ITERATIONS         = 10000;
  public static final double    THRESHOLD          = 0.4;

  public static void main( String[] args ) throws Exception {
    final Board board = new Board( BOARD_TYPE, AMOUNT_OF_HANDICAP );
    //final BoardEval eval = new BoardEval( board );
    final BoardDisplay display = new BoardDisplay( board, TITLE );
    final Player computer = new UCT_Player( new PlayerPolicy() );

    if( board.getNextPlayer() == COMPUTER ) {
      board.move( computer.move( board ) );
      display.update();
    }
    display.setListener( new BoardDisplay.Listener() {
      @Override public void onCellClick( int pos ) {
        System.out.println( pos );
        if( board.isLegalMove( pos ) ) {
          board.move( pos );
          //eval.eval( ITERATIONS, THRESHOLD );
          //eval.dump();
          display.update();

          board.move( computer.move( board ) );
          display.update();
        }
      }
    } );
  }
}
