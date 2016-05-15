package com.mmatyus.go.algorithms;

import java.util.concurrent.Callable;

import com.mmatyus.go.ProgressContainer;
import com.mmatyus.go.model.Board;

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
  int move( Board board, ProgressContainer pc ) throws InterruptedException;

  void setBoard( Board board );

  void setProgressContainer( ProgressContainer progressContainer );
}
