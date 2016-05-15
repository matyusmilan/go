package com.mmatyus.go.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.mmatyus.go.algorithms.Zobrist;

public class Board {
  /////////////////////////////////////////////////////////
  // Universe
  public static final int      EMPTY          = -1;
  public static final int      WHITE          = 0;
  public static final int      BLACK          = 1;

  public static final int      PASS_MOVE      = -1;                     // Special position for pass
  public static final int      RESIGN_MOVE    = -3;                     // Special position for resign

  public static final int      NO_KO_POS      = -1;                     // Position of KO is none.
  public static final int      NO_LAST_POS    = -2;                     // Special position for game starting.
  public static final int      OUT_OF_BOARD   = BoardType.OUT_OF_BOARD;
  public static final int      MAX_NEIGHBORS  = BoardType.MAX_NEIGHBORS;
  public static final double   DEFAULT_KOMI   = 6.5;

  /////////////////////////////////////////////////////////
  // Configuration
  public final BoardType       boardType;
  public final double          komi;
  public final int             sideLength;
  public final int             cellCount;                               // Amount of board positions = size_of_board^2
  public final Zobrist         zobrist;
  /////////////////////////////////////////////////////////
  // State

  protected int[]              cells;
  protected int                koPos;                                   // Position of ko or -1 if there is no ko.

  // Shapes
  protected int[]              shapeAtPos;                              // Shape id for each position. Index is table position.
  protected int[]              nextInShape;                             // Points to the next stone of the shape on this position. Index is table position.

  protected int                moves;                                   // Moves so far
  protected int                passNum;                                 // Number of consecutive passes
  protected int                lastMove;                                // Position of last move
  protected int                nextPlayer;                              // Id of next player, BLACK (0) or WHITE (1) 
  protected transient double[] area;                                    // 0-WHITE, 1-BLACK, 0,5 - neutral

  protected int[]              numberOfLifes;                           // Life counts for each shape. Index is shape id.
  protected BitSet[]           lives;                                   // Life giving cells of each shape. Index is shape id.
  protected int                numberOfEmptyCells;
  protected BitSet             emptyCells;                              // pos -> isEmpty()
  protected int                numberOfShapesInAtari;
  protected BitSet             shapesInAtari;                           // pos -> isAtari()  = position is part of a shape with one life

  protected long               zobristHashKey = 0L;

  public Board( BoardType boardType ) {
    this( boardType, 0, DEFAULT_KOMI );
  }

  public Board( BoardType boardType, int handicap ) {
    this( boardType, handicap, DEFAULT_KOMI );
  }

  public Board( BoardType boardType, int handicap, double komi ) {
    this( boardType, handicap, komi, true );
  }

  private Board( BoardType boardType, int handicap, double komi, boolean doInit ) {
    this.boardType = boardType;
    this.sideLength = boardType.sideLength;
    this.cellCount = sideLength * sideLength;
    this.zobrist = new Zobrist( boardType );

    this.cells = new int[cellCount];
    this.emptyCells = new BitSet( cellCount );
    this.shapesInAtari = new BitSet( cellCount );
    this.area = new double[cellCount];
    this.shapeAtPos = new int[cellCount];
    this.nextInShape = new int[cellCount];
    this.numberOfLifes = new int[cellCount];
    this.lives = new BitSet[cellCount];
    for( int i = 0; i < cellCount; ++i ) {
      this.lives[i] = new BitSet( cellCount );
    }

    if( doInit ) {
      initEmpty();
      initHandi( handicap );
    }

    this.komi = komi;
  }

  public Board( Board other ) {
    this( other.boardType, 0, other.komi, false );
    copyFrom( other );
  }

  public Board clone() {
    return new Board( this );
  }

  private void copyFrom( Board other ) {
    moves = other.moves;
    nextPlayer = other.nextPlayer;
    koPos = other.koPos;
    passNum = other.passNum;
    lastMove = other.lastMove;

    System.arraycopy( other.cells, 0, cells, 0, cells.length );
    System.arraycopy( other.shapeAtPos, 0, shapeAtPos, 0, shapeAtPos.length );
    System.arraycopy( other.nextInShape, 0, nextInShape, 0, nextInShape.length );
    System.arraycopy( other.numberOfLifes, 0, numberOfLifes, 0, numberOfLifes.length );

    for( int i = 0; i < lives.length; ++i ) {
      lives[i].clear();
      lives[i].or( other.lives[i] );
    }

    emptyCells.clear();
    emptyCells.or( other.emptyCells );
    numberOfEmptyCells = other.numberOfEmptyCells;

    shapesInAtari.clear();
    shapesInAtari.or( other.shapesInAtari );
    numberOfShapesInAtari = other.numberOfShapesInAtari;
  }

