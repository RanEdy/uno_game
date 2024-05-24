import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class PacketData implements Serializable{
  public int turno; // Indice del jugador del turno actual (0-3)
  public ServerAction accion; 
  public Card cartaDeCliente; // carta que se le envia al servidor desde el cliente
  public int numCartas; // numero de cartas de un cliente

  public Card cartaInicial;

  public LinkedList <Card> barajaCartas; // cartas enviadas del servidor al cliente (cuando come el cliente)
  public ArrayList<Integer> globalNumCartas; // numero de cartas de cada jugador
  public String nombre; // nombre del cliente
  public ArrayList <String> apodosJugadores; // nombres de los jugadores

  public String jOptionMensaje;
  public String jOptionTitulo;

  @Override 
  public String toString() {
    String s = "";
    s += "Turno: " + turno + "\n";
    s += "Accion: " + accion.name() + "\n";
    //s += "" + carta;
    s += "Nombre: " + nombre + "\n";
    int i = 1;
    if(apodosJugadores != null) {
      for(String apodo : apodosJugadores) {
        s += "Apodo " + i + ": " + apodo + ", ";
        i++;
      }
      s += "\nTotal de jugadores: " + apodosJugadores.size() + "\n";
    }

    return s;
  }
}


