import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

public class PlayerView extends JPanel {
  private int turno;
  public int turnoGlobal;

  private Card topeDePilaDeTiradas;

  private int[] numCartasJugadores;
  
  // Componentes visuales importantes
  private JPanel pilaTiradasPanel;
  private JPanel infoBottomPanel;
  private JPanel midPanel;
  private JLabel nombresJugadores;

  private PlayerDeck playerDeck;

  // Variables para las animaciones
  private Timer animTimer = new Timer(1, null);
  private int time = 0;

  // Variables para pintar el fondo
  private ImageIcon fondo = new ImageIcon("iconos/bg1.png");
  private ImageIcon fondoEscalado = Card.generarImagen(fondo, ManejadorMesa.screenDim.width, ManejadorMesa.screenDim.height);

  public PlayerView(PacketData datosIniciales) {
    super();
    setLayout(null);
    setSize(ManejadorMesa.screenDim);

    // Asignar la informacion del paquete
    for(int i = 0; i < datosIniciales.apodosJugadores.size(); i++) {
      // Encuentra el indice de tu nombre y te lo coloca como tu turno
      if(datosIniciales.nombre.equals(datosIniciales.apodosJugadores.get(i)))
        turno = i;
    }

    turnoGlobal = datosIniciales.turno;

    numCartasJugadores = datosIniciales.globalNumCartas;

    System.out.println("Jugadores: " + datosIniciales.apodosJugadores.size());
    System.out.println("Numero de tu turno: " + turno);

    LinkedList<Card> cartas = datosIniciales.barajaCartas;

    // esto solo es para colocar el panel de la carta tope en el medio de la pantalla
    Card referencia = cartas.getLast();
    pilaTiradasPanel = new JPanel(new BorderLayout(0,0));
    pilaTiradasPanel.setBorder(BorderFactory.createRaisedBevelBorder());
    //pilaTiradasPanel.setOpaque(false);
    pilaTiradasPanel.setBackground(new Color(255,255,255,30));
    pilaTiradasPanel.setBounds(
      getWidth()/2 -referencia.getWidth()/2,
      getHeight()/2 -referencia.getHeight()/2,
      referencia.getWidth(), 
      referencia.getHeight()
    );
    setTope(datosIniciales.cartaInicial);
    //-------------------------------------------------------------------------------------------

    playerDeck = new PlayerDeck(cartas);
    playerDeck.setLocation(getWidth()/2 - playerDeck.getWidth()/2, getHeight()-playerDeck.getHeight()-50);

    // Se tiene que hacer esto para repintar el fondo y que se siga viendo transparente (Cosas de Swing xd)
    playerDeck.addCardComponentListener(new ComponentAdapter() {
      @Override
      public void componentMoved(ComponentEvent e) {
        repaint();
        //playerDeck.repaint();
      }
    });


    add(playerDeck);
    add(pilaTiradasPanel);
    
    setVisible(true);
  }

  private void initInfoBottomPanel() {
    infoBottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    infoBottomPanel.setBackground(Color.BLUE);

  }

  public void setTope(Card tope) {
    if(tope != null) {
      topeDePilaDeTiradas = tope;
      pilaTiradasPanel.removeAll();
      pilaTiradasPanel.add(tope, BorderLayout.CENTER);
      topeDePilaDeTiradas.updateOriginalPos();
      pilaTiradasPanel.revalidate();
      pilaTiradasPanel.repaint();
    }
  }

  public int getTurno() { return turno; }

  public PlayerDeck getPlayerDeck() { return playerDeck; }
// -------------------------------------------------------- Metodos de Acciones -------------------------------------------------------
  public void actionTirarCartaComun(Card cartaSeleccionada, MouseListener listenerCarta) {
    Point cartaPos = cartaSeleccionada.getLocationOnScreen();
    if(cartaSeleccionada.isValid(topeDePilaDeTiradas)) {

      playerDeck.removeCard(cartaSeleccionada);
      Card copia = cartaSeleccionada.copy();
      copia.setJugable(false);

      Point pilaPos = pilaTiradasPanel.getLocation();
      copia.setLocation(cartaPos);

      // Esto solo hacerlo si el servidor responde
      add(copia);
      toPilaAnimation(copia, cartaPos, pilaPos);
      cartaSeleccionada.removeMouseListener(listenerCarta);

      // Push al server de tirar carta
    }
  }

  public void actionTirarCartaEspecial(Card cartaSeleccionada, MouseListener listenerCarta) {

  }
// ---------------------------------------------------------------------------------------------------------------------------------------
  public synchronized void toPilaAnimation(Card card, Point inicio, Point fin) {
    int animDuration = 30;
    setComponentZOrder(card, 0);
    if(animTimer != null) {
      animTimer = new Timer(1, (e) -> {
          if(time >= animDuration) {
            ((Timer) e.getSource()).stop();
            time = 0;
            setTope(card);
            //remove(card);
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
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawImage(fondoEscalado.getImage(), 0, 0, ManejadorMesa.screenDim.width, ManejadorMesa.screenDim.height, null);
  }

}
