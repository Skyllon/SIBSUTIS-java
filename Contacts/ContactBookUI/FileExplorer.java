package ContactBookUI;

import java.io.File;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import ContactBook.ContactBook;
import UserInfo.UserInfo;

public class FileExplorer {
  FileExplorer() {}

  public static File openDialog(JFrame parent) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Выберите телефонную книгу");

    // Filtering
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setFileFilter(new FileNameExtensionFilter(
      "Текстовые файлы (*.txt)", "txt"));

    // Root dir
    fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

    int status = fileChooser.showOpenDialog(parent);

    if (status == JFileChooser.APPROVE_OPTION)
      return fileChooser.getSelectedFile();

    return null;
  }

  public static File saveFileDialog(JFrame parent) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Выберите файл для сохранения телефонную книги");

    // Filtering
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setFileFilter(new FileNameExtensionFilter(
      "Текстовые файлы (*.txt)", "txt"));

    // Default file name
    fileChooser.setSelectedFile(new File("контакты.txt"));

    // Root dir
    fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

    int status = fileChooser.showSaveDialog(parent);

    if (status == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();

      if (!selectedFile.getName().toLowerCase().endsWith(".txt"))
        selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");

      return selectedFile;
    }

    return null;
  }

  public static boolean loadContacts(JFrame parent, HashMap<Integer, UserInfo> contacts) {
    File file = openDialog(parent);

    if (file != null) {
      boolean status = ContactBook.loadUsers(contacts, file);

      if (!status) {
        JOptionPane.showMessageDialog(
          parent,
          "Ошибка при загрузке конктактов",
          "Ошибка",
          JOptionPane.ERROR_MESSAGE);
      }
      return status;
    }

    return false;
  }

  public static boolean saveContacts(JFrame parent,  HashMap<Integer, UserInfo> contacts) {
    File file = saveFileDialog(parent);

    if (file != null) {
      if (file.exists()) {
        int confirm = JOptionPane.showConfirmDialog(
        parent,
        "Файл " + file.getName() + " существует. Перезаписать?",
        "Подтверждение",
        JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.NO_OPTION) return false;
      }

      boolean status = ContactBook.saveUsers(contacts, file);

      if (!status) {
        JOptionPane.showMessageDialog(
          parent,
          "Ошибка при сохранении конктактов",
          "Ошибка",
          JOptionPane.ERROR_MESSAGE);
      }

      return status;
    }

    return false;
  }
}
