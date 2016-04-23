package hu.mmatyus;

import hu.mmatyus.gui.BoardDisplay;
import hu.mmatyus.model.Board;
import hu.mmatyus.model.GameConfig;

public class Play {
  public static final int    AMOUNT_OF_HANDICAP = 0;
  public static final String TITLE              = "{\u03C9} GOmega";
  public static final int    COMPUTER           = Board.BLACK;
  public static final double KOMI               = 6.5;

  public static void main( String[] args ) throws Exception {
    GameConfig gameConfig = new GameConfig();

    final Board board = new Board( gameConfig.getBoardType(), AMOUNT_OF_HANDICAP, KOMI );

    final BoardDisplay display = new BoardDisplay( board, gameConfig, TITLE );

    //final Robot computer = new UCT_Robot( new PlayerPolicy() );

    //final Robot computer = new NegaMaxRobot( 3 );

    //display.setComputerColor( COMPUTER );

    /*
     * if( board.getNextPlayer() == COMPUTER ) {
     * board.move( computer.move( board ) );
     * display.update();
     * }
     */

//    display.setListener( new BoardDisplay.Listener() {
//      @Override
//      public void onCellClick( int pos ) {
//        //System.out.println( pos );
//        if( board.isLegalMove( pos ) ) {
//          board.move( pos );
//          if( !board.isGameOver() && COMPUTER != Board.EMPTY ) {
//            display.update();
//            board.move( computer.move( board ) );
//          }
//          if( board.isGameOver() || board.getPassNum() == 2 ) {
//            BoardEval eval = new BoardEval( board, new PlayerPolicy() );
//            eval.eval( 10000, 0.4 );
//            eval.dump();
//            display.setEval( eval );
//          } else {
//            display.update();
//          }
//        }
//      }
//    } );

  }
}
