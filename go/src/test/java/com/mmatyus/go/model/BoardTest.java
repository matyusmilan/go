package com.mmatyus.go.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith( value = Parameterized.class )
public class BoardTest {

  private Board     testBoard;
  private BoardType boardType;
  private Random    random = new Random();

  public BoardTest( BoardType bt ) {
    this.boardType = bt;
    this.testBoard = new Board( bt );
  }

  @Parameters( name = "{index}:\t boarTpye={0}" )
  public static Iterable<Object[]> data1() {
    return Arrays.asList( //
        new Object[][] {//
        { BoardType.SMALL }, //
            { BoardType.MEDIUM }, //
            { BoardType.LARGE }, //
        } ); //
  }

  public int getLifeOfSingleStone( int pos ) {
    int exceptedLife = -1;
    int size = testBoard.sideLength;
    int bigModulo = ( ( pos % size ) % ( size - 1 ) ) * ( (int) ( pos / size ) % ( size - 1 ) );
    if( bigModulo == 0 ) {
      if( pos == 0 || pos == size - 1 || pos == size * ( size - 1 ) || pos == size * size - 1 ) {
        exceptedLife = 2;
      } else {
        exceptedLife = 3;
      }
    } else {
      exceptedLife = 4;
    }
    return exceptedLife;
  }

  //EMPTY_BOARD
  @Test
  public void testDefaultKomi_EB() {
    //GIVEN
    //WHEN
    //THEN
    assertTrue( testBoard.komi == Board.DEFAULT_KOMI );
  }

  @Test
  public void testCellCount_EB() {
    //GIVEN
    //WHEN
    //THEN
    assertEquals( testBoard.sideLength * testBoard.sideLength, testBoard.cellCount );
  }

  @Test
  public void testEmptyCells_EB() {
    //GIVEN
    //WHEN
    //THEN
    assertEquals( testBoard.getnumberOfEmptyCells(), testBoard.cellCount );
  }

  // All position on empty table are available and there is a pass move!
  @Test
  public void testAvailableActions_EB() {
    //GIVEN
    //WHEN
    //THEN
    assertEquals( testBoard.availableActions().size(), testBoard.cellCount + 1 );
  }

  @Test
  public void testLifeCount_EB() {
    //GIVEN
    //WHEN
    //THEN
    assertEquals( testBoard.getLifeOfShape( random.nextInt( testBoard.cellCount ) ), 0 );
  }

  @Test
  public void testNextPlayer_EB() {
    //GIVEN
    //WHEN
    //THEN
    assertTrue( testBoard.getNextPlayer() == Board.BLACK );
  }

  @Test
  public void testTheOtherColor_EB() {
    //GIVEN
    //WHEN
    //THEN
    assertTrue( Board.theOtherColor( Board.BLACK ) == Board.WHITE );
  }

  @Test
  public void testLastPos_EB() {
    //GIVEN
    //WHEN
    //THEN
    assertTrue( testBoard.lastMove == Board.NO_LAST_POS );
  }

  @Test
  public void testKoPosition_EB() {
    //GIVEN
    //WHEN
    //THEN
    assertTrue( testBoard.koPos == Board.NO_KO_POS );
  }

  @Test
  public void testEmpties_EB() {
    //GIVEN
    BitSet empties = new BitSet( boardType.cellCount );
    //WHEN
    empties.set( 0, boardType.cellCount );
    //THEN
    assertTrue( testBoard.emptyCells.equals( empties ) );
  }

  @Test
  public void testNumberOfShapesInAtari_EB() {
    //GIVEN
    //WHEN
    //THEN
    assertTrue( testBoard.numberOfShapesInAtari == 0 );
  }

  // TAKE A MOVE

  @Test
  public void testNumberOfShapesInAtari_TAM() {
    //GIVEN
    int pos = random.nextInt( boardType.cellCount );
    //WHEN
    testBoard.move( pos );
    //THEN
    assertTrue( testBoard.numberOfShapesInAtari == 0 );
  }

  @Test
  public void testLastPos_TAM() {
    //GIVEN
    int pos = random.nextInt( boardType.cellCount );
    //WHEN
    testBoard.move( pos );
    //THEN
    assertTrue( testBoard.lastMove == pos );
  }

  @Test
  public void testKoPosition_TAM() {
    //GIVEN
    int pos = random.nextInt( boardType.cellCount );
    //WHEN
    testBoard.move( pos );
    //THEN
    assertTrue( testBoard.koPos == Board.NO_KO_POS );
  }

