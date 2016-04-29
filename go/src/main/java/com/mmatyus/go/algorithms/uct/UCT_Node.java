package com.mmatyus.go.algorithms.uct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;

import com.mmatyus.go.model.Game;
import com.mmatyus.go.model.PlayerPolicy;

public class UCT_Node<Action> {

  protected Vector<UCT_Node<Action>> children;
  protected UCT_Node<Action>         parent;
  protected Action                   action;
  protected List<Action>             actions;
  protected PlayerPolicy             policy;
  protected boolean                  first     = true;
  protected Statistic                statistic = new Statistic();
  protected static Random            rand      = new Random();
  protected UCB_Pair<Action>         ucb_pair;

  public UCT_Node( UCT_Node<Action> parent, Game<Action> game, Action action, PlayerPolicy policy, UCB_Pair<Action> ucb_pair ) {
    this.parent = parent;
    this.action = action;
    this.policy = policy;
    this.ucb_pair = ucb_pair;

    if( action != null ) {
      game.take( action );
    }
    actions = game.actions();

    children = new Vector<>();
    children.setSize( actions.size() );
  }

  public void buildTree( Game<Action> game ) {
    if( first ) {
      eval( game );
      first = false;
      return;
    }
    if( action != null ) {
      game.take( action );
    }
    if( children.isEmpty() ) {
      eval( game );
      return;
    }
    bestChild( game ).buildTree( game );
  }

  protected UCT_Node<Action> bestChild( Game<Action> game ) {
    double maxscore = -Double.MAX_VALUE;
    UCT_Node<Action> bestNode = null;
    for( UCT_Node<Action> node : children ) {
      if( node == null ) {
        break;
      }
      double score = node.statistic.score( statistic.count(), policy );
      if( score > maxscore ) {
        maxscore = score;
        bestNode = node;
      }
    }
    if( ( !actions.isEmpty() ) && ( policy.fpu > maxscore ) ) {
      int idx = children.size() - actions.size();
      children.set( idx, new UCT_Node<>( this, game, preferredAction(), policy, ucb_pair == null ? null : ucb_pair.getComplementer() ) );
      bestNode = children.get( idx );
    }
    return bestNode;
  }

  Action preferredAction() {
    if( ucb_pair == null ) {
      return actions.remove( rand.nextInt( actions.size() ) );
    }
    UCB<Action> ucb = ucb_pair.getPrimary();
    Action a = ucb.best( actions, policy );
    //System.out.println( "preferredAction: " + a + " from " + actions.size() );
    actions.remove( a );
    return a;
  }

  protected void eval( Game<Action> game ) {
    buildStatistic( game.eval() );
  }

  protected void buildStatistic( double value ) {
    statistic.add( value );
    if( ucb_pair != null ) {
      ucb_pair.getPrimary().addStatistic( action, value );
    }
    if( parent != null ) {
      parent.buildStatistic( 1 - value );
    }
  }

  public double getWinRate() {
    return statistic.mean();
  }

  public Action bestAction() {
    UCT_Node<Action> node = bestChildForMove();
    return node == null ? null : node.action;
  }

  protected UCT_Node<Action> bestChildForMove() {
    double maxScore = -Double.MAX_VALUE;
    UCT_Node<Action> bestNode = null;
    for( UCT_Node<Action> node : children ) {
      if( node == null ) {
        break;
      }
      double score = node.statistic.mean();
      if( score > maxScore ) {
        maxScore = score;
        bestNode = node;
      }
    }
    return bestNode;
  }

  public int getSubTreeDepth() {
    int max = 0;
    for( UCT_Node<Action> node : children ) {
      if( node != null ) {
        int d = node.getSubTreeDepth();
        if( d > max ) {
          max = d;
        }
      }
    }
    return max + 1;
  }

  /*
   * public int getSubTreeSize() {
   * int n = 1;
   * for( UCT_Node<Action> node : children ) {
   * if( node != null ) {
   * n += node.getSubTreeSize();
   * }
   * }
   * return n;
   * }
   */

  public void dump( int depth, int width, int nn, String prefix ) {
    System.out.print( prefix );
    System.out.print( "a=" + action + ", c=" + statistic.count()
//                  + ", t=" + getSubTreeSize()
        + ", d=" + getSubTreeDepth() + ", m=" + statistic.mean() + ", s=" + statistic.score( nn, policy ) );
    System.out.println();
    if( depth > 0 ) {
      TreeMap<Double, List<UCT_Node<Action>>> m = new TreeMap<>( Collections.reverseOrder() );
      for( UCT_Node<Action> node : children ) {
        if( null != node ) {
          List<UCT_Node<Action>> list = m.get( node.statistic.mean() );
          if( list == null ) {
            list = new ArrayList<>();
            m.put( node.statistic.mean(), list );
          }
          list.add( node );
        }
      }
      int k = 0;
      for( List<UCT_Node<Action>> list : m.values() ) {
        for( UCT_Node<Action> node : list ) {
          node.dump( depth - 1, width, statistic.count(), prefix + "    " );
          ++k;
          if( k >= width ) {
            break;
          }
        }
        if( k >= width ) {
          break;
        }
      }
    }
  }
}
