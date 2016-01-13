package hu.mmatyus.model;

public enum BoardType {
  SMALL( 9, "9x9", 4, 56F ),
  MEDIUM( 13, "13x13", 5, 44F ),
  LARGE( 19, "19x19", 9, 42F );

  public int    size;
  public String label;
  public int    startPoints;
  public float  posLabelSize;

  BoardType( int s, String l, int sp, float pls ) {
    this.label = l;
    this.size = s;
    this.startPoints = sp;
    this.posLabelSize = pls;
  }
}
