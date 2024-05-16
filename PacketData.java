import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class PacketData implements Serializable{
  public int turno; // Indice del jugador del turno actual (0-3)
  public ServerAction accion;
  public Card carta;
  public int numCartas;
  public LinkedList <Card> barajaCartas;
  public int[] globalNumCartas;
  public String nombre;
  public ArrayList <String> apodosJugadores;
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
        s += "Apodo " + i + ": " + apodo + "\n";
        i++;
      }
      s += "Total de jugadores: " + apodosJugadores.size() + "\n";
    }

    return s;
  }
}


