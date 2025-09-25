package UserInfo;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import java.util.Random;
import java.lang.Thread;
import java.io.File;

public class WorkUserInfo extends UserInfo implements ContactAvailability.Callable,
ContactAvailability.Emailable {
  private String company;
  private String post;
  private String email;

  {
    company = "undefined";
    post = "undefined";
    email = "undefined";
  } WorkUserInfo(){}

  public WorkUserInfo(
    String number,
    String name,
    String surname,
    String company,
    String post,
    String email
  ) {
    super(number, name, surname);

    this.company = company;
    this.post    = post;
    this.email   = email;
  }

  public void setCompany(String company) { this.company = company; }
  public void setPost(String post)       { this.post = post;       }
  public void setEmail(String email)     { this.email= email;      }

  public String getCompany()             { return this.company;    }
  public String getPost()                { return this.post;       }
  public String getEmail()               { return this.email;      }

  public String getContactType() { return "Рабочий"; }

  public String getFullInfo() {
    return String.format(
      "Имя: %s\nНомер телефона: %s\nФамилия: %s\nМесто работы: %s\n" +
      "Должность: %s\nПочта: %s",
      userName, userNumber, userSurname, company, post, email);
  }


  public void call() {
    try {
      Random random = new Random();
      int duration = random.nextInt(8) + 3;

      System.out.println("Длительность: " + duration + " секунд");

      playRingtone(duration);

      if (duration % 2 == 0) {
        Thread.sleep(1000);
        playRandomWorkSound();
      }

    } catch (Exception e) { e.printStackTrace(); }
}

  private void playRingtone(int duration) {
    try {
      String path = new File("").getAbsolutePath();
      File ringtoneFile;

      System.out.println(duration);
      if (duration % 2 == 0)
        ringtoneFile = new File(path + "/assets/sounds/dial_sounds/classic-phone-ring.wav");
      else
        ringtoneFile = new File(path + "/assets/sounds/dial_sounds/phone_dial.wav");

      AudioInputStream audioStream = AudioSystem.getAudioInputStream(ringtoneFile);
      Clip clip = AudioSystem.getClip();
      clip.open(audioStream);

      if (duration % 2 == 0) {
        clip.loop(clip.LOOP_CONTINUOUSLY);
        clip.start();
        Thread.sleep(duration * 1000);
        clip.stop();

      } else {
        clip.loop(clip.LOOP_CONTINUOUSLY);
        clip.start();
        Thread.sleep(duration * 100);
        clip.stop();
      }

      clip.close();
    } catch(Exception e) { e.printStackTrace(); }
  }

  private void playRandomWorkSound() {
    try {
      Random random     = new Random();
      String path       = new File("").getAbsolutePath();
      File persSoundDir = new File(path + "/assets/sounds/answer_sounds/Work");
      File[] soundFiles = persSoundDir.listFiles((dir, name) ->
        name.toLowerCase().endsWith(".wav"));

      if (soundFiles == null || soundFiles.length == 0) return;

      File randSoundFile           = soundFiles[random.nextInt(soundFiles.length)];
      AudioInputStream audioStream = AudioSystem.getAudioInputStream(randSoundFile);
      Clip clip = AudioSystem.getClip();
      clip.open(audioStream);

      clip.start();
      Thread.sleep(randSoundFile.length() * 1000);
      clip.close();
    } catch(Exception e) { e.printStackTrace(); }
  }

  public void sendEmail(String msg) {}
}
