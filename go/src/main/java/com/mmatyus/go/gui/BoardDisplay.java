package com.mmatyus.go.gui;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.mmatyus.go.algorithms.NegaMaxRobot;
import com.mmatyus.go.algorithms.uct.UCT_Robot;
import com.mmatyus.go.model.Algorithm;
import com.mmatyus.go.model.Board;
import com.mmatyus.go.model.BoardEvaluator;
import com.mmatyus.go.model.GameConfig;
import com.mmatyus.go.model.Handicap;
import com.mmatyus.go.model.Player;
import com.mmatyus.go.model.PlayerPolicy;
import com.mmatyus.go.model.PlayerType;
import com.mmatyus.go.model.Robot;

@SuppressWarnings( "serial" )
public class BoardDisplay extends AbstractDisplay {
  public static final int     CHANGE_POS                     = 80;
  private static final String TITLE                          = "g(\u03C9) – GOmega / On Board";
  private static final int    AMOUNT_OF_DIFERENT_WHITE_STONE = 16;

  public char[]               letters;
  public int[]                currentWhiteType;                                                                 // There are 16 different white stone graphics
  public Image[]              whiteStones                    = new Image[AMOUNT_OF_DIFERENT_WHITE_STONE];
  public Image                blackStone;
  BoardEvaluator              evaluator                      = null;
  DispCanvas                  canvas;
  Dimension                   dim;
  int                         cell_size;

  // Graphics                currentGraphics;

  Board                       board;
  GameConfig                  gameConfig;
  boolean                     gameInProgress;
  public int                  settingsWindow                 = 0;
  ExecutorService             executor                       = Executors.newSingleThreadExecutor();
  private static final Logger LOGGER                         = Logger.getLogger( BoardDisplay.class.getName() );

