package com.mmatyus.go.gui;

import java.awt.FontFormatException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import com.mmatyus.go.gui.canvas.StartCanvas;

public class StartDisplay extends AbstractDisplay {
  private Menu               result;

  private static final long  serialVersionUID = 1L;
  public static final String TITLE            = "g(\u03C9) – GOmega";
  public static final String GO_RULES_URL     = "http://www.britgo.org/intro/intro2.html";
  private StartCanvas        canvas           = new StartCanvas();

  public StartDisplay( final Object waiter ) throws IOException, FontFormatException {
    super( waiter, TITLE );

    setupCanvas();
    add( canvas );

  }

  void setupCanvas() {
    canvas.addMouseListener( new MouseAdapter() {
      @Override
      public void mouseClicked( MouseEvent e ) {
        if( canvas.buttonsOfPage[0].hasPoint( e.getPoint() ) ) {
          result = Menu.QUICK_GAME;
          dispose();
          return;
        }
        if( canvas.buttonsOfPage[1].hasPoint( e.getPoint() ) ) {
          result = Menu.SETTINGS;
          dispose();
          return;
        }
        if( canvas.buttonsOfPage[2].hasPoint( e.getPoint() ) ) {
          openPageInDefaultBrowser( GO_RULES_URL );
        }
        if( canvas.buttonsOfPage[3].hasPoint( e.getPoint() ) ) {
          result = Menu.EXIT;
          dispose();
          return;
        }
        canvas.update( canvas.getGraphics() );
        super.mouseClicked( e );
      }
    } );
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
