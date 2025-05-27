package librarygui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import singleton.Member;
import singleton.MemberManager;

public class MemberPanel extends JPanel {
    private MainLibraryGUI mainGUI;
    private MemberManager memberManager;
    private JTable memberTable;
    private JTextField idField;
    private JTextField nameField;
    private JTextField emailField;

    public MemberPanel(MainLibraryGUI mainGUI) {
        this.mainGUI = mainGUI;
        this.memberManager = mainGUI.getMemberManager();
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 10));
        
        // Input Panel
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

        // Member Table Panel
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
        nameField = new JTextField();
        emailField = new JTextField();

        // Style components
        Font inputFont = new Font("Arial", Font.PLAIN, 14);
        Component[] inputs = {idField, nameField, emailField};
        for (Component comp : inputs) {
            comp.setFont(inputFont);
            comp.setPreferredSize(fixedSize);
            comp.setMinimumSize(fixedSize);
        }

        // Labels
        JLabel[] labels = {
            new JLabel("Mã Thành Viên:"),
            new JLabel("Họ Tên:"),
            new JLabel("Email:")
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
        
        JButton addButton = new JButton("Thêm");
        JButton updateButton = new JButton("Cập Nhật");
        JButton removeButton = new JButton("Xóa");
        JButton refreshButton = new JButton("Làm Mới");

        // Style buttons
        Dimension buttonSize = new Dimension(120, 35);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        for (JButton button : new JButton[]{addButton, updateButton, removeButton, refreshButton}) {
            button.setFont(buttonFont);
            button.setPreferredSize(buttonSize);
        }

        // Add button actions
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
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        memberTable = new JTable(tableModel);
        setupTable();

        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Danh Sách Thành Viên",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 18)
        ));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void setupTable() {
        memberTable.setFont(new Font("Arial", Font.PLAIN, 16));
        memberTable.setRowHeight(35);
        memberTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add selection listener
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
        Member oldMember = memberManager.getMember(id);
        if (oldMember != null) {
            String name = nameField.getText();
            String email = emailField.getText();
            
            Member newMember = new Member(id, name, email);
            newMember.registerObserver(mainGUI);
            memberManager.removeMember(id);
            memberManager.addMember(newMember);
            updateData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Cập nhật thành viên thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thành viên!");
        }
    }

    private void handleRemoveMember() {
        String id = idField.getText();
        memberManager.removeMember(id);
        updateData();
        clearForm();
        JOptionPane.showMessageDialog(this, "Xóa thành viên thành công!");
    }

    private void handleRefresh() {
        clearForm();
        memberTable.clearSelection();
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