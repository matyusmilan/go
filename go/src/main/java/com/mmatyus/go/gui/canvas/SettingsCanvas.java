package com.mmatyus.go.gui.canvas;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.mmatyus.go.gui.Button;
import com.mmatyus.go.gui.ToggleButton;
import com.mmatyus.go.model.Algorithm;
import com.mmatyus.go.model.Board;
import com.mmatyus.go.model.BoardType;
import com.mmatyus.go.model.GameConfig;
import com.mmatyus.go.model.GameType;
import com.mmatyus.go.model.Handicap;
import com.mmatyus.go.model.Player;
import com.mmatyus.go.model.PlayerType;

public class SettingsCanvas extends AbstractCanvas {
  private static final long serialVersionUID = 1L;

  public final GameConfig   gameConfig;

  public SettingsCanvas( final GameConfig actual ) throws IOException, FontFormatException {
    gameConfig = actual;
  }

  final Image                               background      = ImageIO.read( getClass().getResourceAsStream( "/goBackground3.png" ) );

  public final Map<BoardType, ToggleButton> btnSetBoardType = new HashMap<BoardType, ToggleButton>();
  public final Map<GameType, ToggleButton>  btnSetGameType  = new HashMap<GameType, ToggleButton>();

  public final Button                       btnStart        = new Button( "/buttons/btnStart.png", 1400, 800 );
  public final Button                       btnBack         = new Button( "/buttons/btnBack.png", 50, 800 );
  public final Button                       btnNext         = new Button( "/buttons/btnNext.png", 1400, 800 );

  final Image                               btnRadioOff     = ImageIO.read( getClass().getResourceAsStream( "/buttons/btnRadioOff.png" ) );
  final Image                               btnRadioOn      = ImageIO.read( getClass().getResourceAsStream( "/buttons/btnRadioOn.png" ) );
  final Image                               player01Disk    = ImageIO.read( getClass().getResourceAsStream( "/stones/w0.png" ) );
  final Image                               player02Disk    = ImageIO.read( getClass().getResourceAsStream( "/stones/b.png" ) );
  final Font                                font0           = Font.createFont( Font.TRUETYPE_FONT, getClass().getResourceAsStream( "/Kingthings_Petrock.ttf" ) );
  final Color                               brownColor      = new Color( 67, 20, 16 );
  public int                                wizardPage      = 0;

  // Object initializer block
  {
    btnSetBoardType.put( BoardType.SMALL, new ToggleButton( "SMALL_BOARD", "/buttons/btnBoardSMALLoff.png", "/buttons/btnBoardSMALLon.png", 130, 220 ) );
    btnSetBoardType.put( BoardType.MEDIUM, new ToggleButton( "SMALL_BOARD", "/buttons/btnBoardMEDIUMoff.png", "/buttons/btnBoardMEDIUMon.png", 630, 220 ) );
    btnSetBoardType.put( BoardType.LARGE, new ToggleButton( "SMALL_BOARD", "/buttons/btnBoardLARGEoff.png", "/buttons/btnBoardLARGEon.png", 1130, 220 ) );

    btnSetGameType.put( GameType.HVH, new ToggleButton( "HUMAN_VS_HUMAN", "/buttons/btnGameTypeHVHoff.png", "/buttons/btnGameTypeHVHon.png", 130, 220 ) );
    btnSetGameType.put( GameType.CVH, new ToggleButton( "ROBOT_VS_HUMAN", "/buttons/btnGameTypeCVHoff.png", "/buttons/btnGameTypeCVHon.png", 630, 220 ) );
    btnSetGameType.put( GameType.HVC, new ToggleButton( "HUMAN_VS_ROBOT", "/buttons/btnGameTypeHVCoff.png", "/buttons/btnGameTypeHVCon.png", 1130, 220 ) );
  }

  private void drawSetting( Graphics2D g2d, int row, final String message ) {
    final int SETTINGPOS_LEFT = 10;
    final int SETTINGPOS_TOP = 100;
    final int SETTING_ROWHEIGHT = 50;
    drawtabString( g2d, message, SETTINGPOS_LEFT, SETTINGPOS_TOP + row * SETTING_ROWHEIGHT );
  }

