import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.*;
import java.awt.*;

public class PlayerDeck extends JPanel {
  private LinkedList<Card> cartasLista;
  private LinkedList<ArrayList<Card>> cartasDistribuidas;
  private int index = -1;

  private JPanel deckPanel;
  private JPanel botonesPanel;

  private JButton izq, der;
  private JButton ordenarColor, ordernarTipo;
  private Font miniFont = new Font("Consolas", Font.BOLD, 12);

  private Audio ordenarFx;


  public PlayerDeck(LinkedList<Card> cartasIniciales) {
    super();
    setLayout(new BorderLayout(0,0));
    setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
    setSize(600, 220);

    deckPanel = new JPanel(null);
    deckPanel.setPreferredSize(new Dimension(600, 200));
    deckPanel.setBackground(Color.GRAY);

    botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    botonesPanel.setSize(getWidth(), 20);
    botonesPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));

    izq = new JButton("<");
    izq.addActionListener((e) -> { displayPrev5(); });
    der = new JButton(">");
    der.addActionListener((e) -> { displayNext5(); });

    ordenarColor = new JButton("C");
    ordenarColor.setFont(miniFont);
    ordenarColor.addActionListener((e) -> { 
      if(ordenarFx != null)
        ordenarFx.stop();
      ordenarFx = new Audio("sfx/swap1.wav");
      ordenarFx.play();
      ordenarColor(); 
    });

    ordernarTipo = new JButton("T");
    ordernarTipo.setFont(miniFont);
    ordernarTipo.addActionListener((e) -> { 
      if(ordenarFx != null)
        ordenarFx.stop();
      ordenarFx = new Audio("sfx/swap1.wav");
      ordenarFx.play();
      ordenarTipo();
    });

    botonesPanel.add(ordenarColor);
    botonesPanel.add(ordernarTipo);

    cartasLista = cartasIniciales;
    cartasDistribuidas = new LinkedList<>();

    add(deckPanel, BorderLayout.CENTER);
    add(izq, BorderLayout.WEST);
    add(der, BorderLayout.EAST);
    add(botonesPanel, BorderLayout.NORTH);
    distribuir();
    displayNext5();
  }

  public void reset(LinkedList<Card> nuevasCartas) {
    cartasDistribuidas.clear();
    cartasLista.clear();
    cartasLista = nuevasCartas;
    distribuir();
    index = -1;
    displayNext5();
  }

  private void update() {
    deckPanel.removeAll();
    int x = 30;
    for(Card card : cartasDistribuidas.get(index)) {
      card.setBounds(x, 50, card.getWidth(), card.getHeight());
      card.updateOriginalPos();
      deckPanel.add(card);
      x += card.getWidth() + 15;
    }
    deckPanel.revalidate();
    deckPanel.repaint();

  }

  private void distribuir() {
    cartasDistribuidas.clear();
    ArrayList<Card> buffer5 = new ArrayList<>();
    for(int i = 0; i < cartasLista.size(); i++) {
      buffer5.add(cartasLista.get(i));
      if(((i+1) % 5 == 0 && i != 0) || (i+1) == cartasLista.size()) {
        cartasDistribuidas.add((ArrayList<Card>)buffer5.clone());
        buffer5.clear();
      }
    }
  }

  private void displayNext5() {
    index = (index + 1) % cartasDistribuidas.size();
    update();
  }

  private void displayPrev5() {
    index--;
    if(index < 0)
      index = cartasDistribuidas.size()-1;
    else
      index = index % cartasDistribuidas.size();
    update();
  }

  private void ordenarColor() {
    Collections.sort(cartasLista, Card.getColorComparator());
    distribuir();
    update();
  }

  private void ordenarTipo() {
    Collections.sort(cartasLista, Card.getTypeComparator());
    distribuir();
    update();
  }

}
