package com.mmatyus.go.model;

import java.util.concurrent.Callable;

/**
 * Interface for artificial players.
 */
public interface Robot extends Callable<Integer> {
  /**
   * Calculates the next move from a board (board knows who is next).
   * 
   * @param board
   *          Input. Not altered.
   * @return Row sequential offset of desired move.
   */
  int move( Board board );

  void setBoard( Board board );
}
