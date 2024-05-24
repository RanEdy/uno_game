import javax.sound.sampled.*;
import java.io.*;

public class Audio implements Serializable {
  public Audio(String path, float volume) {
    try {
      File file = new File(path);
      
      Clip clip = AudioSystem.getClip();
      clip.open(AudioSystem.getAudioInputStream(file));

      volume = Math.clamp(volume, 0.0f, 1.0f);

      FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
      volumeControl.setValue(20f * (float) Math.log10(volume));
      clip.start();

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

}
