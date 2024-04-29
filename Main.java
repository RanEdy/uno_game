import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.LinkedList;

import javax.swing.*;

public class Main extends JFrame {

  static Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
  static Dimension winDim = new Dimension(screenDim.width/10 * 9, screenDim.height/10 * 9);

  static int CARTAS_INICIALES = 7;

  public Main() {
    super();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setLayout(null);
    setSize(screenDim);
    setResizable(false);

    LinkedList<Card> baraja = Card.generarBaraja();
    LinkedList<Card> cartas = Card.randomCartas(baraja, CARTAS_INICIALES);
    PlayerDeck playerDeck = new PlayerDeck(cartas);
    playerDeck.setLocation(getWidth()/2 - playerDeck.getWidth()/2, getHeight()-playerDeck.getHeight()-50);

    JButton resetB = new JButton("Reset");
    resetB.setBounds(100, 400, 100, 20);
    resetB.addActionListener((e) -> { playerDeck.reset(Card.randomCartas(baraja, CARTAS_INICIALES));});

    add(playerDeck);
    add(resetB);
    setVisible(true);
    setFocusable(true);
    requestFocus();
  }

  public static void main(String[] args) {
    new Main();
  }

}
