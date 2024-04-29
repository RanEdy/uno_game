import java.awt.*;

public class Animacion {

  public static Point lerp(Point inicio, Point fin, double porcentaje) {
    porcentaje = Math.clamp(porcentaje, 0.0, 1.0);
    return new Point(
      (int)(inicio.x + (fin.x - inicio.x) * porcentaje),
      (int)(inicio.y + (fin.y - inicio.y) * porcentaje)
      );
  }

  public static Point ease_in(Point inicio, Point fin, double porcentaje) {
    return lerp(inicio, fin, ease_in_value(porcentaje));
  }

  public static Point ease_out(Point inicio, Point fin, double porcentaje) { 
    return lerp(inicio, fin, ease_out_value(porcentaje));
  }

  public static Point clamp_pos(Point punto, Point min, Point max) {
    return new Point(Math.clamp(punto.x, min.x, max.x), Math.clamp(punto.y, min.y, max.y));
  }

  private static double ease_in_value(double t) {
    return t*t;
  }

  private static double ease_out_value(double t) {
    return flip(flip(t) * flip(t));
  }

  private static double flip(double x) {
    return 1 - x;
  }



}