  @Override
  protected void drawContent( Graphics g0 ) {
    Graphics2D g2d = (Graphics2D)g0;

    g0.drawImage( background, 0, 0, null );
    Font font1;

    g2d.setColor( brownColor );
    font1 = font0.deriveFont( 48F );
    g2d.setFont( font1 );
    if( wizardPage < 3 ) {
      if( wizardPage == 2 && gameConfig.getGameType() == GameType.HVH )
        drawButton( g0, btnStart );
      else
        drawButton( g0, btnNext );
    } else {
      drawButton( g0, btnStart );
    }
    drawButton( g0, btnBack );

    g2d.drawString( "Settings: ", 50, 50 );
    font1 = font0.deriveFont( 40F );
    g2d.setFont( font1 );
    int i = 0;

    switch( wizardPage ) {
      case 0: {
        drawtabString( g2d, "1.) Board size: ", 10, 100 );
        i = 0;
        font1 = font0.deriveFont( 32F );
        g2d.setFont( font1 );
        final int BOARDTYPES_COL_WIDTH = 500;
        for( BoardType bt : BoardType.values() ) {
          drawToggleButton( g0, btnSetBoardType.get( bt ), ( bt == gameConfig.getBoardType() ? ToggleButton.ON : ToggleButton.OFF ) );
          g2d.drawString( bt.name() + " (" + bt.label + ")", 220 + BOARDTYPES_COL_WIDTH * i, 610 );
          i++;
        }
        break;
      }
      case 1: {
        final BoardType bt = gameConfig.getBoardType();
        drawSetting( g2d, 0, "1.) Board size:\t\t\t\t\t\t" + bt.name() + " (" + bt.label + ")" );
        drawSetting( g2d, 1, "2.) Game type:" );
        i = 0;
        final int GAMETYPE_COL_WIDTH = 500;
        font1 = font0.deriveFont( 32F );
        g2d.setFont( font1 );
        for( GameType gt : GameType.values() ) {
          drawToggleButton( g0, btnSetGameType.get( gt ), ( gt == gameConfig.getGameType() ? ToggleButton.ON : ToggleButton.OFF ) );
          g2d.drawString( gt.label, 180 + GAMETYPE_COL_WIDTH * i, 610 );
          i++;
        }
        break;
      }
      case 2: {
        final BoardType bt = gameConfig.getBoardType();
        final GameType gt = gameConfig.getGameType();
        drawSetting( g2d, 0, "1.) Board size:\t\t\t\t\t\t" + bt.name() + " (" + bt.label + ")" );
        drawSetting( g2d, 1, "2.) Game type:\t\t\t\t\t\t" + gt.label );
        drawSetting( g2d, 2, "3.) Set handicap: " );
        font1 = font0.deriveFont( 32F );
        g2d.setFont( font1 );
        final int HANDICAPS_ROW_HEIGHT = 60;
        for( int h = 0; h <= Handicap.MAX; h++ ) {
          g0.drawImage( ( h == gameConfig.getHandicap() ) ? btnRadioOn : btnRadioOff, ( h < 5 ) ? 90 : 600, ( h % 5 + 1 ) * HANDICAPS_ROW_HEIGHT + 230, null );
          g2d.drawString( h + " stone" + ( ( h == 1 ) ? "" : "s" ), ( h < 5 ) ? 120 : 630, ( h % 5 + 1 ) * HANDICAPS_ROW_HEIGHT + 250 );
        }
        break;
      }
      case 3: {
        drawSetting( g2d, 0, "1.) Board size:\t\t\t\t\t\t" + gameConfig.getBoardType().name() + " (" + gameConfig.getBoardType().label + ")" );
        drawSetting( g2d, 1, "2.) Game type:\t\t\t\t\t\t" + gameConfig.getGameType().label );
        drawSetting( g2d, 2, "3.) Set handicap:\t\t\t\t\t\t" + gameConfig.getHandicap() );
        drawSetting( g2d, 3, "4.) Computer: " );
        drawSetting( g2d, 6, "a.) Algorithm: " );
        drawSetting( g2d, 11, "b.) Strength: " );
        g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, .2f ) );
        g2d.setColor( Color.BLACK );
        int r = 50;
        g2d.fillOval( 120 - r + 4, 320 - r + 4, 2 * r, 2 * r );
        g2d.fillOval( 720 - r + 4, 320 - r + 4, 2 * r, 2 * r );
        g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1.0f ) );
        g0.drawImage( player02Disk.getScaledInstance( 2 * r, 2 * r, Image.SCALE_AREA_AVERAGING ), 120 - r, 320 - r, 2 * r, 2 * r, this );
        g0.drawImage( player01Disk.getScaledInstance( 2 * r, 2 * r, Image.SCALE_AREA_AVERAGING ), 720 - r, 320 - r, 2 * r, 2 * r, this );
        Player[] players = gameConfig.getPlayers();
        g2d.drawString( players[Board.BLACK].playerType.toString(), 190, 330 );
        g2d.drawString( players[Board.WHITE].playerType.toString(), 790, 330 );
        i = 0;
        final int COMPUTER_SETTING_ROW_HEIGHT = 45;
        String[] strength_label = { "Easy", "Medium", "Hard" };
        for( Algorithm alg : Algorithm.values() ) {
          if( players[Board.BLACK].playerType == PlayerType.COMPUTER ) {
            g0.drawImage( ( alg == players[Board.BLACK].algo ) ? btnRadioOn : btnRadioOff, 100, i * COMPUTER_SETTING_ROW_HEIGHT + 435, null );
            g2d.drawString( alg.label, 130, i * COMPUTER_SETTING_ROW_HEIGHT + 460 );
          }

          if( players[Board.WHITE].playerType == PlayerType.COMPUTER ) {
            g0.drawImage( ( alg == players[Board.WHITE].algo ) ? btnRadioOn : btnRadioOff, 700, i * COMPUTER_SETTING_ROW_HEIGHT + 435, null );
            g2d.drawString( alg.label, 730, i * COMPUTER_SETTING_ROW_HEIGHT + 460 );
          }

          i++;
        }
        i = 0;
        for( int s = 0; s < strength_label.length; s++ ) {
          if( players[Board.BLACK].playerType == PlayerType.COMPUTER ) {
            g0.drawImage( ( s == players[Board.BLACK].param ) ? btnRadioOn : btnRadioOff, 100, i * COMPUTER_SETTING_ROW_HEIGHT + 660, null );
            g2d.drawString( strength_label[s], 130, i * COMPUTER_SETTING_ROW_HEIGHT + 685 );
          }
          if( players[Board.WHITE].playerType == PlayerType.COMPUTER ) {
            g0.drawImage( ( s == players[Board.WHITE].param ) ? btnRadioOn : btnRadioOff, 700, i * COMPUTER_SETTING_ROW_HEIGHT + 660, null );
            g2d.drawString( strength_label[s], 730, i * COMPUTER_SETTING_ROW_HEIGHT + 685 );
          }
          i++;
        }
        break;
      }
      default:
        break;
    }
  }

  private void drawtabString( Graphics g, String text, int x, int y ) {
    for( String line : text.split( "\t" ) )
      g.drawString( line, x += g.getFontMetrics().getHeight(), y );
  }

}
