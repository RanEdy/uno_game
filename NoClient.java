import javax.swing.SwingUtilities;

public class NoClient {
  public static void main(String[] args) {
    PacketData prueba1 = new PacketData();
    prueba1.accion = ServerAction.WAIT;
    SwingUtilities.invokeLater(() -> { new ManejadorMesa(prueba1); });
  }
}
