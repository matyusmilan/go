package hu.mmatyus;

import java.awt.FontFormatException;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;

import javax.swing.JOptionPane;

import hu.mmatyus.gui.BoardDisplay;
import hu.mmatyus.gui.BoardDisplay.Listener;
import hu.mmatyus.gui.SettingsDisplay;
import hu.mmatyus.gui.SettingsDisplay.Client;
import hu.mmatyus.model.Board;
import hu.mmatyus.model.GameConfig;

public class Play2 {
  public static final double    KOMI               = 6.5;
  public static final String    TITLE              = "{\u03C9} GOmega";

  public static void main(String[] args) throws IOException, FontFormatException
  {
    final Listener boardClient = new Listener() {
      @Override
      public void onSuccess( double score ) {
        JOptionPane.showMessageDialog( null, "Signed advance on black: " + score, TITLE, JOptionPane.INFORMATION_MESSAGE );
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
        final Board board = new Board( config.getBoardType(), config.getHandicap(), KOMI ); // TODO
        BoardDisplay display;
        try {
          display = new BoardDisplay( board, config, TITLE );
          display.setListener( boardClient );
          display.setVisible( true );
        }
        catch( Exception e )
        {
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
    
    SettingsDisplay sd = new SettingsDisplay( settingsClient );
    sd.setVisible( true );
  }
  
  
  
}
