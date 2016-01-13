package hu.mmatyus.gui;

import hu.mmatyus.model.Board;
import hu.mmatyus.model.Handicap;

import javax.imageio.ImageIO;
import java.awt.*;
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

@SuppressWarnings( "serial" ) public class BoardDisplay extends Frame {
  public static final int      CHANGE_POS   = 80;
  public static final String[] LETTERS      = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T" };
  public              int[]    currentWhite = new int[361];
  DispCanvas canvas;
  Dimension  dim;
  int        cell_size;
  Graphics   currentGraphics;
  Board      board;
  Listener   listener;

  public BoardDisplay( final Board board, String title ) {
    super();
    for( int i = 0; i < currentWhite.length; ++i ) {
      currentWhite[i] = 0;
    }
    setSize( 1400, 1000 );
    setTitle( title );
    setResizable( false );
    setMenuBar( addMenu() );

    addWindowListener( new WindowAdapter() {
      @Override public void windowClosing( WindowEvent we ) {
        System.exit( 0 );
      }
    } );

    canvas = new DispCanvas();
    canvas.addMouseListener( new MouseAdapter() {
      @Override public void mousePressed( MouseEvent e ) {
        if( 0 < ( e.getPoint().x - CHANGE_POS ) * ( e.getPoint().y - CHANGE_POS ) ) {
          int x = ( e.getPoint().x - CHANGE_POS ) / cell_size;
          int y = ( e.getPoint().y - CHANGE_POS ) / cell_size;
          int n = board.getSize();
          System.out.println( x + " " + y );
          if( 0 <= x && x < n && 0 <= y && y < n )
            click( x, y );
        }
      }
    } );
    add( canvas );

    setTitle( title );

    this.board = board;
    setVisible( true );
  }

  public MenuBar addMenu() {
    MenuBar menuBar = new MenuBar();

    Menu fileMenu = new Menu( "File" );
    MenuItem newMenuItem = new MenuItem( "New Game" );
    MenuItem exitMenuItem = new MenuItem( "Exit" );
    fileMenu.add( newMenuItem );
    fileMenu.add( exitMenuItem );

    Menu modeMenu = new Menu( "Mode" );
    MenuItem pvpMenuItem = new MenuItem( "Player VS Player" );
    MenuItem cvpMenuItem = new MenuItem( "Player VS Computer" );
    modeMenu.add( pvpMenuItem );
    modeMenu.add( cvpMenuItem );

    Menu htpMenu = new Menu( "How to play" );
    MenuItem rulesMenuItem = new MenuItem( "Rules" );
    MenuItem tutorialMenuItem = new MenuItem( "Tutorial" );
    MenuItem puzzleMenuItem = new MenuItem( "Puzzle" );
    htpMenu.add( rulesMenuItem );
    htpMenu.add( tutorialMenuItem );
    htpMenu.add( puzzleMenuItem );

    menuBar.add( fileMenu );
    menuBar.add( modeMenu );
    menuBar.add( htpMenu );

    return menuBar;
  }

  void click( int x, int y ) {
    int pos = board.getPos( x, y );
    if( listener != null ) {
      listener.onCellClick( pos );
    }
  }

  @Override public Graphics getGraphics() {
    return currentGraphics;
  }

  public void setBoard( Board board ) {
    this.board = board;
    update();
  }

  public void setListener( Listener l ) {
    listener = l;
  }

  public void update() {
    canvas.re_display( canvas.getGraphics() );
  }

  public interface Listener {
    void onCellClick( int pos );
  }

  class DispCanvas extends Canvas {

    @Override public void paint( Graphics g ) {
      re_display( g );
    }

    @Override public void update( Graphics g ) {
      re_display( g );
    }

