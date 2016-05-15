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

  @Test
  public void testAdd1() throws Exception {
    //GIVEN
    Statistic statistic;
    //WHEN
    statistic = new Statistic();
    statistic.add( 1.0 );
    //THEN
    assertTrue( statistic.amount == 1 && statistic.x == 1.0 && statistic.xx == 1.0 );
  }

  @Test
  public void testAdd2() throws Exception {
    //GIVEN
    Statistic statistic;
    //WHEN
    statistic = new Statistic();
    statistic.add( 1.0 );
    statistic.add( 2.0 );
    //THEN
    assertTrue( statistic.amount == 2 && statistic.x == 3.0 && statistic.xx == 5.0 );
  }

  @Test
  public void testAdd3() throws Exception {
    //GIVEN
    Statistic statistic;
    //WHEN
    statistic = new Statistic();
    statistic.add( 1.0 );
    statistic.add( 2.0 );
    statistic.add( 3.0 );
    //THEN
    assertTrue( statistic.amount == 3.0 && statistic.x == 6.0 && statistic.xx == 14.0 );
  }

  @Test
  public void testMean1() throws Exception {
    //GIVEN
    Statistic statistic;
    //WHEN
    statistic = new Statistic();
    statistic.add( 1.0 );
    //THEN
    assertTrue( statistic.mean() == 1.0 );
  }

  @Test
  public void testMean2() throws Exception {
    //GIVEN
    Statistic statistic;
    //WHEN
    statistic = new Statistic();
    statistic.add( 1.0 );
    statistic.add( 2.0 );
    //THEN
    assertTrue( statistic.mean() == 1.5 );
  }

  @Test
  public void testMean3() throws Exception {
    //GIVEN
    Statistic statistic;
    //WHEN
    statistic = new Statistic();
    statistic.add( 1.0 );
    statistic.add( 2.0 );
    statistic.add( 3.0 );
    //THEN
    assertTrue( statistic.mean() == 2.0 );
  }

  @Test
  public void testScore1() throws Exception {
    //GIVEN
    Statistic statistic;
    //WHEN
    statistic = new Statistic();
    statistic.add( 1.0 );
    //THEN
    assertTrue( statistic.score( Math.pow( Math.E, 2.0 ) ) == 1.0 + 2.0 );
  }

  @Test
  public void testScore2() throws Exception {
    //GIVEN
    Statistic statistic;
    //WHEN
    statistic = new Statistic();
    statistic.add( 1.0 );
    statistic.add( 2.0 );
    //THEN
    assertTrue( statistic.score( Math.pow( Math.E, 4.0 ) ) == 1.5 + 2.0 );
  }

  @Test
  public void testScore3() throws Exception {
    //GIVEN
    Statistic statistic;
    //WHEN
    statistic = new Statistic();
    statistic.add( 1.0 );
    statistic.add( 2.0 );
    statistic.add( 3.0 );
    //THEN
    assertTrue( statistic.score( Math.pow( Math.E, 6.0 ) ) == 2.0 + 2.0 );
  }
}
