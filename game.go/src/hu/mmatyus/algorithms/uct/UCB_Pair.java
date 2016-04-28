package hu.mmatyus.algorithms.uct;

public class UCB_Pair<Action> {
  protected UCB<Action> primary;
  protected UCB<Action> secondary;

  public UCB_Pair( UCB<Action> primary, UCB<Action> secondary ) {
    this.primary = primary;
    this.secondary = secondary;
  }

  public UCB<Action> getPrimary() {
    return primary;
  }

  public UCB_Pair<Action> getComplementer() {
    return new UCB_Pair<Action>( secondary, primary );
  }

  public void multiply( double alpha ) {
    primary.multiply( alpha );
    secondary.multiply( alpha );
  }

}
