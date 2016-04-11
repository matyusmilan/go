package hu.mmatyus.model;

public enum BoardType {
  MINI( 5, "5x5", 2, 64F ),
  SMALL( 9, "9x9", 4, 56F ),
  MEDIUM( 13, "13x13", 5, 44F ),
  LARGE( 19, "19x19", 9, 42F );

  public static final int OUT_OF_BOARD = -1; // Special position of neighbor.
  public static final int MAX_NEIGHBORS = 4;
  
  public final int      sideLength;
  public final int      cellCount;
  public final String   label;
  private final int[][] neighborPos;
  public final int      startPoints;
  
  // GUI only
  public final float    posLabelSize;

  BoardType( int side, String lable, int sp, float pls ) {
    this.label = lable;
    this.sideLength = side;
    this.cellCount = side * side;
    this.startPoints = sp;
    this.neighborPos = calcNeighborPositions(this.sideLength);
    
    this.posLabelSize = pls;
  }

  public static BoardType getBySize( int size ){
    BoardType result = null;
    for( BoardType bt : BoardType.values() ) {
      if( bt.sideLength == size ){
        result = bt;
      }
    }
    return result;
  }
  
  public int toPos(int x, int y)
  {
    assert(x >= 0 && x < sideLength );
    assert(y >= 0 && y < sideLength );
    return y * sideLength + x;
  }
  
  public int toX(int pos)
  {
    assert(pos >= 0 && pos < cellCount);
    return pos % sideLength;
  }
  
  public int toY(int pos)
  {
    assert(pos >= 0 && pos < cellCount);
    return pos / sideLength;
  }
  
  public int neighbor(int pos, int index)
  {
    return neighborPos[pos][index];
  }

  private static int toPos(int x, int y, int side)
  {
    assert(x >= 0 && x < side );
    assert(y >= 0 && y < side );
    return y * side + x;
  }

  private static int[][] calcNeighborPositions(int side) {
    final int cellCount = side * side;
    int[][] data = new int[cellCount][4];
    for( int x = 0; x < side; ++x ) {
      for( int y = 0; y < side; ++y ) {
        int position = toPos( x, y, side );
        data[position][0] = ( y == 0 ) ? OUT_OF_BOARD : position - side; // up
        data[position][1] = ( x == side - 1 ) ? OUT_OF_BOARD : position + 1; // right
        data[position][2] = ( y == side - 1 ) ? OUT_OF_BOARD : position + side; // down
        data[position][3] = ( x == 0 ) ? OUT_OF_BOARD : position - 1; // left
      }
    }
    return data;
  }
}
