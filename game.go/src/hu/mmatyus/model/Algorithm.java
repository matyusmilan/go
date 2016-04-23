package hu.mmatyus.model;

public enum Algorithm {
  UCT ("Monte-Carlo Tree Search + UCT", "Node count", new int[]{ 100_000, 300_000, 500_000 } )
  , NEGAMAX( "NegaMax", "Depth" , new int[]{ 3, 4, 5} )
  , NEGAMAX_AB("NegaMax + AB", "Depth" , new int[]{ 3, 4, 5} )
  , NEGAMAX_AB_TT( "NegaMax + AB + TT" , "Depth", new int[]{ 3, 4, 5} )
  ;
  
  Algorithm(String label, String optionName, int[] options)
  {
    this.label = label;
    this.optionName = optionName;
    this.options = options;
  }
  public final String label;
  public final String optionName;
  int optionCount()
  {
    return options.length;
  }
  public int option(int idx)
  {
    return options[idx];
  }
  
  private final int[] options;
}
