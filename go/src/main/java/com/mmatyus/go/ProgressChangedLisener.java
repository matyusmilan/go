package com.mmatyus.go;

import java.util.EventListener;

public interface ProgressChangedLisener extends EventListener {
  void changed( ProgressChagedEvent e );
}
