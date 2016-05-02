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
  private static final int   BTN_X_POS        = 210;
  private static final int   BTN_Y_POS        = 550;
  private static final int   BTN_PADDING      = 70;
  private static final int   BTN_WIDTH        = 200;
  private static final int   BTN_HEIGHT       = 40;

  public StartDisplay( final Object waiter ) throws IOException, FontFormatException {
    super( waiter, TITLE );

    setupCanvas();
    add( canvas );

  }

  void setupCanvas() {
    canvas.addMouseListener( new MouseAdapter() {
      @Override
      public void mouseClicked( MouseEvent e ) {
        int paddingMultiplier = 0;
        for( MenuButtons sb : MenuButtons.values() ) {
          if( BTN_X_POS <= e.getPoint().x && e.getPoint().x <= BTN_X_POS + BTN_WIDTH && BTN_Y_POS + paddingMultiplier * BTN_PADDING <= e.getPoint().y && e.getPoint().y <= BTN_Y_POS + paddingMultiplier * BTN_PADDING + BTN_HEIGHT ) {
            switch( sb ) {
              case QUICK_GAME:
                result = Menu.QUICK_GAME;
                dispose();
                return;
              case SETTINGS:
                result = Menu.SETTINGS;
                dispose();
                return;
              case RULES_OF_GO:
                openPageInDefaultBrowser( GO_RULES_URL );
                break;
              case EXIT:
                result = Menu.EXIT;
                dispose();
                return;
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
    final Image               background        = ImageIO.read( getClass().getResourceAsStream( "/openScreen2.png" ) );
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

      g0.drawImage( background, 0, 0, null );

      paddingMultiplier = 0;
      for( MenuButtons actual : MenuButtons.values() ) {
        g0.drawImage( actual.getImage(), BTN_X_POS, BTN_Y_POS + paddingMultiplier * BTN_PADDING, null );
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
