package hu.mmatyus.gui;

import java.awt.Canvas;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

public class StartDisplay extends Frame {
  private static final long  serialVersionUID = 1L;
  public static final int    WIDTH            = 1600;
  public static final int    HEIGHT           = 900;
  public static final String TITLE            = "g(\u03C9) – GOmega";
  public static final String GO_RULES_URL     = "http://www.britgo.org/intro/intro2.html";
  private Client             client;
  private StartCanvas        canvas           = new StartCanvas();
  private static final int   btnMainX         = 210;
  private static final int   btnMainY         = 550;
  private static final int   btnMainPadding   = 70;
  private static final int   btnMainWidth     = 200;
  private static final int   btnMainHight     = 40;
  public static final int    amountOfbtnMain  = 3;

  public interface Client {
    void onSuccess();

    void onFailure( Exception e );
  }

  public StartDisplay( Client c ) throws IOException, FontFormatException {
    this.client = c;

    setSize( WIDTH, HEIGHT );
    setTitle( TITLE );
    setResizable( false );

    setupCanvas();
    add( canvas );

    addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing( WindowEvent we ) {
        client.onSuccess();
        System.exit( 0 );
      }
    } );
  }

  public enum StartButton {
    NEW_GAME( "res/buttons/btnNewGame.png" ),
    SETTINGS( "res/buttons/btnSettings.png" ),
    RULES_OF_GO( "res/buttons/btnRulesOfGo.png" );
    StartButton( String sourceImage ) {
      this.sourceImage = sourceImage;
    }

    public final String sourceImage;
  }

  void setupCanvas() {
    canvas.addMouseListener( new MouseAdapter() {
      @Override
      public void mouseClicked( MouseEvent e ) {
        int paddingMultiplier = 0;
        for( StartButton sb : StartButton.values() ) {
          if( btnMainX <= e.getPoint().x && e.getPoint().x <= btnMainX + btnMainWidth && btnMainY + paddingMultiplier * btnMainPadding <= e.getPoint().y && e.getPoint().y <= btnMainY + paddingMultiplier * btnMainPadding + btnMainHight ) {
            switch( sb ) {
              case NEW_GAME:
                System.out.println( "NEW_GAME" );
                break;
              case SETTINGS:
                System.out.println( "SETTINGS" );
                break;
              case RULES_OF_GO:
                System.out.println( "RULES_OF_GO" );
                openPageInDefaultBrowser( GO_RULES_URL );
                break;
              default:
                break;
            }
          }
          paddingMultiplier++;
        }
        canvas.update( canvas.getGraphics() );
        super.mouseClicked( e );
      }
    } );
  }

  class StartCanvas extends Canvas {
    private static final long serialVersionUID  = 1L;
    final Image               background        = ImageIO.read( new File( "res/openScreen2.png" ) );
    final Image[]             startButtons      = { ImageIO.read( new File( StartButton.NEW_GAME.sourceImage ) ), ImageIO.read( new File( StartButton.SETTINGS.sourceImage ) ), ImageIO.read( new File( StartButton.RULES_OF_GO.sourceImage ) ) };
    int                       paddingMultiplier = 0;

    public StartCanvas() throws IOException, FontFormatException {}

    @Override
    public void paint( Graphics g ) {
      re_display( g );
    }

    @Override
    public void update( Graphics g ) {
      re_display( g );
    }

    public void re_display( Graphics g ) {

      final Dimension dim = getSize();
      Image offscreen = createImage( dim.width, dim.height );
      Graphics g0 = offscreen.getGraphics();
      // currentGraphics = g0;

      g0.drawImage( background, 0, 0, null );

      paddingMultiplier = 0;
      for( Image startBtn : startButtons ) {
        g0.drawImage( startBtn, btnMainX, btnMainY + paddingMultiplier * btnMainPadding, null );
        paddingMultiplier++;
      }

      g.drawImage( offscreen, 0, 0, this );
    } // re_display
  }

  public void openPageInDefaultBrowser( String url ) {
    try {
      java.awt.Desktop.getDesktop().browse( java.net.URI.create( url ) );
    }
    catch( java.io.IOException e ) {
      System.out.println( e.getMessage() );
    }
  }
}
