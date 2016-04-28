package hu.mmatyus.gui;

import hu.mmatyus.model.Algorithm;
import hu.mmatyus.model.Board;
import hu.mmatyus.model.BoardType;
import hu.mmatyus.model.GameConfig;
import hu.mmatyus.model.GameType;
import hu.mmatyus.model.Handicap;
import hu.mmatyus.model.Player;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class SettingsDisplay extends Frame {
  private static final long  serialVersionUID = 1L;
  public static final int    WIDTH            = 1600;
  public static final int    HEIGHT           = 900;
  public static final String TITLE            = "g(\u03C9) – GOmega / Settings";

  public interface Client {
    void onSuccess( GameConfig config );

    void onFailure( Exception e );
  }

  public SettingsDisplay( Client c ) throws IOException, FontFormatException {
    this.client = c;

    setSize( WIDTH, HEIGHT );
    setTitle( TITLE );
    setResizable( false );

    setupCanvas();
    add( canvas );

    addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing( WindowEvent we ) {
        System.exit( 0 );
      }
    } );
  }

  private Client client;

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
              client.onSuccess( gameConfig );
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
            client.onSuccess( gameConfig );
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
            Player defaultComputer = new Player( Player.Type.COMPUTER, Algorithm.UCT, 1 );
            Player defaultHuman = new Player( Player.Type.HUMAN );
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
            //System.out.println( gameConfig.getPlayers()[Board.BLACK].type + " -vs- " + gameConfig.getPlayers()[Board.WHITE].type );
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
              if( players[Board.BLACK].type == Player.Type.COMPUTER ) {
                if( alg != players[Board.BLACK].algo ) {
                  if( 100 <= e.getPoint().x && e.getPoint().x <= 100 + 300 && i * padding + 435 <= e.getPoint().y && e.getPoint().y <= i * padding + 435 + 30 ) {
                    System.out.println("BLACK "+alg);
                    players[Board.BLACK] =  new Player(Player.Type.COMPUTER, alg, players[Board.BLACK].param);
                    gameConfig.setPlayers( players );
                  }
                }
              }
              if( players[Board.WHITE].type == Player.Type.COMPUTER ) {
                if( alg != players[Board.WHITE].algo ) {
                  if( 700 <= e.getPoint().x && e.getPoint().x <= 700 + 300 && i * padding + 435 <= e.getPoint().y && e.getPoint().y <= i * padding + 435 + 30 ) {
                    System.out.println("WHITE "+alg);
                    players[Board.WHITE] = new Player(Player.Type.COMPUTER, alg, players[Board.WHITE].param);
                    gameConfig.setPlayers( players );
                  }
                }
              }
              i++;
            }
           
            for( int s = 0; s < strength_label.length; s++ ) {
              if( players[Board.BLACK].type == Player.Type.COMPUTER ) {
                if( s != players[Board.BLACK].param ) {
                  if( 100 <= e.getPoint().x && e.getPoint().x <= 100 + 100 && s * padding + 660 <= e.getPoint().y && e.getPoint().y <= s * padding + 660 + 30 ) {
                    players[Board.BLACK] = new Player(Player.Type.COMPUTER, players[Board.BLACK].algo, s);
                    gameConfig.setPlayers( players );
                  }
                }
              }
            }
            for( int s = 0; s < strength_label.length; s++ ) {
              if( players[Board.WHITE].type == Player.Type.COMPUTER ) {
                if( s != players[Board.WHITE].param ) {
                  if( 700 <= e.getPoint().x && e.getPoint().x <= 700 + 100 && s * padding + 660 <= e.getPoint().y && e.getPoint().y <= s * padding + 660 + 30 ) {
                    players[Board.WHITE] = new Player(Player.Type.COMPUTER, players[Board.WHITE].algo, s);
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

    final Image              background      = ImageIO.read( new File( "res/goBackground3.png" ) );
    final Map<String, Image> btnSetBoardType = new HashMap<String, Image>();
    final Map<String, Image> btnSetGameType  = new HashMap<String, Image>();
    final Image              btnStart        = ImageIO.read( new File( "res/btnStart.png" ) );
    final Image              btnBack         = ImageIO.read( new File( "res/buttons/btnBack.png" ) );
    final Image              btnNext         = ImageIO.read( new File( "res/buttons/btnNext.png" ) );
    final Image              btnRadioOff     = ImageIO.read( new File( "res/btnRadioOff.png" ) );
    final Image              btnRadioOn      = ImageIO.read( new File( "res/btnRadioOn.png" ) );
    final Image              player01Disk    = ImageIO.read( new File( "res/stones/w0.png" ) );
    final Image              player02Disk    = ImageIO.read( new File( "res/stones/b.png" ) );
    final Font               font0           = Font.createFont( Font.TRUETYPE_FONT, new FileInputStream( "res/Kingthings_Petrock.ttf" ) );
    final Color              brownColor      = new Color( 67, 20, 16 );

    // Object initializer block
    {
      btnSetBoardType.put( "btnBoardSMALLon", ImageIO.read( new File( "res/buttons/btnBoardSMALLon.png" ) ) );
      btnSetBoardType.put( "btnBoardSMALLoff", ImageIO.read( new File( "res/buttons/btnBoardSMALLoff.png" ) ) );

      btnSetBoardType.put( "btnBoardMEDIUMon", ImageIO.read( new File( "res/buttons/btnBoardMEDIUMon.png" ) ) );
      btnSetBoardType.put( "btnBoardMEDIUMoff", ImageIO.read( new File( "res/buttons/btnBoardMEDIUMoff.png" ) ) );

      btnSetBoardType.put( "btnBoardLARGEon", ImageIO.read( new File( "res/buttons/btnBoardLARGEon.png" ) ) );
      btnSetBoardType.put( "btnBoardLARGEoff", ImageIO.read( new File( "res/buttons/btnBoardLARGEoff.png" ) ) );

      btnSetGameType.put( "btnGameTypeCVCon", ImageIO.read( new File( "res/buttons/btnGameTypeCVCon.png" ) ) );
      btnSetGameType.put( "btnGameTypeCVCoff", ImageIO.read( new File( "res/buttons/btnGameTypeCVCoff.png" ) ) );

      btnSetGameType.put( "btnGameTypeHVCon", ImageIO.read( new File( "res/buttons/btnGameTypeHVCon.png" ) ) );
      btnSetGameType.put( "btnGameTypeHVCoff", ImageIO.read( new File( "res/buttons/btnGameTypeHVCoff.png" ) ) );

      btnSetGameType.put( "btnGameTypeCVHon", ImageIO.read( new File( "res/buttons/btnGameTypeCVHon.png" ) ) );
      btnSetGameType.put( "btnGameTypeCVHoff", ImageIO.read( new File( "res/buttons/btnGameTypeCVHoff.png" ) ) );

      btnSetGameType.put( "btnGameTypeHVHon", ImageIO.read( new File( "res/buttons/btnGameTypeHVHon.png" ) ) );
      btnSetGameType.put( "btnGameTypeHVHoff", ImageIO.read( new File( "res/buttons/btnGameTypeHVHoff.png" ) ) );
    }

    @Override
    public void paint( Graphics g ) {
      re_display( g );
    }

    @Override
    public void update( Graphics g ) {
      re_display( g );
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
      int padding = 0;
      int i = 0;

      switch( wizardPage ) {
        case 0:
          drawtabString( g2d, "1.) Board size: ", 10, 100 );
          i = 0;
          font1 = font0.deriveFont( 32F );
          g2d.setFont( font1 );
          padding = 500;
          for( BoardType bt : BoardType.values() ) {
            g0.drawImage( ( bt == gameConfig.getBoardType() ) ? btnSetBoardType.get( "btnBoard" + bt.name() + "on" ) : btnSetBoardType.get( "btnBoard" + bt.name() + "off" ), 130 + padding * i, 220, null );
            g2d.drawString( bt.name()+" ("+bt.label+")", 220 + padding * i, 610 );
            i++;
          }
          break;
        case 1:
          drawtabString( g2d, "1.) Board size:\t\t\t\t\t\t" + gameConfig.getBoardType().name() + " (" + gameConfig.getBoardType().label + ")", 10, 100 );
          drawtabString( g2d, "2.) Game type:", 10, 150 );
          i = 0;
          padding = 380;
          font1 = font0.deriveFont( 32F );
          g2d.setFont( font1 );
          for( GameType gt : GameType.values() ) {
            g0.drawImage( ( gt == gameConfig.getGameType() ) ? btnSetGameType.get( "btnGameType" + gt.name() + "on" ) : btnSetGameType.get( "btnGameType" + gt.name() + "off" ), 50 + padding * i++, 220, null );
          }
          break;
        case 2:
          drawtabString( g2d, "1.) Board size:\t\t\t\t\t\t" + gameConfig.getBoardType().name() + " (" + gameConfig.getBoardType().label + ")", 10, 100 );
          drawtabString( g2d, "2.) Game type:\t\t\t\t\t\t" + gameConfig.getGameType().label, 10, 150 );
          drawtabString( g2d, "3.) Set handicap: ", 10, 200 );
          font1 = font0.deriveFont( 32F );
          g2d.setFont( font1 );
          padding = 60;
          for( int h = 0; h <= Handicap.MAX; h++ ) {
            g0.drawImage( ( h == gameConfig.getHandicap() ) ? btnRadioOn : btnRadioOff, ( h < 5 ) ? 90 : 600, ( h % 5 + 1 ) * padding + 230, null );
            g2d.drawString( h + " stone" + ( ( h == 1 ) ? "" : "s" ), ( h < 5 ) ? 120 : 630, ( h % 5 + 1 ) * padding + 250 );
          }
          break;
        case 3:
          drawtabString( g2d, "1.) Board size:\t\t\t\t\t\t" + gameConfig.getBoardType().name() + " (" + gameConfig.getBoardType().label + ")", 10, 100 );
          drawtabString( g2d, "2.) Game type:\t\t\t\t\t\t" + gameConfig.getGameType().label, 10, 150 );
          drawtabString( g2d, "3.) Set handicap:\t\t\t\t\t\t" + gameConfig.getHandicap(), 10, 200 );
          drawtabString( g2d, "4.) Computer: ", 10, 250 );
          drawtabString( g2d, "a.) Algorithm: ", 10, 410 );
          drawtabString( g2d, "b.) Strength: ", 10, 640 );
          g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, .2f ) );
          g2d.setColor( Color.BLACK );
          int r = 50;
          g2d.fillOval( 120 - r + 4, 320 - r + 4, 2 * r, 2 * r );
          g2d.fillOval( 720 - r + 4, 320 - r + 4, 2 * r, 2 * r );
          g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1.0f ) );
          g0.drawImage( player02Disk.getScaledInstance( 2 * r, 2 * r, Image.SCALE_AREA_AVERAGING ), 120 - r, 320 - r, 2 * r, 2 * r, this );
          g0.drawImage( player01Disk.getScaledInstance( 2 * r, 2 * r, Image.SCALE_AREA_AVERAGING ), 720 - r, 320 - r, 2 * r, 2 * r, this );
          Player[] players = gameConfig.getPlayers();
          g2d.drawString( players[Board.BLACK].type.toString(), 190, 330 );
          g2d.drawString( players[Board.WHITE].type.toString(), 790, 330 );
          i = 0;
          padding = 45;
          String[] strength_label = { "Easy", "Medium", "Hard" };
          for( Algorithm alg : Algorithm.values() ) {
            if( players[Board.BLACK].type == Player.Type.COMPUTER ) {
              g0.drawImage( ( alg == players[Board.BLACK].algo ) ? btnRadioOn : btnRadioOff, 100, i * padding + 435, null );
              g2d.drawString( alg.label, 130, i * padding + 460 );
            }

            if( players[Board.WHITE].type == Player.Type.COMPUTER ) {
              g0.drawImage( ( alg == players[Board.WHITE].algo ) ? btnRadioOn : btnRadioOff, 700, i * padding + 435, null );
              g2d.drawString( alg.label, 730, i * padding + 460 );
            }

            i++;
          }
          i = 0;
          for( int s = 0; s < strength_label.length; s++ ) {
            if( players[Board.BLACK].type == Player.Type.COMPUTER ) {
              g0.drawImage( ( s == players[Board.BLACK].param ) ? btnRadioOn : btnRadioOff, 100, i * padding + 660, null );
              g2d.drawString( strength_label[s], 130, i * padding + 685 );
            }
            if( players[Board.WHITE].type == Player.Type.COMPUTER ) {
              g0.drawImage( ( s == players[Board.WHITE].param ) ? btnRadioOn : btnRadioOff, 700, i * padding + 660, null );
              g2d.drawString( strength_label[s], 730, i * padding + 685 );
            }
            i++;
          }
          break;
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

  private MyCanvas   canvas     = new MyCanvas();
  private int        wizardPage = 0;
  private GameConfig gameConfig = new GameConfig();
}
