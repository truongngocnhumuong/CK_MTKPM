package libarygui;

import javax.swing.*;

import factory.BookFactory;
import factory.EBookFactory;
import factory.PaperBookFactory;
import observer.Observer;
import singleton.Book;
import singleton.BookManager;
import singleton.Loan;
import singleton.LoanManager;
import singleton.Member;
import singleton.MemberManager;
import strategy.FeeContext;
import strategy.StandardFeeCalculator;
import abstractfactory.*;

import java.awt.*;
import java.util.Date;
import java.util.List;

// Lớp LibraryGUI cung cấp giao diện người dùng
public class LibraryGUI extends JFrame implements Observer {
    private BookManager bookManager;
    private MemberManager memberManager;
    private LoanManager loanManager;
    private FeeContext feeContext;
    private JTextArea reportArea;

    public LibraryGUI() {
        bookManager = BookManager.getInstanse();
        memberManager = MemberManager.getInstance();
        loanManager = LoanManager.getInstance();
        feeContext = new FeeContext();
        feeContext.setCalculator(new StandardFeeCalculator());

        // Cấu hình JFrame
        setTitle("Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Books", createBooksPanel());
        tabbedPane.addTab("Members", createMembersPanel());
        tabbedPane.addTab("Loans", createLoansPanel());
        tabbedPane.addTab("Reports", createReportsPanel());
        add(tabbedPane);

        // Đăng ký observer
        bookManager.getAllBooks().forEach(book -> book.registerObserver(this));
        memberManager.getAllMembers().forEach(member -> member.registerObserver(this));
    }