  @Test
  public void testNextPlayer_TAM() {
    //GIVEN
    int pos = random.nextInt( boardType.cellCount );
    //WHEN
    testBoard.move( pos );
    //THEN
    assertTrue( testBoard.getNextPlayer() == Board.WHITE );
  }

  @Test
  public void testAvailableActions_TAM() {
    //GIVEN
    int pos = random.nextInt( boardType.cellCount );
    int amountOfValidPositions = testBoard.availableActions().size(); // PASS MOVE
    //WHEN
    testBoard.move( pos );
    //THEN
    assertEquals( testBoard.availableActions().size(), amountOfValidPositions - 1 );
  }

  @Test
  public void testLifeCount_TAM() {
    //GIVEN
    int pos = random.nextInt( boardType.cellCount );
    //WHEN
    testBoard.move( pos );
    //THEN
    assertEquals( getLifeOfSingleStone( pos ), testBoard.getLifeOfShape( pos ) );
  }

  @Test
  public void testEmptyCells_TAM() {
    //GIVEN
    int pos = random.nextInt( boardType.cellCount );
    //WHEN
    testBoard.move( pos );
    //THEN
    assertEquals( testBoard.cellCount - 1, testBoard.getnumberOfEmptyCells() );
  }

  // GAME OVER
  @Test
  public void testDoulblePass_GO() {
    //GIVEN
    //WHEN
    testBoard.move( Board.PASS_MOVE );
    testBoard.move( Board.PASS_MOVE );
    //THEN
    assertTrue( testBoard.isGameOver() );
  }

  @Test
  public void testBlackResign_GO() {
    //GIVEN
    //WHEN
    testBoard.move( Board.RESIGN_MOVE );
    //THEN
    assertTrue( testBoard.isGameOver() );
  }

  @Test
  public void testPassResign_GO() {
    //GIVEN
    //WHEN
    testBoard.move( Board.PASS_MOVE );
    testBoard.move( Board.RESIGN_MOVE );
    //THEN
    assertTrue( testBoard.isGameOver() );
  }

  @Test
  public void testMoveResign_GO() {
    //GIVEN
    int pos = random.nextInt( boardType.cellCount );
    //WHEN
    testBoard.move( pos );
    testBoard.move( Board.PASS_MOVE );
    testBoard.move( Board.RESIGN_MOVE );
    //THEN
    assertTrue( testBoard.isGameOver() );
  }

  @Test
  public void testFullBoard() {
    //GIVEN
    //WHEN
    for( int i = 0; i < boardType.cellCount - 1; i += 2 ) {
      testBoard.move( i );
      testBoard.move( i + 1 );
    }
    //THEN
    assertTrue( testBoard.availableActions().get( 0 ) == Board.PASS_MOVE && testBoard.availableActions().size() == 1 );
  }

  public void takeMoveSequence( List<Integer> moveList ) {
    for( int m : moveList ) {
      testBoard.move( m );
    }
  }

  // Atari
  @Test
  public void testAtari1() {
    //GIVEN
    List<Integer> moveList = new ArrayList<Integer>();
    int center = ( boardType.cellCount - 1 ) / 2;
    moveList.add( center );
    moveList.add( boardType.neighbor( center, 0 ) );
    moveList.add( 0 );
    moveList.add( boardType.neighbor( center, 1 ) );
    moveList.add( 1 );
    moveList.add( boardType.neighbor( center, 2 ) );
    //WHEN
    takeMoveSequence( moveList );
    //THEN
    assertTrue( testBoard.shapesInAtari.get( center ) );
  }

  // Atari
  @Test
  public void testAtari2() {
    //GIVEN
    List<Integer> moveList = new ArrayList<Integer>();
    int center = ( boardType.cellCount - 1 ) / 2;
    moveList.add( center );
    moveList.add( boardType.neighbor( center, 0 ) );
    moveList.add( 0 );
    moveList.add( boardType.neighbor( center, 1 ) );
    moveList.add( 1 );
    moveList.add( boardType.neighbor( center, 2 ) );
    //WHEN
    takeMoveSequence( moveList );
    //THEN
    assertTrue( testBoard.shapesInAtari.cardinality() == 1 );
  }

  // Atari
  @Test
  public void testAtari3() {
    //GIVEN
    List<Integer> moveList = new ArrayList<Integer>();
    int center = ( boardType.cellCount - 1 ) / 2;
    moveList.add( center );
    moveList.add( boardType.neighbor( center, 0 ) );
    moveList.add( 0 );
    moveList.add( boardType.neighbor( center, 1 ) );
    moveList.add( 1 );
    moveList.add( boardType.neighbor( center, 2 ) );
    //WHEN
    takeMoveSequence( moveList );
    //THEN
    assertTrue( testBoard.numberOfShapesInAtari == 1 );
  }

