package hu.mmatyus.model;

/**
 * Interface for artificial players.
 */
public interface Robot {
  /**
   * Calculates the next move from a board (board knows who is next).
   * @param board Input. Not altered.
   * @return Row sequential offset of desired move.
   */
  int move( Board board );
}
