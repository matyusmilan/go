package com.mmatyus.go.gui;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.mmatyus.go.model.Algorithm;
import com.mmatyus.go.model.Board;
import com.mmatyus.go.model.BoardType;
import com.mmatyus.go.model.GameConfig;
import com.mmatyus.go.model.GameType;
import com.mmatyus.go.model.Handicap;
import com.mmatyus.go.model.Player;
import com.mmatyus.go.model.PlayerType;

public class SettingsDisplay extends AbstractDisplay {
  private static final long   serialVersionUID = 1L;
  private static final String TITLE            = "g(\u03C9) – GOmega / Settings";
  private MyCanvas            canvas           = new MyCanvas();
  private int                 wizardPage       = 0;
  public GameConfig           result           = null;
  private GameConfig          gameConfig       = new GameConfig();

  public SettingsDisplay( Object parent ) throws IOException, FontFormatException {
    super( parent, TITLE );

    setupCanvas();
    add( canvas );

  }

  void setupCanvas() {
    canvas.addMouseListener( new MouseAdapter() {
      @Override
      public void mouseClicked( MouseEvent e ) {
        //b.drawImage( btnNext, 1400, 830, null )
        if( wizardPage < 3 ) {
          //b.drawImage( btnNext, 1400, 830, null );
          if( 1400 <= e.getPoint().x && e.getPoint().x <= 1550 && 830 <= e.getPoint().y && e.getPoint().y <= 860 ) {
            if( wizardPage == 2 && gameConfig.getGameType() == GameType.HVH ) {
              System.out.println( "START" );
              setVisible( false );
              result = gameConfig;
              dispose();
              return;
            }
            System.out.println( "NEXT" );
            wizardPage++;
          }
        } else {
          //b.drawImage( btnStart, 1400, 830, null );
          if( 1400 <= e.getPoint().x && e.getPoint().x <= 1550 && 830 <= e.getPoint().y && e.getPoint().y <= 860 ) {
            System.out.println( "START" );
            setVisible( false );
            result = gameConfig;
            dispose();
            return;
          }
        }
        if( 0 < wizardPage ) {
          //b.drawImage( btnBack, 50, 830, null );
          if( 50 <= e.getPoint().x && e.getPoint().x <= 200 && 830 <= e.getPoint().y && e.getPoint().y <= 860 ) {
            System.out.println( "BACK" );
            wizardPage--;
          }
        }

        int padding = 50;
        int i = 0;
        Player[] players = new Player[2];
        switch( wizardPage ) {
          case 0:
            padding = 500;
            for( BoardType bt : BoardType.values() ) {
              if( bt != gameConfig.getBoardType() ) {
                if( 130 + padding * i <= e.getPoint().x && e.getPoint().x <= 130 + 329 + padding * i && 220 <= e.getPoint().y && e.getPoint().y <= 220 + 329 ) {
                  gameConfig.setBoardType( bt );
                }
              }
              i++;
            }
            break;
          case 1:
            padding = 380;
            for( GameType gt : GameType.values() ) {
              if( gt != gameConfig.getGameType() ) {
                if( 50 + padding * i <= e.getPoint().x && e.getPoint().x <= 50 + 329 + padding * i && 220 <= e.getPoint().y && e.getPoint().y <= 220 + 329 ) {
                  gameConfig.setGameType( gt );
                }
              }
              i++;
            }
            Player defaultComputer = new Player( PlayerType.COMPUTER, Algorithm.UCT, 1 );
            Player defaultHuman = new Player( PlayerType.HUMAN );
            switch( gameConfig.getGameType() ) {
              case CVC:
                players[Board.BLACK] = defaultComputer;
                players[Board.WHITE] = defaultComputer;
                break;
              case CVH:
                players[Board.BLACK] = defaultComputer;
                players[Board.WHITE] = defaultHuman;
                break;
              case HVC:
                players[Board.BLACK] = defaultHuman;
                players[Board.WHITE] = defaultComputer;
                break;
              case HVH:
                players[Board.BLACK] = defaultHuman;
                players[Board.WHITE] = defaultHuman;
                break;
            }
            gameConfig.setPlayers( players );
            //System.out.println( gameConfig.getGameType().name() );
            //System.out.println( gameConfig.getPlayers()[Board.BLACK].playerType + " -vs- " + gameConfig.getPlayers()[Board.WHITE].playerType );
            break;
          case 2:
            padding = 60;
            for( int h = 0; h <= Handicap.MAX; h++ ) {
              if( h != gameConfig.getHandicap() ) {
                if( ( ( h < 5 ) ? 90 : 600 ) <= e.getPoint().x && e.getPoint().x <= ( ( h < 5 ) ? 90 : 600 ) + 150 && ( h % 5 + 1 ) * padding + 230 <= e.getPoint().y && e.getPoint().y <= ( h % 5 + 1 ) * padding + 230 + 30 ) {
                  gameConfig.setHandicap( h );
                }
              }
            }

            break;
          case 3:
            i = 0;
            padding = 45;
            String[] strength_label = { "Easy", "Medium", "Hard" };
            players = gameConfig.getPlayers();
            for( Algorithm alg : Algorithm.values() ) {
              if( players[Board.BLACK].playerType == PlayerType.COMPUTER ) {
                if( alg != players[Board.BLACK].algo ) {
                  if( 100 <= e.getPoint().x && e.getPoint().x <= 100 + 300 && i * padding + 435 <= e.getPoint().y && e.getPoint().y <= i * padding + 435 + 30 ) {
                    System.out.println( "BLACK " + alg );
                    players[Board.BLACK] = new Player( PlayerType.COMPUTER, alg, players[Board.BLACK].param );
                    gameConfig.setPlayers( players );
                  }
                }
              }
              if( players[Board.WHITE].playerType == PlayerType.COMPUTER ) {
                if( alg != players[Board.WHITE].algo ) {
                  if( 700 <= e.getPoint().x && e.getPoint().x <= 700 + 300 && i * padding + 435 <= e.getPoint().y && e.getPoint().y <= i * padding + 435 + 30 ) {
                    System.out.println( "WHITE " + alg );
                    players[Board.WHITE] = new Player( PlayerType.COMPUTER, alg, players[Board.WHITE].param );
                    gameConfig.setPlayers( players );
                  }
                }
              }
              i++;
            }

            for( int s = 0; s < strength_label.length; s++ ) {
              if( players[Board.BLACK].playerType == PlayerType.COMPUTER ) {
                if( s != players[Board.BLACK].param ) {
                  if( 100 <= e.getPoint().x && e.getPoint().x <= 100 + 100 && s * padding + 660 <= e.getPoint().y && e.getPoint().y <= s * padding + 660 + 30 ) {
                    players[Board.BLACK] = new Player( PlayerType.COMPUTER, players[Board.BLACK].algo, s );
                    gameConfig.setPlayers( players );
                  }
                }
              }
            }
            for( int s = 0; s < strength_label.length; s++ ) {
              if( players[Board.WHITE].playerType == PlayerType.COMPUTER ) {
                if( s != players[Board.WHITE].param ) {
                  if( 700 <= e.getPoint().x && e.getPoint().x <= 700 + 100 && s * padding + 660 <= e.getPoint().y && e.getPoint().y <= s * padding + 660 + 30 ) {
                    players[Board.WHITE] = new Player( PlayerType.COMPUTER, players[Board.WHITE].algo, s );
                    gameConfig.setPlayers( players );
                  }
                }
              }
            }
            //System.out.println( gameConfig.getPlayers()[Board.BLACK].algo.name()+" "+ strength_label[gameConfig.getPlayers()[Board.BLACK].param]);
            //System.out.println( gameConfig.getPlayers()[Board.WHITE].algo.name()+" "+ strength_label[gameConfig.getPlayers()[Board.WHITE].param]);
            break;
        }
        canvas.update( canvas.getGraphics() );
        super.mouseClicked( e );
      }
    } );
  }

