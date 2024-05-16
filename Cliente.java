import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Cliente extends JFrame{
    private JPanel panelBase;
    private JPanel [] panelJugador;
    private JLabel [] nomJugador;
    private String username;
    private Socket socket;
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;
    private boolean connected = true;

    private LinkedList <Card> baraja = new LinkedList<>();
    
    public Cliente(Socket socket, String username){
        super("[ Jugador ]");
        try {
            this.socket = socket;
            this.username = username;
            this.salida = new ObjectOutputStream(socket.getOutputStream());
            this.entrada = new ObjectInputStream(socket.getInputStream());            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static String Nombre(){
        boolean valido = false;
        String nombre = "";
        while(!valido){
            nombre = JOptionPane.showInputDialog(null, "Introduzca un nombre: ", 
                                    "Inicio - UNO", 3);

            if(nombre.isBlank()){
                JOptionPane.showMessageDialog(null, "No has introducido ningun nombre", 
                "Error de inicio", JOptionPane.ERROR_MESSAGE);
                valido = false;
            } else if(nombre.length()>30){
                JOptionPane.showMessageDialog(null, "Has introducido un nombre muy largo", 
                "Error de inicio", JOptionPane.ERROR_MESSAGE);
                valido = false;
            } else {
                valido = true;
            }

        }
        return nombre;
    }

    public void lobby(){
        Container contenedor = getContentPane();
        ImageIcon icono = new ImageIcon ("iconos/LOGO.png");
        JLabel icono_UNO = new JLabel(icono);
        contenedor.add(icono_UNO, BorderLayout.NORTH);
        
        panelBase =  new JPanel(new GridLayout(4,0,15,15));
        panelBase.setOpaque(false);
        panelBase.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contenedor.add(panelBase,BorderLayout.CENTER);
        
        String [] numJ = {" J1 ", " J2 ", " J3 ", " J4 "};
        panelJugador = new JPanel [4];
        nomJugador = new JLabel [4];
        for(int x = 0; x < 4;  x++) {
            JLabel num = new JLabel(numJ[x]);
            num.setOpaque(true);
            num.setBackground(Color.yellow);
            num.setFont(new java.awt.Font("Segoe UI", 3, 50));
            num.setBorder(BorderFactory.createLineBorder(Color.black, 3));
            
            panelJugador[x] = new JPanel(new BorderLayout());
            panelJugador[x].setOpaque(true);
            panelJugador[x].setBackground(Color.WHITE);
            panelJugador[x].setBorder(BorderFactory.createLineBorder(Color.black, 3));
            
            nomJugador[x] = new JLabel("Jugador");
            nomJugador[x].setFont(new java.awt.Font("Segoe UI", 3, 18));
            
            
            panelJugador[x].add(num, BorderLayout.WEST);
            panelJugador[x].add(nomJugador[x], BorderLayout.EAST);
            panelBase.add(panelJugador[x]);
            panelJugador[x].setVisible(false);
        }
        setBounds(600,100,500,700);
        getContentPane().setBackground(Color.red);
        setVisible(true);

        panelJugador[0].setVisible(true);
        nomJugador[0].setText(username);

    }

    public void sendMove(PacketData enviar) {
        try {
          while(socket.isConnected()) {
            //Aqui es donde se va a mandar la carta
            salida.writeObject(enviar);
            salida.flush();
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
    }

    public void closeEverything() {
        try {
            connected = false;

          if(entrada != null)
            entrada.close();
    
          if(salida != null)
            salida.close();
    
          if(socket != null) 
            socket.close();
        } catch(Exception e) {
          e.printStackTrace();
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
          while(connected && !socket.isClosed()) {
            try {
              PacketData packetDataFromServer = (PacketData) entrada.readObject();
              System.out.println(packetDataFromServer);
              //System.out.println("jugador [" +  packetDataFromServer.turno + "] lanzo una carta");

              switch(packetDataFromServer.accion){
                case START:
                  System.out.println("Juego Iniciado");
                  SwingUtilities.invokeLater(() -> { new ManejadorMesa(); });
                  setVisible(false);
                break;

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
    
                case NEW_ELEMENTS:
                    System.out.println("SERVER> Un nuevo jugador se ha conectado");
                    baraja = packetDataFromServer.barajaCartas;          
                    ArrayList <String> nicknames = packetDataFromServer.apodosJugadores;  
                    for(int x = 0; x < nicknames.size();  x++) {
                        nomJugador[x].setText(nicknames.get(x));
                        panelJugador[x].setVisible(true);
                        //System.out.println("hola: " + nicknames.get(x));
                    }     
                    //System.out.println("total de jugadores: " + nicknames.size());
                break;
    
                default:
                    System.out.println("9");
                break;
            }

            } catch (ClassNotFoundException | IOException  e) {
              closeEverything();
              e.printStackTrace();
            }
          }
        }).start();
      }
    
    public static void main(String[] args){
        String nickname = Nombre();
        try {
            Socket socket = new Socket("10.21.63.218", 9520);
            Cliente cliente = new Cliente(socket, nickname);
            cliente.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            cliente.lobby();
            cliente.listenForMessage();
            cliente.sendNickname();
           // cliente.sendMove();
          }catch(Exception e) {
            e.printStackTrace();
          }
    }

    public void sendNickname() throws IOException{
        PacketData enviar = new PacketData();
        enviar.accion = ServerAction.NEW_ELEMENTS;
        enviar.nombre = username;
        salida.writeObject(enviar);
    }    
}