package com.mmatyus.go.gui;

import java.awt.FontFormatException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import com.mmatyus.go.gui.canvas.SettingsCanvas;
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
  private SettingsCanvas      canvas;
  public Menu                 result           = null;

  public SettingsDisplay( final Object waiter, final GameConfig actual ) throws IOException, FontFormatException {
    super( waiter, TITLE );
    this.canvas = new SettingsCanvas( actual );
    setupCanvas();
    add( canvas );
  }

  void setupCanvas() {
    canvas.addMouseListener( new MouseAdapter() {
      @Override
      public void mouseClicked( MouseEvent e ) {
        if( canvas.wizardPage < 3 ) {
          if( canvas.btnNext.hasPoint( e.getPoint() ) ) {
            if( canvas.wizardPage == 2 && canvas.gameConfig.getGameType() == GameType.HVH ) {
              setVisible( false );
              result = Menu.GAME;
              dispose();
              return;
            }
            if( canvas.gameConfig.getBoardType() != BoardType.SMALL && canvas.wizardPage == 0 ) {
              canvas.wizardPage++;
            }
            canvas.wizardPage++;
          }
        } else {
          if( canvas.btnStart.hasPoint( e.getPoint() ) ) {
            System.out.println( "start" );
            setVisible( false );
            result = Menu.GAME;
            dispose();
            return;
          }
        }
        if( canvas.btnBack.hasPoint( e.getPoint() ) ) {
          if( 0 < canvas.wizardPage ) {
            if( canvas.gameConfig.getBoardType() != BoardType.SMALL && canvas.wizardPage == 2 ) {
              canvas.wizardPage--;
            }
            canvas.wizardPage--;
          } else {
            result = Menu.START;
            dispose();
            return;
          }
        }
        int padding = 50;
        int i = 0;
        Player[] players = new Player[2];
        switch( canvas.wizardPage ) {
          case 0:
            for( BoardType bt : BoardType.values() ) {
              if( bt != canvas.gameConfig.getBoardType() && canvas.btnSetBoardType.get( bt ).hasPoint( e.getPoint() ) )
                canvas.gameConfig.setBoardType( bt );
            }
            if( canvas.gameConfig.getBoardType() != BoardType.SMALL ) {
              canvas.gameConfig.setGameType( GameType.HVH );
              Player defaultHuman = new Player( PlayerType.HUMAN );
              players[Board.BLACK] = defaultHuman;
              players[Board.WHITE] = defaultHuman;
              canvas.gameConfig.setPlayers( players );
            }
            break;
          case 1:
            for( GameType gt : GameType.values() ) {
              if( gt != canvas.gameConfig.getGameType() && canvas.btnSetGameType.get( gt ).hasPoint( e.getPoint() ) )
                canvas.gameConfig.setGameType( gt );
            }
            Player defaultComputer = new Player( PlayerType.COMPUTER, Algorithm.UCT, 1 );
            Player defaultHuman = new Player( PlayerType.HUMAN );
            switch( canvas.gameConfig.getGameType() ) {
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
            canvas.gameConfig.setPlayers( players );
            break;
          case 2:
            padding = 60;
            for( int h = 0; h <= Handicap.MAX; h++ ) {
              if( h != canvas.gameConfig.getHandicap() ) {
                if( ( ( h < 5 ) ? 90 : 600 ) <= e.getPoint().x && e.getPoint().x <= ( ( h < 5 ) ? 90 : 600 ) + 150 && ( h % 5 + 1 ) * padding + 230 <= e.getPoint().y && e.getPoint().y <= ( h % 5 + 1 ) * padding + 230 + 30 ) {
                  canvas.gameConfig.setHandicap( h );
                }
              }
            }

            break;
          case 3:
            i = 0;
            padding = 45;
            String[] strength_label = { "Easy", "Medium", "Hard" };
            players = canvas.gameConfig.getPlayers();
            for( Algorithm alg : Algorithm.values() ) {
              if( players[Board.BLACK].playerType == PlayerType.COMPUTER ) {
                if( alg != players[Board.BLACK].algo ) {
                  if( 100 <= e.getPoint().x && e.getPoint().x <= 100 + 300 && i * padding + 435 <= e.getPoint().y && e.getPoint().y <= i * padding + 435 + 30 ) {
                    players[Board.BLACK] = new Player( PlayerType.COMPUTER, alg, players[Board.BLACK].param );
                    canvas.gameConfig.setPlayers( players );
                  }
                }
              }
              if( players[Board.WHITE].playerType == PlayerType.COMPUTER ) {
                if( alg != players[Board.WHITE].algo ) {
                  if( 700 <= e.getPoint().x && e.getPoint().x <= 700 + 300 && i * padding + 435 <= e.getPoint().y && e.getPoint().y <= i * padding + 435 + 30 ) {
                    players[Board.WHITE] = new Player( PlayerType.COMPUTER, alg, players[Board.WHITE].param );
                    canvas.gameConfig.setPlayers( players );
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
                    canvas.gameConfig.setPlayers( players );
                  }
                }
              }
            }
            for( int s = 0; s < strength_label.length; s++ ) {
              if( players[Board.WHITE].playerType == PlayerType.COMPUTER ) {
                if( s != players[Board.WHITE].param ) {
                  if( 700 <= e.getPoint().x && e.getPoint().x <= 700 + 100 && s * padding + 660 <= e.getPoint().y && e.getPoint().y <= s * padding + 660 + 30 ) {
                    players[Board.WHITE] = new Player( PlayerType.COMPUTER, players[Board.WHITE].algo, s );
                    canvas.gameConfig.setPlayers( players );
                  }
                }
              }
            }
            break;
        }
        canvas.update( canvas.getGraphics() );
        super.mouseClicked( e );
      }
    } );
  }

  @Override
  protected void closing() {
    result = Menu.START;
  }

  @Override
  protected void closed() {
    notifyWaiter();
  }

  @Override
  public Menu getNextScreen() {
    return result;
  }

}
