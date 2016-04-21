package hu.mmatyus.algorithms.tt;

import hu.mmatyus.algorithms.tt.TranspositionTableEntry.Type;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
  private Map<Long, TranspositionTableEntry> table = new HashMap<Long, TranspositionTableEntry>();

  public void addResult(long zobristKey, Type type, int bestMove, int evaluation, int depth) {
    TranspositionTableEntry existingEntry = table.get(zobristKey);
    if (existingEntry == null || depth >= existingEntry.getDepth()) {
        table.put(zobristKey, new TranspositionTableEntry(type, bestMove, evaluation, depth));
    }
  }
  
  /**
   * Return the already calculated value for a Zobrist key or null if no key exists.
   */
  public TranspositionTableEntry getResult(long zobristKey) {
      return table.get(zobristKey);
  }
  
}
