import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class PlayerView extends JPanel {
  private int turno;
  public int turnoGlobal;

  private Card topeDePilaDeTiradas;

  private ArrayList<Integer> numCartasJugadores = new ArrayList<>();
  private ArrayList<String> nombresJugadores;
  
  // Componentes visuales importantes
  private JPanel pilaTiradasPanel;
  private JPanel infoBottomPanel;
  private JPanel midPanel;
  private JLabel nombreTurnoActual;
  private JButton unoButton;

  private PlayerDeck playerDeck;

  // Variables para las animaciones
  private Timer animTimer = new Timer(1, null);
  private int time = 0;

  // Variables para pintar el fondo
  private ImageIcon fondo = new ImageIcon("iconos/bg1.png");
  private ImageIcon fondoEscalado = Card.generarImagen(fondo, ManejadorMesa.screenDim.width, ManejadorMesa.screenDim.height);

  private int imgCartasSize = ManejadorMesa.screenDim.width/5;
  private ImageIcon imgCartas[][] = new ImageIcon[3][5];

  public PlayerView(PacketData datosIniciales) {
    super();
    setLayout(null); // utilizar setBounds para los componentes agregados
    setSize(ManejadorMesa.screenDim);

    cargarImagenes();

    // Asignar la informacion del paquete
    nombresJugadores = datosIniciales.apodosJugadores;
    
    for(int i = 0; i < nombresJugadores.size(); i++) {
      // Encuentra el indice de tu nombre y te lo coloca como tu turno
      if(datosIniciales.nombre.equals(nombresJugadores.get(i)))
        turno = i;
      else
        numCartasJugadores.add(datosIniciales.globalNumCartas.get(i));
    }

    turnoGlobal = datosIniciales.turno;

    System.out.println(" ------------------- Info ----------------");
    System.out.println("Jugadores: " + nombresJugadores.size());
    System.out.println("Cartas Jugadores: " + numCartasJugadores);
    System.out.println("Numero de tu turno: " + turno);
    System.out.println(" --------------------------------------------");

    LinkedList<Card> cartas = datosIniciales.barajaCartas;

    // esto solo es para colocar el panel de la carta tope en el medio de la pantalla
    Card referencia = new Card(new CardColor(CardColor.BLACK), CardType.ONE);
    referencia.escalar(0.7);
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
    playerDeck.setLocation(getWidth()/2 - playerDeck.getWidth()/2, getHeight()-playerDeck.getHeight()-90);

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

    initInfoBottomPanel();
    
    setVisible(true);
  }

  private void initInfoBottomPanel() {
    Font font = new Font("SansSerif", Font.BOLD, 25);
    infoBottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
    infoBottomPanel.setSize(getWidth(), 50);
    infoBottomPanel.setOpaque(false);
    infoBottomPanel.setLocation(0, getHeight()-70);

    unoButton = new JButton("UNO");
    unoButton.setBackground(Color.ORANGE);
    unoButton.setFont(font);

    nombreTurnoActual = new JLabel("");
    nombreTurnoActual.setFont(font);
    actualizarNombreTurnoActual();

    infoBottomPanel.add(unoButton);
    infoBottomPanel.add(nombreTurnoActual);

    add(infoBottomPanel);
  }

  private void actualizarNombreTurnoActual() {
    nombreTurnoActual.setText("TURNO: " + nombresJugadores.get(turnoGlobal));
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
      Card copia = cartaSeleccionada.copy(true);
      copia.setJugable(false);

      Point pilaPos = pilaTiradasPanel.getLocation();
      copia.setLocation(cartaPos);

      add(copia);
      toPilaAnimation(copia, cartaPos, pilaPos);
      cartaSeleccionada.removeMouseListener(listenerCarta);
      turno = -1; // esto se hace para que el jugador no pueda realizar varias acciones en su turno

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

  private void cargarImagenes() {
    for(int y = 0; y < 3; y++) {
      for(int x = 0; x < 5; x++) {
        imgCartas[y][x] = Card.generarImagen(new ImageIcon("iconos/" + (x+1) + "cards" + y + ".png"), imgCartasSize, imgCartasSize);
      }
    }
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    // Pintar el Fondo
    Graphics2D g2d = (Graphics2D) g;
    g.drawImage(fondoEscalado.getImage(), 0, 0, ManejadorMesa.screenDim.width, ManejadorMesa.screenDim.height, null);

    int offset = 50;
    Point[] posicionesImagenesCartas = {
      new Point(-offset, getHeight()/2 - imgCartasSize/2), 
      new Point(getWidth()/2 - imgCartasSize/2, -offset), 
      new Point(getWidth() - imgCartasSize + offset, getHeight()/2 - imgCartasSize/2)
    };

    if(numCartasJugadores != null)
      if(numCartasJugadores.size() > 0)
        for(int i = 0; i < numCartasJugadores.size(); i++) {
          int numCarta = numCartasJugadores.get(i);
          int indexCarta;
          if(numCarta > 5)
            indexCarta = 4;
          else
            indexCarta = numCarta -1;
          g.drawImage(
            imgCartas[i][indexCarta].getImage(), // imagen
            posicionesImagenesCartas[i].x, // pos x
            posicionesImagenesCartas[i].y, // pos y
            imgCartasSize, // ancho
            imgCartasSize, // alto
            null // Observer
          );
          if(numCarta > 5) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.BLACK);
            g2d.fillOval(
              posicionesImagenesCartas[i].x + imgCartasSize/2 - 50,
              posicionesImagenesCartas[i].y + imgCartasSize/2 - 50,
              100,
              100
              );
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Consolas", Font.BOLD, 80));
            g2d.drawString(
              ""+numCarta,
              posicionesImagenesCartas[i].x + imgCartasSize/2 - 25,
              posicionesImagenesCartas[i].y + imgCartasSize/2 
              );
          }
        }
  }

}
