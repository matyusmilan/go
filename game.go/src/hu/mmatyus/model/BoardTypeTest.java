package hu.mmatyus.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class BoardTypeTest {

  BoardType small  = BoardType.SMALL;
  BoardType large  = BoardType.LARGE;
  BoardType medium = BoardType.MEDIUM;

  @Test
  public void testGetSize() {
    assertTrue(BoardType.getBySize(1 ) == null);
    assertTrue(BoardType.getBySize( -10 ) == null);
    
  }
  
  @Test
  public void testGetSize_9() {
    assertTrue(BoardType.getBySize( 9 ) == small);
  }
  @Test
  public void testGetSize_13() {
    assertTrue(BoardType.getBySize( 13 ) == medium);
  }
  @Test
  public void testGetSize_19() {
    assertTrue(BoardType.getBySize( 19 ) == large);
  }
  @Test
  public void testToPos_NegativeX() {
    assertFalse( small.toPos( -1, 0 ) == 0);
    assertFalse( small.toPos( -1, -1 ) == 0);
    assertFalse( small.toPos( -1, 8) == 0);
    assertFalse( medium.toPos( -1, 0 ) == 0);
    assertFalse( medium.toPos( -1, -1 ) == 0);
    assertFalse( medium.toPos( -1, 12) == 0);
    assertFalse( large.toPos( -1, 0 ) == 0);
    assertFalse( large.toPos( -1, -1 ) == 0);
    assertFalse( large.toPos( -1, 18) == 0);
  }
  
  @Test
  public void testToPos_NegativeY() {
    assertFalse( small.toPos( 0, -1 ) == 0);
    assertFalse( small.toPos( -1, -1 ) == 0);
    assertFalse( small.toPos( 8, -1) == 0);
    assertFalse( medium.toPos( 0, -1 ) == 0);
    assertFalse( medium.toPos( -1, -1 ) == 0);
    assertFalse( medium.toPos( 12, -1) == 0);
    assertFalse( large.toPos( 0, -1 ) == 0);
    assertFalse( large.toPos( -1, -1 ) == 0);
    assertFalse( large.toPos( 18, -1) == 0);
  }
}
