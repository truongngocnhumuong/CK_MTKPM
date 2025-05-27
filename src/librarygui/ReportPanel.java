package librarygui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import singleton.*;

public class ReportPanel extends JPanel {
    private MainLibraryGUI mainGUI;
    private BookManager bookManager;
    private MemberManager memberManager;
    private LoanManager loanManager;
    private JTable booksTable;
    private JTable membersTable;
    private JTable loansTable;

    public ReportPanel(MainLibraryGUI mainGUI) {
        this.mainGUI = mainGUI;
        this.bookManager = mainGUI.getBookManager();
        this.memberManager = mainGUI.getMemberManager();
        this.loanManager = mainGUI.getLoanManager();
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel reportsPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        // Books Panel
        reportsPanel.add(createBooksPanel());

        // Members Panel
        reportsPanel.add(createMembersPanel());

        // Loans Panel
        reportsPanel.add(createLoansPanel());

        // Add refresh button
        JButton refreshButton = new JButton("Làm Mới Báo Cáo");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setPreferredSize(new Dimension(150, 35));
        refreshButton.addActionListener(e -> updateData());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        buttonPanel.add(refreshButton);

        add(reportsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Initialize tables
        updateData();
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"Mã Sách", "Tên Sách", "Tác Giả", "Loại", "Yêu thích"};
        booksTable = createReportTable(columns);
        
        // Set boolean renderer for favorite column
        booksTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                boolean isFavorite = (Boolean) value;
                setText(isFavorite ? "Có" : "Không");
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Báo Cáo Sách",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16)
        ));
        panel.add(scrollPane);
        return panel;
    }

    private JPanel createMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"Mã Thành Viên", "Họ Tên", "Email"};
        membersTable = createReportTable(columns);

        JScrollPane scrollPane = new JScrollPane(membersTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Báo Cáo Thành Viên",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16)
        ));
        panel.add(scrollPane);
        return panel;
    }

    private JPanel createLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {
            "Mã Mượn", "Tên Sách", "Tên Thành Viên", 
            "Ngày Mượn", "Ngày Hẹn Trả", "Ngày Trả", 
            "Tiền Phạt", "Trạng Thái", "Thanh Toán"
        };
        loansTable = createReportTable(columns);

        // Custom renderer for payment status
        DefaultTableCellRenderer paymentRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                if ("Chưa thanh toán".equals(status)) {
                    setForeground(Color.RED);
                } else if ("Đã thanh toán".equals(status)) {
                    setForeground(new Color(0, 128, 0)); // Dark green
                } else {
                    setForeground(table.getForeground());
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };

        // Set custom renderer for payment status column
        loansTable.getColumnModel().getColumn(8).setCellRenderer(paymentRenderer);

        JScrollPane scrollPane = new JScrollPane(loansTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Báo Cáo Mượn Trả",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16)
        ));
        panel.add(scrollPane);
        return panel;
    }

    private JTable createReportTable(String[] columns) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        return table;
    }

    public void updateData() {
        // Update books table
        DefaultTableModel booksModel = (DefaultTableModel) booksTable.getModel();
        booksModel.setRowCount(0);
        bookManager.getAllBooks().forEach(book -> {
            booksModel.addRow(new Object[]{
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                convertBookTypeToVietnamese(book.getType()),
                book.isFavorite()
            });
        });

        // Update members table
        DefaultTableModel membersModel = (DefaultTableModel) membersTable.getModel();
        membersModel.setRowCount(0);
        memberManager.getAllMembers().forEach(member -> {
            membersModel.addRow(new Object[]{
                member.getId(),
                member.getName(),
                member.getEmail()
            });
        });

        // Update loans table
        DefaultTableModel loansModel = (DefaultTableModel) loansTable.getModel();
        loansModel.setRowCount(0);
        loanManager.reloadLoans();
        loanManager.getAllLoans().forEach(loan -> {
            double fee = calculateFee(loan);
            String status;
            String paymentStatus = "";
            
            if (loan.getNgayTra() == null) {
                status = "Đang mượn";
            } else if (fee > 0 && !loan.isFeePaid()) {
                status = "Chờ thanh toán";
                paymentStatus = "Chưa thanh toán";
            } else if (fee > 0 && loan.isFeePaid()) {
                status = "Đã thanh toán";
                paymentStatus = "Đã thanh toán";
            } else {
                status = "Hoàn thành";
            }

            loansModel.addRow(new Object[]{
                loan.getId(),
                loan.getBook().getTitle(),
                loan.getMember().getName(),
                formatDate(loan.getNgayMuon()),
                formatDate(loan.getNgayDenHan()),
                loan.getNgayTra() != null ? formatDate(loan.getNgayTra()) : "Chưa trả",
                fee > 0 ? String.format("%,.0f VNĐ", fee) : "0 VNĐ",
                status,
                paymentStatus
            });
        });

        // Refresh all tables
        booksModel.fireTableDataChanged();
        membersModel.fireTableDataChanged();
        loansModel.fireTableDataChanged();
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

    private double calculateFee(Loan loan) {
        if (loan.getNgayTra() == null) return 0;
        long diffInMillies = loan.getNgayTra().getTime() - loan.getNgayDenHan().getTime();
        long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);
        if (diffInDays <= 0) return 0;
        return diffInDays * 20000; // Standard fee rate
    }

    private String formatDate(java.util.Date date) {
        if (date == null) return "";
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}