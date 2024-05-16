import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

  public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

  private Socket socket;
  private ObjectInputStream entrada;
  private ObjectOutputStream salida;

  // En lugar de esta variable -> colocar objeto de PacketData para toda la info necesaria
  private int clientNumber;

  public ClientHandler(Socket socket, int clientNumber) {
    try {
      this.socket = socket;
      this.clientNumber = clientNumber;
      // Stream de Strings -> cambiar por una de objetos
      this.salida = new ObjectOutputStream(socket.getOutputStream());
      this.entrada = new ObjectInputStream(socket.getInputStream());
      clientHandlers.add(this);
      System.out.println("SERVER: " + clientNumber + " entro al chat!");
    } 
    catch(Exception e) {
      System.out.println("problema aqui - 1");
      closeEverything();
      e.printStackTrace();
    }
  }

  public static void broadcastPacketFromServer(PacketData packetDataToSend) {
      for(ClientHandler clientHandler : clientHandlers) {
        try {
            if(packetDataToSend.accion.equals(ServerAction.NEW_ELEMENTS)){
              System.out.println("\nverificacion: " + packetDataToSend.apodosJugadores.size());
                clientHandler.salida.writeObject(packetDataToSend);
                clientHandler.salida.flush();
            } else if(packetDataToSend.accion.equals(ServerAction.ERROR)){
                if(clientHandler.clientNumber == packetDataToSend.turno) {
                    clientHandler.salida.writeObject(packetDataToSend);
                    clientHandler.salida.flush();
                }
            } else {
              if(clientHandler.clientNumber != packetDataToSend.turno) {
                  clientHandler.salida.writeObject(packetDataToSend);
                  clientHandler.salida.flush();
              }
            }
        } catch(Exception e) {
          System.out.println("problema aqui - 2");
          e.printStackTrace();
        } 
      }
  }

  // Reenviar el mensaje a todos los clientes
  public void broadcastPacket(PacketData packetDataToSend) {
    System.out.println("\nverificacion: " + packetDataToSend.apodosJugadores.size());
      for(ClientHandler clientHandler : clientHandlers) {
        try {
            if(packetDataToSend.accion.equals(ServerAction.NEW_ELEMENTS)){
              System.out.println("\nverificacion: " + packetDataToSend.apodosJugadores.size());
                clientHandler.salida.writeObject(packetDataToSend);
                clientHandler.salida.flush();
            } else if(packetDataToSend.accion.equals(ServerAction.ERROR)){
                if(clientHandler.clientNumber == packetDataToSend.turno) {
                    clientHandler.salida.writeObject(packetDataToSend);
                    clientHandler.salida.flush();
                }
            } else {
              if(clientHandler.clientNumber != packetDataToSend.turno) {
                  clientHandler.salida.writeObject(packetDataToSend);
                  clientHandler.salida.flush();
              }
            }
        } catch(Exception e) {
          System.out.println("problema aqui - 2");
          closeEverything();
          e.printStackTrace();
        } 
      }
  }

  public void removeClientHandler() {
    clientHandlers.remove(this);
    System.out.println("SERVER: " + clientNumber + " salio del chat");
  }

  public void closeEverything() {
    removeClientHandler();
    try {
      if(entrada != null)
      entrada.close();

      if(salida != null)
      salida.close();

      if(socket != null) 
        socket.close();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    PacketData packetFromClient, packetFromServer;
    while (socket.isConnected()) {
      try {
        packetFromClient = (PacketData) entrada.readObject();
        packetFromServer = Server.receiveClientMovement(packetFromClient);
        System.out.println("Paquete enviado: ");
        System.out.println(packetFromServer);
        broadcastPacket(packetFromServer);
      }
      catch(Exception e) {
        System.out.println("problema aqui - 3");
        e.printStackTrace();
        closeEverything();
        break;
      }
    }
  }
  
}
