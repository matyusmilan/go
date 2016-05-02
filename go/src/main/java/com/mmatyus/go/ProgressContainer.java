package com.mmatyus.go;

import javax.swing.event.EventListenerList;

public class ProgressContainer {
  private int               sum;
  private int               actual;
  private EventListenerList listenerList = new EventListenerList();

  public int getSum() {
    return sum;
  }

  public void setSum( int sum ) {
    this.sum = sum;
  }

  public int getActual() {
    return actual;
  }

  public void setActual( int actual ) {
    this.actual = actual;
    String currentProgress = getCurrentProgress();
    fireProgressChanged( new ProgressChagedEvent( this, currentProgress ) );
  }

  public String getCurrentProgress() {
    int result = 0;
    if( sum != 0 ) {
      result = ( actual * 100 / sum );
    }
    return "(" + result + " %)";
  }

  public void reset() {
    sum = 0;
    actual = 0;
  }

  public void addProgressChangedLisener( ProgressChangedLisener l ) {
    listenerList.add( ProgressChangedLisener.class, l );
  }

  public void removeProgressChangedLisener( ProgressChangedLisener l ) {
    listenerList.remove( ProgressChangedLisener.class, l );
  }

  protected void fireProgressChanged( ProgressChagedEvent e ) {
    ProgressChangedLisener[] ls = listenerList.getListeners( ProgressChangedLisener.class );
    for( ProgressChangedLisener l : ls ) {
      l.changed( e );
    }
  }
}
