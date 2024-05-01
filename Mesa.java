import javax.swing.*;
import java.awt.*;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.event.*;
import java.awt.geom.Point2D;
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
    //pilaTiradasPanel.setOpaque(false);
    pilaTiradasPanel.setBackground(new Color(255,255,255,30));
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

    // Se tiene que hacer esto para repintar el fondo y que se siga viendo transparente (Cosas de Swing xd)
    playerDeck.addCardComponentListener(new ComponentAdapter() {
      @Override
      public void componentMoved(ComponentEvent e) {
        repaint();
        //playerDeck.repaint();
      }
    });

    JButton resetB = new JButton("Reset");
    resetB.setBounds(100, 400, 100, 20);
    resetB.addActionListener((e) -> { 
      playerDeck.reset(Card.randomCartas(baraja, CARTAS_INICIALES));
      repaint();
      pilaTiradasPanel.repaint();
      playerDeck.repaint();
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
    int animDuration = 30;
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
    Graphics2D g2d = (Graphics2D) g;
    // Prueba

    int width = getWidth();
    int height = getHeight();
    Point2D center = new Point2D.Double(width/2, height/2);

    // Gradiente de esquina a esquina
    Color[] colors = { new Color(220, 0, 0, 255), new Color(240, 120, 30, 255)};
    float[] fracs = { 0.3f, 1.0f};
    Point2D esquinaInferiorIzq = new Point2D.Double(0, height);
    Point2D esquinaSuperiorDer = new Point2D.Double(width, 0);
    Point2D esquinaSuperiorIzq = new Point2D.Double(0, 0);
    Point2D esquinaInferiorDer = new Point2D.Double(width, height);

    Point2D esquinaSuperiorDerAumentada = new Point2D.Double(width-center.getX(), -center.getY());

    LinearGradientPaint p = new LinearGradientPaint(esquinaSuperiorDerAumentada, esquinaInferiorIzq, fracs, colors);
    g2d.setPaint(p);
    g2d.fillRect(0, 0, getWidth(), getHeight());

    // Detalles de lineas en el centro
    Color[] colors2 = { new Color(200, 0, 0, 0), new Color(100, 0, 0, 150), new Color(255, 0, 0, 0)};
    float[] fracs2 = { 0.1f, 0.5f, 1.0f};
    LinearGradientPaint p2 = new LinearGradientPaint(esquinaSuperiorIzq, esquinaInferiorDer, fracs2, colors2, CycleMethod.REPEAT);
    LinearGradientPaint p3 = new LinearGradientPaint(esquinaSuperiorDer, esquinaInferiorIzq, fracs2, colors2, CycleMethod.REPEAT);
    g2d.setPaint(p2);
    g2d.fillRect(0, 0, getWidth(), getHeight());
    g2d.setPaint(p3);
    g2d.fillRect(0, 0, getWidth(), getHeight());

    // Detalles de Circulos
    float radius = 150;
    int alpha = 20;
    Point2D circ_centro = new Point2D.Double(center.getX(), center.getY()-80);
    Point2D focus = new Point2D.Double(circ_centro.getX(), circ_centro.getY()+30);
    float[] dist = {0.3f, 0.5f, 1.0f};
    float[] dist2 = {0.3f, 0.7f, 1.0f};
    Color[] colors3 = {new Color(200, 0, 0, alpha), new Color(200, 100, 30, alpha), new Color(250, 0, 0, alpha)};
    Color[] centroColors = { new Color(255, 150, 30, 200), new Color(200, 0, 0, 200), new Color(255, 255, 255, 1)};


    RadialGradientPaint p4 = new RadialGradientPaint(circ_centro, radius, focus, dist, colors3, CycleMethod.REPEAT);
    g2d.setPaint(p4);
    g2d.fillRect(0, 0, getWidth(), getHeight());

    //radius = 100;
    circ_centro = new Point2D.Double(center.getX(), center.getY()-80);
    focus = new Point2D.Double(circ_centro.getX(), circ_centro.getY());
    RadialGradientPaint centroGradiente = new RadialGradientPaint(circ_centro, radius, focus, dist2, centroColors, CycleMethod.NO_CYCLE);
    g2d.setPaint(centroGradiente);
    g2d.fillRect(0, 0, getWidth(), getHeight());


    // Circulos intersectando
    /* 
    circ_centro = new Point2D.Double(0, center.getY());
    focus = new Point2D.Double(circ_centro.getX(), circ_centro.getY());
    radius = 100;
    alpha = 1;

    RadialGradientPaint p5 = new RadialGradientPaint(circ_centro, radius, focus, dist, colors3, CycleMethod.REPEAT);
    g2d.setPaint(p5);
    g2d.fillRect(0, 0, getWidth(), getHeight());

    circ_centro = new Point2D.Double(center.getX(), height);
    focus = new Point2D.Double(circ_centro.getX(), circ_centro.getY());

    RadialGradientPaint p6 = new RadialGradientPaint(circ_centro, radius, focus, dist, colors3, CycleMethod.REPEAT);
    g2d.setPaint(p6);
    g2d.fillRect(0, 0, getWidth(), getHeight());

    circ_centro = new Point2D.Double(width, center.getY());
    focus = new Point2D.Double(circ_centro.getX(), circ_centro.getY());

    RadialGradientPaint p7 = new RadialGradientPaint(circ_centro, radius, focus, dist, colors3, CycleMethod.REPEAT);
    g2d.setPaint(p7);
    g2d.fillRect(0, 0, getWidth(), getHeight());

    circ_centro = new Point2D.Double(center.getX(), 0);
    focus = new Point2D.Double(circ_centro.getX(), circ_centro.getY());

    RadialGradientPaint p8 = new RadialGradientPaint(circ_centro, radius, focus, dist, colors3, CycleMethod.REPEAT);
    g2d.setPaint(p8);
    g2d.fillRect(0, 0, getWidth(), getHeight());
    */

  }
  
}
