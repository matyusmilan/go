package hu.mmatyus.gui;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.event.KeyEvent.*;

public class MenuStructure {

  public MenuBar crateMenuStructure(){
    MenuBar menuBar = new MenuBar();
    MenuItemListener menuItemListener = new MenuItemListener();

    Menu fileMenu = new Menu( "File" );
    fileMenu.getAccessibleContext().setAccessibleDescription( "Main menu for set up and start a new game!" );
    MenuItem newMenuItem = new MenuItem( "New Game" );
    MenuItem exitMenuItem;
    exitMenuItem = new MenuItem( "Exit", new MenuShortcut(VK_X) );
    exitMenuItem.setActionCommand("Exit");
    exitMenuItem.addActionListener(menuItemListener);
    fileMenu.add( newMenuItem );
    fileMenu.add( exitMenuItem );

    Menu modeMenu = new Menu( "Mode" );
    MenuItem pvpMenuItem = new MenuItem( "Human VS Human" );
    MenuItem pvcMenuItem = new MenuItem( "Human VS MR. Robot" );
    MenuItem cvpMenuItem = new MenuItem( "MR. Robot VS Human" );
    modeMenu.add( pvpMenuItem );
    modeMenu.add( pvcMenuItem );
    modeMenu.add( cvpMenuItem );

    Menu inGameMenu = new Menu( "In Game Action" );
    MenuItem passMenuItem = new MenuItem( "Pass" );
    passMenuItem.setActionCommand("Pass");
    passMenuItem.addActionListener(menuItemListener);

    MenuItem resignMenuItem = new MenuItem( "Resign" );

    inGameMenu.add( passMenuItem );
    inGameMenu.add( resignMenuItem );


    Menu htpMenu = new Menu( "How to play" );
    MenuItem rulesMenuItem = new MenuItem( "Rules" );
    MenuItem tutorialMenuItem = new MenuItem( "Tutorial" );
    MenuItem puzzleMenuItem = new MenuItem( "Puzzle" );
    htpMenu.add( rulesMenuItem );
    htpMenu.add( tutorialMenuItem );
    htpMenu.add( puzzleMenuItem );

    menuBar.add( fileMenu );
    menuBar.add( modeMenu );
    menuBar.add( inGameMenu );
    menuBar.add( htpMenu );
    return menuBar;
  }
}
class MenuItemListener implements ActionListener {
  public void actionPerformed(ActionEvent evt) {
    String what = evt.getActionCommand();

    if (what.equals("Exit"))
      System.exit( 0 );
    if (what.equals("Pass"))
      System.out.println("Passed");
      //board.move(-1);
    }

}