  private void initEmpty() {
    for( int i = 0; i < cellCount; ++i ) {
      cells[i] = EMPTY;
      shapeAtPos[i] = i;
      nextInShape[i] = i;
      numberOfLifes[i] = 0;
      lives[i].clear();
    }
    zobristHashKey = 0L;
    passNum = 0;
    moves = 0;
    lastMove = NO_LAST_POS;
    nextPlayer = BLACK;
    koPos = NO_KO_POS;

    emptyCells.set( 0, cellCount );
    numberOfEmptyCells = cellCount;

    shapesInAtari.clear();
    numberOfShapesInAtari = 0;
  }

  private void initHandi( int handicap ) {
    Handicap handi = new Handicap( this.boardType );
    if( handicap != 0 ) {
      for( int stone : handi.getHandicapStones( handicap ) ) {
        nextPlayer = BLACK;
        move( stone );
      }
    }
  }

  public static int theOtherColor( int color ) {
    return ( 1 - color );
  }

  public int[] getLifesForColor() {
    int[] lifeCount = new int[2];
    lifeCount[BLACK] = 0;
    lifeCount[WHITE] = 0;
    int headOfString;
    HashSet<Integer> s = new HashSet<>();
    for( int i = 0; i < cellCount; ++i ) {
      if( 0 <= cells[i] ) {
        headOfString = shapeAtPos[i];
        if( !s.contains( headOfString ) ) {
          s.add( headOfString );
          lifeCount[cells[headOfString]] += numberOfLifes[shapeAtPos[headOfString]];
        }
      }
    }
    return lifeCount;
  }

  public int[] getNumOfPieces() {
    int[] numOfPieces = new int[4];
    numOfPieces[BLACK] = 0;
    numOfPieces[WHITE] = 0;
    numOfPieces[2 + BLACK] = 0;
    numOfPieces[2 + WHITE] = 0;
    for( int i = 0; i < cellCount; ++i ) {
      if( 0 <= cells[i] ) {
        numOfPieces[cells[i]]++;
        // (x%y)%(y-1) * (x/y)%(y-1) 
        if( ( ( i % sideLength ) % ( sideLength - 1 ) ) * ( (int) ( i / sideLength ) % ( sideLength - 1 ) ) == 0 ) {
          numOfPieces[2 + cells[i]]++;
        }
      }
    }
    return numOfPieces;
  }

  public int[] getEulerNumber() {
    int[] numEuler = new int[2];
    numEuler[BLACK] = 0;
    numEuler[WHITE] = 0;
    int[] nQ1 = new int[2];
    nQ1[BLACK] = 0;
    nQ1[WHITE] = 0;
    int[] nQ3 = new int[2];
    nQ3[BLACK] = 0;
    nQ3[WHITE] = 0;
    int[] nQd = new int[2];
    nQd[BLACK] = 0;
    nQd[WHITE] = 0;
    int[] sumColor = new int[2];
    sumColor[BLACK] = 0;
    sumColor[WHITE] = 0;
    boolean[] onBoard = new boolean[4];
    int x, y;
    for( int i = -1; i < sideLength; i++ ) {
      for( int j = -1; j < sideLength; j++ ) {
        x = i;
        y = j;
        onBoard[0] = false;
        if( x != -1 && y != -1 && x != sideLength && y != sideLength ) {
          onBoard[0] = true;
          if( 0 <= cells[getPos( x, y )] )
            sumColor[cells[getPos( x, y )]]++;
        }
        x = i + 1;
        y = j;
        onBoard[1] = false;
        if( x != -1 && y != -1 && x != sideLength && y != sideLength ) {
          onBoard[1] = true;
          if( 0 <= cells[getPos( x, y )] )
            sumColor[cells[getPos( x, y )]]++;
        }
        x = i;
        y = j + 1;
        onBoard[3] = false;
        if( x != -1 && y != -1 && x != sideLength && y != sideLength ) {
          onBoard[3] = true;
          if( 0 <= cells[getPos( x, y )] )
            sumColor[cells[getPos( x, y )]]++;
        }
        x = i + 1;
        y = j + 1;
        onBoard[2] = false;
        if( x != -1 && y != -1 && x != sideLength && y != sideLength ) {
          onBoard[2] = true;
          if( 0 <= cells[getPos( x, y )] )
            sumColor[cells[getPos( x, y )]]++;
        }
        for( int color = 0; color <= 1; color++ ) {
          switch( sumColor[color] ) {
            case 1:
              nQ1[color]++;
              break;
            case 2:
              if( ( onBoard[0] && onBoard[2] && cells[getPos( i, j )] + cells[getPos( i + 1, j + 1 )] == 2 * color ) || ( onBoard[1] && onBoard[3] && cells[getPos( i + 1, j )] + cells[getPos( i, j + 1 )] == 2 * color ) )
                nQd[color]++;
              break;
            case 3:
              nQ3[color]++;
              break;
            default:
              break;
          }
        }
        sumColor[BLACK] = 0;
        sumColor[WHITE] = 0;
      }
    }
    for( int color = 0; color <= 1; color++ ) {
      //System.out.println("c: "+color+" nQ1: "+nQ1[color]+" nQ3: " + nQ3[color]+" 2nQd: "+(2 * nQd[color]));
      numEuler[color] = ( nQ1[color] - nQ3[color] + 2 * nQd[color] ) / 4;
    }
    return numEuler;
  }

