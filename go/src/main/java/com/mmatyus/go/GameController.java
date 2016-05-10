package com.mmatyus.go;

import java.awt.FontFormatException;
import java.io.IOException;

import com.mmatyus.go.gui.AbstractDisplay;
import com.mmatyus.go.gui.BoardDisplay;
import com.mmatyus.go.gui.Menu;
import com.mmatyus.go.gui.SettingsDisplay;
import com.mmatyus.go.gui.StartDisplay;
import com.mmatyus.go.model.Algorithm;
import com.mmatyus.go.model.Board;
import com.mmatyus.go.model.GameConfig;
import com.mmatyus.go.model.Player;
import com.mmatyus.go.model.PlayerType;

public class GameController {

  private final static GameConfig DEFAULT_GAME_CONFIG = createDefaultConfig();
  private static final double     KOMI                = 6.5;
  private final GameConfig        ACTUAL              = new GameConfig();

  public void startGame() throws IOException, FontFormatException, InterruptedException {
    Menu activeScreen = Menu.START;
    AbstractDisplay screen = null;

    while( true ) {
      try {
        switch( activeScreen ) {
          case START:
            screen = runStartDisplay();
            break;
          case EXIT:
            return;
          case QUICK_GAME:
            screen = runBoardDisplay( DEFAULT_GAME_CONFIG );
            break;
          case GAME:
            screen = runBoardDisplay( ACTUAL );
            break;
          case SETTINGS:
            screen = runSettingsDisplay( ACTUAL );
            break;
          default:
            throw new IllegalStateException( "Unhandled state!" );
        }
        synchronized( this ) {
          this.wait();
        }
        activeScreen = screen.getNextScreen();
      }
      catch( GoException e ) {
        activeScreen = Menu.START;
      }
    }

  }

  private AbstractDisplay runStartDisplay() throws IOException, FontFormatException, InterruptedException {
    final StartDisplay sd = new StartDisplay( this );
    sd.setVisible( true );
    return sd;
  }

  private AbstractDisplay runSettingsDisplay( GameConfig gc ) throws IOException, FontFormatException, InterruptedException {
    final SettingsDisplay sd = new SettingsDisplay( this, gc );
    sd.setVisible( true );
    return sd;
  }

  private AbstractDisplay runBoardDisplay( GameConfig gc ) throws IOException, FontFormatException, InterruptedException {
    final Board board = new Board( gc.getBoardType(), gc.getHandicap(), KOMI );
    final BoardDisplay bd = new BoardDisplay( this, board, gc );
    bd.setVisible( true );
    bd.canvas.firstMoveOfComputer();
    return bd;
  }

  private static GameConfig createDefaultConfig() {
    GameConfig result = new GameConfig();
    result.getPlayers()[1] = new Player( PlayerType.COMPUTER, Algorithm.UCT, 0 );
    return result;
  }
}
