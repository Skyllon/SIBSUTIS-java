package ContactBook;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import UserInfo.UserInfo;
import UserInfo.WorkUserInfo;
import UserInfo.PersonalUserInfo;

public class ContactBook {
  private static final int MAX_CONTACT_NUMBER = 1000;
  private static int nextId = 1;

  public static HashMap<Integer, UserInfo> init() {
    HashMap<Integer, UserInfo> contactBook = new HashMap<>(MAX_CONTACT_NUMBER);
    nextId = 1;
    return contactBook;
  }

  public static int addUser(UserInfo user, HashMap<Integer, UserInfo> contactBook) {
    int id = nextId++;
    contactBook.put(id, user);
    return id;
  }

  public static boolean saveUsers(HashMap<Integer, UserInfo> contacts, File file) {
    try (BufferedWriter buffer = new BufferedWriter(new FileWriter(file))) {
      for (Map.Entry<Integer, UserInfo> entry : contacts.entrySet()) {
        UserInfo user = entry.getValue();
        String line;

        if (user instanceof PersonalUserInfo) {
          PersonalUserInfo personal = (PersonalUserInfo) user;
          line = String.format("%d|personal|%s|%s|%s|%s|%s|%s",
            entry.getKey(),
            personal.getNumber(),
            personal.getName(),
            personal.getSurname(),
            personal.getBirthDate(),
            personal.getAboutUser(),
            personal.getAddress()
          );
        } else if (user instanceof WorkUserInfo) {
          WorkUserInfo work = (WorkUserInfo) user;
          line = String.format("%d|work|%s|%s|%s|%s|%s|%s",
            entry.getKey(),
            work.getNumber(),
            work.getName(),
            work.getSurname(),
            work.getCompany(),
            work.getPost(),
            work.getEmail()
          );
        } else {
          line = String.format("%d|basic|%s|%s|%s",
            entry.getKey(),
            user.getNumber(),
            user.getName(),
            user.getSurname()
          );
        }

        buffer.write(line);
        buffer.newLine();
      }
      buffer.flush();

      return true;
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
  }

  public static boolean loadUsers(HashMap<Integer, UserInfo> contacts, File file) {
    if (!file.exists() || !file.isFile()) return false;

    try (BufferedReader buffer = new BufferedReader(new FileReader(file))) {
      String line;
      contacts.clear();
      int maxId = 0;

      while ((line = buffer.readLine()) != null) {
        String[] parts = line.split("\\|");

        if (parts.length >= 5) {
          Integer id = Integer.parseInt(parts[0]);
          String type = parts[1];
          String number = parts[2];
          String name = parts[3];
          String surname = parts[4];

          if (id > maxId) maxId = id;

          UserInfo user;

          if ("personal".equals(type) && parts.length >= 8) {
            // pers: id|personal|number|name|surname|birthDate|aboutUser|address
            user = new PersonalUserInfo(
              number,
              name,
              surname,
              parts[5],
              parts[6],
              parts[7]
              );
          } else if ("work".equals(type) && parts.length >= 8) {
              // work: id|work|number|name|surname|company|post|email
              user = new WorkUserInfo(
                number,
                name,
                surname,
                parts[5],
                parts[6],
                parts[7]
              );
          } else {
            // err
            user = new UserInfo(number, name, surname) {
              public String getContactType() { return "Базовый"; }
              public String getFullInfo() {
                return String.format("Имя: %s\nНомер: %s\nФамилия: %s",
                  userName, userNumber, userSurname);
              }
            };
            }

            contacts.put(id, user);
          }
      }

      nextId = maxId + 1;
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
}

  public static int getNextId() { return nextId; }
}
