package singleton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Lớp LoanManager quản lý giao dịch mượn (Singleton pattern)
public class LoanManager {
    private static LoanManager instance;
    private Connection connection;

    private LoanManager() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public static synchronized LoanManager getInstance() {
        if (instance == null) {
            instance = new LoanManager();
        }
        return instance;
    }

    // Thêm giao dịch mượn
    public void addLoan(Loan loan) {
        String sql = "INSERT INTO loans (id, book_id, member_id, borrow_date, due_date, return_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, loan.getId());
            pstmt.setString(2, loan.getBook().getId());
            pstmt.setString(3, loan.getMember().getId());
            pstmt.setString(4, formatDate(loan.getNgayMuon()));
            pstmt.setString(5, formatDate(loan.getNgayDenHan()));
            pstmt.setString(6, loan.getNgayTra() != null ? formatDate(loan.getNgayTra()) : null);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding loan: " + e.getMessage());
        }
    }

    // Cập nhật ngày trả sách
    public void updateLoanReturnDate(String loanId, Date returnDate) {
        String sql = "UPDATE loans SET return_date = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, formatDate(returnDate));
            pstmt.setString(2, loanId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating loan return date: " + e.getMessage());
        }
    }

    // Lấy tất cả giao dịch mượn (dùng để báo cáo)
    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = """
            SELECT l.id, l.book_id, l.member_id, l.borrow_date, l.due_date, l.return_date,
                   b.title, b.author, b.type,
                   m.name, m.email
            FROM loans l
            JOIN books b ON l.book_id = b.id
            JOIN members m ON l.member_id = m.id
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Book book = new Book(rs.getString("book_id"), rs.getString("title"), rs.getString("author"), rs.getString("type"));
                Member member = new Member(rs.getString("member_id"), rs.getString("name"), rs.getString("email"));
                Loan loan = new Loan(
                    rs.getString("id"),
                    book,
                    member,
                    parseDate(rs.getString("borrow_date")),
                    parseDate(rs.getString("due_date"))
                );
                String returnDateStr = rs.getString("return_date");
                if (returnDateStr != null) {
                    loan.setNgayTra(null);
                }
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.out.println("Error getting all loans: " + e.getMessage());
        }
        return loans;
    }

    // Định dạng ngày thành chuỗi
    private String formatDate(Date date) {
        return date != null ? new SimpleDateFormat("yyyy-MM-dd").format(date) : null;
    }

    // Parse chuỗi thành ngày
    private Date parseDate(String dateStr) {
        try {
            return dateStr != null ? new SimpleDateFormat("yyyy-MM-dd").parse(dateStr) : null;
        } catch (Exception e) {
            return null;
        }
    }
}