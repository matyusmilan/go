package hu.mmatyus.gui;

import hu.mmatyus.model.Board;
import hu.mmatyus.model.Handicap;

import javax.imageio.ImageIO;
import javax.swing.*;
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

/**
 * Created by milan on 2016.02.22..
 */
public class BoardDisplaySwing extends JFrame {
  public static final int      CHANGE_POS   = 80;
  public static final String[] LETTERS      = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T" };
  public              int[]    currentWhite = new int[361];
  DispCanvas            canvas;
  Dimension             dim;
  int                   cell_size;
  Graphics              currentGraphics;
  Board                 board;
  Listener listener;

  public interface Listener {
    void onCellClick( int pos );
  }

  private static ImageIcon createImageIcon(String path, String description) {
    java.net.URL imgURL = BoardDisplaySwing.class.getResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL, description);
    } else {
      System.err.println("Couldn't find file: " + path);
      return null;
    }
  }

  public BoardDisplaySwing( final Board board, String title ) {
    super();
    for( int i = 0; i < currentWhite.length; ++i ) {
      currentWhite[i] = 0;
    }
    setSize( 1600, 900 );
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
      @Override public void mousePressed( MouseEvent e ) {
        if( 0 < ( e.getPoint().x - CHANGE_POS ) * ( e.getPoint().y - CHANGE_POS ) ) {
          int x = ( e.getPoint().x - CHANGE_POS ) / cell_size;
          int y = ( e.getPoint().y - CHANGE_POS ) / cell_size;
          int side = board.sideLength;
          System.out.println( x + " " + y );
          if( 0 <= x && x < side && 0 <= y && y < side )
            click( x, y );
        }
      }
    } );
    //setLayout(  new BorderLayout());
    setLayout(new GridLayout(1,2));
    //BackgroundPanel bp = new BackgroundPanel();
    //JPanel bp = new JPanel();
    //ImagePanel panel = new ImagePanel(new ImageIcon("res/goBackground3b.png").getImage());
    JPanel panel = new JPanel();

    canvas.setSize( 900, 900 );
    //bp.setSize( 700, 900 );
    panel.setSize( 700, 900 );
    ImageIcon icon = createImageIcon("ib_newGame.png","normal");
    ImageIcon iconHover = createImageIcon("ib_newGame2.png","hover");

    JButton javaButton = new JButton( icon);
    //javaButton.setBorder(BorderFactory.createEmptyBorder());
    javaButton.setBorderPainted(false);
    javaButton.setFocusPainted(false);
    javaButton.setContentAreaFilled(false);
    javaButton.setRolloverIcon(iconHover);
    //Button newGame = new Button("New Game");
    //newGame.setBackground( new Color( 227, 195, 168) );
    //newGame.setSize( 600, 100 );
    //newGame.setLocation( 0,0 );
    //bp.add(newGame);
    //ImageButton ib = new ImageButton(Toolkit.getDefaultToolkit().createImage("res/ib_newGame.png"), "New Game");
    panel.add(javaButton);
    add(canvas);//to see something on top
    getContentPane().add(panel);
    //canvas.setSize( 900, 900 );
    //add( canvas );
    //Label l = new Label( "Panel vagyok!!!" );
    //p.add(l);
    //Image img = Toolkit.getDefaultToolkit().createImage("res/goBackground3.png");
    //.getGraphics().drawImage(img, 0, 0, null);
    this.board = board;
    setVisible( true );
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

  public void setListener( Listener l ) {
    listener = l;
  }

  public void update() {
    canvas.paintComponent( canvas.getGraphics() );
  }


  class ImagePanel extends JPanel {

    private Image img;

    public ImagePanel(String img) {
      this(new ImageIcon(img).getImage());
    }

    public ImagePanel(Image img) {
      this.img = img;
      Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
      setPreferredSize(size);
      setMinimumSize(size);
      setMaximumSize(size);
      setSize(size);
      setLayout(null);
    }

    public void paintComponent(Graphics g) {
      g.drawImage(img, 0, 0, null);
    }

  }

  class DispCanvas extends JPanel {
    private static final long serialVersionUID = 1L;

    public DispCanvas() {
    }
    @Override public void paint( Graphics g ) {
      paintComponent( g );
    }

    @Override public void update( Graphics g ) {
      paintComponent( g );
    }

    public void paintComponent( Graphics g ) {
      dim = getSize();
      Image offscreen = createImage( dim.width, dim.height );

      Graphics b = offscreen.getGraphics();
      Graphics2D g2d = (Graphics2D)b;
      currentGraphics = b;

      Image background;
      Font font = new Font( "volter", Font.BOLD, 32 );

      try {
        background = ImageIO.read( new File( "res/goBackground3a.png" ) );
        b.drawImage( background, 0, 0, null );

        try {
          font = Font.createFont( Font.TRUETYPE_FONT, new FileInputStream( "res/WoodLook.ttf" ) );
        }
        catch( FontFormatException e ) {
          e.printStackTrace();
        }
      }
      catch( IOException e ) {
        e.printStackTrace();
      }

      Color brownColor = new Color( 0, 0, 0 );
      // draw board
      if( board != null ) {
        int n = board.sideLength;
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
              if( currentDisk != null ) {
                b.drawImage( currentDisk.getScaledInstance( 2 * r, 2 * r, Image.SCALE_AREA_AVERAGING ), x1 - r, y1 - r, 2 * r, 2 * r, this );
              }
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
              FontMetrics metrics = g2d.getFontMetrics( font );
              g2d.drawString( LETTERS[y], d2 + y * cell_size - metrics.stringWidth( LETTERS[y] ) / 2, d2 - cell_size / 2 );
            }
            if( y == 0 ) {
              g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
              g2d.setColor( brownColor );
              font = font.deriveFont( board.boardType.posLabelSize );
              g2d.setFont( font );
              FontMetrics metrics = g2d.getFontMetrics( font );
              g2d.drawString( n - x + "", d2 - cell_size / 10 * 8 - ( metrics.stringWidth( n - x + "" ) / 2 ), d2 + x * cell_size + board.boardType.posLabelSize / 10 * 4 );
            }

            // pos
            //if (board.getLifeOfString(pos) != 0) {
            //String posStr = board.getLifeOfString( pos ) + "";
            String posStr = pos + "";
            b.setFont( Font.decode( "Arial bold 12" ) );
            Rectangle2D rect = b.getFontMetrics().getStringBounds( posStr, b );
            b.setColor( Color.BLUE );
            //b.drawString( posStr, 10 + (int)( x1 - rect.getWidth() / 2 ), 10 + (int)( y1 + rect.getHeight() / 2 ) );
            //}
          }
        }

      }

      g.drawImage( offscreen, 0, 0, this );
    }
  }
}