  public boolean isGameOver() {
    return passNum == 2 || moves > 2 * cellCount || lastMove == RESIGN_MOVE;
  }

  public int getPassNum() {
    return passNum;
  }

  public boolean isKo( int pos ) {
    return pos == koPos;
  }

  public boolean isLegalMove( int pos ) {
    return pos < cellCount && !isGameOver() && ( pos == RESIGN_MOVE || pos == PASS_MOVE || ( isEmpty( pos ) && !isSuicide( pos ) && !isKo( pos ) ) );
  }

  public boolean isSuicide( int pos ) {
    int selfColor = nextPlayer;
    int otherColor = theOtherColor( selfColor );
    for( int ni = 0; ni < MAX_NEIGHBORS; ++ni ) {
      int p = boardType.neighbor( pos, ni );
      if( p == OUT_OF_BOARD )
        continue;
      if( isEmpty( p ) ) {
        return false;
      } else if( cells[p] == selfColor ) {
        if( numberOfLifes[shapeAtPos[p]] > 1 )
          return false;
      } else if( cells[p] == otherColor ) {
        if( numberOfLifes[shapeAtPos[p]] == 1 )
          return false;
      }
    }
    return true;
  }

  public int getLifeOfShape( int pos ) {
    return numberOfLifes[shapeAtPos[pos]];
  }

  public int getPos( int x, int y ) {
    return y * sideLength + x;
  }

  public boolean isEmpty( int pos ) {
    return cells[pos] == EMPTY;
  }

  public void pass() {
    nextPlayer = theOtherColor( nextPlayer );
    koPos = NO_KO_POS;
    passNum++;
    moves++;
    lastMove = PASS_MOVE;
  }

  public void resign() {
    moves++;
    lastMove = RESIGN_MOVE;
  }

  public int getState( int pos ) {
    return cells[pos];
  }

  protected void addStone( int pos, int color ) {
    assert ( cells[pos] == EMPTY );
    cells[pos] = color;
    zobristHashKey ^= zobrist.zArray[color][pos];
    emptyCells.clear( pos );
    numberOfEmptyCells--;
  }

  public int getnumberOfEmptyCells() {
    return this.numberOfEmptyCells;
  }

  public void move( int pos ) {
    assert ( !isGameOver() );
    assert ( isLegalMove( pos ) );
    if( pos == PASS_MOVE ) {
      pass();
      return;
    }
    if( pos == RESIGN_MOVE ) {
      resign();
      return;
    }
    koPos = NO_KO_POS;
    passNum = 0;
    lastMove = pos;
    int selfColor = nextPlayer;
    int otherColor = theOtherColor( selfColor );
    addStone( pos, selfColor );

    for( int ni = 0; ni < MAX_NEIGHBORS; ++ni ) {
      int np = boardType.neighbor( pos, ni );
      if( np == OUT_OF_BOARD )
        continue;
      if( cells[np] == selfColor ) {
        mergeShapes( pos, np );
      } else if( cells[np] == otherColor ) {
        removeLifeFromShape( np, pos );
      } else if( cells[np] == EMPTY ) {
        addLifeToShape( pos, np );
      }
    }
    nextPlayer = otherColor;
    moves++;
  }

  public int getLastMove() {
    return lastMove;
  }

  private void mergeShapes( int pos1, int pos2 ) {
    final int shapeId1 = shapeAtPos[pos1];
    final int shapeId2 = shapeAtPos[pos2];
    if( shapeId1 == shapeId2 )
      return;

    lives[shapeId2].or( lives[shapeId1] );
    setLifeCount( shapeId2, lives[shapeId2].cardinality() );
    setLifeCount( shapeId1, 0 );

    shapeAtPos[pos1] = shapeId2;
    for( int pos = nextInShape[pos1]; pos != pos1; pos = nextInShape[pos] ) {
      shapeAtPos[pos] = shapeId2;
    }
    int pos = nextInShape[pos1];
    nextInShape[pos1] = nextInShape[pos2];
    nextInShape[pos2] = pos;
    removeLifeNoKill( pos2, pos1 );
  }

  private void removeLifeNoKill( int target, int source ) {
    if( lives[shapeAtPos[target]].get( source ) ) {
      lives[shapeAtPos[target]].set( source, false );
      setLifeCount( shapeAtPos[target], numberOfLifes[shapeAtPos[target]] - 1 );
    }
  }

