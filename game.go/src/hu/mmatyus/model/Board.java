package hu.mmatyus.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Board {
  public static final int    EMPTY        = -1;
  public static final int    WHITE        = 0;
  public static final int    BLACK        = 1;
  public static final int    PASS_MOVE    = -1;
  public static final int    NO_KO        = -1;
  public static final int    OUT_OF_BOARD = -2;
  public static final int    NO_LAST_MOVE = -2;
  public static final double KOMI         = 6.5;
  public static       Handicap  handi;
  public static       BoardType boardType;
  protected           int       board[];                                                      //String id for each position. Index is table position.
  protected           int       string[];                                                     //Life counts for each string. Index is string id.
  protected           int       lifeCounts[];                                                 //Lives of each string. Index is string id.
  protected           BitSet    lives[];                                                      //Points to the next stone of the string on this position. Index is table position.
  protected           int       next[];                                                       //Position of ko or -1 if there is no ko.
  protected           int       ko;                                                           //valid size: 9, 13, 19
  protected           double    komi;
  protected           int       size_of_board;                                                //amount of board positions = size_of_board^2
  protected           int       n;
  //count of moves
  protected           int       moves;
  protected           int       passNum;
  protected           int       lastMove;
  protected           int       nextPlayer;                                                   //0-WHITE, 1-BLACK, 0,5 - neutral
  protected transient double    area[];
  protected           int       neighborPos[][];
  protected           BitSet    empty;
  protected           int       numberOfStringsInAtari;
  protected           BitSet    stringsInAtari;
  protected           int       numberOfEmptyCells;

  public Board( BoardType boardType ) {
    this( boardType, 0 );
  }

  public Board( BoardType boardType, int handicap ) {
    Board.boardType = boardType;
    this.size_of_board = boardType.size;
    handi = new Handicap( size_of_board );
    this.n = size_of_board * size_of_board;

    this.board = new int[n];
    this.empty = new BitSet( n );
    stringsInAtari = new BitSet( n );
    this.area = new double[n];
    this.string = new int[n];
    this.next = new int[n];
    this.lifeCounts = new int[n];
    this.lives = new BitSet[n];
    for( int i = 0; i < n; ++i ) {
      this.lives[i] = new BitSet( n );
    }
    this.komi = KOMI;
    fillNeighborPosition();
    reset( handicap );
  }

  public static int theOtherColor( int color ) {
    return ( 1 - color );
  }

  public static int getOtherColor( int color ) {
    return ( 1 - color );
  }

  public boolean isGameOver() {
    return passNum == 2 || moves > 2 * n;
  }

  public boolean isKo( int pos ) {
    return pos == ko;
  }

  public boolean isLegalMove( int pos ) {
    return pos < n && !isGameOver() && ( pos == PASS_MOVE || ( isEmpty( pos ) && !isSuicide( pos ) && !isKo( pos ) ) );
  }

  public boolean isSuicide( int pos ) {
    int selfColor = nextPlayer;
    int otherColor = theOtherColor( selfColor );
    for( int p : neighborPos[pos] ) {
      if( p == OUT_OF_BOARD ) {
        continue;
      }
      if( isEmpty( p ) ) {
        return false;
      } else if( board[p] == selfColor ) {
        if( lifeCounts[string[p]] > 1 )
          return false;
      } else if( board[p] == otherColor ) {
        if( lifeCounts[string[p]] == 1 )
          return false;
      }
    }
    return true;
  }

  public int getPos( int x, int y ) {
    return y * size_of_board + x;
  }

  public int getSize() {
    return size_of_board;
  }

  public int getNumberOfCells() {
    return n;
  }

  public int getMoves() {
    return moves;
  }

  public boolean isEmpty( int pos ) {
    return board[pos] == EMPTY;
  }

  public BoardType getBoardType() {
    return boardType;
  }

  public void reset( int handicap ) {
    for( int i = 0; i < n; ++i ) {
      board[i] = EMPTY;
      string[i] = i;
      next[i] = i;
      lifeCounts[i] = 0;
      lives[i].clear();
    }
    passNum = 0;
    moves = 0;
    lastMove = NO_LAST_MOVE;
    nextPlayer = BLACK;
    ko = NO_KO;
    komi = KOMI;

    empty.set( 0, n );
    numberOfEmptyCells = n;

    stringsInAtari.clear();
    numberOfStringsInAtari = 0;

    // handicap moves
    if( 0 < handicap )
      for( int stone : handi.getHandicapStones( handicap ) ) {
        nextPlayer = BLACK;
        move( stone );
      }
  }

  public void copyFrom( Board b ) {
    assert ( boardType == boardType );
    moves = b.moves;
    nextPlayer = b.nextPlayer;
    ko = b.ko;
    passNum = b.passNum;
    lastMove = b.lastMove;
    komi = b.komi;

    System.arraycopy( b.board, 0, board, 0, board.length );
    System.arraycopy( b.string, 0, string, 0, string.length );
    System.arraycopy( b.next, 0, next, 0, next.length );
    System.arraycopy( b.lifeCounts, 0, lifeCounts, 0, lifeCounts.length );

    for( int i = 0; i < lives.length; ++i ) {
      lives[i].clear();
      lives[i].or( b.lives[i] );
    }

    empty.clear();
    empty.or( b.empty );
    numberOfEmptyCells = b.numberOfEmptyCells;

    stringsInAtari.clear();
    stringsInAtari.or( b.stringsInAtari );
    numberOfStringsInAtari = b.numberOfStringsInAtari;
  }

  public void pass() {
    nextPlayer = theOtherColor( nextPlayer );
    passNum++;
    moves++;
    lastMove = PASS_MOVE;
  }

  public void clearPasses() {
    passNum = 0;
  }

  public int getState( int pos ) {
    return board[pos];
  }

  protected void addStone( int pos, int color ) {
    assert ( board[pos] == EMPTY );
    board[pos] = color;
    empty.clear( pos );
    numberOfEmptyCells--;
  }

  public void move( int pos ) {
    assert ( !isGameOver() );
    assert ( isLegalMove( pos ) );
    if( pos == PASS_MOVE ) {
      pass();
      return;
    }
    ko = NO_KO;
    passNum = 0;
    lastMove = pos;
    int selfColor = nextPlayer;
    int otherColor = theOtherColor( selfColor );

    addStone( pos, selfColor );

    for( int p : neighborPos[pos] ) {
      if( p == OUT_OF_BOARD )
        continue;
      if( board[p] == selfColor ) {
        mergeStrings( pos, p );
      } else if( board[p] == otherColor ) {
        removeLife( p, pos );
      } else if( board[p] == EMPTY ) {
        addLife( pos, p );
      }
    }

    nextPlayer = otherColor;
    moves++;
  }

  public int getLastMove() {
    return lastMove;
  }

  private void mergeStrings( int x, int y ) {
    if( string[x] == string[y] ) {
      return;
    }
    lives[string[y]].or( lives[string[x]] );
    setLifeCount( string[y], lives[string[y]].cardinality() );
    setLifeCount( string[x], 0 );

    string[x] = string[y];
    for( int z = next[x]; z != x; z = next[z] ) {
      string[z] = string[y];
    }
    int z = next[x];
    next[x] = next[y];
    next[y] = z;
    removeLifeNoKill( y, x );
  }

  private void removeLifeNoKill( int x, int y ) {
    if( lives[string[x]].get( y ) ) {
      lives[string[x]].set( y, false );
      setLifeCount( string[x], lifeCounts[string[x]] - 1 );
    }
  }

  private void addLife( int x, int y ) {
    if( !lives[string[x]].get( y ) ) {
      lives[string[x]].set( y );
      setLifeCount( string[x], lifeCounts[string[x]] + 1 );
    }
  }

  private void removeLife( int x, int y ) {
    if( lives[string[x]].get( y ) ) {
      lives[string[x]].set( y, false );
      if( setLifeCount( string[x], lifeCounts[string[x]] - 1 ) == 0 ) {
        killString( x );
      }
    }
  }

  private void killString( int x ) {
    if( next[x] == x ) {
      ko = x;
    }
    int z = x;
    do {
      int y = next[z];
      removeStone( z );
      z = y;
    } while( z != x );
  }

  protected void removeStone( int x ) {
    assert ( board[x] != EMPTY );
    int color = board[x];
    int otherColor = theOtherColor( color );
    for( int p : neighborPos[x] ) {
      if( p != OUT_OF_BOARD && board[p] == otherColor ) {
        addLife( p, x );
      }
    }
    board[x] = EMPTY;
    next[x] = x;
    string[x] = x;
    lives[x].clear();

    setLifeCount( x, 0 );

    empty.set( x );
    numberOfEmptyCells++;
  }

  private int setLifeCount( int x, int val ) {
    // System.out.println(x+"="+val);
    if( lifeCounts[x] == 1 ) {
      // stringFromAtari( x );
    }
    lifeCounts[x] = val;
    if( lifeCounts[x] == 1 ) {
      // stringToAtari( x );
    }
    return lifeCounts[x];
  }

  private void fillNeighborPosition() {
    neighborPos = new int[n][4];
    for( int x = 0; x < size_of_board; ++x ) {
      for( int y = 0; y < size_of_board; ++y ) {
        int position = getPos( x, y );
        neighborPos[position][0] = ( y == 0 ) ? OUT_OF_BOARD : position - size_of_board; // up
        neighborPos[position][1] = ( x == size_of_board - 1 ) ? OUT_OF_BOARD : position + 1; // right
        neighborPos[position][2] = ( y == size_of_board - 1 ) ? OUT_OF_BOARD : position + size_of_board; // down
        neighborPos[position][3] = ( x == 0 ) ? OUT_OF_BOARD : position - 1; // left
      }
    }
  }

  /**
   * Calculates the score and areas based on the Chinese scoring method.
   * Board has to be in the end game state: all empty positions should be trivial eyes.
   *
   * @return points of black - points of white - komi
   */
  public double calcChineseScore() {
    int black = 0;
    int white = 0;
    for( int pos = 0; pos < n; ++pos ) {
      if( board[pos] == BLACK ) {
        area[pos] = BLACK;
        black++;
      } else if( board[pos] == WHITE ) {
        area[pos] = WHITE;
        white++;
      } else {
        int b = 0;
        int w = 0;
        for( int p : neighborPos[pos] ) {
          if( p == OUT_OF_BOARD || isEmpty( p ) )
            continue;
          if( board[p] == WHITE )
            w = 1;
          else if( board[p] == BLACK )
            b = 1;

          if( w == 1 && b == 0 )
            area[pos] = WHITE;
          else if( b == 1 && w == 0 )
            area[pos] = BLACK;
          else
            area[pos] = 0.5;
        }
        black += b;
        white += w;
      }
    }
    return black - white - komi;
  }

  public double getArea( int pos ) {
    return area[pos];
  }

  public double getKomi() {
    return komi;
  }

  public int getNextPlayer() {
    return nextPlayer;
  }

  public List<Integer> availableActions() {
    List<Integer> actions = new ArrayList<Integer>();
    if( isGameOver() ) {
      return actions;
    }
    for( int pos = empty.nextSetBit( 0 ); pos != -1; pos = empty.nextSetBit( pos + 1 ) ) {
      if( isLegalMove( pos ) ) {
        actions.add( pos );
      }
    }
    actions.add( PASS_MOVE );
    return actions;
  }

}
