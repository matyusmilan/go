package com.mmatyus.go.gui;

import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import com.mmatyus.go.ProgressChagedEvent;
import com.mmatyus.go.ProgressChangedLisener;
import com.mmatyus.go.gui.canvas.BoardCanvas;
import com.mmatyus.go.model.Board;
import com.mmatyus.go.model.GameConfig;

@SuppressWarnings( "serial" )
public class BoardDisplay extends AbstractDisplay {

  private static final String TITLE          = "g(\u03C9) – GOmega / On Board";

  public BoardCanvas          canvas;
  Dimension                   dim;

  // Graphics                currentGraphics;

  boolean                     gameInProgress;
  public int                  settingsWindow = 0;

  public BoardDisplay( final Object waiter, final Board board, final GameConfig gameConfig ) throws IOException, FontFormatException {
    super( waiter, TITLE );
    gameInProgress = false;
    canvas = new BoardCanvas( board, gameConfig );
    canvas.progressContainer.addProgressChangedLisener( new ProgressChangedLisener() {
      @Override
      public void changed( ProgressChagedEvent e ) {
        update();
      }
    } );

    setupCanvas();
    add( canvas );

    setVisible( true );

  }

  void setupCanvas() {
    canvas.addMouseListener( new MouseAdapter() {
      @Override
      public void mousePressed( MouseEvent e ) {
        if( !canvas.board.isGameOver() && canvas.nextIsHuman() ) {
          if( e.getPoint().y < HEIGHT && e.getPoint().x < HEIGHT ) {
            if( 0 < ( e.getPoint().x - BoardCanvas.CHANGE_POS ) * ( e.getPoint().y - BoardCanvas.CHANGE_POS ) ) {
              int x = ( e.getPoint().x - BoardCanvas.CHANGE_POS ) / canvas.cell_size;
              int y = ( e.getPoint().y - BoardCanvas.CHANGE_POS ) / canvas.cell_size;
              int side = canvas.board.sideLength;
              if( 0 <= x && x < side && 0 <= y && y < side )
                canvas.click( x, y );
            }
          } else {
            int padding = 300;
            for( int color = 0; color < 2; color++ ) {
              if( canvas.board.getNextPlayer() == color ) {
                if( 1330 <= e.getPoint().x && e.getPoint().x <= 1380 && 520 - color * padding <= e.getPoint().y && e.getPoint().y <= 620 - color * padding ) {
                  canvas.placeStone( Board.PASS_MOVE );
                } else if( 1330 <= e.getPoint().x && e.getPoint().x <= 1380 && 620 - color * padding <= e.getPoint().y && e.getPoint().y <= 720 - color * padding ) {
                  canvas.placeStone( Board.RESIGN_MOVE );
                }
              }
            }
            update();
          }
        }
      }

    } );
  }

  public void update() {
    canvas.re_display( canvas.getGraphics() );
  }

  @Override
  protected void closing() {
    canvas.executor.shutdownNow();
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
