package hu.mmatyus.gui;

import java.awt.*;

/**
 * Created by milan on 2016.02.21..
 */
public class ImageButton extends Canvas {
  private Image image;
  private String command;
  private boolean selected = false;
  private Dimension size;

  public ImageButton(Image img, String command) {
    super();
    this.image = img;
    this.command = command;
    if (image == null) {
      size = new Dimension(0, 0);
    } else {
      size = new Dimension(image.getWidth(this), image.getHeight(this));
    }
  }
  public boolean handleEvent(Event e) {
    if (e.id == Event.MOUSE_UP) {
      if (selected) {
        e.id = Event.ACTION_EVENT;
        e.arg = command;
      }
    }
    if (e.id == Event.MOUSE_ENTER) {
      selected = true;
      repaint();
      return true;
    }
    if (e.id == Event.MOUSE_EXIT) {
      selected = false;
      repaint();
      return true;
    }
    return super.handleEvent(e);
  }
  public Dimension minimumSize() {
    return size;
  }
  public void paint(Graphics g) {
    g.drawImage(image, 0, 0, this);
    if (selected) {
      g.setColor(Color.yellow);
      g.drawRect(0, 0, image.getWidth(this)-1, image.getHeight(this)-1);
    }
  }
  public Dimension preferredSize() {
    return minimumSize();
  }
  public void update(Graphics g) {
    paint(g);
  }
}
