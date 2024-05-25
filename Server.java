import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

public class Server extends JFrame {
  private ServerSocket serverSocket;

  private static ArrayList <String> apodos;
  private static ArrayList<Integer> numCartasJugadores = new ArrayList<>();
  private static int contadorJugadores = 0;
  private boolean comenzar = false;
  private static int jugadorActual = 0;
  private JButton iniciar;
  private static int direccion = 1;

  private Stack<Card> baraja;

  public Server(ServerSocket ss) {
    super("Servidor del UNO");
    serverSocket = ss;
    apodos = new ArrayList<>();
    try {
      System.out.println("IP: " + InetAddress.getLocalHost().getHostAddress());
      System.out.println("Puerto: " + ss.getLocalPort());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    System.out.println("[ Servidor Iniciado correctamente ]");

    getContentPane().setBackground(Color.red);
    ImageIcon icono = new ImageIcon ("iconos/logo.png");
    JLabel icono_UNO = new JLabel(icono);
    add(icono_UNO, BorderLayout.NORTH);
    
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    panel.setOpaque(false);
    add(panel, BorderLayout.CENTER);
    
    iniciar = new JButton ("Iniciar");
    iniciar.setBorder(BorderFactory.createLineBorder(Color.black, 3));
    iniciar.setFont(new java.awt.Font("Segoe UI", 3, 58));
    iniciar.setBackground(Color.yellow);
    // Boton iniciar
    iniciar.addActionListener((ActionEvent evento) -> {
        if(contadorJugadores >= 1){
            Random rand = new Random();
            baraja = Card.generarBaraja();
            Card cartaInicial = baraja.pop();
            cartaInicial.removeMouseListener(cartaInicial);
            if(cartaInicial.getColorInt() == CardColor.BLACK)
              cartaInicial.setColor(new CardColor(rand.nextInt(4)));
            comenzar = true;

            PacketData paqueteEnviar = new PacketData();
            paqueteEnviar.globalNumCartas = new ArrayList<>(contadorJugadores);

            // Rellenar el numero de cartas de cada jugador
            for(int n = 0; n < contadorJugadores; n++) {
              paqueteEnviar.globalNumCartas.add(7);
              numCartasJugadores.add(7);
            }
            // Se realiaza un for porque se necesita enviarle a cada cliente 7 cartas de la baraja distintas a cada uno (si se utiliza broadcastFromServer se enviarian las 7 mismas cartas a todos los jugadores)
            for(int i = 0; i < contadorJugadores; i++) {
              paqueteEnviar.barajaCartas = Card.comerCartas(baraja, 7);
              paqueteEnviar.cartaInicial = cartaInicial;
              paqueteEnviar.accion = ServerAction.START;
              paqueteEnviar.turno = 0;
              paqueteEnviar.direccion = direccion;
              paqueteEnviar.nombre = apodos.get(i);
              paqueteEnviar.apodosJugadores = apodos;

              //Envia el paquete a todos los clientes
              ClientHandler.sendPacketToClientFromServer(paqueteEnviar, i);
            }
            setVisible(false);

            // Panel de Pruebas para Debug 
            //SwingUtilities.invokeLater(()-> { new PanelDebug(this); });

        } else {
            JOptionPane.showMessageDialog(null, "Necesitas al menos dos jugadores + [ " + contadorJugadores + " ]", 
            "Error de inicio", JOptionPane.ERROR_MESSAGE);
        }
    });
    //
    panel.add(iniciar, BorderLayout.CENTER);
    setSize(500,350);
    setVisible(true);
    }

  public void startServer() {
    try {
      while(!serverSocket.isClosed()) {
        Socket socket = serverSocket.accept();
        if(!comenzar && contadorJugadores < 4){
          contadorJugadores ++;
          System.out.println("Cliente conectado " + socket.getInetAddress().getHostAddress());
          System.out.println("SERVER> Total de jugadores conectados: " + contadorJugadores);
          ClientHandler clientHandler = new ClientHandler(socket, contadorJugadores);
          Thread thread = new Thread(clientHandler);
          thread.start();
        } else {
          System.out.println("SERVER> El juego ya ha iniciado");
          socket.close();
        }
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void closeServerSocket() {
    try {
      if(serverSocket != null) {
        serverSocket.close();
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(9520, 4);
    Server server = new Server(serverSocket);
    server.startServer();
  }

  // Importante: no modificar directamente los objetos del paquete recibido, en cambio crear uno nuevo y reemplazarlo
  // Nota: Siempre crear una nueva instancia de un ArrayList si se quiere modificar la informacion del paquete
  public static PacketData receiveClientMovement(PacketData Movement){
    System.out.println("Paquete recibido de " + Movement.nombre + "\n" + Movement);
      switch(Movement.accion){
        case NEW_ELEMENTS:
          apodos.add(Movement.nombre);
          Movement.apodosJugadores = (ArrayList<String>) apodos.clone();
        break;

        case THROW_CARD:
          Card carta = Movement.cartaDeCliente.copy(false);
          if(Movement.numCartas == 0)
            System.out.println(Movement.nombre + " Gano");
          if(carta.getCardType() == CardType.REVERSE)
            direccion *= -1;
          siguienteTurno();
          numCartasJugadores.set(Movement.turno, Movement.numCartas);
          Movement.apodosJugadores = (ArrayList<String>) apodos.clone();
          Movement.globalNumCartas = new ArrayList<>(numCartasJugadores);
          Movement.accion = ServerAction.UPDATE_INFO;
          Movement.turno = jugadorActual;
          Movement.direccion = direccion;
        break;
  
        case EAT:
        System.out.println("2");
        break;
  
        case EAT_2:
        System.out.println("3");
        break;
  
        case EAT_4:
        System.out.println("4");
        break;
  
        case CHANGE_COLOR:
        System.out.println("6");
        break;
  
        case BLOCK:
        System.out.println("7");
        break;
  
        case UNO:
        System.out.println("8");                    
        break;
  
        default:
            System.out.println("9");
        break;
      }
    return Movement;
  }

  public static void siguienteTurno() {
    jugadorActual += direccion;
    if(jugadorActual >= contadorJugadores)
      jugadorActual = 0;
    if(jugadorActual < 0)
      jugadorActual = contadorJugadores-1;
  }
}

  
