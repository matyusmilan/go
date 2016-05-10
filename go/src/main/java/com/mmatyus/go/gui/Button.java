package com.mmatyus.go.gui;

import java.awt.Image;
import java.awt.Point;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Button {

  public Button( String path, int x, int y ) {
    this.path = path;
    try {
      this.images = ImageIO.read( getClass().getResourceAsStream( path ) );
    }
    catch( IOException e ) {
      throw new IllegalArgumentException( String.format( "Given resource (%s) does not exist!", path ), e );
    }
    this.x = x;
    this.y = y;
    this.w = images.getWidth( null );
    this.h = images.getHeight( null );
  }

  boolean hasPoint( int px, int py ) {
    return px >= x && px < x + w && py >= y && py < y + h;
  }

  boolean hasPoint( Point p ) {
    return hasPoint( p.x, p.y );
  }

  public final Image  images;
  public final String path;
  public final int    x, y;
  public final int    w, h;
}
