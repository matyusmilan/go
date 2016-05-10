package com.mmatyus.go.algorithms.uct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.mmatyus.go.model.PlayerPolicy;

public class UCB<Action> {
  protected Statistic              statistic  = new Statistic();
  protected Map<Action, Statistic> listOfStatistic = new HashMap<>();
  protected int                    emptyStatisticCount;
  protected static Random          rand       = new Random();

  public void addStatistic( Action action, double value ) {
    Statistic stat = listOfStatistic.get( action );
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
    listOfStatistic.put( action, new Statistic() );
    ++emptyStatisticCount;
  }

  public void multiply( double alpha ) {
    for( Statistic stat : listOfStatistic.values() ) {
      stat.multiply( alpha );
    }
    statistic.multiply( alpha );
    if( alpha == 0.0 ) {
      emptyStatisticCount = listOfStatistic.size();
    }
  }

  public Action best( List<Action> actions, PlayerPolicy policy ) {
    Action bestAction = null;
    double bestScore = -Double.MAX_VALUE;
    int k = rand.nextInt( emptyStatisticCount );
    for( Action action : actions ) {
      double score = policy.fpu;
      Statistic stat = listOfStatistic.get( action );
      if( stat == null ) {
        stat = new Statistic();
        listOfStatistic.put( action, stat );
      }
      if( stat.count() >= 0.00001 ) {
        score = stat.score( statistic.count(), policy );
      }
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
