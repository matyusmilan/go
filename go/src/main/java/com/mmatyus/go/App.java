package com.mmatyus.go;

import java.awt.FontFormatException;
import java.io.IOException;

public class App {

  private static final GameController GAME_CONTROLLER = new GameController();

  public static void main( String[] args ) throws IOException, FontFormatException, InterruptedException {
    GAME_CONTROLLER.startGame();
  }
}
