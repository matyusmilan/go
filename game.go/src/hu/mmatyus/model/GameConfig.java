package hu.mmatyus.model;

import hu.mmatyus.model.Player.Type;

public class GameConfig {


  public static final BoardType DEFAULT_BOARD_TYPE = BoardType.SMALL;
  public static final GameType DEFAULT_GAME_TYPE = GameType.CVH;
  public static final Player DEFAULT_BLACK = new Player(Type.COMPUTER, Algorithm.UCT, 1);
  public static final Player DEFAULT_WHITE = new Player(Type.HUMAN);
  public static final int DEFAULT_HANDICAP = 0;
  
	BoardType boardType = null;
	GameType gameType = null;
	Player[] players = new Player[2];
	int handicap;
	
	public GameConfig() {
	  this.gameType = DEFAULT_GAME_TYPE;
	  this.boardType = DEFAULT_BOARD_TYPE;
	  this.players[Board.BLACK] = DEFAULT_BLACK;
	  this.players[Board.WHITE] = DEFAULT_WHITE;
	  this.handicap = DEFAULT_HANDICAP;
  }
	
  public BoardType getBoardType() {
    return boardType;
  }

  public void setBoardType( BoardType boardType ) {
    this.boardType = boardType;
  }
  
  public GameType getGameType(){
    return gameType;
  }

  public void setGameType( GameType gameType ) {
    this.gameType = gameType;
  }
  
  public Player[] getPlayers() {
    return players;
  }

  public void setPlayers( Player[] players ) {
    this.players = players;
  }

  public int getHandicap() {
    return handicap;
  }

  public void setHandicap( int handicap ) {
    this.handicap = handicap;
  }
}