  class MyCanvas extends Canvas {
    private static final long serialVersionUID = 1L;

    public MyCanvas() throws IOException, FontFormatException {}

    final Image              background      = ImageIO.read( getClass().getResourceAsStream( "/goBackground3.png" ) );
    final Map<String, Image> btnSetBoardType = new HashMap<String, Image>();
    final Map<String, Image> btnSetGameType  = new HashMap<String, Image>();
    final Image              btnStart        = ImageIO.read( getClass().getResourceAsStream( "/btnStart.png" ) );
    final Image              btnBack         = ImageIO.read( getClass().getResourceAsStream( "/buttons/btnBack.png" ) );
    final Image              btnNext         = ImageIO.read( getClass().getResourceAsStream( "/buttons/btnNext.png" ) );
    final Image              btnRadioOff     = ImageIO.read( getClass().getResourceAsStream( "/btnRadioOff.png" ) );
    final Image              btnRadioOn      = ImageIO.read( getClass().getResourceAsStream( "/btnRadioOn.png" ) );
    final Image              player01Disk    = ImageIO.read( getClass().getResourceAsStream( "/stones/w0.png" ) );
    final Image              player02Disk    = ImageIO.read( getClass().getResourceAsStream( "/stones/b.png" ) );
    final Font               font0           = Font.createFont( Font.TRUETYPE_FONT, getClass().getResourceAsStream( "/Kingthings_Petrock.ttf" ) );
    final Color              brownColor      = new Color( 67, 20, 16 );

