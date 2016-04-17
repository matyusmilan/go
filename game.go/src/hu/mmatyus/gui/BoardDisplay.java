package hu.mmatyus.gui;

import hu.mmatyus.Play;
import hu.mmatyus.model.Board;
import hu.mmatyus.model.BoardEval;
import hu.mmatyus.model.BoardType;
import hu.mmatyus.model.GameType;
import hu.mmatyus.model.GameConfig;
import hu.mmatyus.model.Handicap;
import hu.mmatyus.model.Player;
import hu.mmatyus.model.Algorithm;
import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@SuppressWarnings( "serial" )
public class BoardDisplay extends Frame {
  public static final int CHANGE_POS     = 80;
  public static final int HEIGHT         = 900;
  public char[]           letters;
  public int[]            currentWhiteType;     // There are 16 different white stone graphics
  BoardEval               eval           = null;
  DispCanvas              canvas;
  Dimension               dim;
  int                     cell_size;
  Graphics                currentGraphics;
  Board                   board;
  GameConfig              gameConfig;
  int                     computer;
  Listener                listener;
  boolean                 gameInProgress;
  public int              settingsWindow = 0;

  public BoardDisplay( final Board board, final GameConfig gameConfig, String title ) {
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
    setSize( 1600, HEIGHT );
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
        if( gameInProgress ) {
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
                  listener.onCellClick( Board.PASS_MOVE );
                } else if( 1330 <= e.getPoint().x && e.getPoint().x <= 1380 && 620 <= e.getPoint().y && e.getPoint().y <= 720 ) {
                  System.out.println( "WHITE clicked the RESIGN button!" );
                  listener.onCellClick( Board.RESIGN_MOVE );
                }
              }
              if( board.getNextPlayer() == Board.BLACK ) {
                if( 1330 <= e.getPoint().x && e.getPoint().x <= 1380 && 220 <= e.getPoint().y && e.getPoint().y <= 320 ) {
                  System.out.println( "BLACK clicked the PASS button!" );
                  listener.onCellClick( Board.PASS_MOVE );
                } else if( 1330 <= e.getPoint().x && e.getPoint().x <= 1380 && 320 <= e.getPoint().y && e.getPoint().y <= 420 ) {
                  System.out.println( "BLACK clicked the RESIGN button!" );
                  listener.onCellClick( Board.RESIGN_MOVE );
                }
              }
              update();
            }
          }
        } else {
          
          //b.drawImage( btnNext, 1400, 830, null )
          if(settingsWindow < 3){
            //b.drawImage( btnNext, 1400, 830, null );
            if( 1400 <= e.getPoint().x && e.getPoint().x <= 1550 && 830 <= e.getPoint().y && e.getPoint().y <= 860 ) {
              System.out.println( "NEXT" );
              settingsWindow++;
            }
          } else {
            //b.drawImage( btnStart, 1400, 830, null );
            if( 1400 <= e.getPoint().x && e.getPoint().x <= 1550 && 830 <= e.getPoint().y && e.getPoint().y <= 860 ) {
              System.out.println( "START" );
              gameInProgress = true;
              settingsWindow = 0;
            }
          }
          if(0 < settingsWindow){
            //b.drawImage( btnBack, 50, 830, null );
            if( 50 <= e.getPoint().x && e.getPoint().x <= 200 && 830 <= e.getPoint().y && e.getPoint().y <= 860 ) {
              System.out.println( "BACK" );
              settingsWindow--;
            }
          }
          update();
          int padding = 50;
          int i = 0;
          switch( settingsWindow ) {
            case 0:
              padding = 400;
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
              break;
            case 2:
              padding=60;
              for(int h=0; h<=Handicap.MAX; h++){
                if( h != gameConfig.getHandicap()){
                  if(((h%2 == 0)?90:600) <= e.getPoint().x && e.getPoint().x <= ((h%2 == 0)?90:600) + 150  && (int)h/2* padding + 230 <= e.getPoint().y && e.getPoint().y <= (int)h/2* padding + 230 + 30 ) {
                    gameConfig.setHandicap( h );
                  }
                }
              }
              break;
            case 3:

              break;

            default:
              break;
          }

          update();
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
      listener.onCellClick( pos );
    }
  }

  public void setEval( BoardEval eval ) {
    this.eval = eval;
    update();
  }

  private void drawtabString( Graphics g, String text, int x, int y ) {
    for( String line : text.split( "\t" ) )
      g.drawString( line, x += g.getFontMetrics().getHeight(), y );
  }

  @Override
  public Graphics getGraphics() {
    return currentGraphics;
  }

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
    void onCellClick( int pos );
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

      Graphics b = offscreen.getGraphics();
      Graphics2D g2d = (Graphics2D)b;
      currentGraphics = b;

      Image background, player01Disk = null, player02Disk = null, statusMessage = null, switchButtonOff = null, activePassButton = null;
      Image btnRules, btnNewGame, btnStart = null, btnRadioOff = null, btnRadioOn = null, btnBack = null, btnNext = null;

      Map<String, Image> btnSetBoardType = new HashMap<String, Image>();
      Map<String, Image> btnSetGameType = new HashMap<String, Image>();
      Font font = null;

      try {
        background = ImageIO.read( new File( "res/goBackground3.png" ) );
        b.drawImage( background, 0, 0, null );

        player01Disk = ImageIO.read( new File( "res/stones/w0.png" ) );
        player02Disk = ImageIO.read( new File( "res/stones/b.png" ) );

        statusMessage = ImageIO.read( new File( "res/StatusMessage.png" ) );

        switchButtonOff = ImageIO.read( new File( "res/SwitchOff.png" ) );
        activePassButton = ImageIO.read( new File( "res/SwitchOn.png" ) );

        btnRules = ImageIO.read( new File( "res/btnRulesOfGo.png" ) );
        btnNewGame = ImageIO.read( new File( "res/btnNewGame.png" ) );
        btnStart = ImageIO.read( new File( "res/btnStart.png" ) );
        btnBack = ImageIO.read( new File( "res/buttons/btnBack.png" ) );
        btnNext = ImageIO.read( new File( "res/buttons/btnNext.png" ) );
        
        btnRadioOff = ImageIO.read( new File( "res/btnRadioOff.png" ) );
        btnRadioOn = ImageIO.read( new File( "res/btnRadioOn.png" ) );

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

        try {
          font = Font.createFont( Font.TRUETYPE_FONT, new FileInputStream( "res/Kingthings_Petrock.ttf" ) );
        }
        catch( FontFormatException e ) {
          e.printStackTrace();
        }
      }
      catch( IOException e ) {
        e.printStackTrace();
      }

      Color brownColor = new Color( 67, 20, 16 );

      if( gameInProgress ) {
        // draw board
        if( board != null ) {

          font = font.deriveFont( 48F );
          g2d.setFont( font );
          FontMetrics metrics = g2d.getFontMetrics( font );
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
          b.drawImage( statusMessage, 940, 776, null );
          g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1.0f ) );
          //b.drawLine( 900, 0, 900, 900 );
          //b.drawLine( 900, 124, 1600, 124 );

          if( board.getLastMove() != Board.RESIGN_MOVE ) {
            if( board.getLastMove() != Board.PASS_MOVE ) {
              if( PlayerGraphic.HUMAN == whitePlayer ) {
                b.drawImage( switchButtonOff, 1330, 520, null );
                g2d.drawString( "Pass", 1380, 560 );
                b.drawImage( switchButtonOff, 1330, 620, null );
                g2d.drawString( "Resign", 1380, 660 );
              }
              if( PlayerGraphic.HUMAN == blackPlayer ) {
                updateButtons( b, g2d, switchButtonOff );
              }
            } else {
              if( board.getPassNum() == 2 ) {
                if( PlayerGraphic.HUMAN == whitePlayer ) {
                  b.drawImage( activePassButton, 1330, 520, null );
                  g2d.drawString( "Passed", 1380, 560 );
                  b.drawImage( switchButtonOff, 1330, 620, null );
                  g2d.drawString( "Resign", 1380, 660 );
                }
                if( PlayerGraphic.HUMAN == blackPlayer ) {
                  b.drawImage( activePassButton, 1330, 220, null );
                  g2d.drawString( "Passed", 1380, 260 );
                  b.drawImage( switchButtonOff, 1330, 320, null );
                  g2d.drawString( "Resign", 1380, 360 );
                }
              } else {
                if( board.getNextPlayer() == Board.BLACK ) {
                  if( PlayerGraphic.HUMAN == whitePlayer ) {
                    b.drawImage( activePassButton, 1330, 520, null );
                    g2d.drawString( "Passed", 1380, 560 );
                    b.drawImage( switchButtonOff, 1330, 620, null );
                    g2d.drawString( "Resign", 1380, 660 );
                  }
                  if( PlayerGraphic.HUMAN == blackPlayer ) {
                    b.drawImage( switchButtonOff, 1330, 220, null );
                    g2d.drawString( "Pass", 1380, 260 );
                    b.drawImage( switchButtonOff, 1330, 320, null );
                    g2d.drawString( "Resign", 1380, 360 );
                  }
                } else {
                  if( PlayerGraphic.HUMAN == whitePlayer ) {
                    b.drawImage( switchButtonOff, 1330, 520, null );
                    g2d.drawString( "Passed", 1380, 560 );
                    b.drawImage( switchButtonOff, 1330, 620, null );
                    g2d.drawString( "Resign", 1380, 660 );
                  }
                  if( PlayerGraphic.HUMAN == blackPlayer ) {
                    b.drawImage( activePassButton, 1330, 220, null );
                    g2d.drawString( "Passed", 1380, 260 );
                    b.drawImage( switchButtonOff, 1330, 320, null );
                    g2d.drawString( "Resign", 1380, 360 );
                  }
                }
              }
            }
          } else {
            if( board.getNextPlayer() == Board.BLACK ) {
              if( PlayerGraphic.HUMAN == whitePlayer ) {
                b.drawImage( switchButtonOff, 1330, 520, null );
                g2d.drawString( "Pass", 1380, 560 );
                b.drawImage( switchButtonOff, 1330, 620, null );
                g2d.drawString( "Resign", 1380, 660 );
              }
              if( PlayerGraphic.HUMAN == blackPlayer ) {
                b.drawImage( switchButtonOff, 1330, 220, null );
                g2d.drawString( "Pass", 1380, 260 );
                b.drawImage( activePassButton, 1330, 320, null );
                g2d.drawString( "Resigned", 1380, 360 );
              }
            } else {
              if( PlayerGraphic.HUMAN == whitePlayer ) {
                b.drawImage( switchButtonOff, 1330, 520, null );
                g2d.drawString( "Pass", 1380, 560 );
                b.drawImage( activePassButton, 1330, 620, null );
                g2d.drawString( "Resigned", 1380, 660 );
              }
              if( PlayerGraphic.HUMAN == blackPlayer ) {
                b.drawImage( switchButtonOff, 1330, 220, null );
                g2d.drawString( "Pass", 1380, 260 );
                b.drawImage( switchButtonOff, 1330, 320, null );
                g2d.drawString( "Resigned", 1380, 360 );
              }
            }
          }

          g2d.setColor( new Color( 255, 255, 255 ) );
          if( !board.isGameOver() ) {
            if( board.getNextPlayer() == Board.BLACK ) {
              b.drawImage( blackPlayer.activeImg, 1100, 200, null );
              b.drawImage( whitePlayer.passiveImg, 1100, 500, null );
              g2d.drawString( ( PlayerGraphic.COMPUTER == blackPlayer ) ? "[GO]> BLACK is thinking..." : "[GO]> waiting for the BLACK player...", 960, 810 );
            }
            if( board.getNextPlayer() == Board.WHITE ) {
              b.drawImage( blackPlayer.passiveImg, 1100, 200, null );
              b.drawImage( whitePlayer.activeImg, 1100, 500, null );
              g2d.drawString( ( PlayerGraphic.COMPUTER == whitePlayer ) ? "[GO]> WHITE is thinking..." : "[GO]> waiting for the WHITE player...", 960, 810 );
            }
          } else {
            b.drawImage( blackPlayer.passiveImg, 1100, 200, null );
            b.drawImage( whitePlayer.passiveImg, 1100, 500, null );
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
          b.drawImage( player02Disk.getScaledInstance( 2 * r, 2 * r, Image.SCALE_AREA_AVERAGING ), 1020 - r, 295 - r, 2 * r, 2 * r, this );
          b.drawImage( player01Disk.getScaledInstance( 2 * r, 2 * r, Image.SCALE_AREA_AVERAGING ), 1020 - r, 590 - r, 2 * r, 2 * r, this );

          Random randomGenerator = new Random();
          // draw grid
          b.setColor( Color.black );
          for( int i = 0; i < n; ++i ) {
            b.drawLine( d2 + i * cell_size, d2, d2 + i * cell_size, d2 + ( n - 1 ) * cell_size );
            b.drawLine( d2, d2 + i * cell_size, d2 + ( n - 1 ) * cell_size, d2 + i * cell_size );
          }
          Set<Integer> bigPoints = new Handicap( board.boardType ).getHandicapStones( board.boardType.startPoints );
          // draw stones
          b.setFont( Font.decode( "Arial bold 12" ) );
          BufferedImage currentDisk = null;
          for( int x = 0; x < n; ++x ) {
            for( int y = 0; y < n; ++y ) {
              int x1 = d2 + x * cell_size;
              int y1 = d2 + y * cell_size;

              int pos = board.getPos( x, y );

              if( bigPoints.contains( pos ) ) {
                b.setColor( Color.BLACK );
                b.fillOval( x1 - 3, y1 - 3, 6, 6 );
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
                  b.drawImage( currentDisk.getScaledInstance( 2 * r, 2 * r, Image.SCALE_AREA_AVERAGING ), x1 - r, y1 - r, 2 * r, 2 * r, this );
                }
              }

              // eval
              if( eval != null ) {
                double t = eval.getArea( pos );
                int size = 0;
                if( t < 0.5 ) {
                  size = (int) ( r * ( 0.5 - t ) );
                  b.setColor( Color.WHITE );
                } else {
                  size = (int) ( r * ( t - 0.5 ) );
                  b.setColor( Color.BLACK );
                }
                b.fillRect( x1 - size, y1 - size, 2 * size, 2 * size );
              }
              // last move
              if( pos == board.getLastMove() ) {
                b.setColor( Color.RED );
                b.fillOval( x1 - r / 3, y1 - r / 3, 2 * r / 3, 2 * r / 3 );
              }
              if( x == 0 ) {
                g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                g2d.setColor( brownColor );
                font = font.deriveFont( board.boardType.posLabelSize );
                g2d.setFont( font );
                metrics = g2d.getFontMetrics( font );
                g2d.drawString( "" + letters[y], d2 + y * cell_size - metrics.stringWidth( "" + letters[y] ) / 2, d2 - cell_size / 2 );
              }
              if( y == 0 ) {
                g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                g2d.setColor( brownColor );
                font = font.deriveFont( board.boardType.posLabelSize );
                g2d.setFont( font );
                metrics = g2d.getFontMetrics( font );
                g2d.drawString( n - x + "", d2 - cell_size / 10 * 8 - ( metrics.stringWidth( n - x + "" ) / 2 ), d2 + x * cell_size + board.boardType.posLabelSize / 10 * 4 );
              }

              // pos
              //if (board.getLifeOfString(pos) != 0) {
              //String posStr = board.getLifeOfString( pos ) + "";
              String posStr = pos + "";
              b.setFont( Font.decode( "Arial bold 12" ) );
              Rectangle2D rect = b.getFontMetrics().getStringBounds( posStr, b );
              b.setColor( Color.BLUE );
              b.drawString( posStr, 10 + (int) ( x1 - rect.getWidth() / 2 ), 10 + (int) ( y1 + rect.getHeight() / 2 ) );
              //}
            }
          }
        }
      } else {
        g2d.setColor( brownColor );
        font = font.deriveFont( 48F );
        g2d.setFont( font );
        if(settingsWindow < 3){
          b.drawImage( btnNext, 1400, 830, null );
        } else {
          b.drawImage( btnStart, 1400, 830, null );
        }
        if(0 < settingsWindow){
          b.drawImage( btnBack, 50, 830, null );
        }
        g2d.drawString( "Settings: ", 50, 50 );
        font = font.deriveFont( 40F );
        g2d.setFont( font );
        int padding = 0;
        int i = 0;
        
        switch( settingsWindow ) {
          case 0:
            //g2d.drawString( "1.) Board size: ", 50, 100 );
            drawtabString( g2d, "1.) Board size: ", 10, 100 );
            i = 0;
            font = font.deriveFont( 32F );
            g2d.setFont( font );
            padding = 500;
            for( BoardType bt : BoardType.values() ) {
              b.drawImage( ( bt == gameConfig.getBoardType() ) ? btnSetBoardType.get( "btnBoard" + bt.name() + "on" ) : btnSetBoardType.get( "btnBoard" + bt.name() + "off" ), 130 + padding * i, 220, null );
              //g2d.drawString( "" + bt.name().toLowerCase() + " (" + bt.label + ")", 220 + padding * i, 200 );
              i++;
            }
            break;
          case 1:
            drawtabString( g2d, "1.) Board size:\t\t\t\t\t\t" + gameConfig.getBoardType().name() + " (" + gameConfig.getBoardType().label + ")", 10, 100 );
            drawtabString( g2d, "2.) Game type:", 10, 150 );
            //g2d.drawString( "1.) Board size: " + gameConfig.getBoardType().name() + " (" + gameConfig.getBoardType().label + ")", 50, 100 );
            //g2d.drawString( "2.) Game type: ", 50, 150 );
            i = 0;
            padding = 380;
            font = font.deriveFont( 32F );
            g2d.setFont( font );
            for( GameType gt : GameType.values() ) {
              //System.out.println(btnSetBoardType.get( "btnGameType" + gt.name() + "off"));
              b.drawImage( ( gt == gameConfig.getGameType() ) ? btnSetGameType.get( "btnGameType" + gt.name() + "on" ) : btnSetGameType.get( "btnGameType" + gt.name() + "off" ), 50 + padding * i, 220, null );
              //g2d.drawString( gt.label, 80 + padding * i, 200 );
              i++;
            }
            break;
          case 2:
            drawtabString( g2d, "1.) Board size:\t\t\t\t\t\t" + gameConfig.getBoardType().name() + " (" + gameConfig.getBoardType().label + ")", 10, 100 );
            drawtabString( g2d, "2.) Game type:\t\t\t\t\t\t" + gameConfig.getGameType().label, 10, 150 );
            drawtabString( g2d, "3.) Set handicap: ", 10, 200 );
            font = font.deriveFont( 32F );
            g2d.setFont( font );
            padding=60;
            for(int h=0; h<=Handicap.MAX; h++){
              b.drawImage( ( h == gameConfig.getHandicap()) ? btnRadioOn : btnRadioOff, (h%2 == 0)?90:600, (int)h/2* padding + 230, null );
              g2d.drawString( h + " stone" + ((h==1)?"":"s"), (h%2 == 0)?120:630, (int)h/2* padding + 250);
            }
            break;
          case 3:
            drawtabString( g2d, "1.) Board size:\t\t\t\t\t\t" + gameConfig.getBoardType().name() + " (" + gameConfig.getBoardType().label + ")", 10, 100 );
            drawtabString( g2d, "2.) Game type:\t\t\t\t\t\t" + gameConfig.getGameType().label, 10, 150 );
            drawtabString( g2d, "3.) Set handicap:\t\t\t\t\t\t"+ gameConfig.getHandicap(), 10, 200 );
            drawtabString( g2d, "3.) Computer: ", 10, 250 );
            drawtabString( g2d, "a.) Algorithm: ", 10, 410 );
            drawtabString( g2d, "b.) Strength: ", 10, 640 );
            g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, .2f ) );
            g2d.setColor( Color.BLACK );
            int r = 50;
            g2d.fillOval( 120 - r + 4, 320 - r + 4, 2 * r, 2 * r );
            g2d.fillOval( 720 - r + 4, 320 - r + 4, 2 * r, 2 * r );
            g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1.0f ) );
            b.drawImage( player02Disk.getScaledInstance( 2 * r, 2 * r, Image.SCALE_AREA_AVERAGING ), 120 - r, 320 - r, 2 * r, 2 * r, this );
            b.drawImage( player01Disk.getScaledInstance( 2 * r, 2 * r, Image.SCALE_AREA_AVERAGING ), 720 - r, 320 - r, 2 * r, 2 * r, this );
            Player[] players = gameConfig.getPlayers();
            g2d.drawString( players[Board.BLACK].type.toString(), 190, 330);
            g2d.drawString( players[Board.WHITE].type.toString(), 790, 330);
            i = 0;
            padding = 45;
            String[] strength_label = {"Easy", "Medium", "Hard"};
            for( Algorithm alg : Algorithm.values() ) {
              b.drawImage( ( alg == players[Board.BLACK].algo) ? btnRadioOn : btnRadioOff, 100, i * padding + 435, null );
              g2d.drawString( alg.label, 130,  i * padding + 460);
              i++;
            }
            i = 0;
            for( int s = 0; s < strength_label.length; s++ ) {
              b.drawImage( ( s == players[Board.BLACK].param) ? btnRadioOn : btnRadioOff, 100, i * padding + 660, null );
              g2d.drawString(strength_label[s], 130,  i * padding + 685);
              i++;
            }
            break;
          default:
            break;
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
