import javax.sound.sampled.*;
import java.io.*;

public class Audio {
  private Clip clip;
  public Audio(String path) {
    try {
      File file = new File(path);
      clip = AudioSystem.getClip();
      clip.open(AudioSystem.getAudioInputStream(file));
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void play() { clip.start(); }

  public void stop() { clip.stop(); }
}