  @Test
  public void testLifes1() {
    //GIVEN
    List<Integer> moveList = new ArrayList<Integer>();
    int center = ( boardType.cellCount - 1 ) / 2;
    moveList.add( center );
    moveList.add( boardType.neighbor( center, 0 ) );
    moveList.add( 0 );
    moveList.add( boardType.neighbor( center, 1 ) );
    moveList.add( 1 );
    moveList.add( boardType.neighbor( center, 2 ) );
    //WHEN
    takeMoveSequence( moveList );
    //THEN
    assertTrue( testBoard.lives[testBoard.shapeAtPos[0]].cardinality() == 3 );
  }

  @Test
  public void testLifes2() {
    //GIVEN
    List<Integer> moveList = new ArrayList<Integer>();
    int center = ( boardType.cellCount - 1 ) / 2;
    moveList.add( center );
    moveList.add( boardType.neighbor( center, 0 ) );
    moveList.add( 0 );
    moveList.add( boardType.neighbor( center, 1 ) );
    moveList.add( 1 );
    moveList.add( boardType.neighbor( center, 2 ) );
    //WHEN
    takeMoveSequence( moveList );
    //THEN
    assertTrue( testBoard.lives[testBoard.shapeAtPos[center]].cardinality() == 1 );
  }

  @Test
  public void testLifes3() {
    //GIVEN
    List<Integer> moveList = new ArrayList<Integer>();
    int center = ( boardType.cellCount - 1 ) / 2;
    moveList.add( center );
    moveList.add( boardType.neighbor( center, 0 ) );
    moveList.add( 0 );
    moveList.add( boardType.neighbor( center, 1 ) );
    moveList.add( 1 );
    moveList.add( boardType.neighbor( center, 2 ) );
    //WHEN
    takeMoveSequence( moveList );
    //THEN
    assertTrue( testBoard.numberOfLifes[testBoard.shapeAtPos[center]] == 1 );
  }

  @Test
  public void testEmpty1() {
    //GIVEN
    List<Integer> moveList = new ArrayList<Integer>();
    int center = ( boardType.cellCount - 1 ) / 2;
    moveList.add( center );
    moveList.add( boardType.neighbor( center, 0 ) );
    moveList.add( 0 );
    moveList.add( boardType.neighbor( center, 1 ) );
    moveList.add( 1 );
    moveList.add( boardType.neighbor( center, 2 ) );
    //WHEN
    takeMoveSequence( moveList );
    //THEN
    assertTrue( testBoard.emptyCells.cardinality() == boardType.cellCount - moveList.size() );
  }

  @Test
  public void testEmpty2() {
    //GIVEN
    List<Integer> moveList = new ArrayList<Integer>();
    int center = ( boardType.cellCount - 1 ) / 2;
    moveList.add( center );
    moveList.add( boardType.neighbor( center, 0 ) );
    moveList.add( 0 );
    moveList.add( boardType.neighbor( center, 1 ) );
    moveList.add( 1 );
    moveList.add( boardType.neighbor( center, 2 ) );
    //WHEN
    takeMoveSequence( moveList );
    //THEN
    assertTrue( testBoard.numberOfEmptyCells == boardType.cellCount - moveList.size() );
  }

  @Test
  public void testEmpty3() {
    //GIVEN
    BitSet notEmpty = new BitSet( boardType.cellCount );
    notEmpty.clear();
    List<Integer> moveList = new ArrayList<Integer>();
    int center = ( boardType.cellCount - 1 ) / 2;
    moveList.add( center );
    notEmpty.set( center );
    moveList.add( boardType.neighbor( center, 0 ) );
    notEmpty.set( boardType.neighbor( center, 0 ) );
    moveList.add( 0 );
    notEmpty.set( 0 );
    moveList.add( boardType.neighbor( center, 1 ) );
    notEmpty.set( boardType.neighbor( center, 1 ) );
    moveList.add( 1 );
    notEmpty.set( 1 );
    moveList.add( boardType.neighbor( center, 2 ) );
    notEmpty.set( boardType.neighbor( center, 2 ) );
    //WHEN
    takeMoveSequence( moveList );
    //THEN
    notEmpty.or( testBoard.emptyCells );
    assertTrue( notEmpty.cardinality() == boardType.cellCount );
  }
}
