package com.mmatyus.go.model;

import java.util.List;
import java.util.Random;

public class RandomPlayerBoard implements Game<Integer> {
  public static final int MAX_RANDOM_TRIES = 50;
  Board                   board;
  Random                  rand             = new Random();
  PlayerPolicy            policy;

  public RandomPlayerBoard( BoardType boardType, PlayerPolicy policy ) {
    board = new Board( boardType );
    this.policy = policy;
  }

  public void setBoard( Board board ) {
    this.board = board;
  }

  public int selectRandomMove() {
    if( board.numberOfEmptyCells == 0 )
      return Board.PASS_MOVE;
    // tries to kill an enemy string
    if( policy.preferKill && board.numberOfShapesInAtari > 0 ) {
      int k = rand.nextInt( board.numberOfShapesInAtari );
      int l = board.shapesInAtari.nextSetBit( 0 );
      for( int i = 0; i < k; ++i ) {
        l = board.shapesInAtari.nextSetBit( l + 1 );
      }
      int pos = board.lives[l].nextSetBit( 0 );
      if( board.cells[l] == Board.theOtherColor( board.nextPlayer ) && board.isLegalMove( pos ) && !isTrivialEye( pos, board.nextPlayer ) ) {
        return pos;
      }
    }

    // select between two strategies for faster random selection
    // 3*size is an experimental optimum
    if( board.numberOfEmptyCells > 3 * board.boardType.sideLength ) {
      return selectRandomMoveInMiddleGame();
    } else {
      return selectRandomMoveInEndGame();
    }
  }

  protected int selectRandomMoveInMiddleGame() {
    int p = rand.nextInt( board.cellCount );
    int tries = 0;
    while( tries < MAX_RANDOM_TRIES && ( !board.isLegalMove( p ) || isTrivialEye( p, board.nextPlayer ) ) ) {
      p = rand.nextInt( board.cellCount );
      tries++;
    }
    if( tries == MAX_RANDOM_TRIES ) {
      return selectRandomMoveInEndGame();
    }
    return p;
  }

  protected int selectRandomMoveInEndGame() {
    // tries to throw random moves a couple of times
    for( int tries = 0; tries < 3; ++tries ) {
      int k = rand.nextInt( board.numberOfEmptyCells );
      int l = board.empties.nextSetBit( 0 );
      for( int i = 0; i < k; ++i ) {
        l = board.empties.nextSetBit( l + 1 );
      }
      if( board.isLegalMove( l ) && !isTrivialEye( l, board.nextPlayer ) ) {
        return l;
      }
    }

    // random tries failed, so pick the first move or pass if no more
    int l = board.empties.nextSetBit( 0 );
    while( l != -1 ) {
      if( board.isLegalMove( l ) && !isTrivialEye( l, board.nextPlayer ) ) {
        return l;
      }
      l = board.empties.nextSetBit( l + 1 );
    }

    return Board.PASS_MOVE;
  }

  protected boolean isTrivialEye( int pos, int color ) {
    int otherColor = Board.theOtherColor( color );

    for( int ni = 0; ni < BoardType.MAX_NEIGHBORS; ++ni ) {
      int p = board.boardType.neighbor( pos, ni );
      if( p == Board.OUT_OF_BOARD )
        continue;
      if( board.cells[p] == Board.EMPTY || board.cells[p] == otherColor ) {
        return false;
      } else if( board.cells[p] == color ) {
        if( board.lifeCounts[board.shapeAtPos[p]] == 1 ) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public List<Integer> actions() {
    return board.availableActions();
  }

  @Override
  /**
   * Evaluations for the last player. 1 if the last player wins 0 otherwise.
   *
   *
   */
  public double eval() {
    if( board.nextPlayer == Board.BLACK )
      return 1 - absoluteEval();
    else
      return absoluteEval();
  }

  @Override
  public void take( Integer action ) {
    board.move( action );
  }

  /**
   * Returns an absolute evaluation. Score is 1 if black wins, 0 if white wins.
   *
   * @return double - ChineseScore
   */
  public double absoluteEval() {
    while( !board.isGameOver() ) {
      board.move( selectRandomMove() );
    }
    return board.calcChineseScore() > 0 ? 1 : 0;
  }

  //BOARD_EVAL
  public void playRandomGame() {
    while( !board.isGameOver() ) {
      board.move( selectRandomMove() );
    }
  }

}
