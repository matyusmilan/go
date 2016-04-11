package hu.mmatyus.model;

public enum Algorithm {
  UCT ("Node count", new int[]{ 100_000, 300_000, 500_000 } )
  , NEGAMAX( "Depth" , new int[]{ 3, 4, 5} )
  , NEGAMAX_ALPHABETA( "Depth" , new int[]{ 3, 4, 5} )
  , NEGASCOUT( "Depth" , new int[]{ 3, 4, 5} )
  ;
  
  Algorithm(String optionName, int[] options)
  {
    this.optionName = optionName;
    this.options = options;
  }
  
  public final String optionName;
  int optionCount()
  {
    return options.length;
  }
  int option(int idx)
  {
    return options[idx];
  }
  
  private final int[] options;
}
