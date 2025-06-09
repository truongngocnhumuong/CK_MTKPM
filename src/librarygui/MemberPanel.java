package librarygui;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import singleton.Member;
import singleton.MemberManager;

public class MemberPanel extends JPanel {
    private MainLibraryGUI mainGUI;
    private MemberManager memberManager;
    private JTable memberTable;
    private JTextField idField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField searchField;
    private JButton searchButton;

    public MemberPanel(MainLibraryGUI mainGUI) {
        this.mainGUI = mainGUI;
        this.memberManager = mainGUI.getMemberManager();
        setupUI();
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

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(topSection);
        splitPane.setBottomComponent(tablePanel);
        splitPane.setResizeWeight(0.6);
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

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));

        searchField = new JTextField(40);
        searchField.setPreferredSize(new Dimension(500, 35));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.putClientProperty("JTextField.placeholderText", "Nhập mã thành viên, tên hoặc email để tìm kiếm...");

        // Thêm DocumentListener để tự động tìm kiếm khi gõ
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

        Dimension fixedSize = new Dimension(400, 35);
        idField = new JTextField();
        nameField = new JTextField();
        emailField = new JTextField();

        Component[] inputs = {idField, nameField, emailField};
        Font inputFont = new Font("Arial", Font.PLAIN, 14);
        for (Component comp : inputs) {
            comp.setFont(inputFont);
            comp.setPreferredSize(fixedSize);
            comp.setMinimumSize(fixedSize);
        }

        JLabel[] labels = {
            new JLabel("Mã Thành Viên:"),
            new JLabel("Họ Tên:"),
            new JLabel("Email:")
        };
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Dimension labelSize = new Dimension(120, 35);
        for (JLabel label : labels) {
            label.setFont(labelFont);
            label.setPreferredSize(labelSize);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        }

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.2;
            formPanel.add(labels[i], gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.8;
            formPanel.add(inputs[i], gbc);
        }

        // Wrap formPanel in a container with padding
        JPanel formContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        formContainer.add(formPanel);
        formContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        return formContainer;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));

        JButton addButton = new JButton("Thêm");
        JButton updateButton = new JButton("Cập Nhật");
        JButton removeButton = new JButton("Xóa");
        JButton refreshButton = new JButton("Làm Mới");

        Dimension buttonSize = new Dimension(150, 40);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        JButton[] buttons = {addButton, updateButton, removeButton, refreshButton};
        for (JButton button : buttons) {
            button.setFont(buttonFont);
            button.setPreferredSize(buttonSize);
            button.setBackground(new Color(240, 240, 240));
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));
        }

        addButton.addActionListener(e -> handleAddMember());
        updateButton.addActionListener(e -> handleUpdateMember());
        removeButton.addActionListener(e -> handleRemoveMember());
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

        String[] columnNames = {"Mã Thành Viên", "Họ Tên", "Email"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        memberTable = new JTable(tableModel);
        setupTable();

        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Danh Sách Thành Viên",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16)
        ));

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void setupTable() {
        memberTable.setFont(new Font("Arial", Font.PLAIN, 14));
        memberTable.setRowHeight(30);
        memberTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        memberTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = memberTable.getSelectedRow();
                if (row >= 0) {
                    idField.setText((String) memberTable.getValueAt(row, 0));
                    nameField.setText((String) memberTable.getValueAt(row, 1));
                    emailField.setText((String) memberTable.getValueAt(row, 2));
                }
            }
        });
    }
    private void handleAddMember() {
        String id = idField.getText();
        String name = nameField.getText();
        String email = emailField.getText();

        if (!validateFields(id, name, email) || !validateDuplicateMember(id)) {
            return;
        }

        Member member = new Member(id, name, email);
        member.registerObserver(mainGUI);
        memberManager.addMember(member);
        updateData();
        clearForm();
        JOptionPane.showMessageDialog(this, "Thêm thành viên thành công!");
    }

    private void handleUpdateMember() {
        String id = idField.getText();
        Member existingMember = memberManager.getMember(id);

        if (existingMember != null) {
            String name = nameField.getText();
            String email = emailField.getText();

            Member updatedMember = new Member(id, name, email);
            updatedMember.registerObserver(mainGUI);
            memberManager.updateMember(updatedMember);
            updateData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Cập nhật thành viên thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thành viên!");
        }
    }

    private void handleRemoveMember() {
        String id = idField.getText();
        if (id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã thành viên cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        memberManager.removeMember(id);
        updateData();
        clearForm();
        JOptionPane.showMessageDialog(this, "Xóa thành viên thành công!");
    }

    private void handleRefresh() {
        clearForm();
        memberTable.clearSelection();
        updateData();
    }

    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            updateData();
            return;
        }

        List<Member> searchResults = new ArrayList<>();
        for (Member member : memberManager.getAllMembers()) {
            // Tìm kiếm theo ID, tên hoặc email
            if (member.getId().toLowerCase().contains(query) ||
                member.getName().toLowerCase().contains(query) ||
                member.getEmail().toLowerCase().contains(query)) {
                searchResults.add(member);
            }
        }

        updateSearchResults(searchResults);
    }

    public void updateData() {
        DefaultTableModel model = (DefaultTableModel) memberTable.getModel();
        model.setRowCount(0);
        memberManager.getAllMembers().forEach(member -> {
            model.addRow(new Object[]{
                member.getId(),
                member.getName(),
                member.getEmail()
            });
        });
        model.fireTableDataChanged();
    }

    private void updateSearchResults(List<Member> members) {
        DefaultTableModel model = (DefaultTableModel) memberTable.getModel();
        model.setRowCount(0);
        members.forEach(member -> {
            model.addRow(new Object[]{
                member.getId(),
                member.getName(),
                member.getEmail()
            });
        });
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
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

    private boolean validateDuplicateMember(String id) {
        if (memberManager.getMember(id) != null) {
            JOptionPane.showMessageDialog(this, "Mã thành viên đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
