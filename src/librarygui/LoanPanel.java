package librarygui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import javax.swing.border.EmptyBorder;
import singleton.*;
import strategy.FeeContext;
import strategy.StandardFeeCalculator;
import strategy.PremiumFeeCalculator;
import strategy.FlexibleFeeCalculator;
import strategy.FeeCalculator;

public class LoanPanel extends JPanel {
    private MainLibraryGUI mainGUI;
    private LoanManager loanManager;
    private BookManager bookManager;
    private MemberManager memberManager;
    private FeeContext feeContext;
    
    private JTable loanTable;
    private JTextField loanIdField;
    private JTextField bookIdField;
    private JTextField memberIdField;
    private JTextField borrowDateField;
    private JTextField dueDateField;
    private JTextField returnDateField;
    private JTextField feeField;
    private JButton payFeeButton;
    private JComboBox<String> feeCalculatorCombo;

    public LoanPanel(MainLibraryGUI mainGUI) {
        this.mainGUI = mainGUI;
        this.loanManager = mainGUI.getLoanManager();
        this.bookManager = mainGUI.getBookManager();
        this.memberManager = mainGUI.getMemberManager();
        this.feeContext = new FeeContext();
        feeContext.setCalculator(new StandardFeeCalculator());
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 10));
        
        // Input Panel with 2 columns
        JPanel topSection = new JPanel();
        topSection.setLayout(new GridLayout(1, 2, 20, 0));
        topSection.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Left Column - Loan Form
        JPanel leftPanel = createLoanFormPanel();

        // Right Column - Return Form
        JPanel rightPanel = createReturnFormPanel();

        // Add panels to top section
        topSection.add(leftPanel);
        topSection.add(rightPanel);

        // Loan Table Panel
        JPanel tablePanel = createLoanTablePanel();

        // Add sections to main panel
        add(topSection, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);

        // Initialize table
        updateData();
    }

    private JPanel createLoanFormPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Thông Tin Mượn Sách",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16)
        ));

        JPanel loanFormPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize form components
        Dimension fixedSize = new Dimension(200, 35);
        loanIdField = new JTextField();
        bookIdField = new JTextField();
        memberIdField = new JTextField();
        borrowDateField = new JTextField(formatDate(new Date()));
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        dueDateField = new JTextField(formatDate(calendar.getTime()));

        // Style form components
        Font inputFont = new Font("Arial", Font.PLAIN, 14);
        Component[] loanInputs = {loanIdField, bookIdField, memberIdField, borrowDateField, dueDateField};
        for (Component comp : loanInputs) {
            comp.setFont(inputFont);
            comp.setPreferredSize(fixedSize);
            comp.setMinimumSize(fixedSize);
        }

        // Labels
        JLabel[] loanLabels = {
            new JLabel("Mã Mượn:"),
            new JLabel("Mã Sách:"),
            new JLabel("Mã Thành Viên:"),
            new JLabel("Ngày Mượn:"),
            new JLabel("Ngày Hẹn Trả:")
        };
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        for (JLabel label : loanLabels) {
            label.setFont(labelFont);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        }

        // Add components to form
        for (int i = 0; i < loanLabels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3;
            loanFormPanel.add(loanLabels[i], gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            loanFormPanel.add(loanInputs[i], gbc);
        }

        // Button Panel
        JPanel loanButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton borrowButton = new JButton("Mượn Sách");
        JButton updateLoanButton = new JButton("Sửa Thông Tin");
        JButton refreshButton = new JButton("Làm Mới");

        // Style buttons
        Dimension buttonSize = new Dimension(120, 35);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        for (JButton button : new JButton[]{borrowButton, updateLoanButton, refreshButton}) {
            button.setFont(buttonFont);
            button.setPreferredSize(buttonSize);
        }

        // Add button actions
        borrowButton.addActionListener(e -> handleBorrowBook());
        updateLoanButton.addActionListener(e -> handleUpdateLoan());
        refreshButton.addActionListener(e -> handleRefresh());

        loanButtonPanel.add(borrowButton);
        loanButtonPanel.add(updateLoanButton);
        loanButtonPanel.add(refreshButton);

        leftPanel.add(loanFormPanel, BorderLayout.CENTER);
        leftPanel.add(loanButtonPanel, BorderLayout.SOUTH);

        return leftPanel;
    }

    private JPanel createReturnFormPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Thông Tin Trả Sách",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16)
        ));

        JPanel returnFormPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize form components
        Dimension fixedSize = new Dimension(200, 35);
        returnDateField = new JTextField(formatDate(new Date()));
        returnDateField.setEditable(true);
        feeField = new JTextField();
        feeField.setEditable(false);
        feeCalculatorCombo = new JComboBox<>(new String[]{
            "Tiêu Chuẩn (20,000/ngày)",
            "Cao Cấp (40,000/ngày)",
            "Linh Hoạt (tăng dần)"
        });

        // Style components
        Font inputFont = new Font("Arial", Font.PLAIN, 14);
        Component[] returnInputs = {returnDateField, feeField, feeCalculatorCombo};
        for (Component comp : returnInputs) {
            comp.setFont(inputFont);
            comp.setPreferredSize(fixedSize);
            comp.setMinimumSize(fixedSize);
        }

        // Labels
        JLabel[] returnLabels = {
            new JLabel("Ngày Trả:"),
            new JLabel("Tiền Phạt:"),
            new JLabel("Loại Phí Phạt:")
        };
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        for (JLabel label : returnLabels) {
            label.setFont(labelFont);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        }

        // Add components to form
        for (int i = 0; i < returnLabels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3;
            returnFormPanel.add(returnLabels[i], gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            returnFormPanel.add(returnInputs[i], gbc);
        }

        // Button Panel
        JPanel returnButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton returnButton = new JButton("Trả Sách");
        payFeeButton = new JButton("Thanh Toán Phạt");
        payFeeButton.setEnabled(false);

        // Style buttons
        Dimension buttonSize = new Dimension(120, 35);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        for (JButton button : new JButton[]{returnButton, payFeeButton}) {
            button.setFont(buttonFont);
            button.setPreferredSize(buttonSize);
        }

        // Add button actions
        returnButton.addActionListener(e -> handleReturnBook());
        payFeeButton.addActionListener(e -> handlePayFee());

        returnButtonPanel.add(returnButton);
        returnButtonPanel.add(payFeeButton);

        rightPanel.add(returnFormPanel, BorderLayout.CENTER);
        rightPanel.add(returnButtonPanel, BorderLayout.SOUTH);

        return rightPanel;
    }

    private JPanel createLoanTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        
        String[] columnNames = {"Mã Mượn", "Mã Sách", "Tên Sách", "Mã Thành Viên", 
            "Tên Thành Viên", "Ngày Mượn", "Ngày Hẹn Trả", "Ngày Trả", "Tiền Phạt", "Trạng Thái", "Thanh toán"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 10;
            }
        };
        
        loanTable = new JTable(tableModel);
        setupTable();

        JScrollPane scrollPane = new JScrollPane(loanTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Danh Sách Mượn Trả",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16)
        ));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void setupTable() {
        loanTable.setFont(new Font("Arial", Font.PLAIN, 14));
        loanTable.setRowHeight(30);
        loanTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Add selection listener
        loanTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = loanTable.getSelectedRow();
                if (row >= 0) {
                    populateFormFromSelectedRow(row);
                }
            }
        });

        // Custom renderer for payment status
        TableColumn payColumn = loanTable.getColumnModel().getColumn(10);
        payColumn.setCellRenderer(new TableCellRenderer() {
            private final JLabel label = new JLabel();
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                label.setHorizontalAlignment(JLabel.CENTER);
                String status = (String) value;
                label.setText(status);
                if ("Chưa thanh toán".equals(status)) {
                    label.setForeground(Color.RED);
                } else if ("Đã thanh toán".equals(status)) {
                    label.setForeground(new Color(0, 128, 0));
                }
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setOpaque(true);
                } else {
                    label.setBackground(table.getBackground());
                    label.setOpaque(false);
                }
                return label;
            }
        });

        // Click handler for payment column
        loanTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = loanTable.rowAtPoint(e.getPoint());
                int column = loanTable.columnAtPoint(e.getPoint());
                if (column == 10 && row >= 0) {
                    handlePaymentClick(row);
                }
            }
        });
    }

    private void handlePaymentClick(int row) {
        String loanId = (String) loanTable.getValueAt(row, 0);
        
        // Reload loans to ensure we have the latest data
        loanManager.reloadLoans();
        Loan loan = loanManager.getLoan(loanId);
        
        if (loan != null && !loan.isFeePaid() && loan.getNgayTra() != null && calculateFee(loan) > 0) {
            // Update payment status
            loan.setFeePaid(true);
            loanManager.updateLoanFeePaid(loanId, true);
            
            // Update UI
            SwingUtilities.invokeLater(() -> {
                updateData();
                
                // Reselect the updated row
                for (int i = 0; i < loanTable.getRowCount(); i++) {
                    if (loanId.equals(loanTable.getValueAt(i, 0))) {
                        loanTable.setRowSelectionInterval(i, i);
                        loanTable.scrollRectToVisible(loanTable.getCellRect(i, 0, true));
                        break;
                    }
                }
                
                // Update form if the loan is selected
                if (loanIdField.getText().equals(loanId)) {
                    Loan updatedLoan = loanManager.getLoan(loanId);
                    if (updatedLoan != null) {
                        double updatedFee = calculateFee(updatedLoan);
                        feeField.setText(String.format("%,.0f VNĐ", updatedFee));
                        payFeeButton.setEnabled(false);
                    }
                }
            });
            
            JOptionPane.showMessageDialog(this, "Thanh toán phí thành công!");
        }
    }

    private void handleBorrowBook() {
        String loanId = loanIdField.getText();
        String bookId = bookIdField.getText();
        String memberId = memberIdField.getText();
        String borrowDate = borrowDateField.getText();
        String dueDate = dueDateField.getText();

        if (!validateFields(loanId, bookId, memberId, borrowDate, dueDate)) return;

        Book book = bookManager.getBook(bookId);
        Member member = memberManager.getMember(memberId);
        if (book == null || member == null) {
            JOptionPane.showMessageDialog(this, "Mã sách hoặc mã thành viên không hợp lệ!");
            return;
        }
        if (loanManager.getLoan(loanId) != null) {
            JOptionPane.showMessageDialog(this, "Mã mượn đã tồn tại!");
            return;
        }

        Loan loan = new Loan(loanId, book, member, parseDate(borrowDate), parseDate(dueDate));
        loanManager.addLoan(loan);
        updateData();
        clearForm();
        JOptionPane.showMessageDialog(this, "Mượn sách thành công!");
    }

    private void handleUpdateLoan() {
        String oldLoanId = loanIdField.getText();
        Loan oldLoan = loanManager.getLoan(oldLoanId);
        if (oldLoan != null) {
            String newLoanId = loanIdField.getText();
            String bookId = bookIdField.getText();
            String memberId = memberIdField.getText();
            String borrowDate = borrowDateField.getText();
            String dueDate = dueDateField.getText();

            Book book = bookManager.getBook(bookId);
            Member member = memberManager.getMember(memberId);
            if (book == null || member == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin sách hoặc thành viên!");
                return;
            }

            Loan newLoan = new Loan(newLoanId, book, member, parseDate(borrowDate), parseDate(dueDate));
            newLoan.setNgayTra(oldLoan.getNgayTra());
            newLoan.setFeePaid(oldLoan.isFeePaid());
            loanManager.updateLoan(newLoan);
            loanManager.reloadLoans();
            updateData();
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin mượn sách thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin mượn sách!");
        }
    }

    private void handleReturnBook() {
        String loanId = loanIdField.getText();
        String returnDateStr = returnDateField.getText();
        if (!validateFields(loanId, returnDateStr)) return;
        
        loanManager.reloadLoans();
        Loan loan = loanManager.getLoan(loanId);
        if (loan == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin mượn sách!");
            return;
        }

        Date ngayTra = parseDate(returnDateStr);
        if (ngayTra == null) {
            JOptionPane.showMessageDialog(this, "Ngày trả không hợp lệ!");
            return;
        }
        if (ngayTra.before(loan.getNgayMuon())) {
            JOptionPane.showMessageDialog(this, "Ngày trả phải sau hoặc bằng ngày mượn!");
            return;
        }
        
        loan.setNgayTra(ngayTra);
        loanManager.updateLoanReturnDate(loanId, ngayTra);
        loanManager.reloadLoans();
        
        double fee = calculateFee(loan);
        feeField.setText(String.format("%,.0f VNĐ", fee));
        payFeeButton.setEnabled(fee > 0 && !loan.isFeePaid());
        
        updateData();
        JOptionPane.showMessageDialog(this, "Trả sách thành công!");
    }

    private void handlePayFee() {
        String loanId = loanIdField.getText();
        
        loanManager.reloadLoans();
        Loan loan = loanManager.getLoan(loanId);
        
        if (loan != null && loan.getNgayTra() != null) {
            double fee = calculateFee(loan);
            if (fee > 0 && !loan.isFeePaid()) {
                loan.setFeePaid(true);
                loanManager.updateLoanFeePaid(loanId, true);
                loanManager.reloadLoans();
                payFeeButton.setEnabled(false);
                
                SwingUtilities.invokeLater(() -> {
                    updateData();
                    Loan updatedLoan = loanManager.getLoan(loanId);
                    if (updatedLoan != null) {
                        double updatedFee = calculateFee(updatedLoan);
                        feeField.setText(String.format("%,.0f VNĐ", updatedFee));
                    }
                });
                
                JOptionPane.showMessageDialog(this, "Thanh toán phí thành công!");
            } else if (loan.isFeePaid()) {
                JOptionPane.showMessageDialog(this, "Phí đã được thanh toán trước đó!");
            } else {
                JOptionPane.showMessageDialog(this, "Không có phí cần thanh toán!");
            }
        }
    }

    private void handleRefresh() {
        clearForm();
        loanTable.clearSelection();
        returnDateField.setText(formatDate(new Date()));
    }

    public void updateData() {
        DefaultTableModel model = (DefaultTableModel) loanTable.getModel();
        model.setRowCount(0);
        loanManager.getAllLoans().forEach(loan -> {
            double fee = calculateFee(loan);
            String status;
            if (loan.getNgayTra() == null) {
                status = "Đang mượn";
            } else if (fee > 0 && !loan.isFeePaid()) {
                status = "Chờ thanh toán";
            } else if (fee > 0 && loan.isFeePaid()) {
                status = "Đã thanh toán";
            } else {
                status = "Hoàn thành";
            }
            
            String payStatus = "";
            if (loan.getNgayTra() != null && fee > 0) {
                payStatus = loan.isFeePaid() ? "Đã thanh toán" : "Chưa thanh toán";
            }
            
            model.addRow(new Object[]{
                loan.getId(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getMember().getId(),
                loan.getMember().getName(),
                formatDate(loan.getNgayMuon()),
                formatDate(loan.getNgayDenHan()),
                loan.getNgayTra() != null ? formatDate(loan.getNgayTra()) : "Chưa trả",
                fee > 0 ? String.format("%,.0f VNĐ", fee) : "0 VNĐ",
                status,
                payStatus
            });
        });
        model.fireTableDataChanged();
    }

    private void populateFormFromSelectedRow(int row) {
        String loanId = (String) loanTable.getValueAt(row, 0);
        Loan loan = loanManager.getLoan(loanId);
        if (loan != null) {
            loanIdField.setText(loan.getId());
            bookIdField.setText(loan.getBook().getId());
            memberIdField.setText(loan.getMember().getId());
            borrowDateField.setText(formatDate(loan.getNgayMuon()));
            dueDateField.setText(formatDate(loan.getNgayDenHan()));
            
            // Nếu sách chưa trả, hiển thị ngày hiện tại
            if (loan.getNgayTra() == null) {
                returnDateField.setText(formatDate(new Date()));
            } else {
                returnDateField.setText(formatDate(loan.getNgayTra()));
            }
            
            double fee = calculateFee(loan);
            feeField.setText(String.format("%,.0f VNĐ", fee));
            payFeeButton.setEnabled(fee > 0 && !loan.isFeePaid());
        }
    }

    private void clearForm() {
        loanIdField.setText("");
        bookIdField.setText("");
        memberIdField.setText("");
        borrowDateField.setText(formatDate(new Date()));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        dueDateField.setText(formatDate(calendar.getTime()));
        returnDateField.setText(formatDate(new Date()));
        feeField.setText("0 VNĐ");
        payFeeButton.setEnabled(false);
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

    private double calculateFee(Loan loan) {
        if (loan.getNgayTra() == null) return 0;
        long diffInMillies = loan.getNgayTra().getTime() - loan.getNgayDenHan().getTime();
        long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);
        if (diffInDays <= 0) return 0;

        String selectedCalculator = (String) feeCalculatorCombo.getSelectedItem();
        FeeCalculator calculator;
        
        if (selectedCalculator.contains("Cao Cấp")) {
            calculator = new PremiumFeeCalculator();
        } else if (selectedCalculator.contains("Linh Hoạt")) {
            calculator = new FlexibleFeeCalculator();
        } else {
            calculator = new StandardFeeCalculator();
        }
        
        return calculator.calculateFee(diffInDays);
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private Date parseDate(String dateStr) {
        try {
            return dateStr != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateStr) : null;
        } catch (Exception e) {
            return null;
        }
    }
} 