package com.mmatyus.go;

import java.awt.FontFormatException;
import java.io.IOException;

import com.mmatyus.go.gui.BoardDisplay;
import com.mmatyus.go.gui.Menu;
import com.mmatyus.go.gui.SettingsDisplay;
import com.mmatyus.go.gui.StartDisplay;
import com.mmatyus.go.model.Algorithm;
import com.mmatyus.go.model.Board;
import com.mmatyus.go.model.GameConfig;
import com.mmatyus.go.model.Player;
import com.mmatyus.go.model.PlayerType;

/**
 * Hello world!
 */
public class App {
  public static final double KOMI = 6.5;

  private static Menu runStartDisplay( Object us ) throws IOException, FontFormatException, InterruptedException {
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
    final BoardDisplay bd = new BoardDisplay( us, board, gc );
    bd.setVisible( true );
    bd.update();
    synchronized( us ) {
      us.wait();
    }
  }

  public static void innerMain() throws IOException, FontFormatException, InterruptedException {
    Object us = new Object();
    while( true ) {
      Menu result = runStartDisplay( us );
      final GameConfig gc;
      switch( result ) {
        case EXIT:
          return;
        case QUICK_GAME:
          gc = new GameConfig();
          gc.getPlayers()[1] = new Player( PlayerType.COMPUTER, Algorithm.UCT, 0 );
          break;
        case SETTINGS:
          gc = runSettingsDisplay( us );
          break;
        default: // FUCK ORACLE!
          gc = null;
      }
      if( gc == null )
        continue;
      runBoardDisplay( us, gc );
    }
  }

  public static void main( String[] args ) throws IOException, FontFormatException, InterruptedException {
    innerMain();
    System.err.println( "EXITED!" );
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
