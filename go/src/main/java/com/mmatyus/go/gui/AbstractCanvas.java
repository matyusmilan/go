package com.mmatyus.go.gui;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

public class AbstractCanvas extends Canvas {
  private static final long serialVersionUID = 1L;

  AbstractCanvas() throws IOException {}

  @Override
  public void paint( Graphics g ) {
    re_display( g );
  }

  @Override
  public void update( Graphics g ) {
    re_display( g );
  }

  protected void re_display( Graphics g ) {
    final Dimension dim = getSize();
    Image offscreen = createImage( dim.width, dim.height );
    Graphics g0 = offscreen.getGraphics();

    drawContent( g0 );

    g.drawImage( offscreen, 0, 0, this );
  }

  protected void drawContent( Graphics g0 ) {}

  protected void drawButton( Graphics g, Button b ) {
    g.drawImage( b.images, b.x, b.y, null );
  }

  protected void drawToggleButton( Graphics g, ToggleButton b, int onOrOff ) {
    g.drawImage( b.images[onOrOff], b.x, b.y, null );
  }
}
