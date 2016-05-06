package com.mmatyus.go.gui;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;

public class StartDisplay extends AbstractDisplay {
  private Menu               result;

  private static final long  serialVersionUID = 1L;
  public static final String TITLE            = "g(\u03C9) – GOmega";
  public static final String GO_RULES_URL     = "http://www.britgo.org/intro/intro2.html";
  private StartCanvas        canvas           = new StartCanvas();
  private final Button[]     buttonsOfPage    = new Button[4];

  public StartDisplay( final Object waiter ) throws IOException, FontFormatException {
    super( waiter, TITLE );

    buttonsOfPage[0] = new Button( "QUICK_GAME", "/buttons/btnQuickGame.png", 210, 550 );
    buttonsOfPage[1] = new Button( "SETTINGS", "/buttons/btnSettings.png", 210, 620 );
    buttonsOfPage[2] = new Button( "RULES", "/buttons/btnRulesOfGo.png", 210, 690 );
    buttonsOfPage[3] = new Button( "EXIT", "/buttons/btnExit.png", 210, 760 );

    setupCanvas();
    add( canvas );

  }

  void setupCanvas() {
    canvas.addMouseListener( new MouseAdapter() {
      @Override
      public void mouseClicked( MouseEvent e ) {
        if( buttonsOfPage[0].hasPoint( e.getPoint() ) ) {
          result = Menu.QUICK_GAME;
          dispose();
          return;
        }
        if( buttonsOfPage[1].hasPoint( e.getPoint() ) ) {
          result = Menu.SETTINGS;
          dispose();
          return;
        }
        if( buttonsOfPage[2].hasPoint( e.getPoint() ) ) {
          openPageInDefaultBrowser( GO_RULES_URL );
        }
        if( buttonsOfPage[3].hasPoint( e.getPoint() ) ) {
          result = Menu.EXIT;
          dispose();
          return;
        }
        canvas.update( canvas.getGraphics() );
        super.mouseClicked( e );
      }
    } );
  }

  class StartCanvas extends Canvas {
    private static final long serialVersionUID  = 1L;
    private final Image       background        = ImageIO.read( getClass().getResourceAsStream( "/openScreen2.png" ) );
    int                       paddingMultiplier = 0;

    public StartCanvas() throws IOException, FontFormatException {

    }

    @Override
    public void paint( Graphics g ) {
      re_display( g );
    }

    @Override
    public void update( Graphics g ) {
      re_display( g );
    }

    private void drawButton( Graphics g, Button b ) {
      g.drawImage( b.image, b.x, b.y, null );
    }

    public void re_display( Graphics g ) {

      final Dimension dim = getSize();
      Image offscreen = createImage( dim.width, dim.height );
      Graphics g0 = offscreen.getGraphics();

      g0.drawImage( background, 0, 0, null );

      for( Button button : buttonsOfPage )
        drawButton( g0, button );

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

  @Override
  protected void closing() {
    result = Menu.EXIT;
  }

  @Override
  protected void closed() {
    notifyWaiter();
  }

  @Override
  public Menu getNextScreen() {
    return result;
  }
}
