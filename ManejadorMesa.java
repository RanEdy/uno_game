import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;


import javax.swing.*;

public class ManejadorMesa extends JFrame {
  static Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
  static Dimension winDim = new Dimension(screenDim.width/10 * 9, screenDim.height/10 * 9);
  public PlayerView playerView;


  public ManejadorMesa(PacketData datosIniciales) {
    super("UNO");
    playerView = new PlayerView(datosIniciales);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setResizable(false);
    setSize(screenDim);
    setLayout(new BorderLayout(0,0));

    add(playerView, BorderLayout.CENTER);
    setVisible(true);
  }

}
