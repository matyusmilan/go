package com.mmatyus.go.algorithms.uct;

import com.mmatyus.go.ProgressContainer;
import com.mmatyus.go.model.Board;
import com.mmatyus.go.model.PlayerPolicy;
import com.mmatyus.go.model.RandomPlayerBoard;
import com.mmatyus.go.model.Robot;

public class UCT_Robot implements Robot {
  protected PlayerPolicy      policy;
  protected UCB_Pair<Integer> ucb_pair;
  protected Board             board;
  protected ProgressContainer progressContainer;

  public UCT_Robot( PlayerPolicy policy ) {
    this.policy = policy;
  }

  public void setBoard( Board board ) {
    this.board = board;
  }

  public void setProgressContainer( ProgressContainer progressContainer ) {
    this.progressContainer = progressContainer;
  }

  @Override
  public int move( Board board, ProgressContainer pc ) throws InterruptedException {
    pc.reset();
    pc.setSum( policy.iterations );

    if( policy.useUCBPrior ) {
      if( ucb_pair == null ) {
        UCB<Integer> primary = new UCB<>();
        UCB<Integer> secondary = new UCB<>();
        primary.addActions( board.availableActions() );
        secondary.addActions( board.availableActions() );
        ucb_pair = new UCB_Pair<>( primary, secondary );
      }
      ucb_pair.multiply( policy.forceOfPrior );
    }

    RandomPlayerBoard randomPlayerBoard = new RandomPlayerBoard( board.boardType, policy );
    randomPlayerBoard.setBoard( board.clone() );

    UCT_Node<Integer> root = new UCT_Node<>( null, randomPlayerBoard, null, policy, ucb_pair );

    for( int i = 0; i < policy.iterations; ++i ) {
      randomPlayerBoard.setBoard( board.clone() );
      root.buildTree( randomPlayerBoard );
      if( 0 == i % 10000 ) {
        pc.setActual( i );
        //System.err.println( "UCT: " + pc.getCurrentProgress() );
      }
      if( Thread.interrupted() ) {
        throw new InterruptedException();
      }
    }

    if( policy.useUCBPrior ) {
      System.out.println( "UCT Tree" );
      root.dump( 2, 2, policy.iterations, "" );
    }
    if( root.getWinRate() < policy.passThreshold ) {
      System.out.println( "Best action: PASS" );
      return Board.PASS_MOVE;
    } else if( root.getWinRate() > 1 - policy.passThreshold ) {
      System.out.println( "Best action: RESIGN" );
      return Board.RESIGN_MOVE;
    } else {
      int ba = root.bestAction();
      System.out.println( "Best action: " + ba );
      return ba;
    }
  }

  @Override
  public Integer call() throws Exception {
    return move( board, progressContainer );
  }
}