    // Panel cho quản lý sách
    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2));
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"paper", "ebook", "academic", "entertainment"});

        panel.add(new JLabel("Book ID:"));
        panel.add(idField);
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Type:"));
        panel.add(typeCombo);

        JButton addButton = new JButton("Add Book");
        addButton.addActionListener(e -> {
            String id = idField.getText();
            String title = titleField.getText();
            String author = authorField.getText();
            String type = (String) typeCombo.getSelectedItem();
            AbstractBookFactory factory = type.equals("paper") ? new PaperBookFactory() : type.equals("ebook") ? new EBookFactory() :
                                        type.equals("academic") ? new AcademicBookFactory() : new EntertainmentBookFactory();
            Book book = factory.createBook(id, title, author); // Giả sử AbstractBookFactory có createBook
            book.registerObserver(this);
            bookManager.addBook(book);
            JOptionPane.showMessageDialog(this, "Book added successfully!");
        });
        panel.add(addButton);

        JButton removeButton = new JButton("Remove Book");
        removeButton.addActionListener(e -> {
            String id = idField.getText();
            bookManager.removeBook(id);
            JOptionPane.showMessageDialog(this, "Book removed successfully!");
        });
        panel.add(removeButton);

        JButton updateButton = new JButton("Update Book");
        updateButton.addActionListener(e -> {
            String id = idField.getText();
            String title = titleField.getText();
            String author = authorField.getText();
            String type = (String) typeCombo.getSelectedItem();
            AbstractBookFactory factory = type.equals("paper") ? new PaperBookFactory() : type.equals("ebook") ? new EBookFactory() :
                                        type.equals("academic") ? new AcademicBookFactory() : new EntertainmentBookFactory();
            Book book = factory.createBook(id, title, author); // Giả sử AbstractBookFactory có createBook
            bookManager.updateBook(book);
            JOptionPane.showMessageDialog(this, "Book updated successfully!");
        });
        panel.add(updateButton);

        return panel;
    }

    // Panel cho quản lý thành viên
    private JPanel createMembersPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();

        panel.add(new JLabel("Member ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        JButton addButton = new JButton("Add Member");
        addButton.addActionListener(e -> {
            String id = idField.getText();
            String name = nameField.getText();
            String email = emailField.getText();
            Member member = new Member(id, name, email);
            member.registerObserver(this);
            memberManager.addMember(member);
            JOptionPane.showMessageDialog(this, "Member added successfully!");
        });
        panel.add(addButton);

        JButton removeButton = new JButton("Remove Member");
        removeButton.addActionListener(e -> {
            String id = idField.getText();
            memberManager.removeMember(id);
            JOptionPane.showMessageDialog(this, "Member removed successfully!");
        });
        panel.add(removeButton);

        JButton updateButton = new JButton("Update Member");
        updateButton.addActionListener(e -> {
            String id = idField.getText();
            String name = nameField.getText();
            String email = emailField.getText();
            Member member = new Member(id, name, email);
            memberManager.updateMember(member);
            JOptionPane.showMessageDialog(this, "Member updated successfully!");
        });
        panel.add(updateButton);

        return panel;
    }

    // Panel cho quản lý mượn/trả sách
    private JPanel createLoansPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2));
        JTextField loanIdField = new JTextField();
        JTextField bookIdField = new JTextField();
        JTextField memberIdField = new JTextField();
        JTextField borrowDateField = new JTextField("2025-05-18"); // Ví dụ
        JTextField dueDateField = new JTextField("2025-05-25"); // Ví dụ
        JTextField returnDateField = new JTextField("2025-05-28"); // Ví dụ

        panel.add(new JLabel("Loan ID:"));
        panel.add(loanIdField);
        panel.add(new JLabel("Book ID:"));
        panel.add(bookIdField);
        panel.add(new JLabel("Member ID:"));
        panel.add(memberIdField);
        panel.add(new JLabel("Borrow Date (yyyy-MM-dd):"));
        panel.add(borrowDateField);
        panel.add(new JLabel("Due Date (yyyy-MM-dd):"));
        panel.add(dueDateField);
        panel.add(new JLabel("Return Date (yyyy-MM-dd):"));
        panel.add(returnDateField);

        JButton borrowButton = new JButton("Borrow Book");
        borrowButton.addActionListener(e -> {
            String loanId = loanIdField.getText();
            String bookId = bookIdField.getText();
            String memberId = memberIdField.getText();
            Book book = bookManager.getBook(bookId);
            Member member = memberManager.getMember(memberId);
            if (book != null && member != null) {
                Loan loan = new Loan(loanId, book, member,
                    parseDate(borrowDateField.getText()),
                    parseDate(dueDateField.getText()));
                loanManager.addLoan(loan);
                JOptionPane.showMessageDialog(this, "Book borrowed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid book or member ID!");
            }
        });
        panel.add(borrowButton);

        JButton returnButton = new JButton("Return Book & Calculate Fee");
        returnButton.addActionListener(e -> {
            String loanId = loanIdField.getText();
            Date returnDate = parseDate(returnDateField.getText());
            loanManager.updateLoanReturnDate(loanId, returnDate);
            List<Loan> loans = loanManager.getAllLoans();
            Loan loan = loans.stream().filter(l -> l.getId().equals(loanId)).findFirst().orElse(null);
            if (loan != null) {
                double fee = loan.calculateLateFee(feeContext.getCalculator());
                JOptionPane.showMessageDialog(this, "Book returned! Late fee: $" + fee);
            }
        });
        panel.add(returnButton);

        return panel;
    }

    // Panel cho báo cáo
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh Reports");
        refreshButton.addActionListener(e -> updateReport());
        panel.add(refreshButton, BorderLayout.SOUTH);

        updateReport();
        return panel;
    }

    // Cập nhật báo cáo
    private void updateReport() {
        StringBuilder report = new StringBuilder();
        report.append("Books Report:\n");
        for (Book book : bookManager.getAllBooks()) {
            report.append("ID: ").append(book.getId()).append(", Title: ").append(book.getTitle())
                  .append(", Author: ").append(book.getAuthor()).append(", Type: ").append(book.getType()).append("\n");
        }

        report.append("\nMembers Report:\n");
        for (Member member : memberManager.getAllMembers()) {
            report.append("ID: ").append(member.getId()).append(", Name: ").append(member.getName())
                  .append(", Email: ").append(member.getEmail()).append("\n");
        }

        report.append("\nLoans Report:\n");
        for (Loan loan : loanManager.getAllLoans()) {
            report.append("Loan ID: ").append(loan.getId()).append(", Book: ").append(loan.getBook().getTitle())
                  .append(", Member: ").append(loan.getMember().getName())
                  .append(", Borrow Date: ").append(loan.getNgayMuon())
                  .append(", Due Date: ").append(loan.getNgayDenHan())
                  .append(", Return Date: ").append(loan.getNgayTra() != null ? loan.getNgayTra() : "Not returned")
                  .append("\n");
        }

        reportArea.setText(report.toString());
    }

    // Triển khai Observer
    @Override
    public void update(String event, Object data) {
        updateReport();
    }

    // Parse chuỗi thành ngày
    private Date parseDate(String dateStr) {
        try {
            return dateStr != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateStr) : null;
        } catch (Exception e) {
            return null;
        }
    }

    // Phương thức main để chạy GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }
}
