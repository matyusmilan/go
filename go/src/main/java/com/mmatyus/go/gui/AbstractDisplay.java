package com.mmatyus.go.gui;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;

@SuppressWarnings( "serial" )
public abstract class AbstractDisplay extends Frame {

  public static final int WIDTH  = 1600;
  public static final int HEIGHT = 900;
  protected final Object  parent;

  public AbstractDisplay( final Object parent, final String title ) throws IOException {
    this.parent = parent;
    setTitle( title );
    setSize( WIDTH, HEIGHT );
    setResizable( false );
    setIconImage( ImageIO.read( getClass().getResourceAsStream( "/icon128.png" ) ) );

    addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing( WindowEvent we ) {
        closing();
        dispose();
      }

      @Override
      public void windowClosed( WindowEvent we ) {
        synchronized( parent ) {
          parent.notifyAll();
        }
        closed();
      }
    } );
  }

  protected abstract void closing();

  protected abstract void closed();

}
