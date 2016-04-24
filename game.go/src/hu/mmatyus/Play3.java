package hu.mmatyus;

import hu.mmatyus.gui.StartDisplay;
import hu.mmatyus.gui.StartDisplay.Client;

import java.awt.FontFormatException;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Play3 {
  public static final double KOMI  = 6.5;
  public static final String TITLE = "{\u03C9} GOmega";

  public static void main( String[] args ) throws IOException, FontFormatException {
    final Client startClient = new Client() {
      @Override
      public void onSuccess() {

      }
      
      @Override
      public void onFailure( Exception e ) {
        JOptionPane.showMessageDialog( null, e, TITLE, JOptionPane.ERROR_MESSAGE );
        System.exit( -1 );
      }

    };

    StartDisplay sd = new StartDisplay( startClient );
    sd.setVisible( true );
  }
}