  public BoardDisplay( final Object waiter, final Board board, final GameConfig gameConfig ) throws IOException, FontFormatException {
    super( waiter, TITLE );
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
    for( int i = 0; i < AMOUNT_OF_DIFERENT_WHITE_STONE; i++ ) {
      whiteStones[i] = ImageIO.read( getClass().getResourceAsStream( "/stones/w" + i + ".png" ) );
    }
    blackStone = ImageIO.read( getClass().getResourceAsStream( "/stones/b.png" ) );
    canvas = new DispCanvas();

    canvas.addMouseListener( new MouseAdapter() {
      @Override
      public void mousePressed( MouseEvent e ) {
        if( !board.isGameOver() && nextIsHuman() ) {
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
            int padding = 300;
            for( int color = 0; color < 2; color++ ) {
              if( board.getNextPlayer() == color ) {
                if( 1330 <= e.getPoint().x && e.getPoint().x <= 1380 && 520 - color * padding <= e.getPoint().y && e.getPoint().y <= 620 - color * padding ) {
                  placeStone( Board.PASS_MOVE );
                } else if( 1330 <= e.getPoint().x && e.getPoint().x <= 1380 && 620 - color * padding <= e.getPoint().y && e.getPoint().y <= 720 - color * padding ) {
                  placeStone( Board.RESIGN_MOVE );
                }
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

    if( !nextIsHuman() ) {
      robotMove();
      update();
    }
  }

  void click( int x, int y ) {
    int pos = board.getPos( x, y );
    placeStone( pos );
  }

  private boolean nextIsHuman() {
    final int playerIdx = board.getNextPlayer();
    final Player player = gameConfig.getPlayers()[playerIdx];
    return ( PlayerType.HUMAN == player.playerType );
  }

  private void robotMove() {
    final Robot r;
    final Player p = gameConfig.getPlayers()[board.getNextPlayer()];
    if( p.algo == Algorithm.UCT ) {
      r = new UCT_Robot( new PlayerPolicy( Algorithm.UCT.option( p.param ) ) );
    } else {
      r = new NegaMaxRobot( p.algo, p.param );
    }
    r.setBoard( board );
    //final int robotIdea = r.move( board );
    int robotIdea = Board.PASS_MOVE;

    Future<Integer> future = executor.submit( r );
    try {
      robotIdea = future.get();
    }
    catch( InterruptedException | ExecutionException e ) {
      JOptionPane.showMessageDialog( null, e, TITLE, JOptionPane.ERROR_MESSAGE );
    }

    board.move( robotIdea );
  }

  void finishGame() {
    evaluator = new BoardEvaluator( board, new PlayerPolicy( 300_000 ) );
    final int EVAL_STEPS = 10000;
    final double EVAL_THRESHOLD = 0.4;
    evaluator.eval( EVAL_STEPS, EVAL_THRESHOLD );
    update();
  }

  /**
   * Next player moves.
   * 
   * @param pos
   *          Linear position on board.
   */
  public boolean placeStone( int pos ) {
    if( !board.isLegalMove( pos ) )
      return false;
    board.move( pos );
    update();
    if( board.isGameOver() && board.getLastMove() != Board.RESIGN_MOVE )
      finishGame();
    if( !nextIsHuman() ) {
      robotMove();
      update();
      if( board.isGameOver() && board.getLastMove() != Board.RESIGN_MOVE )
        finishGame();
    }
    return true;
  }

  public void setEval( BoardEvaluator eval ) {
    this.evaluator = eval;
    update();
  }

  public void update() {
    canvas.re_display( canvas.getGraphics() );
  }

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

  class DispCanvas extends Canvas {

    PlayerGraphic[] players = new PlayerGraphic[2];

    public DispCanvas() throws IOException, FontFormatException {}

    final Image  background      = ImageIO.read( getClass().getResourceAsStream( "/goBackground3.png" ) );
    final Image  player01Disk    = whiteStones[0];
    final Image  player02Disk    = blackStone;
    final Image  statusMessage   = ImageIO.read( getClass().getResourceAsStream( "/StatusMessage.png" ) );

    final Image  btnRules        = ImageIO.read( getClass().getResourceAsStream( "/buttons/btnRulesOfGo.png" ) );
    final Image  btnNewGame      = ImageIO.read( getClass().getResourceAsStream( "/buttons/btnNewGame.png" ) );

    final Font   font0           = Font.createFont( Font.TRUETYPE_FONT, getClass().getResourceAsStream( "/Kingthings_Petrock.ttf" ) );
    final Color  brownColor      = new Color( 67, 20, 16 );
    final Color  whiteColor      = Color.WHITE;
    final Color  blackColor      = Color.BLACK;
    final Random randomGenerator = new Random();

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

      g0.drawImage( background, 0, 0, null );
      Font font1;

      // draw board
      if( board != null ) {
        font1 = font0.deriveFont( 48F );
        g2d.setFont( font1 );
        FontMetrics metrics = g2d.getFontMetrics( font1 );

        g2d.setFont( Font.decode( "Arial bold 24" ) );
        g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, .6f ) );
        g0.drawImage( statusMessage, 940, 776, null );
        g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1.0f ) );
        g2d.setColor( whiteColor );

        g2d.setColor( brownColor );
        for( int color = 0; color < 2; color++ ) {
          players[color] = ( PlayerType.COMPUTER == gameConfig.getPlayers()[color].playerType ) ? PlayerGraphic.COMPUTER : PlayerGraphic.HUMAN;
          players[color].setPass( "off", g0, g2d, color );
          players[color].setResign( "off", g0, g2d, color );
          g0.drawImage( players[color].passiveImg, 1100, 500 - color * 300, null );
          g2d.drawString( players[color].name, 1200 - metrics.stringWidth( players[color].name ) / 2, 480 - color * 300 );
        }
        String nextPlayerColor = ( board.getNextPlayer() == Board.BLACK ) ? "BLACK" : "WHITE";
        int nextPlayer = board.getNextPlayer();

        if( board.getLastMove() == Board.PASS_MOVE ) {
          players[1 - nextPlayer].setPass( "on", g0, g2d, 1 - nextPlayer );
        }
        if( board.getLastMove() == Board.RESIGN_MOVE ) {
          players[nextPlayer].setResign( "on", g0, g2d, nextPlayer );
        }
        g0.drawImage( players[nextPlayer].activeImg, 1100, ( ( nextPlayer == Board.BLACK ) ? 200 : 500 ), null );
        g0.drawImage( players[1 - board.getNextPlayer()].passiveImg, 1100, ( 1 - board.getNextPlayer() == Board.BLACK ) ? 200 : 500, null );

        g2d.setColor( whiteColor );
        System.out.println( "NEXT: " + ( ( board.getNextPlayer() == Board.BLACK ) ? "BLACK" : "WHITE" ) );
        System.out.println( "NEXT: " + nextPlayer );

        if( board.isGameOver() ) {
          g0.drawImage( players[Board.BLACK].passiveImg, 1100, 200, null );
          g0.drawImage( players[Board.WHITE].passiveImg, 1100, 500, null );
          if( board.getLastMove() == Board.PASS_MOVE ) {
            //g2d.drawString( "[GO]> The winner is the " + ( eval.getScore() > 0 ? "BLACK" : "WHITE" ) + "!", 960, 810 );
          } else if( board.getLastMove() == Board.RESIGN_MOVE ) {
            g2d.drawString( "[GO]> GAME OVER! The " + ( ( board.getNextPlayer() == Board.BLACK ) ? "BLACK resigned." : "WHITE resigned." ), 960, 810 );
          }
        } else {
          g2d.drawString( ( PlayerGraphic.COMPUTER == players[nextPlayer] ) ? "[GO]> " + nextPlayerColor + " is thinking..." : "[GO]> waiting for the " + nextPlayerColor + " player...", 960, 810 );
        }

        int n = board.sideLength;
        cell_size = ( dim.width - CHANGE_POS ) / n;
        if( dim.height / n < cell_size )
          cell_size = ( dim.height - CHANGE_POS ) / n;
        int d2 = cell_size / 2 + CHANGE_POS;
        int r = cell_size / 2 - 3;

        g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, .2f ) );
        g2d.setColor( blackColor );
        g2d.fillOval( 1020 - r + 4, 295 - r + 4, 2 * r, 2 * r );
        g2d.fillOval( 1020 - r + 4, 590 - r + 4, 2 * r, 2 * r );
        g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1.0f ) );
        g0.drawImage( player02Disk.getScaledInstance( 2 * r, 2 * r, Image.SCALE_AREA_AVERAGING ), 1020 - r, 295 - r, 2 * r, 2 * r, this );
        g0.drawImage( player01Disk.getScaledInstance( 2 * r, 2 * r, Image.SCALE_AREA_AVERAGING ), 1020 - r, 590 - r, 2 * r, 2 * r, this );

