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
  private int contadorJugadores = 0;
  private boolean comenzar = false;
  private int jugadorActual = 0;
  private JButton iniciar;

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
        iniciar.addActionListener((ActionEvent evento) -> {
            if(contadorJugadores >= 2){
                comenzar = true;
                PacketData paqueteEnviar = new PacketData();
                paqueteEnviar.accion = ServerAction.START;
                ClientHandler.broadcastPacketFromServer(paqueteEnviar);
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(null, "Necesitas al menos dos jugadores + [ " + contadorJugadores + " ]", 
                "Error de inicio", JOptionPane.ERROR_MESSAGE);
            }
        });
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

  public static PacketData receiveClientMovement(PacketData Movement){
    System.out.println("Paquete recibido: ");
    System.out.println(Movement);
    if(Movement.accion.equals(ServerAction.NEW_ELEMENTS)){
      apodos.add(Movement.nombre);
      System.out.println("Nuevo Tamanio arreglo server: " + apodos.size());
      Movement.apodosJugadores = (ArrayList<String>) apodos.clone();
      System.out.println("Nuevo Tamanio arreglo paquete: " + Movement.apodosJugadores.size());

      
    } else {

      switch(Movement.accion){

        case THROW_CARD:
            System.out.println("1");
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
  
        case CHANGE_DIRECTION:
        System.out.println("5");
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
  }
    return Movement;
  }
}

  