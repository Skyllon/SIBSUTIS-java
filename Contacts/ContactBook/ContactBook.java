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

public class ContactBook {
  private static final int MAX_CONTACT_NUMBER = 1000;
  private static int nextId = 1;

  public static HashMap<Integer, UserInfo> init() {
    HashMap<Integer, UserInfo> contactBook = new HashMap<>(MAX_CONTACT_NUMBER);
    nextId = 1;
    return contactBook;
  }

  public static int addUser(
    String userNumber,
    String userName,
    String userSurname,
    HashMap<Integer, UserInfo> contactBook
  ) {
    UserInfo newUser = new UserInfo(userNumber, userName, userSurname);
    int id = ++nextId;
    contactBook.put(id, newUser);
    return id;
  }

  public static boolean saveUsers(HashMap<Integer, UserInfo> contacts, File file) {
    BufferedWriter buffer = null;

    try {
      buffer = new BufferedWriter(new FileWriter(file));

      for (Map.Entry<Integer, UserInfo> entry : contacts.entrySet()) {
        UserInfo user = entry.getValue();
        String line = String.format("%d|%s|%s|%s",
          entry.getKey(),
          user.getNumber(),
          user.getName(),
          user.getSurname()
        );
        buffer.write(line);
        buffer.newLine();
      }

      buffer.flush();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (buffer != null) buffer.close();
      } catch (Exception e) { e.printStackTrace(); }
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

        if (parts.length == 4) {
          Integer id       = Integer.parseInt(parts[0]);
          String number    = parts[1];
          String name      = parts[2];
          String surname   = parts[3];

          if (id > maxId) maxId = id;

          UserInfo user = new UserInfo(number, name, surname);
          contacts.put(id, user);
        }
      }

      nextId = maxId++;

      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static int getNextId() { return nextId; }
}