    public void re_display( Graphics g ) {
      dim = getSize();
      Image offscreen = createImage( dim.width, dim.height );
      Graphics b = offscreen.getGraphics();
      Graphics2D g2d = (Graphics2D)b;
      currentGraphics = b;

      // paint background
      // b.setColor( Color.orange.darker() );
      // b.fillRect( 0, 0, dim.width, dim.height );

      Image img;
      try {
        img = ImageIO.read( new File( "res/woodBackground.jpg" ) );
        b.drawImage( img, 0, 0, null );
      }
      catch( IOException e ) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      Font font = new Font( "volter", Font.BOLD, 32 );
      Font font2 = new Font( "volter", Font.BOLD, 32 );
      //Color brownColor = new Color(139,69,19);
      Color brownColor = new Color( 0, 0, 0 );
      try {
        font = Font.createFont( Font.TRUETYPE_FONT, new FileInputStream( "res/WoodLook.ttf" ) );
      }
      catch( FontFormatException e ) {
        e.printStackTrace();
      }
      catch( IOException e ) {
        e.printStackTrace();
      }
      // draw board
      if( board != null ) {
        int n = board.getSize();
        cell_size = ( dim.width - CHANGE_POS ) / n;
        if( dim.height / n < cell_size )
          cell_size = ( dim.height - CHANGE_POS ) / n;
        int d2 = cell_size / 2 + CHANGE_POS;
        int r = cell_size / 2 - 3;
        Random randomGenerator = new Random();
        // draw grid
        b.setColor( Color.black );
        for( int i = 0; i < n; ++i ) {
          b.drawLine( d2 + i * cell_size, d2, d2 + i * cell_size, d2 + ( n - 1 ) * cell_size );
          b.drawLine( d2, d2 + i * cell_size, d2 + ( n - 1 ) * cell_size, d2 + i * cell_size );
        }
        Set<Integer> bigPoints = new Handicap( n ).getHandicapStones( board.getBoardType().startPoints );
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
                  if( currentWhite[pos] == 0 ) {
                    currentWhite[pos] = randomGenerator.nextInt( 16 );
                  }
                  currentDisk = ImageIO.read( new File( "res/stones/w" + currentWhite[pos] + ".png" ) );
                } else {
                  currentDisk = ImageIO.read( new File( "res/stones/b.png" ) );
                }
              }
              catch( Exception ex ) {
                ex.printStackTrace();
              }
              b.drawImage( currentDisk.getScaledInstance( 2 * r, 2 * r, Image.SCALE_AREA_AVERAGING ), x1 - r, y1 - r, 2 * r, 2 * r, this );
            }
            // last move
            if( pos == board.getLastMove() ) {
              b.setColor( Color.RED );
              b.fillOval( x1 - r / 3, y1 - r / 3, 2 * r / 3, 2 * r / 3 );
            }
            if( x == 0 ) {
              g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
              g2d.setColor( brownColor );
              font = font.deriveFont( board.getBoardType().posLabelSize );
              g2d.setFont( font );
              FontMetrics metrics = g2d.getFontMetrics( font );
              g2d.drawString( LETTERS[y], d2 + y * cell_size - metrics.stringWidth( LETTERS[y] ) / 2, d2 - cell_size / 2 );
            }
            if( y == 0 ) {
              g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
              g2d.setColor( brownColor );
              font = font.deriveFont( board.getBoardType().posLabelSize );
              g2d.setFont( font );
              FontMetrics metrics = g2d.getFontMetrics( font );
              g2d.drawString( n - x + "", d2 - cell_size / 10 * 8 - ( metrics.stringWidth( n - x + "" ) / 2 ), d2 + x * cell_size + board.getBoardType().posLabelSize / 10 * 4 );
            }

            // pos
            String posStr = pos + "";
            b.setFont( Font.decode( "Arial bold 12" ) );
            Rectangle2D rect = b.getFontMetrics().getStringBounds( posStr, b );
            b.setColor( Color.BLUE );
            b.drawString( posStr, 10 + (int)( x1 - rect.getWidth() / 2 ), 10 + (int)( y1 + rect.getHeight() / 2 ) );
          }
        }

      }

      g.drawImage( offscreen, 0, 0, this );
    }
  }
}
