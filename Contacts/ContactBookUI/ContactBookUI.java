package ContactBookUI;


import java.awt.Color;
import java.awt.GridLayout;
import java.util.Random;
import java.util.ArrayList;

// UI and ADT
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;

// Contacts manage and store
import ContactBook.ContactBook;
import UserInfo.UserInfo;
import UserInfo.PersonalUserInfo;
import UserInfo.WorkUserInfo;

public class ContactBookUI extends JFrame {
	private HashMap<Integer, UserInfo> contacts;
	private Set<UserInfo> favoriteContacts;
	private JList<String> contactsList;
	private ArrayList<Integer> idList;
	private JPopupMenu contextMenu;
	private DefaultListModel<String> listModel;
	private JTextField searchField;
	private JLabel countLabel;
	private String currentFilter = "";

	FileExplorer fileExplorer = new FileExplorer();

	public ContactBookUI() {
		contacts = ContactBook.init();
		favoriteContacts = new HashSet<>();

		// Window
		setTitle(WindowParams.WINDOW_TITLE);
		setVisible(WindowParams.WINDOW_VISIBILITY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(WindowParams.WINDOW_RELATIVENESS);
		setSize(WindowParams.WINDOW_WIDTH, WindowParams.WINDOW_HEIGHT);
		getContentPane().setBackground(WindowParams.WINDOW_BACKGROUND_COLOR);

		initUI();
	}


	public void initUI() {
    setLayout(new BorderLayout());

    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    searchField = new JTextField(20);
    searchField.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) { filterContacts(); }

        @Override
        public void removeUpdate(DocumentEvent e) { filterContacts(); }

        @Override
        public void changedUpdate(DocumentEvent e) { filterContacts(); }
    });
    topPanel.add(new JLabel("Поиск:"));
    topPanel.add(searchField);

    JButton addUserBtn = new JButton("Добавить пользователя");
    addUserBtn.addActionListener((event) -> addUserDialog(contacts));
    topPanel.add(addUserBtn);

    JButton loadFileBtn = new JButton("Загрузить файл");
    loadFileBtn.addActionListener((event) -> {
			if (fileExplorer.loadContacts(this, contacts))
				updateContactsList(contacts);
    });
    topPanel.add(loadFileBtn);

    JButton saveFileBtn = new JButton("Сохранить файл");
    saveFileBtn.addActionListener((event) -> fileExplorer.saveContacts(this, contacts));
    topPanel.add(saveFileBtn);

    add(topPanel, BorderLayout.NORTH);

    createContactsList();
    JScrollPane scrollPane = new JScrollPane(contactsList);
    add(scrollPane, BorderLayout.CENTER);

    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    countLabel = new JLabel("Количество контактов: 0");
    bottomPanel.add(countLabel);
    add(bottomPanel, BorderLayout.SOUTH);

    setupContextMenu();
    createMenuBar();

    revalidate();
    repaint();
	}

	public void createMenuBar() {
		var menuBar = new JMenuBar();
		var fileMenuContent = new JMenu("Файл");
		fileMenuContent.setMnemonic(KeyEvent.VK_F);

		var loadFileMenuItem = new JMenuItem("Загрузить файл");
		loadFileMenuItem.setMnemonic(KeyEvent.VK_E);
		loadFileMenuItem.addActionListener((event) -> {
			if (fileExplorer.loadContacts(this, contacts)) {
				updateContactsList(contacts);
			}
		});

		var saveFileMenuItem = new JMenuItem("Сохранить файл");
		saveFileMenuItem.setMnemonic(KeyEvent.VK_E);
		saveFileMenuItem.addActionListener((event) -> fileExplorer.saveContacts(this, contacts));

		fileMenuContent.add(loadFileMenuItem);
		fileMenuContent.add(saveFileMenuItem);
		menuBar.add(fileMenuContent);

		setJMenuBar(menuBar);
	}

	public void createContactsList() {
		listModel = new DefaultListModel<>();
		contactsList = new JList<>(listModel);
		contactsList.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
  }

	public void createAddUserButton() {
		JButton addUserBtn = new JButton();
		addUserBtn.setText("Добавить пользователя");
		addUserBtn.setBounds(400, 100, 300, 70);
		addUserBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		addUserBtn.setVisible(true);
		add(addUserBtn);
		addUserBtn.addActionListener((event) -> addUserDialog(contacts));
	}

	public void updateContactsList(HashMap<Integer, UserInfo> contacts) {
		if (listModel == null) listModel = new DefaultListModel<>();
		if (contactsList == null) contactsList = new JList<>(listModel);

		listModel.clear();
		idList = new ArrayList<>();

		List<Map.Entry<Integer, UserInfo>> sortedContacts = new ArrayList<>();

		for (Map.Entry<Integer, UserInfo> entry : contacts.entrySet()) {
			UserInfo user = entry.getValue();

			if (currentFilter.isEmpty() ||
				user.getName().toLowerCase().contains(currentFilter) ||
				user.getSurname().toLowerCase().contains(currentFilter) ||
				user.getNumber().toLowerCase().contains(currentFilter)) {
				sortedContacts.add(entry);
			}
		}

		sortedContacts.sort((e1, e2) -> {
			UserInfo u1 = e1.getValue();
			UserInfo u2 = e2.getValue();

			int nameCompare = u1.getName().compareToIgnoreCase(u2.getName());
			if (nameCompare != 0) return nameCompare;

			int surnameCompare = u1.getSurname().compareToIgnoreCase(u2.getSurname());
			if (surnameCompare != 0) return surnameCompare;

			return u1.getNumber().compareToIgnoreCase(u2.getNumber());
		});

		for (Map.Entry<Integer, UserInfo> entry : sortedContacts) {
			UserInfo user 		 = entry.getValue();
			String contactInfo = String.format("(%s) %s %s: %s",
				user.getContactType(), user.getName(), user.getSurname(), user.getNumber());
			listModel.addElement(contactInfo);
			idList.add(entry.getKey());
		}

		countLabel.setText("Всего контактов: " + listModel.getSize());
  }

 private void addUserDialog(HashMap<Integer, UserInfo> contacts) {
		String[] contactTypes = {"Личный", "Рабочий"};
		String selectedType   = (String) JOptionPane.showInputDialog(
			this, "Выберите тип контакта:", "Тип контакта",
			JOptionPane.QUESTION_MESSAGE, null, contactTypes, contactTypes[0]);

		if (selectedType == null) return;

		JDialog dialog = new JDialog(this, "Добавить " + selectedType.toLowerCase() + " контакт", true);
		dialog.setLayout(new GridLayout(0, 2, 4, 4));
		dialog.setSize(400, 300);
		dialog.setLocationRelativeTo(this);

		JTextField numberField  	= new JTextField();
		JTextField nameField    	= new JTextField();
		JTextField surnameField 	= new JTextField();

		// Work
		JTextField birthDateField = new JTextField();
		JTextField extraInfoField = new JTextField();
		JTextField addressField   = new JTextField();

		// Pers
		JTextField companyField 	= new JTextField();
		JTextField postField  	  = new JTextField();
		JTextField emailField     = new JTextField();

		dialog.add(new JLabel("* - обязательные поля"));
		dialog.add(new JLabel(""));
		dialog.add(new JLabel("Номер*:"));
		dialog.add(numberField);
		dialog.add(new JLabel("Имя*:"));
		dialog.add(nameField);
		dialog.add(new JLabel("Фамилия:"));
		dialog.add(surnameField);

		if ("Личный".equals(selectedType)) {
			dialog.add(new JLabel("Дата рождения:"));
			dialog.add(birthDateField);
			dialog.add(new JLabel("Описание:"));
			dialog.add(extraInfoField);
			dialog.add(new JLabel("Адрес:"));
			dialog.add(addressField);
		} else {
			dialog.add(new JLabel("Компания:"));
			dialog.add(companyField);
			dialog.add(new JLabel("Должность:"));
			dialog.add(postField);
			dialog.add(new JLabel("Почта:"));
			dialog.add(emailField);
		}

		JButton addButton = new JButton("Добавить");
		addButton.addActionListener(event -> {
			String number  = numberField.getText();
			String name 	 = nameField.getText();
			String surname = surnameField.getText();

			if (!name.isEmpty() && !number.isEmpty()) {
				try {
					UserInfo newContact;

					if ("Личный".equals(selectedType)) {
						newContact = new PersonalUserInfo(
							number, name, surname,
							birthDateField.getText(),
							extraInfoField.getText(),
							addressField.getText()
						);
					} else {
						newContact = new WorkUserInfo(
							number, name, surname,
							companyField.getText(),
							postField.getText(),
							emailField.getText()
						);
					}

					int id = ContactBook.addUser(newContact, contacts);
					updateContactsList(contacts);
					dialog.dispose();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(dialog, "Ошибка при создании контакта: " + e.getMessage());
				}
			} else {
				JOptionPane.showMessageDialog(dialog, "Заполните обязательные поля!");
			}
		});

		dialog.add(new JLabel(""));
		dialog.add(addButton);
		dialog.setVisible(true);
	}

	private void setupContextMenu() {
    contextMenu = new JPopupMenu();

    contactsList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					int index = contactsList.locationToIndex(e.getPoint());

					if (index >= 0) {
						contactsList.setSelectedIndex(index);
						int contactId    = idList.get(index);
						UserInfo contact = contacts.get(contactId);

						contextMenu.removeAll();

						JMenuItem editMenuItem     = new JMenuItem("Изменить");
						JMenuItem deleteMenuItem   = new JMenuItem("Удалить");
						JMenuItem aboutMenuItem    = new JMenuItem("Подробнее");

						JMenuItem favoriteMenuItem = new JMenuItem(
							favoriteContacts.contains(contact) ? "Удалить из избранного"
							: "Добавить в избранное"
						);

						editMenuItem.addActionListener(e1 -> editSelectedContact());
						deleteMenuItem.addActionListener(e1 -> deleteSelectedContact());
						aboutMenuItem.addActionListener(e1 -> aboutSelectedContact());
						favoriteMenuItem.addActionListener(e1 -> favoriteSelectedContact());

						contextMenu.add(editMenuItem);
						contextMenu.add(deleteMenuItem);
						contextMenu.add(aboutMenuItem);
						contextMenu.add(favoriteMenuItem);

						contextMenu.addSeparator();

						JMenuItem callMenuItem = new JMenuItem("Позвонить");
						callMenuItem.addActionListener(e1 -> callSelectedContact());
						contextMenu.add(callMenuItem);

						if (contact instanceof WorkUserInfo) {
							JMenuItem emailMenuItem = new JMenuItem("Отправить сообщение");
							emailMenuItem.addActionListener(e1 -> emailSelectedContact());
							contextMenu.add(emailMenuItem);
						}

						contextMenu.show(contactsList, e.getX(), e.getY());
					}
				}
        }
    });
	}

	private void editSelectedContact() {
    int selectedIndex = contactsList.getSelectedIndex();

    if (selectedIndex >= 0 && selectedIndex < idList.size()) {
			int contactId = idList.get(selectedIndex);
			UserInfo user = contacts.get(contactId);
			editUserDialog(contactId, user);
    }
	}

	private void deleteSelectedContact() {
		int selectedIndex = contactsList.getSelectedIndex();
		if (selectedIndex >= 0 && selectedIndex < idList.size()) {
			int contactId = idList.get(selectedIndex);
			int confirm = JOptionPane.showConfirmDialog(this,
				"Вы уверены, что хотите удалить этот контакт?",
				"Подтверждение удаления",
				JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				contacts.remove(contactId);
				updateContactsList(contacts);
			}
		}
	}

	private void aboutSelectedContact() {
		int selectedIndex = contactsList.getSelectedIndex();
		if (selectedIndex >= 0 && selectedIndex < idList.size()) {
			int contactId = idList.get(selectedIndex);
			UserInfo contact = contacts.get(contactId);

			JTextArea textArea = new JTextArea(contact.getFullInfo());
			textArea.setEditable(false);
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);

			JScrollPane scrollPane = new JScrollPane(textArea);
			scrollPane.setPreferredSize(new Dimension(400, 300));

			JOptionPane.showMessageDialog(this, scrollPane,
				"Подробная информация о контакте", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void favoriteSelectedContact() {
		int selectedIndex = contactsList.getSelectedIndex();
		if (selectedIndex >= 0 && selectedIndex < idList.size()) {
			int contactId = idList.get(selectedIndex);
			UserInfo contact = contacts.get(contactId);

			if (favoriteContacts.contains(contact)) {
				favoriteContacts.remove(contact);

				JOptionPane.showMessageDialog(this,
					"Контакт удален из избранного",
					"Избранное", JOptionPane.INFORMATION_MESSAGE);
			} else {
				favoriteContacts.add(contact);

				JOptionPane.showMessageDialog(this,
					"Контакт добавлен в избранное",
					"Избранное", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	private void editUserDialog(int contactId, UserInfo user) {
    String contactType = user.getContactType();
    JDialog dialog = new JDialog(this, "Редактировать " + contactType.toLowerCase() + " контакт", true);

    int rows = ("Личный".equals(contactType)) ? 8 : 8;
    dialog.setLayout(new GridLayout(rows, 2, 4, 4));
    dialog.setSize(400, 350);
    dialog.setLocationRelativeTo(this);

    JTextField numberField  	= new JTextField(user.getNumber());
    JTextField nameField      = new JTextField(user.getName());
    JTextField surnameField   = new JTextField(user.getSurname());
    JTextField birthDateField = new JTextField();
    JTextField aboutUserField = new JTextField();
    JTextField addressField 	= new JTextField();
    JTextField companyField 	= new JTextField();
    JTextField postField 			= new JTextField();
    JTextField emailField 		= new JTextField();

    if (user instanceof PersonalUserInfo) {
			PersonalUserInfo personal = (PersonalUserInfo) user;
			birthDateField.setText(personal.getBirthDate());
			aboutUserField.setText(personal.getAboutUser());
			addressField.setText(personal.getAddress());
    } else if (user instanceof WorkUserInfo) {
			WorkUserInfo work = (WorkUserInfo) user;
			companyField.setText(work.getCompany());
			postField.setText(work.getPost());
			emailField.setText(work.getEmail());
    }

    dialog.add(new JLabel("* - обязательные поля"));
    dialog.add(new JLabel(""));
    dialog.add(new JLabel("Номер*:"));
    dialog.add(numberField);
    dialog.add(new JLabel("Имя*:"));
    dialog.add(nameField);
    dialog.add(new JLabel("Фамилия:"));
    dialog.add(surnameField);

    if (user instanceof PersonalUserInfo) {
			dialog.add(new JLabel("Дата рождения:"));
			dialog.add(birthDateField);
			dialog.add(new JLabel("Описание:"));
			dialog.add(aboutUserField);
			dialog.add(new JLabel("Адрес:"));
			dialog.add(addressField);
    } else if (user instanceof WorkUserInfo) {
			dialog.add(new JLabel("Компания:"));
			dialog.add(companyField);
			dialog.add(new JLabel("Должность:"));
			dialog.add(postField);
			dialog.add(new JLabel("Email:"));
			dialog.add(emailField);
    }

    JButton saveButton = new JButton("Сохранить");
    saveButton.addActionListener(event -> {
			String number = numberField.getText();
			String name = nameField.getText();
			String surname = surnameField.getText();

			if (!name.isEmpty() && !number.isEmpty()) {
				user.setNumber(number);
				user.setName(name);
				user.setSurname(surname);

				if (user instanceof PersonalUserInfo) {
					PersonalUserInfo personal = (PersonalUserInfo) user;
					personal.setBirthDate(birthDateField.getText());
					personal.setAboutUser(aboutUserField.getText());
					personal.setAddress(addressField.getText());
				} else if (user instanceof WorkUserInfo) {
					WorkUserInfo work = (WorkUserInfo) user;
					work.setCompany(companyField.getText());
					work.setPost(postField.getText());
					work.setEmail(emailField.getText());
				}

				contacts.put(contactId, user);
				updateContactsList(contacts);
				dialog.dispose();
			} else
				JOptionPane.showMessageDialog(dialog, "Заполните обязательные поля!");
    });

    dialog.add(new JLabel(""));
    dialog.add(saveButton);
    dialog.setVisible(true);
}

	private void filterContacts() {
		currentFilter = searchField.getText().toLowerCase();
		updateContactsList(contacts);
  }

	private void callSelectedContact() {
    int selectedIndex = contactsList.getSelectedIndex();

    if (selectedIndex >= 0 && selectedIndex < idList.size()) {
			int contactId = idList.get(selectedIndex);
			UserInfo contact = contacts.get(contactId);

			JDialog callDialog = new JDialog(this, "Звонок", false);
			callDialog.setLayout(new BorderLayout());
			callDialog.setSize(300, 200);
			callDialog.setLocationRelativeTo(this);

			JLabel statusLabel = new JLabel("🕐 Начинаем звонок...", JLabel.CENTER);
			callDialog.add(statusLabel, BorderLayout.CENTER);

			JButton closeBtn = new JButton("Закрыть");
			closeBtn.setEnabled(true);
			callDialog.add(closeBtn, BorderLayout.SOUTH);

			Thread soundThread = new Thread(() -> {
				try {
					SwingUtilities.invokeLater(() -> statusLabel.setText("🔔 Идет звонок..."));

					if (contact instanceof PersonalUserInfo)
						((PersonalUserInfo) contact).call();
					else if (contact instanceof WorkUserInfo)
						((WorkUserInfo) contact).call();

					SwingUtilities.invokeLater(() -> {
						statusLabel.setText("✅ Звонок завершен");
					});

				} catch (Exception e) {
					SwingUtilities.invokeLater(() -> {
						statusLabel.setText("❌ Ошибка: " + e.getMessage());
					});
				}
			});

			closeBtn.addActionListener(e -> {
				soundThread.interrupt();
				callDialog.dispose();
			});

			soundThread.start();
			callDialog.setVisible(true);
	}
}

	private void emailSelectedContact() {
		int selectedIndex = contactsList.getSelectedIndex();
		if (selectedIndex >= 0 && selectedIndex < idList.size()) {
			int contactId = idList.get(selectedIndex);
			UserInfo contact = contacts.get(contactId);

			if (contact instanceof WorkUserInfo) {
				WorkUserInfo workContact = (WorkUserInfo) contact;
				String email 						 = workContact.getEmail();
				String message 					 = JOptionPane.showInputDialog(this,
					"Введите сообщение для " + workContact.getName() + ":\nПолучатель: " + email,
					"Отправить сообщение", JOptionPane.QUESTION_MESSAGE);

				if (message != null && !message.trim().isEmpty()) {
					// 50/50 работающая халява. Actually useless
					String[] randErrors = {
						"Потеряно соединение с интернетом...",
						"Указанная почта не найдена :(",
						"Пользователь запретил Вам отправлять ему сообщения",
						"Вы не нравитесь получателю"
					};

					Random rand = new Random();
					int status  = rand.nextInt(4);
					System.out.println("[DEBUG_EMAIL_STATUS]: " + status);

					if (status % 2 == 0) {
						JOptionPane.showMessageDialog(this,
							"✉️ Сообщение отправлено на " + email + ":\n" + message,
							"✅ Сообщение отправлено", JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(this,
							"✉️ Сообщение не было отправлено на " + email + ":\n" + randErrors[status],
							"❌ Сообщение не отправлено", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		}
	}
}
