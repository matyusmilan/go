package hu.mmatyus.algorithms.uct;

import hu.mmatyus.model.PlayerPolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UCB<Action> {
  protected Statistic              statistic  = new Statistic();
  protected Map<Action, Statistic> statistics = new HashMap<>();
  protected int emptyStatisticCount;
  protected static Random rand = new Random();

  public void addStatistic( Action action, double value ) {
    Statistic stat = statistics.get( action );
    if( stat != null ) {
      if( stat.count() == 0.0 && value != 0.0 ) {
        --emptyStatisticCount;
      }
      stat.add( value );
      statistic.add( value );
    }
  }

  public void addActions( List<Action> actions ) {
    for( Action action : actions ) {
      addAction( action );
    }
  }

  public void addAction( Action action ) {
    statistics.put( action, new Statistic() );
    ++emptyStatisticCount;
  }

  public void multiply( double alpha ) {
    for( Statistic stat : statistics.values() ) {
      stat.multiply( alpha );
    }
    statistic.multiply( alpha );
    if( alpha == 0.0 ) {
      emptyStatisticCount = statistics.size();
    }
  }

  public Action best( List<Action> actions, PlayerPolicy policy ) {
    Action bestAction = null;
    double bestScore = -Double.MAX_VALUE;
    int k = rand.nextInt( emptyStatisticCount );
    for( Action action : actions ) {
      double score = policy.fpu;
      Statistic stat = statistics.get( action );
      if( stat == null ) {
        stat = new Statistic();
        statistics.put( action, stat );
      }
      if( stat.count() >= 0.00001 ) {
        score = stat.score( statistic.count(), policy );
      }
//      System.out.println( "  A- " + action + " s: " + score );
      if( score > bestScore ) {
        if( stat.count() > 0 || k >= 0 ) {
          bestAction = action;
          bestScore = score;
        }
      }
      if( stat.count() <= 0.00001 ) {
        --k;
      }
    }
    return bestAction;
  }
}
