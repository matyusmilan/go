package com.mmatyus.go.algorithms.uct;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StatisticTest {

  @Test
  public void testConstuctor() throws Exception {
    //GIVEN
    Statistic statistic;
    //WHEN
    statistic = new Statistic();
    //THEN
    assertTrue( statistic.amount == 0 && statistic.x == 0.0 && statistic.xx == 0.0 );
  }
}
