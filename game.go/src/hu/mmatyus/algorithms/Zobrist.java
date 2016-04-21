package hu.mmatyus.algorithms;

import hu.mmatyus.model.Board;
import hu.mmatyus.model.BoardType;

import java.security.*;

public class Zobrist {
  private final SecureRandom random     = new SecureRandom();
  public final long         zArray[][];
  private final long         zWhiteMove = random64();
  private final int          size;

  public Zobrist( BoardType boardType ) {
    this.size = boardType.sideLength * boardType.sideLength;
    this.zArray = new long[2][this.size];
    zobristFillArray();
  }

  public long random64() {
    return random.nextLong();
  }

  private void zobristFillArray() {
    for( int pos = 0; pos < size; pos++ ) {
      zArray[Board.BLACK][pos] = random64();
      zArray[Board.WHITE][pos] = random64();
    }
  }

  public long getZobristHash( Board board ) {
    boolean blackToMove = ( board.getNextPlayer() == Board.BLACK ) ? true : false;
    long returnZobristKey = 0;
    for( int pos = 0; pos < size; pos++ ) {
      if( board.getState( pos ) == Board.WHITE ) {
        returnZobristKey ^= zArray[Board.WHITE][pos];
      } else if( board.getState( pos ) == Board.BLACK ) {
        returnZobristKey ^= zArray[Board.BLACK][pos];
      }
    }
    if( !blackToMove )
      returnZobristKey ^= zWhiteMove;
    return returnZobristKey;
  }
}
