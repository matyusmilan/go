package com.mmatyus.go.gui;

import java.awt.Image;
import java.awt.Point;
import java.io.IOException;

import javax.imageio.ImageIO;

class ToggleButton {
  public static final int OFF = 0;
  public static final int ON  = 1;

  public ToggleButton( String name, String pathOff, String pathOn, int x, int y ) throws IOException {
    this.name = name;
    try {
      this.images[OFF] = ImageIO.read( getClass().getResourceAsStream( pathOff ) );
    }
    catch( IOException e ) {
      throw new IllegalArgumentException( String.format( "Given resource (%s) does not exist!", pathOff ), e );
    }
    try {
      this.images[ON] = ImageIO.read( getClass().getResourceAsStream( pathOn ) );
    }
    catch( IOException e ) {
      throw new IllegalArgumentException( String.format( "Given resource (%s) does not exist!", pathOn ), e );
    }
    this.pathOff = pathOff;
    this.pathOn = pathOn;
    this.x = x;
    this.y = y;
    this.w = images[OFF].getWidth( null );
    this.h = images[OFF].getHeight( null );
  }

  boolean hasPoint( int px, int py ) {
    return px >= x && px < x + w && py >= y && py < y + h;
  }

  boolean hasPoint( Point p ) {
    return hasPoint( p.x, p.y );
  }

  public final Image[] images = new Image[2];
  public final String  pathOff;
  public final String  pathOn;
  public final String  name;
  public final int     x, y;
  public final int     w, h;
}
