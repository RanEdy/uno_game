import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Stack;

import javax.swing.*;

public class ManejadorMesa extends JFrame {
  static Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
  static Dimension winDim = new Dimension(screenDim.width/10 * 9, screenDim.height/10 * 9);


  public ManejadorMesa() {
    super("UNO");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setResizable(false);
    setSize(screenDim);
    setLayout(new BorderLayout(0,0));

    add(new Mesa(), BorderLayout.CENTER);
    setVisible(true);
  }

}
