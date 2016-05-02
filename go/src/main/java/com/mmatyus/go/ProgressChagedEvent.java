package com.mmatyus.go;

import java.util.EventObject;

public class ProgressChagedEvent extends EventObject {
  private static final long serialVersionUID = 1L;

  private final String      currentProgress;

  public ProgressChagedEvent( Object source, String currentProgress ) {
    super( source );
    this.currentProgress = currentProgress;
  }

  public String getCurrentProgress() {
    return currentProgress;
  }
}