  private void addLifeToShape( int target, int emptyPos ) {
    if( !lives[shapeAtPos[target]].get( emptyPos ) ) {
      lives[shapeAtPos[target]].set( emptyPos );
      setLifeCount( shapeAtPos[target], numberOfLifes[shapeAtPos[target]] + 1 );
    }
  }

  private void removeLifeFromShape( int target, int source ) {
    if( lives[shapeAtPos[target]].get( source ) ) {
      lives[shapeAtPos[target]].set( source, false );
      if( setLifeCount( shapeAtPos[target], numberOfLifes[shapeAtPos[target]] - 1 ) == 0 ) {
        killShape( target, source );
      }
    }
  }

  private void killShape( int target, int killer ) {
    if( nextInShape[target] == target && nextInShape[killer] == killer ) {
      koPos = target;
    }
    int z = target;
    do {
      int y = nextInShape[z];
      removeStone( z );
      z = y;
    } while( z != target );
  }

  protected void removeStone( int pos ) {
    assert ( cells[pos] != EMPTY );
    int color = cells[pos];
    int otherColor = theOtherColor( color );
    for( int ni = 0; ni < MAX_NEIGHBORS; ++ni ) {
      int p = boardType.neighbor( pos, ni );
      if( p != OUT_OF_BOARD && cells[p] == otherColor ) {
        addLifeToShape( p, pos );
      }
    }
    cells[pos] = EMPTY;
    zobristHashKey ^= zobrist.zArray[color][pos];
    nextInShape[pos] = pos;
    shapeAtPos[pos] = pos;
    lives[pos].clear();

    setLifeCount( pos, 0 );

    emptyCells.set( pos );
    numberOfEmptyCells++;
  }

  private int setLifeCount( int shapeId, int val ) {
    if( numberOfLifes[shapeId] == val )
      return val;
    if( numberOfLifes[shapeId] == 1 ) {
      removeShapeFromAtari( shapeId );
    }
    numberOfLifes[shapeId] = val;
    if( val == 1 ) {
      addShapeToAtari( shapeId );
    }
    return val;
  }

  private void addShapeToAtari( int shapeId ) {
    shapesInAtari.set( shapeId );
    numberOfShapesInAtari++;
  }

  private void removeShapeFromAtari( int shapeId ) {
    shapesInAtari.clear( shapeId );
    numberOfShapesInAtari--;
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
    for( int pos = 0; pos < cellCount; ++pos ) {
      if( cells[pos] == BLACK ) {
        area[pos] = BLACK;
        black++;
      } else if( cells[pos] == WHITE ) {
        area[pos] = WHITE;
        white++;
      } else {
        int b = 0;
        int w = 0;
        for( int ni = 0; ni < MAX_NEIGHBORS; ++ni ) {
          int p = boardType.neighbor( pos, ni );
          if( p == OUT_OF_BOARD || isEmpty( p ) )
            continue;
          if( cells[p] == WHITE )
            w = 1;
          else if( cells[p] == BLACK )
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

  public int getNextPlayer() {
    return nextPlayer;
  }

  public List<Integer> availableActions() {
    List<Integer> actions = new ArrayList<>();
    if( isGameOver() ) {
      return actions;
    }
    for( int pos = emptyCells.nextSetBit( 0 ); pos != -1; pos = emptyCells.nextSetBit( pos + 1 ) ) {
      if( isLegalMove( pos ) ) {
        actions.add( pos );
      }
    }
    actions.add( PASS_MOVE );
    return actions;
  }

  public int[] orderMoves() {
    List<Integer> moves = availableActions();
    int[] ordered = new int[moves.size()];
    int i = 0;
    if( 0 <= lastMove ) {
      int lx = lastMove % sideLength;
      int ly = lastMove / sideLength;
      int manhattanDist = 1;
      int mx, my;
      while( 0 < moves.size() ) {
        for( Iterator<Integer> iterator = moves.iterator(); iterator.hasNext(); ) {
          int m = iterator.next();
          mx = m % sideLength;
          my = m / sideLength;
          if( Math.abs( lx - mx ) + Math.abs( ly - my ) == manhattanDist ) {
            ordered[i] = m;
            iterator.remove();
            i++;
          }
        }
        manhattanDist++;
      }
    } else {
      for( int m : moves ) {
        ordered[i] = m;
        i++;
      }
    }
    return ordered;
  }

  //BOARD_EVAL
  public void clearPasses() {
    passNum = 0;
  }

  public double getArea( int pos ) {
    return area[pos];
  }

  public int getNumberOfCells() {
    return cellCount;
  }

  public double getKomi() {
    return komi;
  }

  public long getZobristHashKey() {
    return zobristHashKey;
  }
}
