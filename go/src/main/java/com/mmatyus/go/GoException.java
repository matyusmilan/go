package com.mmatyus.go;

public class GoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public GoException( Throwable cause ) {
    super( cause );
  }
}
