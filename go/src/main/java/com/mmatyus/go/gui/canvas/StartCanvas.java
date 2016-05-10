package com.mmatyus.go.gui.canvas;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mmatyus.go.gui.Button;

public class StartCanvas extends AbstractCanvas {
  private static final long serialVersionUID = 1L;
  private final Image       background       = ImageIO.read( getClass().getResourceAsStream( "/openScreen2.png" ) );
  public final Button[]     buttonsOfPage    = new Button[4];

  public StartCanvas() throws IOException {
    buttonsOfPage[0] = new Button( "/buttons/btnQuickGame.png", 210, 550 );
    buttonsOfPage[1] = new Button( "/buttons/btnSettings.png", 210, 620 );
    buttonsOfPage[2] = new Button( "/buttons/btnRulesOfGo.png", 210, 690 );
    buttonsOfPage[3] = new Button( "/buttons/btnExit.png", 210, 760 );
  }

  @Override
  protected void drawContent( Graphics g0 ) {
    g0.drawImage( background, 0, 0, null );

    for( Button button : buttonsOfPage )
      drawButton( g0, button );
  }
}
