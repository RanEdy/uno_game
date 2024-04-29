import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.*;

public class ManejadorMesa extends JFrame {

  static Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
  static Dimension winDim = new Dimension(screenDim.width/10 * 9, screenDim.height/10 * 9);

  static int CARTAS_INICIALES = 7;

  public ManejadorMesa() {
    super();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setLayout(null);
    setSize(screenDim);
    setResizable(false);

    LinkedList<Card> baraja = Card.generarBaraja();
    Queue<Card> pilaTiradas = new LinkedList<>();
    LinkedList<Card> cartas = Card.randomCartas(baraja, CARTAS_INICIALES);
    PlayerDeck playerDeck = new PlayerDeck(cartas);
    playerDeck.setLocation(getWidth()/2 - playerDeck.getWidth()/2, getHeight()-playerDeck.getHeight()-50);
    playerDeck.addCardMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        // Aqui agregar la funcionalidad al hacer click a la carta
        Card carta = (Card) e.getSource();
        System.out.println("Carta clickeada > " + carta);
      }
    });

    JButton resetB = new JButton("Reset");
    resetB.setBounds(100, 400, 100, 20);
    resetB.addActionListener((e) -> { playerDeck.reset(Card.randomCartas(baraja, CARTAS_INICIALES));});

    add(playerDeck);
    add(resetB);
    setVisible(true);
  }
}
