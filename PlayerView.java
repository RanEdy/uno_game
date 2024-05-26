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
  private int direccion;
  private String username;

  private Card topeDePilaDeTiradas;

  private ArrayList<Integer> numCartasJugadores = new ArrayList<>();
  private ArrayList<String> nombresJugadoresGlobal = new ArrayList<>();
  private ArrayList<String> nombresJugadoresLocal = new ArrayList<>();
  
  // Componentes visuales importantes
  private JPanel pilaTiradasPanel;
  private JPanel infoBottomPanel;
  private JPanel midPanel;
  private JLabel nombreTurnoActual;
  private JButton unoButton;
  private JLabel barajaSobrante;

  private JButton botonesColor[] = new JButton[4];

  private PlayerDeck playerDeck;

  // Variables para las animaciones
  private Timer animTimer = new Timer(1, null);
  private int time = 0;

  // Variables para pintar el fondo
  private ImageIcon fondo = new ImageIcon("iconos/bg1.png");
  private ImageIcon fondoEscalado = Card.generarImagen(fondo, ManejadorMesa.screenDim.width, ManejadorMesa.screenDim.height);

  private int imgBarajaSobranteSize = ManejadorMesa.screenDim.width/11;
  private ImageIcon imgBarajaSobrante = Card.generarImagen(new ImageIcon("iconos/barajasobrante.png"), imgBarajaSobranteSize*11/10, imgBarajaSobranteSize-15);

  private int imgDireccionSize = ManejadorMesa.screenDim.height/3;
  private ImageIcon imgDireccion1 = Card.generarImagen(new ImageIcon("iconos/direction1.png"), imgDireccionSize, imgDireccionSize);
  private ImageIcon imgDireccion2 = Card.generarImagen(new ImageIcon("iconos/direction-1.png"), imgDireccionSize, imgDireccionSize);

  private int imgCartasSize = ManejadorMesa.screenDim.width/5;
  private ImageIcon imgCartas[][] = new ImageIcon[3][5];

  public PlayerView(PacketData datosIniciales) {
    super();
    setLayout(null); // utilizar setBounds para los componentes agregados
    setSize(ManejadorMesa.screenDim);

    cargarImagenes();

    // Asignar la informacion del paquete
    nombresJugadoresGlobal = datosIniciales.apodosJugadores;
    for(int i = 0; i < datosIniciales.apodosJugadores.size(); i++) {
      // Encuentra el indice de tu nombre y te lo coloca como tu turno
      if(datosIniciales.nombre.equals(datosIniciales.apodosJugadores.get(i)))
        turno = i;
      // Si no es tu nombre se agregan la informacion de los otros
      else {
        numCartasJugadores.add(datosIniciales.globalNumCartas.get(i));
        nombresJugadoresLocal.add(datosIniciales.apodosJugadores.get(i));
      }
    }

    turnoGlobal = datosIniciales.turno;
    direccion = datosIniciales.direccion;
    username = datosIniciales.nombre;

    System.out.println(" ------------------- Info ----------------");
    System.out.println("Nombre: " + username);
    System.out.println("Jugadores: " + nombresJugadoresGlobal.size());
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
    playerDeck.addCardComponentListener(new ComponentAdapter() { public void componentMoved(ComponentEvent e) { repaint();}});

    barajaSobrante = new JLabel(imgBarajaSobrante);
    barajaSobrante.setSize(imgBarajaSobranteSize*11/10, imgBarajaSobranteSize-15);
    barajaSobrante.setLocation(pilaTiradasPanel.getX()-barajaSobrante.getWidth()-150, pilaTiradasPanel.getY()-barajaSobrante.getHeight()/2 + 50);
    barajaSobrante.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        barajaSobrante.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 6, true));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        barajaSobrante.setBorder(null);
      }
    });

    midPanel = new JPanel(new GridLayout(2,2));
    midPanel.setSize(500, 600);
    midPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 20, true));
    midPanel.setLocation(getWidth()/2 - midPanel.getWidth()/2, getHeight()/2 - midPanel.getHeight()/2);
    midPanel.setBackground(Color.WHITE);
    midPanel.setVisible(false);
    Color[] colores = { Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN };
    for(int i = 0; i < 4; i++) {
      JButton b = new JButton();
      b.setSize(100, 100);
      b.setBackground(colores[i]);
      b.setName(""+i);
      midPanel.add(b);
      botonesColor[i] = b;
    }
    
    add(barajaSobrante);
    add(playerDeck);
    add(pilaTiradasPanel);
    add(midPanel);

    initInfoBottomPanel();
    
    setComponentZOrder(midPanel, 0);
    setVisible(true);
  }

  private void initInfoBottomPanel() {
    Font font = new Font("SansSerif", Font.BOLD, 25);
    infoBottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 35, 0));
    infoBottomPanel.setSize(getWidth(), 50);
    infoBottomPanel.setOpaque(false);
    infoBottomPanel.setLocation(0, getHeight()-70);

    JLabel nombreDeUsuario = new JLabel("NOMBRE: " + username);
    nombreDeUsuario.setFont(font);
    nombreDeUsuario.setForeground(Color.WHITE);

    unoButton = new JButton("UNO");
    unoButton.setBackground(Color.ORANGE);
    unoButton.setFont(font);
    unoButton.setEnabled(false);

    nombreTurnoActual = new JLabel("");
    nombreTurnoActual.setFont(font);
    nombreTurnoActual.setForeground(Color.WHITE);
    actualizarNombreTurnoActual();

    String orden = "";
    for(String s : nombresJugadoresGlobal) {
      orden += s + " => ";
    }
    JLabel ordenLabel = new JLabel("ORDEN: " + orden + nombresJugadoresGlobal.get(0) + "  ");
    ordenLabel.setFont(new Font("Consolas", Font.PLAIN, 18));
    ordenLabel.setForeground(Color.WHITE);

    
    infoBottomPanel.add(nombreDeUsuario);
    infoBottomPanel.add(unoButton);
    infoBottomPanel.add(nombreTurnoActual);
    infoBottomPanel.add(ordenLabel);

    add(infoBottomPanel);
  }

  private void actualizarNombreTurnoActual() {
    System.out.println("Jugadores Globales: " + nombresJugadoresGlobal);
    nombreTurnoActual.setText("TURNO: " + nombresJugadoresGlobal.get(turnoGlobal));
    nombreTurnoActual.setBackground(Color.ORANGE);
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

  public Card getTope() { return topeDePilaDeTiradas; }

  public PlayerDeck getPlayerDeck() { return playerDeck; }

  public int getNumCartas() { return playerDeck.getListaCartasSize(); }

  public ArrayList<Integer> getCartasJugadores() { return numCartasJugadores; }

  public JLabel getBarajaSobrante() { return barajaSobrante; }

  public JButton[] getBotonesColor() { return botonesColor; }

  public JButton getUnoButton() { return unoButton; }

  public JPanel getMidPanel() { return midPanel; }
// -------------------------------------------------------- Metodos de Acciones -------------------------------------------------------
  public void actionUpdateInfo(PacketData informacionNueva) {
    ArrayList<Integer> globales = new ArrayList<>(informacionNueva.globalNumCartas);
    ArrayList<Integer> nuevasCartas = new ArrayList<>();
    for(int i = 0; i < informacionNueva.apodosJugadores.size(); i++) {
      if(!username.equals(informacionNueva.apodosJugadores.get(i))) {
        nuevasCartas.add(globales.get(i));
      }
    }

    numCartasJugadores = nuevasCartas;
    turnoGlobal = informacionNueva.turno;
    direccion = informacionNueva.direccion;

    Card cartaRecibida = informacionNueva.cartaDeCliente.copy(true);
    cartaRecibida.setJugable(false);
    cartaRecibida.removeMouseListener(cartaRecibida);
    actualizarNombreTurnoActual();
    setTope(cartaRecibida);
    repaint();

  }

  public PacketData actionTirarCartaComun(Card cartaSeleccionada, MouseListener listenerCarta, LinkedList<Card> cartasAcumuladas) {
    Point cartaPos = cartaSeleccionada.getLocationOnScreen();
    if(cartaSeleccionada.isValid(topeDePilaDeTiradas)) {
      cartaSeleccionada.removeMouseListener(listenerCarta);
      playerDeck.removeCard(cartaSeleccionada);
      Card copia = cartaSeleccionada.copy(true);
      copia.setJugable(false);

      Point pilaPos = pilaTiradasPanel.getLocation();
      copia.setLocation(cartaPos);

      add(copia);
      toPilaAnimation(copia, cartaPos, pilaPos);
      PacketData paqueteEnviar = new PacketData();
      paqueteEnviar.accion = ServerAction.THROW_CARD;
      paqueteEnviar.cartaDeCliente = cartaSeleccionada;
      paqueteEnviar.nombre = username;
      paqueteEnviar.numCartas = getNumCartas();
      paqueteEnviar.turno = turno;
      paqueteEnviar.barajaCartas = new LinkedList<>(cartasAcumuladas);
      return paqueteEnviar;
      // Push al server de tirar carta
    }
    return null;
  }

  public void actionTirarCartaEspecial(Card cartaSeleccionada, MouseListener listenerCarta) {
    Point cartaPos = cartaSeleccionada.getLocationOnScreen();
    if(cartaSeleccionada.isValid(topeDePilaDeTiradas)) {
      cartaSeleccionada.removeMouseListener(listenerCarta);
      playerDeck.removeCard(cartaSeleccionada);
      Card copia = cartaSeleccionada.copy(true);
      copia.setJugable(false);
      Point pilaPos = pilaTiradasPanel.getLocation();
      copia.setLocation(cartaPos);

      add(copia);
      toPilaAnimation(copia, cartaPos, pilaPos);
      setComponentZOrder(midPanel,0);
    }
  }

  public void seleccionarColor() {
    midPanel.setVisible(true);
    playerDeck.setVisible(false);
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
    Image imgDir;
    //Pintar Direccion
    if(direccion == 1)
      imgDir = imgDireccion1.getImage();
    else
      imgDir = imgDireccion2.getImage();

    g.drawImage(
      imgDir,
      getWidth()/2 - imgDireccionSize/2 + 2,
      getHeight()/2 - imgDireccionSize/2 + 10,
      imgDireccionSize, 
      imgDireccionSize, 
      null
    );

    int offset = 50;
    Point[] posicionesImagenesCartas = {
      new Point(-offset, getHeight()/2 - imgCartasSize/2), 
      new Point(getWidth()/2 - imgCartasSize/2, -offset), 
      new Point(getWidth() - imgCartasSize + offset, getHeight()/2 - imgCartasSize/2)
    };

    Point[] posicionesNombres = {
      new Point(30, getHeight()/2 + imgCartasSize/2),
      new Point(getWidth()/2 - imgCartasSize + offset, 30), 
      new Point(getWidth() - imgCartasSize/2, getHeight()/2 + imgCartasSize/2)

    };

    // Imagenes de cada carta
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

          // Pintar el numero de cartas de cada jugador
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
            if(numCarta <= 9) {
              g2d.setFont(new Font("Consolas", Font.BOLD, 90));
              g2d.drawString(
                ""+numCarta,
                posicionesImagenesCartas[i].x + imgCartasSize/2 - 22,
                posicionesImagenesCartas[i].y + imgCartasSize/2 + 30 
                );
            }
            else {
              g2d.setFont(new Font("Consolas", Font.BOLD, 70));
              g2d.drawString(
                ""+numCarta,
                posicionesImagenesCartas[i].x + imgCartasSize/2 - 35,
                posicionesImagenesCartas[i].y + imgCartasSize/2 + 30 
                );
            }
          }
          // Pintar los nombres de cada jugador
          g2d.setColor(Color.BLACK);
          g2d.fillRoundRect(
            posicionesNombres[i].x,
            posicionesNombres[i].y,
            150,
            50,
            25,
            25
            );
            int fontSize = 30;
          g2d.setColor(Color.WHITE);
          g2d.setFont(new Font("SansSerif", Font.BOLD, fontSize));
          g2d.drawString(
            nombresJugadoresLocal.get(i),
            posicionesNombres[i].x + 5,
            posicionesNombres[i].y + fontSize + 5
          );
        }
  }

}
