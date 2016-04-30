package com.mmatyus.go.gui;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

public enum MenuButtons {
  QUICK_GAME( "/buttons/btnQuickGame.png" ),
  SETTINGS( "/buttons/btnSettings.png" ),
  RULES_OF_GO( "/buttons/btnRulesOfGo.png" ),
  EXIT( "/buttons/btnExit.png" );

  MenuButtons( String sourceImage ) {
    this.imagePath = sourceImage;
    try {
      this.image = ImageIO.read( getClass().getResourceAsStream( sourceImage ) );
    }
    catch( IOException e ) {
      throw new IllegalArgumentException( String.format( "Given resource (%s) does not exist!", sourceImage ), e );
    }
  }

  private final Image  image;
  private final String imagePath;

  public Image getImage() {
    return image;
  }

  public String getImagePath() {
    return imagePath;
  }
}
