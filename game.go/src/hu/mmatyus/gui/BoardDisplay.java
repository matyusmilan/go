package hu.mmatyus.gui;

import hu.mmatyus.model.Board;
import hu.mmatyus.model.BoardEval;
import hu.mmatyus.model.GameConfig;
import hu.mmatyus.model.Handicap;
import hu.mmatyus.model.Player;
import hu.mmatyus.model.PlayerPolicy;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

@SuppressWarnings( "serial" )
public class BoardDisplay extends Frame {
  public static final int CHANGE_POS     = 80;
  public static final int WIDTH          = 1600;
  public static final int HEIGHT         = 900;

  public char[]           letters;
  public int[]            currentWhiteType;     // There are 16 different white stone graphics
  BoardEval               eval           = null;
  DispCanvas              canvas;
  Dimension               dim;
  int                     cell_size;

  // Graphics                currentGraphics;

  Board                   board;
  GameConfig              gameConfig;
  int                     computer;
  Listener                listener;
  boolean                 gameInProgress;
  public int              settingsWindow = 0;

  public BoardDisplay( final Board board, final GameConfig gameConfig, String title ) throws IOException, FontFormatException {
    super();
    gameInProgress = false;
    // Initializing 'letters' with first sideLength alphabet's element
    letters = new char[board.boardType.sideLength];
    for( int li = 0; li < letters.length; li++ ) {
      letters[li] = (char) ( li + 65 );
    }
    // Initializing 'currentWhiteType' with zero
    currentWhiteType = new int[board.boardType.sideLength * board.boardType.sideLength];
    for( int i = 0; i < currentWhiteType.length; ++i ) {
      currentWhiteType[i] = 0;
    }
    setSize( WIDTH, HEIGHT );
    setTitle( title );
    setResizable( false );
    try {
      Image gameIcon = ImageIO.read( new File( "res/icon128.png" ) );
      setIconImage( gameIcon );
    }
    catch( IOException e ) {
      e.printStackTrace();
    }
    addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing( WindowEvent we ) {
        System.exit( 0 );
      }
    } );

    canvas = new DispCanvas();

    canvas.addMouseListener( new MouseAdapter() {
      @Override
      public void mousePressed( MouseEvent e ) {
        if( !board.isGameOver() ) {
          if( e.getPoint().y < HEIGHT && e.getPoint().x < HEIGHT ) {
            if( 0 < ( e.getPoint().x - CHANGE_POS ) * ( e.getPoint().y - CHANGE_POS ) ) {
              int x = ( e.getPoint().x - CHANGE_POS ) / cell_size;
              int y = ( e.getPoint().y - CHANGE_POS ) / cell_size;
              int side = board.sideLength;
              System.out.println( x + " " + y );
              if( 0 <= x && x < side && 0 <= y && y < side )
                click( x, y );
            }
          } else {
            if( board.getNextPlayer() == Board.WHITE ) {
              if( 1330 <= e.getPoint().x && e.getPoint().x <= 1380 && 520 <= e.getPoint().y && e.getPoint().y <= 620 ) {
                System.out.println( "WHITE clicked the PASS button!" );
                onCellClick( Board.PASS_MOVE );
              } else if( 1330 <= e.getPoint().x && e.getPoint().x <= 1380 && 620 <= e.getPoint().y && e.getPoint().y <= 720 ) {
                System.out.println( "WHITE clicked the RESIGN button!" );
                onCellClick( Board.RESIGN_MOVE );
              }
            }
            if( board.getNextPlayer() == Board.BLACK ) {
              if( 1330 <= e.getPoint().x && e.getPoint().x <= 1380 && 220 <= e.getPoint().y && e.getPoint().y <= 320 ) {
                System.out.println( "BLACK clicked the PASS button!" );
                onCellClick( Board.PASS_MOVE );
              } else if( 1330 <= e.getPoint().x && e.getPoint().x <= 1380 && 320 <= e.getPoint().y && e.getPoint().y <= 420 ) {
                System.out.println( "BLACK clicked the RESIGN button!" );
                onCellClick( Board.RESIGN_MOVE );
              }
            }
            update();
          }
        }
      }
    } );

    add( canvas );
    this.board = board;
    this.gameConfig = gameConfig;
    setVisible( true );
  }

  void click( int x, int y ) {
    int pos = board.getPos( x, y );
    if( listener != null ) {
      onCellClick( pos );
    }
  }

  boolean nextIsHuman() {
    final int playerIdx = board.getNextPlayer();
    final Player player = gameConfig.getPlayers()[playerIdx];
    return ( player.type == Player.Type.HUMAN );
  }

  /**
   * Next player moves.
   * 
   * @param pos
   *          Linear position on board.
   */
  void onCellClick( int pos ) {
    if( !board.isLegalMove( pos ) )
      return;
    board.move( pos );
    if( board.isGameOver() || board.getPassNum() == 2 ) {
      this.setVisible( false );
      BoardEval eval = new BoardEval( board, new PlayerPolicy() );
      eval.eval( 10000, 0.4 );
      listener.onSuccess( eval.getScore() );
    }
    update();
  }

  public void setEval( BoardEval eval ) {
    this.eval = eval;
    update();
  }

