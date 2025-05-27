package librarygui;

import abstractfactory.*;
import decorator.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.EmptyBorder;
import singleton.Book;
import singleton.BookManager;

public class BookPanel extends JPanel {
    private MainLibraryGUI mainGUI;
    private BookManager bookManager;
    private Map<String, FavoriteBookDecorator> decoratedBooks;
    private JTable bookTable;
    private JTextField idField;
    private JTextField titleField;
    private JTextField authorField;
    private JComboBox<String> typeCombo;

    public BookPanel(MainLibraryGUI mainGUI) {
        this.mainGUI = mainGUI;
        this.bookManager = mainGUI.getBookManager();
        this.decoratedBooks = new HashMap<>();
        loadExistingBooks();
        setupUI();
    }

    private void loadExistingBooks() {
        bookManager.getAllBooks().forEach(book -> {
            FavoriteBookDecorator decorator = new FavoriteBookDecorator(book);
            decoratedBooks.put(book.getId(), decorator);
            book.registerObserver(mainGUI);
        });
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 10));
        
        // Input Panel (45% of height)
        JPanel topSection = new JPanel();
        topSection.setLayout(new BorderLayout(0, 5));
        topSection.setPreferredSize(new Dimension(1024, 346));

        // Form Panel
        JPanel formPanel = createFormPanel();
        
        // Button Panel
        JPanel buttonPanel = createButtonPanel();

        // Center the form and add padding
        JPanel centeringPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        centeringPanel.add(formPanel);
        
        // Add components to sections
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.add(centeringPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        topSection.add(contentPanel, BorderLayout.CENTER);

        // Book Table Panel
        JPanel tablePanel = createTablePanel();

        // Add sections to main panel
        add(topSection, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);

        // Initialize table
        updateData();
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize form components
        Dimension fixedSize = new Dimension(250, 40);
        idField = new JTextField();
        titleField = new JTextField();
        authorField = new JTextField();
        typeCombo = new JComboBox<>(new String[]{
            "Sách giấy", "Sách điện tử", "Sách học thuật", "Sách giải trí"
        });

        // Style components
        Font inputFont = new Font("Arial", Font.PLAIN, 14);
        Component[] inputs = {idField, titleField, authorField, typeCombo};
        for (Component comp : inputs) {
            comp.setFont(inputFont);
            comp.setPreferredSize(fixedSize);
            comp.setMinimumSize(fixedSize);
        }

        // Labels
        JLabel[] labels = {
            new JLabel("Mã Sách:"),
            new JLabel("Tên Sách:"),
            new JLabel("Tác Giả:"),
            new JLabel("Loại Sách:")
        };
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        for (JLabel label : labels) {
            label.setFont(labelFont);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        }

        // Add components to form
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3;
            formPanel.add(labels[i], gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            formPanel.add(inputs[i], gbc);
        }

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton addButton = new JButton("Thêm Sách");
        JButton updateButton = new JButton("Cập Nhật");
        JButton removeButton = new JButton("Xóa Sách");
        JButton refreshButton = new JButton("Làm Mới");

        // Style buttons
        Dimension buttonSize = new Dimension(120, 35);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        for (JButton button : new JButton[]{addButton, updateButton, removeButton, refreshButton}) {
            button.setFont(buttonFont);
            button.setPreferredSize(buttonSize);
        }

        // Add button actions
        addButton.addActionListener(e -> handleAddBook());
        updateButton.addActionListener(e -> handleUpdateBook());
        removeButton.addActionListener(e -> handleRemoveBook());
        refreshButton.addActionListener(e -> handleRefresh());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(refreshButton);

        return buttonPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        
        String[] columnNames = {"Mã Sách", "Tên Sách", "Tác Giả", "Loại", "Yêu thích"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Boolean.class;
                return String.class;
            }
        };
        
        bookTable = new JTable(tableModel);
        setupTable();

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Danh Sách Sách",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 18)
        ));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void setupTable() {
        bookTable.setFont(new Font("Arial", Font.PLAIN, 16));
        bookTable.setRowHeight(35);
        bookTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add selection listener
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = bookTable.getSelectedRow();
                if (row >= 0) {
                    String id = (String) bookTable.getValueAt(row, 0);
                    Book book = bookManager.getBook(id);
                    if (book != null) {
                        populateForm(book);
                    }
                }
            }
        });

        // Add checkbox listener
        bookTable.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (col == 4 && row >= 0) {
                String bookId = (String) bookTable.getValueAt(row, 0);
                boolean isFavorite = (Boolean) bookTable.getValueAt(row, 4);
                updateBookFavorite(bookId, isFavorite);
            }
        });
    }

    private void handleAddBook() {
        String id = idField.getText();
        String title = titleField.getText();
        String author = authorField.getText();
        String type = (String) typeCombo.getSelectedItem();
        
        if (!validateFields(id, title, author) || !validateDuplicateBook(id)) {
            return;
        }

        String englishType = convertBookType(type);
        AbstractBookFactory factory = getBookFactory(englishType);
        Book book = factory.createBook(id, title, author);
        FavoriteBookDecorator decorator = new FavoriteBookDecorator(book);
        decoratedBooks.put(id, decorator);
        book.registerObserver(mainGUI);
        bookManager.addBook(book);
        updateData();
        clearForm();
        JOptionPane.showMessageDialog(this, "Thêm sách thành công!");
    }

    private void handleUpdateBook() {
        String id = idField.getText();
        Book oldBook = bookManager.getBook(id);
        if (oldBook != null) {
            String title = titleField.getText();
            String author = authorField.getText();
            String type = (String) typeCombo.getSelectedItem();
            
            String englishType = convertBookType(type);
            AbstractBookFactory factory = getBookFactory(englishType);
            Book newBook = factory.createBook(id, title, author);
            newBook.setFavorite(oldBook.isFavorite());
            
            bookManager.removeBook(id);
            decoratedBooks.remove(id);
            FavoriteBookDecorator decorator = new FavoriteBookDecorator(newBook);
            decoratedBooks.put(id, decorator);
            newBook.registerObserver(mainGUI);
            bookManager.addBook(newBook);
            
            updateData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Cập nhật sách thành công!");
        }
    }

    private void handleRemoveBook() {
        String id = idField.getText();
        bookManager.removeBook(id);
        decoratedBooks.remove(id);
        updateData();
        clearForm();
        JOptionPane.showMessageDialog(this, "Xóa sách thành công!");
    }

    private void handleRefresh() {
        clearForm();
        bookTable.clearSelection();
    }

    private void updateBookFavorite(String bookId, boolean isFavorite) {
        FavoriteBookDecorator decorator = decoratedBooks.get(bookId);
        if (decorator != null) {
            decorator.setFavorite(isFavorite);
            Book book = bookManager.getBook(bookId);
            if (book != null) {
                book.setFavorite(isFavorite);
                bookManager.updateBook(book);
            }
        }
    }

    public void updateData() {
        DefaultTableModel model = (DefaultTableModel) bookTable.getModel();
        model.setRowCount(0);
        bookManager.getAllBooks().forEach(book -> {
            FavoriteBookDecorator decorator = decoratedBooks.get(book.getId());
            if (decorator == null) {
                decorator = new FavoriteBookDecorator(book);
                decoratedBooks.put(book.getId(), decorator);
                book.registerObserver(mainGUI);
            }
            model.addRow(new Object[]{
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                convertBookTypeToVietnamese(book.getType()),
                book.isFavorite()
            });
        });
        model.fireTableDataChanged();
    }

    private void clearForm() {
        idField.setText("");
        titleField.setText("");
        authorField.setText("");
        typeCombo.setSelectedIndex(0);
    }

    private void populateForm(Book book) {
        idField.setText(book.getId());
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        String type = book.getType();
        switch (type) {
            case "paper":
            case "Paper Book":
                typeCombo.setSelectedItem("Sách giấy"); break;
            case "ebook":
            case "E-Book":
                typeCombo.setSelectedItem("Sách điện tử"); break;
            case "academic":
            case "Academic Book":
                typeCombo.setSelectedItem("Sách học thuật"); break;
            case "entertainment":
            case "Entertainment Book":
                typeCombo.setSelectedItem("Sách giải trí"); break;
            default:
                typeCombo.setSelectedItem(type);
        }
    }

    private boolean validateFields(String... fields) {
        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private boolean validateDuplicateBook(String id) {
        if (bookManager.getBook(id) != null) {
            JOptionPane.showMessageDialog(this, "Mã sách đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private String convertBookType(String type) {
        switch (type) {
            case "Sách giấy": return "Paper Book";
            case "Sách điện tử": return "E-Book";
            case "Sách học thuật": return "Academic Book";
            case "Sách giải trí": return "Entertainment Book";
            default: return type;
        }
    }

    private String convertBookTypeToVietnamese(String type) {
        switch (type) {
            case "Paper Book":
            case "paper":
                return "Sách giấy";
            case "E-Book":
            case "ebook":
                return "Sách điện tử";
            case "Academic Book":
            case "academic":
                return "Sách học thuật";
            case "Entertainment Book":
            case "entertainment":
                return "Sách giải trí";
            default:
                return type;
        }
    }

    private AbstractBookFactory getBookFactory(String type) {
        switch (type) {
            case "Paper Book": return new PaperBookFactory();
            case "E-Book": return new EBookFactory();
            case "Academic Book": return new AcademicBookFactory();
            default: return new EntertainmentBookFactory();
        }
    }
}