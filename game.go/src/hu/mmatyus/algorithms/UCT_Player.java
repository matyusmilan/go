package hu.mmatyus.algorithms;

import hu.mmatyus.model.Board;
import hu.mmatyus.model.Player;
import hu.mmatyus.model.PlayerPolicy;
import hu.mmatyus.model.RandomPlayerBoard;

public class UCT_Player implements Player {
  protected PlayerPolicy      policy;
  protected UCB_Pair<Integer> ucb_pair;

  public UCT_Player( PlayerPolicy policy ) {
    this.policy = policy;
  }

  @Override public int move( Board board ) {

    if( policy.useUCBPrior ) {
      if( ucb_pair == null ) {
        UCB<Integer> primary = new UCB<Integer>();
        UCB<Integer> secondary = new UCB<Integer>();
        primary.addActions( board.availableActions() );
        secondary.addActions( board.availableActions() );
        ucb_pair = new UCB_Pair<Integer>( primary, secondary );
      }
      ucb_pair.multiply( policy.forceOfPrior );
    }

    RandomPlayerBoard randomPlayerBoard = new RandomPlayerBoard( board.getBoardType(), policy );
    randomPlayerBoard.copyFrom( board );

    UCT_Node<Integer> root = new UCT_Node<Integer>( null, randomPlayerBoard, null, policy, ucb_pair );
    for( int i = 0; i < policy.iterations; ++i ) {
      randomPlayerBoard.copyFrom( board );
      root.buildTree( randomPlayerBoard );

    }
    if( policy.useUCBPrior ) {
      //System.out.println( "primary" );
      //ucb_pair.primary.dump( 0.02 * policy.iterations );
      //System.out.println( "secondary" );
      //ucb_pair.secondary.dump( 0.02 * policy.iterations );
      System.out.println( "UCT Tree" );
      System.out.println( "Best action: " + root.bestAction() );
      root.dump( 2, 2, policy.iterations, "" );
    }
    if( root.getWinRate() < policy.passThreshold || root.getWinRate() > 1 - policy.passThreshold )
      return Board.PASS_MOVE;
    else
      return root.bestAction();
  }
}
