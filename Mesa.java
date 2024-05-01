import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Stack;

public class Mesa extends JPanel {

  int CARTAS_INICIALES = 7;

  Stack<Card> pilaTiradas;
  JPanel pilaTiradasPanel;

  Timer animTimer = new Timer(1, null);
  int time = 0;

  public Mesa() {
    super();
    setLayout(null);
    setSize(ManejadorMesa.screenDim);

    LinkedList<Card> baraja = Card.generarBaraja();
    pilaTiradas = new Stack<>();
    LinkedList<Card> cartas = Card.randomCartas(baraja, CARTAS_INICIALES);

    Card referencia = baraja.getLast();
    pilaTiradasPanel = new JPanel(new BorderLayout(0,0));
    pilaTiradasPanel.setBorder(BorderFactory.createRaisedBevelBorder());
    pilaTiradasPanel.setBounds(
      getWidth()/2 -referencia.getWidth()/2,
      getHeight()/2 -referencia.getHeight()/2 -100,
      referencia.getWidth(), 
      referencia.getHeight()
    );

    PlayerDeck playerDeck = new PlayerDeck(cartas);
    playerDeck.setLocation(getWidth()/2 - playerDeck.getWidth()/2, getHeight()-playerDeck.getHeight()-50);
    // Esta funcionalidad deberia agregarse desde el lado del server
    playerDeck.addCardMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        Card carta = (Card) e.getSource();
        Point cartaPos = carta.getLocationOnScreen();

        Card tope = null;
        if(!pilaTiradas.isEmpty())
          tope = pilaTiradas.peek();
        if(pilaTiradas.isEmpty() || carta.isValid(tope)) {
          carta.removeMouseListener(this);
          playerDeck.removeCard(carta);

          Card copia = carta.copy();
          copia.escalar(0.5);
          copia.setJugable(false);

          Point pilaPos = pilaTiradasPanel.getLocation();
          copia.setLocation(cartaPos);
          add(copia);

          toPilaAnimation(copia, cartaPos, pilaPos);

          pilaTiradas.push(copia);
        }
      }
    });

    JButton resetB = new JButton("Reset");
    resetB.setBounds(100, 400, 100, 20);
    resetB.addActionListener((e) -> { 
      playerDeck.reset(Card.randomCartas(baraja, CARTAS_INICIALES));
      repaint();
      pilaTiradasPanel.repaint();
    });

    add(playerDeck);
    add(pilaTiradasPanel);
    add(resetB);
    setVisible(true);
  }

  public void updatePilaTiradas() {
    Card tope = pilaTiradas.peek();
    if(tope != null) {
      pilaTiradasPanel.removeAll();
      pilaTiradasPanel.add(tope, BorderLayout.CENTER);
      tope.updateOriginalPos();
      pilaTiradasPanel.revalidate();
      pilaTiradasPanel.repaint();
    }
  }

  public synchronized void toPilaAnimation(Card card, Point inicio, Point fin) {
    int animDuration = 20;
    if(animTimer != null) {
      animTimer = new Timer(1, (e) -> {
          if(time >= animDuration) {
            ((Timer) e.getSource()).stop();
            time = 0;
            updatePilaTiradas();
            remove(card);
            repaint();
            return;
          }
          card.setLocation(Animacion.ease_in(inicio, fin, (double)time/(double)animDuration));
          time++;
      });
      animTimer.start();
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    
  }
  
}
