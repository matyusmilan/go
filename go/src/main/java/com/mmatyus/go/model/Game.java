package com.mmatyus.go.model;

import java.util.List;

public interface Game<Action> {
  List<Action> actions();

  void take( Action action );

  double eval();
}