    // Object initializer block
    {
      btnSetBoardType.put( "btnBoardSMALLon", ImageIO.read( getClass().getResourceAsStream( "/buttons/btnBoardSMALLon.png" ) ) );
      btnSetBoardType.put( "btnBoardSMALLoff", ImageIO.read( getClass().getResourceAsStream( "/buttons/btnBoardSMALLoff.png" ) ) );

      btnSetBoardType.put( "btnBoardMEDIUMon", ImageIO.read( getClass().getResourceAsStream( "/buttons/btnBoardMEDIUMon.png" ) ) );
      btnSetBoardType.put( "btnBoardMEDIUMoff", ImageIO.read( getClass().getResourceAsStream( "/buttons/btnBoardMEDIUMoff.png" ) ) );

      btnSetBoardType.put( "btnBoardLARGEon", ImageIO.read( getClass().getResourceAsStream( "/buttons/btnBoardLARGEon.png" ) ) );
      btnSetBoardType.put( "btnBoardLARGEoff", ImageIO.read( getClass().getResourceAsStream( "/buttons/btnBoardLARGEoff.png" ) ) );

      btnSetGameType.put( "btnGameTypeCVCon", ImageIO.read( getClass().getResourceAsStream( "/buttons/btnGameTypeCVCon.png" ) ) );
      btnSetGameType.put( "btnGameTypeCVCoff", ImageIO.read( getClass().getResourceAsStream( "/buttons/btnGameTypeCVCoff.png" ) ) );

      btnSetGameType.put( "btnGameTypeHVCon", ImageIO.read( getClass().getResourceAsStream( "/buttons/btnGameTypeHVCon.png" ) ) );
      btnSetGameType.put( "btnGameTypeHVCoff", ImageIO.read( getClass().getResourceAsStream( "/buttons/btnGameTypeHVCoff.png" ) ) );

      btnSetGameType.put( "btnGameTypeCVHon", ImageIO.read( getClass().getResourceAsStream( "/buttons/btnGameTypeCVHon.png" ) ) );
      btnSetGameType.put( "btnGameTypeCVHoff", ImageIO.read( getClass().getResourceAsStream( "/buttons/btnGameTypeCVHoff.png" ) ) );

      btnSetGameType.put( "btnGameTypeHVHon", ImageIO.read( getClass().getResourceAsStream( "/buttons/btnGameTypeHVHon.png" ) ) );
      btnSetGameType.put( "btnGameTypeHVHoff", ImageIO.read( getClass().getResourceAsStream( "/buttons/btnGameTypeHVHoff.png" ) ) );
    }

    @Override
    public void paint( Graphics g ) {
      re_display( g );
    }

    @Override
    public void update( Graphics g ) {
      re_display( g );
    }

    private void drawSetting( Graphics2D g2d, int row, final String message ) {
      final int SETTINGPOS_LEFT = 10;
      final int SETTINGPOS_TOP = 100;
      final int SETTING_ROWHEIGHT = 50;
      drawtabString( g2d, message, SETTINGPOS_LEFT, SETTINGPOS_TOP + row * SETTING_ROWHEIGHT );
    }

    public void re_display( Graphics g ) {

      final Dimension dim = getSize();
      Image offscreen = createImage( dim.width, dim.height );
      Graphics g0 = offscreen.getGraphics();
      Graphics2D g2d = (Graphics2D)g0;
      // currentGraphics = g0;

      g0.drawImage( background, 0, 0, null );
      Font font1;

      g2d.setColor( brownColor );
      font1 = font0.deriveFont( 48F );
      g2d.setFont( font1 );
      if( wizardPage < 3 ) {
        if( wizardPage == 2 && gameConfig.getGameType() == GameType.HVH )
          g0.drawImage( btnStart, 1400, 830, null );
        else
          g0.drawImage( btnNext, 1400, 830, null );
      } else {
        g0.drawImage( btnStart, 1400, 830, null );
      }
      if( 0 < wizardPage ) {
        g0.drawImage( btnBack, 50, 830, null );
      }
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
            final String onOff = ( bt == gameConfig.getBoardType() ? "on" : "off" );
            g0.drawImage( btnSetBoardType.get( "btnBoard" + bt.name() + onOff ), 130 + BOARDTYPES_COL_WIDTH * i, 220, null );
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
          final int GAMETYPE_COL_WIDTH = 380;
          font1 = font0.deriveFont( 32F );
          g2d.setFont( font1 );
          for( GameType gt : GameType.values() ) {
            final String onOff = ( gt == gameConfig.getGameType() ? "on" : "off" );
            g0.drawImage( btnSetGameType.get( "btnGameType" + gt.name() + onOff ), 50 + GAMETYPE_COL_WIDTH * i++, 220, null );
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
      g.drawImage( offscreen, 0, 0, this );

    } // re_display()

    private void drawtabString( Graphics g, String text, int x, int y ) {
      for( String line : text.split( "\t" ) )
        g.drawString( line, x += g.getFontMetrics().getHeight(), y );
    }

  } // class MyCanvas

  @Override
  protected void closing() {
    result = null;
  }

  @Override
  protected void closed() {
    // TODO Auto-generated method stub

  }

}
