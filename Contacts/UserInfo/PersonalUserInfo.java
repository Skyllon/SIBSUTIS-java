package UserInfo;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import java.util.Random;
import java.lang.Thread;
import java.io.File;

public class PersonalUserInfo  extends UserInfo implements ContactAvailability.Callable {
  private String birthDate;
  private String aboutUser;
  private String address;

  {
    birthDate = "undefined";
    aboutUser = "undefined";
    address   = "undefined";
  } public PersonalUserInfo(){}

  public PersonalUserInfo(
    String number,
    String name,
    String surname,
    String birthDate,
    String aboutUser,
    String address
  ) {
    super(number, name, surname);

    this.birthDate = birthDate;
    this.aboutUser = aboutUser;
    this.address   = address;
  }

  public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
  public void setAboutUser(String aboutUser) { this.aboutUser = aboutUser; }
  public void setAddress(String address)     { this.address   = address;   }

  public String getBirthDate()               { return this.birthDate;      }
  public String getAboutUser()               { return this.aboutUser;      }
  public String getAddress()                 { return this.address;        }


  public String getContactType() { return "Личный"; }

  public String getFullInfo() {
    return String.format(
      "Имя: %s\nНомер телефона: %s\nФамилия: %s\nДень рождения: %s\n" +
      "Описание: %s\nАдрес: %s",
      userName, userNumber, userSurname, birthDate, aboutUser, address);
  }

  public void call() {
    try {
        Random random = new Random();
        int duration = random.nextInt(8) + 3;

        System.out.println("Длительность: " + duration + " секунд");

        playRingtone(duration);

        if (duration % 2 == 0) {
          Thread.sleep(1000);
          playRandomPersonalSound();
        }

    } catch (Exception e) { e.printStackTrace(); }
}

  private void playRingtone(int duration) {
    try {
      String path = new File("").getAbsolutePath();
      File ringtoneFile;

      System.out.println("[DEBUG]: " + duration);
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

  private void playRandomPersonalSound() {
    try {
      Random random     = new Random();
      String path       = new File("").getAbsolutePath();
      File persSoundDir = new File(path + "/assets/sounds/answer_sounds/Personal");
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

}
