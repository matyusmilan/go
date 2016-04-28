package hu.mmatyus.algorithms.uct;

import static org.junit.Assert.*;

import org.junit.Test;

public class StatisticTest {

  @Test
  public void test() {
    final double DELTA = 0.0001;
    Statistic s0 = new Statistic();
    assertEquals( 0, s0.amount );
    assertEquals( 0.0, s0.x, DELTA );
    assertEquals( 0.0, s0.xx, DELTA );
  }

}
