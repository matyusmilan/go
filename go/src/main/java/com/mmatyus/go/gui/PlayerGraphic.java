package com.mmatyus.go.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.mmatyus.go.model.Board;

public enum PlayerGraphic {
  HUMAN( "Human", "/actHuman2.png", "/pasHuman.png" ),
  COMPUTER( "MR. ROBOT", "/actRobot2.png", "/pasRobot.png" );
  public final String       name;
  public Image              activeImg    = null;
  public Image              passiveImg   = null;
  final Map<String, Image>  switchButton = new HashMap<String, Image>();
  final Map<String, String> passLabel    = new HashMap<String, String>();
  final Map<String, String> resignLabel  = new HashMap<String, String>();

  PlayerGraphic( String name, String actImgPath, String pasImgPath ) {
    this.name = name;
    try {
      this.activeImg = ImageIO.read( getClass().getResourceAsStream( actImgPath ) );
      this.passiveImg = ImageIO.read( getClass().getResourceAsStream( pasImgPath ) );
      this.switchButton.put( "on", ImageIO.read( getClass().getResourceAsStream( "/buttons/btnSwitchOn.png" ) ) );
      this.switchButton.put( "off", ImageIO.read( getClass().getResourceAsStream( "/buttons/btnSwitchOff.png" ) ) );
      this.passLabel.put( "on", "Passed" );
      this.passLabel.put( "off", "Pass" );
      this.resignLabel.put( "on", "Resigned" );
      this.resignLabel.put( "off", "Resign" );
    }
    catch( IOException e ) {
      e.printStackTrace();
    }
  }

  public void setPass( String s, Graphics g0, Graphics2D g2d, int color ) {
    g0.drawImage( switchButton.get( s ), 1330, ( color == Board.WHITE ) ? 520 : 220, null );
    g2d.drawString( passLabel.get( s ), 1380, ( color == Board.WHITE ) ? 560 : 260 );
  }

  public void setResign( String s, Graphics g0, Graphics2D g2d, int color ) {
    g0.drawImage( switchButton.get( s ), 1330, ( color == Board.WHITE ) ? 620 : 320, null );
    g2d.drawString( resignLabel.get( s ), 1380, ( color == Board.WHITE ) ? 660 : 360 );
  }
}
