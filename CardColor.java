import java.awt.GradientPaint;
import java.awt.Point;
import java.io.Serializable;
import java.awt.Color;

public class CardColor implements Serializable {
  
  public static final int RED = 0, YELLOW = 1, GREEN = 2, BLUE = 3, BLACK = 4, MAX_COLORS = 5;
  private Color color;
  private String colorName;
  private int colorInt;

  public CardColor(int colorType) {
    switch(colorType) {
      case CardColor.RED:
        color = new Color(255,45, 0);
        colorName = "RED";
        colorInt = RED;
        break;
      case CardColor.YELLOW:
        color = new Color(255,255, 0);
        colorName = "YELLOW";
        colorInt = YELLOW;
        break;
      case CardColor.GREEN:
        color = new Color(60,200, 45);
        colorName = "GREEN";
        colorInt = GREEN;
        break;
      case CardColor.BLUE:
        color = new Color(0,180, 255);
        colorName = "BLUE";
        colorInt = BLUE;
        break;
      case CardColor.BLACK:
        color = new Color(0,0, 0);
        colorName = "BLACK";
        colorInt = BLACK;
        break;
    }
  }

  public GradientPaint getGradiente(int width, int height) {
    Point p1 = new Point(0, height);
    Point p2 = new Point(width, height);
    return new GradientPaint(p2, color.brighter(), p1, color.darker());
  }

  public String getColorName() { return this.colorName; }
  public int getColorInt() { return this.colorInt; }
}
