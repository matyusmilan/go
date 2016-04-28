package hu.mmatyus;

import hu.mmatyus.gui.BoardDisplay;
import hu.mmatyus.gui.SettingsDisplay;
import hu.mmatyus.gui.StartDisplay;
import hu.mmatyus.gui.StartDisplay.Decision;
import hu.mmatyus.model.Algorithm;
import hu.mmatyus.model.Board;
import hu.mmatyus.model.GameConfig;
import hu.mmatyus.model.Player;
import hu.mmatyus.model.Player.Type;

import java.awt.FontFormatException;
import java.io.IOException;

public class Play2 {
  public static final double KOMI  = 6.5;
  public static final String TITLE = "g(\u03C9) – GOmega / On Board";

  private static Decision runStartDisplay( Object us ) throws IOException, FontFormatException, InterruptedException {
    final StartDisplay sd = new StartDisplay( us );
    sd.setVisible( true );
    synchronized( us ) {
      us.wait();
    }
    return sd.result;
  }

  private static GameConfig runSettingsDisplay( Object us ) throws IOException, FontFormatException, InterruptedException {
    final SettingsDisplay sd = new SettingsDisplay( us );
    sd.setVisible( true );
    synchronized( us ) {
      us.wait();
    }
    return sd.result;
  }

  private static void runBoardDisplay( Object us, GameConfig gc ) throws IOException, FontFormatException, InterruptedException {
    final Board board = new Board( gc.getBoardType(), gc.getHandicap(), KOMI );
    final BoardDisplay bd = new BoardDisplay( us, board, gc, TITLE );
    bd.setVisible( true );
    bd.update();
    synchronized( us ) {
      us.wait();
    }
  }

  public static void innerMain() throws IOException, FontFormatException, InterruptedException
  {
    Object us = new Object();
    while( true ) {
      Decision result = runStartDisplay( us );
      final GameConfig gc;
      switch( result ) {
        case EXIT:
          return;
        case QUICK_GAME:
          gc = new GameConfig();
          gc.getPlayers()[1] = new Player( Type.COMPUTER, Algorithm.UCT, 0 );
          break;
        case SETTINGS:
          gc = runSettingsDisplay( us );
          break;
        default: // FUCK ORACLE!
          gc = null;
      }
      if( gc == null )
        continue;
      runBoardDisplay(us, gc);
    }
  }
  
  public static void main( String[] args ) throws IOException, FontFormatException, InterruptedException {
    innerMain();
    System.err.println("EXITED!");
//    final Listener boardClient = new Listener() {
//      @Override
//      public void onSuccess( double score ) {
//        JOptionPane.showMessageDialog( null, "Signed advance on black: " + score, TITLE, JOptionPane.INFORMATION_MESSAGE );
//        System.exit( 0 );
//      }
//
//      @Override
//      public void onFailure( Exception e ) {
//        JOptionPane.showMessageDialog( null, e, TITLE, JOptionPane.ERROR_MESSAGE );
//        System.exit( -1 );
//      }
//    };
//    
//    final Client settingsClient = new Client() {
//      @Override
//      public void onSuccess( GameConfig config ) {
//        final Board board = new Board( config.getBoardType(), config.getHandicap(), KOMI );
//        BoardDisplay display;
//        
//        try {
//          display = new BoardDisplay( board, config, TITLE );
//          display.setListener( boardClient );
//          display.setVisible( true );
//          display.update();
//        }
//        catch( Exception e ) {
//          JOptionPane.showMessageDialog( null, e, TITLE, JOptionPane.ERROR_MESSAGE );
//          System.exit( -1 );
//        } 
//      }
//
//      @Override
//      public void onFailure( Exception e ) {
//        JOptionPane.showMessageDialog( null, e, TITLE, JOptionPane.ERROR_MESSAGE );
//        System.exit( -1 );
//      }
//    };
//
//    SettingsDisplay settingsDisplay = new SettingsDisplay( settingsClient );
//    settingsDisplay.setVisible( true );
  }

}
