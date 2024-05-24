import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.*;

public class PanelDebug extends JFrame {

  private JPanel panelBotones, panelControles;
  private JTextField turnoNum;

  public PanelDebug(Server servidor) {
    super("Panel Debug");
    setSize(500, 600);
    setLayout(new BorderLayout());

    panelControles = new JPanel(new FlowLayout());
    JLabel turnoL = new JLabel("Turno");
    turnoNum = new JTextField(20);
    panelControles.add(turnoL);
    panelControles.add(turnoNum);

    panelBotones = new JPanel();
    panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));

    add(panelControles, BorderLayout.NORTH);
    add(panelBotones, BorderLayout.CENTER);
    setVisible(true);
  }
  
}
