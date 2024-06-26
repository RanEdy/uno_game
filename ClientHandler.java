import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

  public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

  private Socket socket;
  private ObjectInputStream entrada;
  private ObjectOutputStream salida;

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

  // Reenviar el paquete a todos los clientes desde el servidor (servidor -> clientes)
  public static void broadcastPacketFromServer(PacketData packetDataToSend) {
      for(ClientHandler clientHandler : clientHandlers) {
        synchronized(clientHandler.salida) {
          try {
            if(clientHandler.clientNumber != packetDataToSend.turno) {
              if(packetDataToSend.accion == ServerAction.EAT)
                System.out.println("[broadcastPacketFromServer] Paquete Enviado:\n" + packetDataToSend);
                clientHandler.salida.writeObject(packetDataToSend);
                clientHandler.salida.flush();
            }
            
          } catch(Exception e) {
            System.out.println("problema aqui - 2");
            e.printStackTrace();
          } 
        }
      }
  }

  // Metodo para enviar un paquete a un cliente especifico desde el servidor
  public static void sendPacketToClientFromServer(PacketData packetDataToSend, int clientNum) {
    try {
      ClientHandler cliente = clientHandlers.get(clientNum);
      System.out.println("[SendPacketToClientFromServer] Paquete Enviado:\n"+packetDataToSend);
      cliente.salida.writeObject(packetDataToSend);
      cliente.salida.flush();
    }
    catch(IndexOutOfBoundsException e) {
      System.out.println("Error al enviar paquete al cliente " + clientNum + " | cliente no valido");
    }
    catch(Exception e) {
      System.out.println("Error al enviar paquete al cliente " + clientNum);
      e.printStackTrace();
    }
  }

  // Reenviar el paquete a todos los clientes desde el cliente (cliente -> clientes)
  public void broadcastPacket(PacketData packetDataToSend) {
      for(ClientHandler clientHandler : clientHandlers) {
        synchronized(clientHandler.salida) {
          try {
            if(packetDataToSend.accion == ServerAction.EAT) {
              System.out.println("[Paquete Enviado desde broadcastPacket]: \n" + packetDataToSend);
            }
            clientHandler.salida.writeObject(packetDataToSend);
            clientHandler.salida.flush();
          } catch(Exception e) {
            System.out.println("problema aqui - 2");
            closeEverything();
            e.printStackTrace();
          } 
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

  // Metodo esta leyendo del cliente
  @Override
  public void run() {
    PacketData packetFromClient, packetFromServer;
    while (socket.isConnected()) {
      try {
        //Lee el paquete del Cliente
        packetFromClient = (PacketData) entrada.readObject();
        // Informacion devuelta por el servidor despues de procesar el paquete enviado del cliente
        packetFromServer = Server.receiveClientMovement(packetFromClient);
        if(packetFromServer.accion == ServerAction.EAT)
          System.out.println("[Paquete Enviado desde ClientHandler]\n" + packetFromServer);
        broadcastPacket(packetFromServer);
      }
      catch(Exception e) {
        closeEverything();
        break;
      }
    }
  }
  
}
