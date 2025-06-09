package librarygui;

import abstractfactory.*;
import decorator.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import singleton.Book;
import singleton.BookManager;
import strategy.*;

public class BookPanel extends JPanel {
    private MainLibraryGUI mainGUI;
    private BookManager bookManager;
    private Map<String, FavoriteBookDecorator> decoratedBooks;
    private JTable bookTable;
    private JTextField idField;
    private JTextField titleField;
    private JTextField authorField;
    private JComboBox<String> typeCombo;

    // Tìm kiếm nâng cao
    private JTextField searchField;
    private JButton searchButton;
    private SearchContext<Book> searchContext;

    public BookPanel(MainLibraryGUI mainGUI) {
        this.mainGUI = mainGUI;
        this.bookManager = mainGUI.getBookManager();
        this.decoratedBooks = new HashMap<>();
        this.searchContext = new SearchContext<>();
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
        setLayout(new BorderLayout(0, 15));

        // Top section
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));

        // Search panel ở trên cùng với chiều rộng đầy đủ
        JPanel searchPanel = createSearchPanel();
        searchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Search", 
            TitledBorder.LEFT, TitledBorder.TOP));
        searchPanel.setBackground(new Color(245, 245, 245));
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, searchPanel.getPreferredSize().height));

        // Form panel
        JPanel formPanel = createFormPanel();
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Form", 
            TitledBorder.LEFT, TitledBorder.TOP));
        formPanel.setBackground(new Color(245, 245, 245));
        formPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, formPanel.getPreferredSize().height));

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, buttonPanel.getPreferredSize().height));

        // Add components to top section
        topSection.add(searchPanel);
        topSection.add(Box.createVerticalStrut(10));
        topSection.add(formPanel);
        topSection.add(Box.createVerticalStrut(10));
        topSection.add(buttonPanel);

        // Table panel
        JPanel tablePanel = createTablePanel();
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Data Table", 
            TitledBorder.LEFT, TitledBorder.TOP));
        tablePanel.setBackground(new Color(245, 245, 245));

        // Split pane với tỷ lệ giống MemberPanel
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(topSection);
        splitPane.setBottomComponent(tablePanel);
        splitPane.setResizeWeight(0.6); // Đặt tỷ lệ giống với MemberPanel
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);

        // Add to main panel
        add(splitPane, BorderLayout.CENTER);

        // Maintain split proportion
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                splitPane.setDividerLocation(0.6);
            }
        });

        updateData();
    }

    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            updateData();
            return;
        }

        // Tìm kiếm kết hợp theo cả mã sách, tên sách và tác giả
        List<Book> searchResults = new ArrayList<>();
        for (Book book : bookManager.getAllBooks()) {
            if (book.getId().toLowerCase().contains(query) ||
                book.getTitle().toLowerCase().contains(query) ||
                book.getAuthor().toLowerCase().contains(query)) {
                searchResults.add(book);
            }
        }

        updateSearchResults(searchResults);
    }

    private void updateSearchResults(List<Book> books) {
        DefaultTableModel model = (DefaultTableModel) bookTable.getModel();
        model.setRowCount(0);
        books.forEach(book -> model.addRow(new Object[]{
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            convertBookTypeToVietnamese(book.getType()),
            book.isFavorite()
        }));
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));

        searchField = new JTextField(40);
        searchField.setPreferredSize(new Dimension(500, 35));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.putClientProperty("JTextField.placeholderText", "Nhập mã sách, tên sách hoặc tác giả để tìm kiếm...");

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { handleSearch(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { handleSearch(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { handleSearch(); }
        });

        searchButton = new JButton("Làm mới");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setPreferredSize(new Dimension(120, 35));
        searchButton.setBackground(new Color(240, 240, 240));
        searchButton.setFocusPainted(false);
        searchButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        searchButton.addActionListener(e -> {
            searchField.setText("");
            updateData();
        });

        panel.add(searchField);
        panel.add(searchButton);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;

        // Kích thước cho các ô input (giảm xuống vì chia 2 cột)
        Dimension fixedSize = new Dimension(250, 35);
        idField = new JTextField();
        titleField = new JTextField();
        authorField = new JTextField();
        typeCombo = new JComboBox<>(new String[]{
            "Sách giấy", "Sách điện tử", "Sách học thuật", "Sách giải trí"
        });

        Font inputFont = new Font("Arial", Font.PLAIN, 14);
        Component[] inputs = {idField, titleField, authorField, typeCombo};
        for (Component comp : inputs) {
            comp.setFont(inputFont);
            comp.setPreferredSize(fixedSize);
            comp.setMinimumSize(fixedSize);
        }

        // Labels với kích thước phù hợp
        JLabel[] labels = {
            new JLabel("Mã Sách:"),
            new JLabel("Tên Sách:"),
            new JLabel("Tác Giả:"),
            new JLabel("Loại Sách:")
        };
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Dimension labelSize = new Dimension(100, 35);
        for (JLabel label : labels) {
            label.setFont(labelFont);
            label.setPreferredSize(labelSize);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        }

        // Sắp xếp thành 2 cột
        for (int i = 0; i < labels.length; i++) {
            // Tính toán vị trí hàng và cột
            int row = i / 2;    // 0, 0, 1, 1
            int col = i % 2;    // 0, 1, 0, 1
            
            // Label
            gbc.gridx = col * 2;  // 0, 2, 0, 2
            gbc.gridy = row;      // 0, 0, 1, 1
            gbc.weightx = 0.1;
            formPanel.add(labels[i], gbc);

            // Input field
            gbc.gridx = col * 2 + 1;  // 1, 3, 1, 3
            gbc.weightx = 0.4;
            formPanel.add(inputs[i], gbc);
        }

        // Wrap formPanel in a container with padding
        JPanel formContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        formContainer.add(formPanel);
        formContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        return formContainer;
    }
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15)); // Tăng khoảng cách giữa các nút

        JButton addButton = new JButton("Thêm Sách");
        JButton updateButton = new JButton("Cập Nhật");
        JButton removeButton = new JButton("Xóa Sách");
        JButton refreshButton = new JButton("Làm Mới");

        // Tăng kích thước nút
        Dimension buttonSize = new Dimension(150, 40);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        for (JButton button : new JButton[]{addButton, updateButton, removeButton, refreshButton}) {
            button.setFont(buttonFont);
            button.setPreferredSize(buttonSize);
            button.setBackground(new Color(240, 240, 240));
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));
        }

        addButton.addActionListener(e -> handleAddBook());
        updateButton.addActionListener(e -> handleUpdateBook());
        removeButton.addActionListener(e -> handleRemoveBook());
        refreshButton.addActionListener(e -> handleRefresh());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(refreshButton);

        // Thêm padding cho panel chứa nút
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

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
