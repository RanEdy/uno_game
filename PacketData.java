public class PacketData {
  int turno; // Indice del jugador del turno actual (0-3)
  ServerAction accion;
  Card carta;
  int numCartas;
  int[] globalNumCartas;
}
