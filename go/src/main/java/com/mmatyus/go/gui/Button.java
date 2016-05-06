package com.mmatyus.go.gui;

import java.awt.Image;
import java.awt.Point;
import java.io.IOException;

import javax.imageio.ImageIO;

class Button {
  public Button( String name, String path, int x, int y ) throws IOException {
    this.name = name;
    try {
      this.image = ImageIO.read( getClass().getResourceAsStream( path ) );
    }
    catch( IOException e ) {
      throw new IllegalArgumentException( String.format( "Given resource (%s) does not exist!", path ), e );
    }
    this.path = path;
    this.x = x;
    this.y = y;
    this.w = image.getWidth( null );
    this.h = image.getHeight( null );
  }

  boolean hasPoint( int px, int py ) {
    return px >= x && px < x + w && py >= y && py < y + h;
  }

  boolean hasPoint( Point p ) {
    return hasPoint( p.x, p.y );
  }

  public final Image  image;
  public final String path;
  public final String name;
  public final int    x, y;
  public final int    w, h;
}
