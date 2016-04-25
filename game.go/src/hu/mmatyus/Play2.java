package hu.mmatyus;

import hu.mmatyus.algorithms.NegaMaxRobot;
import hu.mmatyus.algorithms.UCT_Robot;
import hu.mmatyus.gui.BoardDisplay;
import hu.mmatyus.gui.BoardDisplay.Listener;
import hu.mmatyus.gui.SettingsDisplay;
import hu.mmatyus.gui.SettingsDisplay.Client;
import hu.mmatyus.model.Algorithm;
import hu.mmatyus.model.Board;
import hu.mmatyus.model.GameConfig;
import hu.mmatyus.model.Player;
import hu.mmatyus.model.PlayerPolicy;
import hu.mmatyus.model.Robot;

import java.awt.FontFormatException;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Play2 {
  public static final double KOMI  = 6.5;
  public static final String TITLE = "g(\u03C9) – GOmega / On Board";

  public static void main( String[] args ) throws IOException, FontFormatException {
   
    final Listener boardClient = new Listener() {
      @Override
      public void onSuccess( double score ) {
        JOptionPane.showMessageDialog( null, "Signed advance on black: " + score, TITLE, JOptionPane.INFORMATION_MESSAGE );
        System.exit( 0 );
      }

      @Override
      public void onFailure( Exception e ) {
        JOptionPane.showMessageDialog( null, e, TITLE, JOptionPane.ERROR_MESSAGE );
        System.exit( -1 );
      }
    };

    final Client settingsClient = new Client() {
      @Override
      public void onSuccess( GameConfig config ) {
        final Board board = new Board( config.getBoardType(), config.getHandicap(), KOMI );
        BoardDisplay display;
        
        Player[] players = config.getPlayers();
        Robot blackRobot = null, whiteRobot = null;
        if( players[Board.BLACK].type == Player.Type.COMPUTER ) {
          if( players[Board.BLACK].algo == Algorithm.UCT ) {
            blackRobot = new UCT_Robot( new PlayerPolicy(Algorithm.UCT.option( players[Board.BLACK].param)) );
          } else {
            blackRobot = new NegaMaxRobot( players[Board.BLACK] );
          }
        }
        if( players[Board.WHITE].type == Player.Type.COMPUTER ) {
          if( players[Board.WHITE].algo == Algorithm.UCT ) {
            whiteRobot = new UCT_Robot( new PlayerPolicy(Algorithm.UCT.option( players[Board.WHITE].param)) );
          } else {
            whiteRobot = new NegaMaxRobot( players[Board.WHITE] );
          }
        }
        
        try {
          display = new BoardDisplay( board, config, TITLE );
          display.setListener( boardClient );
          display.setVisible( true );
          display.update();

          if( board.getNextPlayer() == Board.BLACK && players[Board.BLACK].type == Player.Type.COMPUTER )
            display.onCellClick( blackRobot.move( board ) );
          if( board.getNextPlayer() == Board.WHITE && players[Board.WHITE].type == Player.Type.COMPUTER )
            display.onCellClick( whiteRobot.move( board ) );

        }
        catch( Exception e ) {
          JOptionPane.showMessageDialog( null, e, TITLE, JOptionPane.ERROR_MESSAGE );
          System.exit( -1 );
        } 
      }

      @Override
      public void onFailure( Exception e ) {
        JOptionPane.showMessageDialog( null, e, TITLE, JOptionPane.ERROR_MESSAGE );
        System.exit( -1 );
      }
    };

    SettingsDisplay settingsDisplay = new SettingsDisplay( settingsClient );
    settingsDisplay.setVisible( true );


  }

}