//  @Override
//  public Graphics getGraphics() {
//    return currentGraphics;
//  }

  public void setListener( Listener l ) {
    listener = l;
  }

  public void update() {
    canvas.re_display( canvas.getGraphics() );
  }

  public void setComputerColor( int computerColor ) {
    this.computer = computerColor;
  }

  public interface Listener {
    void onSuccess( double score );

    void onFailure( Exception e );
  }

  public enum PlayerGraphic {
    HUMAN( "Human", "res/actHuman2.png", "res/pasHuman.png" ),
    COMPUTER( "MR. ROBOT", "res/actRobot2.png", "res/pasRobot.png" );
    public final String name;
    public Image        activeImg  = null;
    public Image        passiveImg = null;

    PlayerGraphic( String name, String actImgPath, String pasImgPath ) {
      this.name = name;
      try {
        this.activeImg = ImageIO.read( new File( actImgPath ) );
        this.passiveImg = ImageIO.read( new File( pasImgPath ) );
      }
      catch( IOException e ) {
        e.printStackTrace();
      }
    }
  }

  class DispCanvas extends Canvas {
    PlayerGraphic blackPlayer = PlayerGraphic.HUMAN;
    PlayerGraphic whitePlayer = PlayerGraphic.HUMAN;

    public DispCanvas() throws IOException, FontFormatException {}

    final Image background       = ImageIO.read( new File( "res/goBackground3.png" ) );
    final Image player01Disk     = ImageIO.read( new File( "res/stones/w0.png" ) );
    final Image player02Disk     = ImageIO.read( new File( "res/stones/b.png" ) );
    final Image statusMessage    = ImageIO.read( new File( "res/StatusMessage.png" ) );
    final Image switchButtonOff  = ImageIO.read( new File( "res/SwitchOff.png" ) );
    final Image activePassButton = ImageIO.read( new File( "res/SwitchOn.png" ) );
    final Image btnRules         = ImageIO.read( new File( "res/btnRulesOfGo.png" ) );
    final Image btnNewGame       = ImageIO.read( new File( "res/btnNewGame.png" ) );

    final Font  font0            = Font.createFont( Font.TRUETYPE_FONT, new FileInputStream( "res/Kingthings_Petrock.ttf" ) );
    final Color brownColor       = new Color( 67, 20, 16 );

    @Override
    public void paint( Graphics g ) {
      re_display( g );
    }

    @Override
    public void update( Graphics g ) {
      re_display( g );
    }

    public void re_display( Graphics g ) {
      dim = getSize();

      Image offscreen = createImage( dim.width, dim.height );
      Graphics g0 = offscreen.getGraphics();
      Graphics2D g2d = (Graphics2D)g0;
      // currentGraphics = g0;

      g0.drawImage( background, 0, 0, null );
      Font font1;

      // draw board
      if( board != null ) {

        font1 = font0.deriveFont( 48F );
        g2d.setFont( font1 );
        FontMetrics metrics = g2d.getFontMetrics( font1 );
        g2d.setColor( brownColor );

        if( Board.BLACK == computer ) {
          blackPlayer = PlayerGraphic.COMPUTER;
        }
        if( Board.WHITE == computer ) {
          whitePlayer = PlayerGraphic.COMPUTER;
        }

        g2d.drawString( blackPlayer.name, 1200 - metrics.stringWidth( blackPlayer.name ) / 2, 180 );
        g2d.drawString( whitePlayer.name, 1200 - metrics.stringWidth( whitePlayer.name ) / 2, 480 );

        g2d.setFont( Font.decode( "Arial bold 24" ) );
        g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, .6f ) );
        g0.drawImage( statusMessage, 940, 776, null );
        g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1.0f ) );
        //b.drawLine( 900, 0, 900, 900 );
        //b.drawLine( 900, 124, 1600, 124 );

        if( board.getLastMove() != Board.RESIGN_MOVE ) {
          if( board.getLastMove() != Board.PASS_MOVE ) {
            if( PlayerGraphic.HUMAN == whitePlayer ) {
              g0.drawImage( switchButtonOff, 1330, 520, null );
              g2d.drawString( "Pass", 1380, 560 );
              g0.drawImage( switchButtonOff, 1330, 620, null );
              g2d.drawString( "Resign", 1380, 660 );
            }
            if( PlayerGraphic.HUMAN == blackPlayer ) {
              updateButtons( g0, g2d, switchButtonOff );
            }
          } else {
            if( board.getPassNum() == 2 ) {
              if( PlayerGraphic.HUMAN == whitePlayer ) {
                g0.drawImage( activePassButton, 1330, 520, null );
                g2d.drawString( "Passed", 1380, 560 );
                g0.drawImage( switchButtonOff, 1330, 620, null );
                g2d.drawString( "Resign", 1380, 660 );
              }
              if( PlayerGraphic.HUMAN == blackPlayer ) {
                g0.drawImage( activePassButton, 1330, 220, null );
                g2d.drawString( "Passed", 1380, 260 );
                g0.drawImage( switchButtonOff, 1330, 320, null );
                g2d.drawString( "Resign", 1380, 360 );
              }
            } else {
              if( board.getNextPlayer() == Board.BLACK ) {
                if( PlayerGraphic.HUMAN == whitePlayer ) {
                  g0.drawImage( activePassButton, 1330, 520, null );
                  g2d.drawString( "Passed", 1380, 560 );
                  g0.drawImage( switchButtonOff, 1330, 620, null );
                  g2d.drawString( "Resign", 1380, 660 );
                }
                if( PlayerGraphic.HUMAN == blackPlayer ) {
                  g0.drawImage( switchButtonOff, 1330, 220, null );
                  g2d.drawString( "Pass", 1380, 260 );
                  g0.drawImage( switchButtonOff, 1330, 320, null );
                  g2d.drawString( "Resign", 1380, 360 );
                }
              } else {
                if( PlayerGraphic.HUMAN == whitePlayer ) {
                  g0.drawImage( switchButtonOff, 1330, 520, null );
                  g2d.drawString( "Passed", 1380, 560 );
                  g0.drawImage( switchButtonOff, 1330, 620, null );
                  g2d.drawString( "Resign", 1380, 660 );
                }
                if( PlayerGraphic.HUMAN == blackPlayer ) {
                  g0.drawImage( activePassButton, 1330, 220, null );
                  g2d.drawString( "Passed", 1380, 260 );
                  g0.drawImage( switchButtonOff, 1330, 320, null );
                  g2d.drawString( "Resign", 1380, 360 );
                }
              }
            }
          }
        } else {
          if( board.getNextPlayer() == Board.BLACK ) {
            if( PlayerGraphic.HUMAN == whitePlayer ) {
              g0.drawImage( switchButtonOff, 1330, 520, null );
              g2d.drawString( "Pass", 1380, 560 );
              g0.drawImage( switchButtonOff, 1330, 620, null );
              g2d.drawString( "Resign", 1380, 660 );
            }
            if( PlayerGraphic.HUMAN == blackPlayer ) {
              g0.drawImage( switchButtonOff, 1330, 220, null );
              g2d.drawString( "Pass", 1380, 260 );
              g0.drawImage( activePassButton, 1330, 320, null );
              g2d.drawString( "Resigned", 1380, 360 );
            }
          } else {
            if( PlayerGraphic.HUMAN == whitePlayer ) {
              g0.drawImage( switchButtonOff, 1330, 520, null );
              g2d.drawString( "Pass", 1380, 560 );
              g0.drawImage( activePassButton, 1330, 620, null );
              g2d.drawString( "Resigned", 1380, 660 );
            }
            if( PlayerGraphic.HUMAN == blackPlayer ) {
              g0.drawImage( switchButtonOff, 1330, 220, null );
              g2d.drawString( "Pass", 1380, 260 );
              g0.drawImage( switchButtonOff, 1330, 320, null );
              g2d.drawString( "Resigned", 1380, 360 );
            }
          }
        }

        g2d.setColor( new Color( 255, 255, 255 ) );
        if( !board.isGameOver() ) {
          if( board.getNextPlayer() == Board.BLACK ) {
            g0.drawImage( blackPlayer.activeImg, 1100, 200, null );
            g0.drawImage( whitePlayer.passiveImg, 1100, 500, null );
            g2d.drawString( ( PlayerGraphic.COMPUTER == blackPlayer ) ? "[GO]> BLACK is thinking..." : "[GO]> waiting for the BLACK player...", 960, 810 );
          }
          if( board.getNextPlayer() == Board.WHITE ) {
            g0.drawImage( blackPlayer.passiveImg, 1100, 200, null );
            g0.drawImage( whitePlayer.activeImg, 1100, 500, null );
            g2d.drawString( ( PlayerGraphic.COMPUTER == whitePlayer ) ? "[GO]> WHITE is thinking..." : "[GO]> waiting for the WHITE player...", 960, 810 );
          }
        } else {
          g0.drawImage( blackPlayer.passiveImg, 1100, 200, null );
          g0.drawImage( whitePlayer.passiveImg, 1100, 500, null );
          if( board.getLastMove() == Board.PASS_MOVE ) {
            g2d.drawString( "[GO]> The winner is the " + ( eval.getScore() > 0 ? "BLACK" : "WHITE" ) + "!", 960, 810 );
          } else if( board.getLastMove() == Board.RESIGN_MOVE ) {
            g2d.drawString( "[GO]> GAME OVER! The " + ( board.getNextPlayer() == Board.BLACK ? "BLACK" : "WHITE" ) + " resigned.", 960, 810 );
          }
        }

        int n = board.sideLength;
        cell_size = ( dim.width - CHANGE_POS ) / n;
        if( dim.height / n < cell_size )
          cell_size = ( dim.height - CHANGE_POS ) / n;
        int d2 = cell_size / 2 + CHANGE_POS;
        int r = cell_size / 2 - 3;

        g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, .2f ) );
        g2d.setColor( Color.BLACK );
        g2d.fillOval( 1020 - r + 4, 295 - r + 4, 2 * r, 2 * r );
        g2d.fillOval( 1020 - r + 4, 590 - r + 4, 2 * r, 2 * r );
        g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1.0f ) );
        g0.drawImage( player02Disk.getScaledInstance( 2 * r, 2 * r, Image.SCALE_AREA_AVERAGING ), 1020 - r, 295 - r, 2 * r, 2 * r, this );
        g0.drawImage( player01Disk.getScaledInstance( 2 * r, 2 * r, Image.SCALE_AREA_AVERAGING ), 1020 - r, 590 - r, 2 * r, 2 * r, this );

        Random randomGenerator = new Random();
        // draw grid
        g0.setColor( Color.black );
        for( int i = 0; i < n; ++i ) {
          g0.drawLine( d2 + i * cell_size, d2, d2 + i * cell_size, d2 + ( n - 1 ) * cell_size );
          g0.drawLine( d2, d2 + i * cell_size, d2 + ( n - 1 ) * cell_size, d2 + i * cell_size );
        }
        Set<Integer> bigPoints = new Handicap( board.boardType ).getHandicapStones( board.boardType.startPoints );
        // draw stones
        g0.setFont( Font.decode( "Arial bold 12" ) );
        BufferedImage currentDisk = null;
        for( int x = 0; x < n; ++x ) {
          for( int y = 0; y < n; ++y ) {
            int x1 = d2 + x * cell_size;
            int y1 = d2 + y * cell_size;

            int pos = board.getPos( x, y );

            if( bigPoints.contains( pos ) ) {
              g0.setColor( Color.BLACK );
              g0.fillOval( x1 - 3, y1 - 3, 6, 6 );
            }

            // state
            int state = board.getState( pos );
            if( state != Board.EMPTY ) {
              g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, .2f ) );
              g2d.setColor( Color.BLACK );
              g2d.fillOval( x1 - r + 4, y1 - r + 4, 2 * r, 2 * r );
              g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1.0f ) );
              try {
                if( state == Board.WHITE ) {
                  if( currentWhiteType[pos] == 0 ) {
                    currentWhiteType[pos] = randomGenerator.nextInt( 16 );
                  }
                  currentDisk = ImageIO.read( new File( "res/stones/w" + currentWhiteType[pos] + ".png" ) );
                } else {
                  currentDisk = ImageIO.read( new File( "res/stones/b.png" ) );
                }
              }
              catch( Exception ex ) {
                ex.printStackTrace();
              }
              if( currentDisk != null ) {
                g0.drawImage( currentDisk.getScaledInstance( 2 * r, 2 * r, Image.SCALE_AREA_AVERAGING ), x1 - r, y1 - r, 2 * r, 2 * r, this );
              }
            }

            // eval
            if( eval != null ) {
              double t = eval.getArea( pos );
              int size = 0;
              if( t < 0.5 ) {
                size = (int) ( r * ( 0.5 - t ) );
                g0.setColor( Color.WHITE );
              } else {
                size = (int) ( r * ( t - 0.5 ) );
                g0.setColor( Color.BLACK );
              }
              g0.fillRect( x1 - size, y1 - size, 2 * size, 2 * size );
            }
            // last move
            if( pos == board.getLastMove() ) {
              g0.setColor( Color.RED );
              g0.fillOval( x1 - r / 3, y1 - r / 3, 2 * r / 3, 2 * r / 3 );
            }
            if( x == 0 ) {
              g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
              g2d.setColor( brownColor );
              font1 = font0.deriveFont( board.boardType.posLabelSize );
              g2d.setFont( font1 );
              metrics = g2d.getFontMetrics( font1 );
              g2d.drawString( "" + letters[y], d2 + y * cell_size - metrics.stringWidth( "" + letters[y] ) / 2, d2 - cell_size / 2 );
            }
            if( y == 0 ) {
              g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
              g2d.setColor( brownColor );
              font1 = font0.deriveFont( board.boardType.posLabelSize );
              g2d.setFont( font1 );
              metrics = g2d.getFontMetrics( font1 );
              g2d.drawString( n - x + "", d2 - cell_size / 10 * 8 - ( metrics.stringWidth( n - x + "" ) / 2 ), d2 + x * cell_size + board.boardType.posLabelSize / 10 * 4 );
            }

            // pos
            //if (board.getLifeOfString(pos) != 0) {
            //String posStr = board.getLifeOfString( pos ) + "";
            String posStr = pos + "";
            g0.setFont( Font.decode( "Arial bold 12" ) );
            Rectangle2D rect = g0.getFontMetrics().getStringBounds( posStr, g0 );
            g0.setColor( Color.BLUE );
            g0.drawString( posStr, 10 + (int) ( x1 - rect.getWidth() / 2 ), 10 + (int) ( y1 + rect.getHeight() / 2 ) );
            //}
          }
        }
      }
      g.drawImage( offscreen, 0, 0, this );
    }

    private void updateButtons( Graphics b, Graphics2D g2d, Image passButton ) {
      b.drawImage( passButton, 1330, 220, null );
      g2d.drawString( "Pass", 1380, 260 );
      b.drawImage( passButton, 1330, 320, null );
      g2d.drawString( "Resign", 1380, 360 );
    }
  }
}
