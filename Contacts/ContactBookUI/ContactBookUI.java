package ContactBookUI;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;

// UI and ADT
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

public class ContactBookUI extends JFrame {
	private HashMap<Integer, UserInfo> contacts;
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
			UserInfo user = entry.getValue();
			String contactInfo = String.format("%s %s: %s",
				user.getName(), user.getSurname(), user.getNumber());
			listModel.addElement(contactInfo);
			idList.add(entry.getKey());
		}

		countLabel.setText("Всего контактов: " + listModel.getSize());
  }

	private void addUserDialog(HashMap<Integer, UserInfo> contacts) {
		JDialog dialog = new JDialog(this, "Добавить контакт", true);
		dialog.setLayout(new GridLayout(5, 2, 4, 4));
		dialog.setSize(300, 250);
		dialog.setLocationRelativeTo(this);

		JTextField numberField = new JTextField();
		JTextField nameField = new JTextField();
		JTextField surnameField = new JTextField();

		JButton addButton = new JButton("Добавить");
		addButton.addActionListener(event -> {
			String number = numberField.getText();
			String name = nameField.getText();
			String surname = surnameField.getText();

			if (!name.isEmpty() && !number.isEmpty()) {
				ContactBook.addUser(number, name, surname, contacts);
				updateContactsList(contacts);
				dialog.dispose();
			} else
				JOptionPane.showMessageDialog(dialog, "Заполните обязательные поля!");
		});

		dialog.add(new JLabel("* - обязательные поля"));
		dialog.add(new JLabel(""));
		dialog.add(new JLabel("Номер*:"));
		dialog.add(numberField);
		dialog.add(new JLabel("Имя*:"));
		dialog.add(nameField);
		dialog.add(new JLabel("Фамилия:"));
		dialog.add(surnameField);
		dialog.add(addButton);

		dialog.setVisible(true);
	}

	private void setupContextMenu() {
    contextMenu 						 = new JPopupMenu();
    JMenuItem editMenuItem   = new JMenuItem("Изменить");
    JMenuItem deleteMenuItem = new JMenuItem("Удалить");

    editMenuItem.addActionListener(e -> editSelectedContact());
    deleteMenuItem.addActionListener(e -> deleteSelectedContact());

    contextMenu.add(editMenuItem);
    contextMenu.add(deleteMenuItem);

    contactsList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					int index = contactsList.locationToIndex(e.getPoint());

					if (index >= 0) {
						contactsList.setSelectedIndex(index);
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

	// TODO: refactor this.
	// This part dublicate of userAdd. Maybe method's overload?
	private void editUserDialog(int contactId, UserInfo user) {
		JDialog dialog = new JDialog(this, "Редактировать контакт", true);
		dialog.setLayout(new GridLayout(5, 2, 4, 4));
		dialog.setSize(300, 250);
		dialog.setLocationRelativeTo(this);

		JTextField numberField = new JTextField(user.getNumber());
		JTextField nameField = new JTextField(user.getName());
		JTextField surnameField = new JTextField(user.getSurname());

		JButton saveButton = new JButton("Сохранить");
		saveButton.addActionListener(event -> {
			String number = numberField.getText();
			String name = nameField.getText();
			String surname = surnameField.getText();

			if (!name.isEmpty() && !number.isEmpty()) {
				user.setNumber(number);
				user.setName(name);
				user.setSurname(surname);
				contacts.put(contactId, user);
				updateContactsList(contacts);
				dialog.dispose();
			} else
				JOptionPane.showMessageDialog(dialog, "Заполните обязательные поля!");
		});

		dialog.add(new JLabel("* - обязательные поля"));
		dialog.add(new JLabel(""));
		dialog.add(new JLabel("Номер*:"));
		dialog.add(numberField);
		dialog.add(new JLabel("Имя*:"));
		dialog.add(nameField);
		dialog.add(new JLabel("Фамилия:"));
		dialog.add(surnameField);
		dialog.add(new JLabel(""));
		dialog.add(saveButton);

		dialog.setVisible(true);
	}

	private void filterContacts() {
		currentFilter = searchField.getText().toLowerCase();
		updateContactsList(contacts);
  }
}