        // draw grid
        for( int i = 0; i < n; ++i ) {
          g0.drawLine( d2 + i * cell_size, d2, d2 + i * cell_size, d2 + ( n - 1 ) * cell_size );
          g0.drawLine( d2, d2 + i * cell_size, d2 + ( n - 1 ) * cell_size, d2 + i * cell_size );
        }
        Set<Integer> bigPoints = new Handicap( board.boardType ).getHandicapStones( board.boardType.startPoints );
        // draw stones
        g0.setFont( Font.decode( "Arial bold 12" ) );
        Image currentDisk = null;
        for( int x = 0; x < n; ++x ) {
          for( int y = 0; y < n; ++y ) {
            int x1 = d2 + x * cell_size;
            int y1 = d2 + y * cell_size;

            int pos = board.getPos( x, y );

            if( bigPoints.contains( pos ) ) {
              g0.setColor( blackColor );
              g0.fillOval( x1 - 3, y1 - 3, 6, 6 );
            }

            // state
            int state = board.getState( pos );
            if( state != Board.EMPTY ) {
              g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, .2f ) );
              g2d.setColor( blackColor );
              g2d.fillOval( x1 - r + 4, y1 - r + 4, 2 * r, 2 * r );
              g2d.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1.0f ) );
              try {
                if( state == Board.WHITE ) {
                  if( currentWhiteType[pos] == 0 ) {
                    currentWhiteType[pos] = randomGenerator.nextInt( 16 );
                  }
                  currentDisk = whiteStones[currentWhiteType[pos]];
                } else {
                  currentDisk = blackStone;
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
            if( evaluator != null ) {
              double t = evaluator.getArea( pos );
              int size = 0;
              if( t < 0.5 ) {
                size = (int) ( r * ( 0.5 - t ) );
                g0.setColor( whiteColor );
              } else {
                size = (int) ( r * ( t - 0.5 ) );
                g0.setColor( blackColor );
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
            //String posStr = pos + "";
            //g0.setFont( Font.decode( "Arial bold 12" ) );
            //Rectangle2D rect = g0.getFontMetrics().getStringBounds( posStr, g0 );
            //g0.setColor( Color.BLUE );
            //g0.drawString( posStr, 10 + (int) ( x1 - rect.getWidth() / 2 ), 10 + (int) ( y1 + rect.getHeight() / 2 ) );
            //}
          }
        }
      }
      g.drawImage( offscreen, 0, 0, this );
    }
  }

  @Override
  protected void closing() {
    LOGGER.info( "Closing!!!" );
    executor.shutdownNow();
  }

  @Override
  protected void closed() {
    notifyWaiter();
  }

  @Override
  public Menu getNextScreen() {
    return Menu.START;
  }

}
