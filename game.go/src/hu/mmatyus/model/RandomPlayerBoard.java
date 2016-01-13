package hu.mmatyus.model;

import java.util.List;
import java.util.Random;

public class RandomPlayerBoard extends Board implements Game<Integer> {
  public static final int MAX_RANDOM_TRIES = 50;
  Random rand = new Random();
  PlayerPolicy policy;

  public RandomPlayerBoard( BoardType boardType, PlayerPolicy policy ) {
    super( boardType );
    this.policy = policy;
  }

  public void playRandomGame() {
    while( !isGameOver() ) {
      move( selectRandomMove() );
    }
  }

  public int selectRandomMove() {
    if( numberOfEmptyCells == 0 )
      return PASS_MOVE;
    // tries to kill an enemy string
    if( policy.preferKill && numberOfStringsInAtari > 0 ) {
      int k = rand.nextInt( numberOfStringsInAtari );
      int l = stringsInAtari.nextSetBit( 0 );
      for( int i = 0; i < k; ++i ) {
        l = stringsInAtari.nextSetBit( l + 1 );
      }
      int pos = lives[l].nextSetBit( 0 );
      if( board[l] == theOtherColor( nextPlayer ) && isLegalMove( pos ) && !isTrivialEye( pos, nextPlayer ) ) {
        return pos;
      }
    }

    // select between two strategies for faster random selection
    // 3*size is an experimental optimum
    if( numberOfEmptyCells > 3 * boardType.size ) {
      return selectRandomMoveInMiddleGame();
    } else {
      return selectRandomMoveInEndGame();
    }
  }

  protected int selectRandomMoveInMiddleGame() {
    int p = rand.nextInt( n );
    int tries = 0;
    while( tries < MAX_RANDOM_TRIES && ( !isLegalMove( p ) || isTrivialEye( p, nextPlayer ) ) ) {
      p = rand.nextInt( n );
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
      int k = rand.nextInt( numberOfEmptyCells );
      int l = empty.nextSetBit( 0 );
      for( int i = 0; i < k; ++i ) {
        l = empty.nextSetBit( l + 1 );
      }
      if( isLegalMove( l ) && !isTrivialEye( l, nextPlayer ) ) {
        return l;
      }
    }

    // random tries failed, so pick the first move or pass if no more
    int l = empty.nextSetBit( 0 );
    while( l != -1 ) {
      if( isLegalMove( l ) && !isTrivialEye( l, nextPlayer ) ) {
        return l;
      }
      l = empty.nextSetBit( l + 1 );
    }

    return PASS_MOVE;
  }

  protected boolean isTrivialEye( int pos, int color ) {
    int otherColor = theOtherColor( color );

    for( int p : neighborPos[pos] ) {
      if( p == OUT_OF_BOARD ) {
        continue;
      }
      if( board[p] == EMPTY || board[p] == otherColor ) {
        return false;
      } else if( board[p] == color ) {
        if( lifeCounts[string[p]] == 1 ) {
          return false;
        }
      }
    }
    return true;
  }

  @Override public List<Integer> actions() {
    return availableActions();
  }

  @Override
  /**
   * Evalutaions for the last player. 1 if the last player wins 0 otherwise.
   */ public double eval() {
    if( nextPlayer == Board.BLACK )
      return 1 - absoluteEval();
    else
      return absoluteEval();
  }

  @Override public void take( Integer action ) {
    move( action );
  }

  /**
   * Returns an absolute evalalutation. Score is 1 if black wins, 0 if white wins.
   *
   * @return
   */
  public double absoluteEval() {
    while( !isGameOver() ) {
      move( selectRandomMove() );
    }
    return calcChineseScore() > 0 ? 1 : 0;
  }
}
