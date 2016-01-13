package hu.mmatyus.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by milan on 2015.11.29..
 */
public class Handicap {
  private int handiLine_1, handiLine_2, handiLine_3, size_of_board;

  // 9, 13, 19
  public Handicap( int size ) {
    this.size_of_board = size;
    if( size_of_board >= 13 ) {
      handiLine_1 = 3;
      handiLine_3 = size_of_board - 4;
    } else {
      handiLine_1 = 2;
      handiLine_3 = size_of_board - 3;
    }
    handiLine_2 = ( size_of_board - 1 ) / 2;
  }

  public int getPos( int x, int y ) {
    return y * size_of_board + x;
  }

  /**
   * Get location of handicap stones.
   *
   * @param n The number of handicap stones.
   * @return List of points corresponding to the handicap stone locations.
   * http://www.lysator.liu.se/~gunnar/gtp/gtp2-spec-draft2/gtp2-spec.html#SECTION00051000000000000000
   */
  public Set<Integer> getHandicapStones( int n ) {
    Set<Integer> result = new HashSet<>();
    if( n < 2 || n > 9 )
      return null;
    if( n > 1 && 10 > n ) {

      int line1 = handiLine_1;
      int line2 = handiLine_2;
      int line3 = handiLine_3;

      if( n >= 2 ) {
        result.add( getPos( line1, line1 ) );
        result.add( getPos( line3, line3 ) );
      }
      if( n >= 3 )
        result.add( getPos( line1, line3 ) );
      if( n >= 4 )
        result.add( getPos( line3, line1 ) );
      if( n >= 5 && n % 2 != 0 ) {
        result.add( getPos( line2, line2 ) );
        --n;
      }
      if( n >= 5 )
        result.add( getPos( line1, line2 ) );
      if( n >= 6 )
        result.add( getPos( line3, line2 ) );
      if( n >= 7 )
        result.add( getPos( line2, line1 ) );
      if( n >= 8 )
        result.add( getPos( line2, line3 ) );
    }
    return result;
  }
}
