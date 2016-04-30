package com.mmatyus.go.model;

public enum GameType {
  HVH( "Human -VS- Human" ),
  HVC( "Human -VS- Robot" ),
  CVH( "Robot -VS- Human" );

  public final String label;

  GameType( String label ) {
    this.label = label;
  }
}
