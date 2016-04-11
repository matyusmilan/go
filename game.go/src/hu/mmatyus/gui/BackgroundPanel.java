package hu.mmatyus.gui;

import java.awt.*;

/**
 * Created by milan on 2016.02.19..
 */
public class BackgroundPanel extends Panel
{
  // The Image to store the background image in.
  Image img;
  public BackgroundPanel()
  {
    // Loads the background image and stores in img object.
    img = Toolkit.getDefaultToolkit().createImage("res/goBackground3b.png");
  }

  public void paint(Graphics g)
  {
    // Draws the img to the BackgroundPanel.
    g.drawImage(img, 0, 0, this.getWidth(),this.getHeight(),this);
  }
}
