package com.mmatyus.go.gui;

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

    buttonsOfPage[0] = new Button( "/buttons/btnQuickGame.png", 210, 550 );
    buttonsOfPage[1] = new Button( "/buttons/btnSettings.png", 210, 620 );
    buttonsOfPage[2] = new Button( "/buttons/btnRulesOfGo.png", 210, 690 );
    buttonsOfPage[3] = new Button( "/buttons/btnExit.png", 210, 760 );

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

  //class StartCanvas extends Canvas {
  class StartCanvas extends AbstractCanvas {
    private static final long serialVersionUID = 1L;
    private final Image       background       = ImageIO.read( getClass().getResourceAsStream( "/openScreen2.png" ) );

    StartCanvas() throws IOException {}

    @Override
    protected void drawContent( Graphics g0 ) {
      g0.drawImage( background, 0, 0, null );

      for( Button button : buttonsOfPage )
        drawButton( g0, button );
    }
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
