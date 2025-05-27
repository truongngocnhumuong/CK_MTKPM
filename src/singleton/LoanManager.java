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
    private List<Loan> loans = new ArrayList<>();

    private LoanManager() {
        connection = DatabaseConnection.getInstance().getConnection();
        loadLoans(); // Load loans from database when initializing
    }

    public static synchronized LoanManager getInstance() {
        if (instance == null) {
            instance = new LoanManager();
        }
        return instance;
    }

    // Load all loans from database
    private void loadLoans() {
        loans.clear();
        String sql = """
            SELECT l.id, l.book_id, l.member_id, l.borrow_date, l.due_date, l.return_date, l.fee_paid,
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
                    loan.setNgayTra(parseDate(returnDateStr));
                }
                loan.setFeePaid(rs.getBoolean("fee_paid"));
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.out.println("Error loading loans: " + e.getMessage());
        }
    }

    // Thêm giao dịch mượn
    public void addLoan(Loan loan) {
        String sql = "INSERT INTO loans (id, book_id, member_id, borrow_date, due_date, return_date, fee_paid) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, loan.getId());
            pstmt.setString(2, loan.getBook().getId());
            pstmt.setString(3, loan.getMember().getId());
            pstmt.setString(4, formatDate(loan.getNgayMuon()));
            pstmt.setString(5, formatDate(loan.getNgayDenHan()));
            pstmt.setString(6, loan.getNgayTra() != null ? formatDate(loan.getNgayTra()) : null);
            pstmt.setBoolean(7, loan.isFeePaid());
            pstmt.executeUpdate();
            loans.add(loan); // Add to memory after successful database insert
            reloadLoans();
        } catch (SQLException e) {
            System.out.println("Error adding loan: " + e.getMessage());
        }
    }

    public Loan getLoan(String id) {
        String sql = """
            SELECT l.id, l.book_id, l.member_id, l.borrow_date, l.due_date, l.return_date, l.fee_paid,
                   b.title, b.author, b.type,
                   m.name, m.email
            FROM loans l
            JOIN books b ON l.book_id = b.id
            JOIN members m ON l.member_id = m.id
            WHERE l.id = ?
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
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
                    loan.setNgayTra(parseDate(returnDateStr));
                }
                loan.setFeePaid(rs.getBoolean("fee_paid"));
                return loan;
            }
        } catch (SQLException e) {
            System.out.println("Error getting loan: " + e.getMessage());
        }
        return null;
    }

    public void removeLoan(String id) {
        String sql = "DELETE FROM loans WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            loans.removeIf(loan -> loan.getId().equals(id)); // Remove from memory after successful database delete
            reloadLoans();
        } catch (SQLException e) {
            System.out.println("Error removing loan: " + e.getMessage());
        }
    }

    // Cập nhật ngày trả sách
    public void updateLoanReturnDate(String loanId, Date returnDate) {
        String sql = "UPDATE loans SET return_date = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, formatDate(returnDate));
            pstmt.setString(2, loanId);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("Successfully updated return date for loan: " + loanId);
            } else {
                System.out.println("No loan found with ID: " + loanId);
            }
        } catch (SQLException e) {
            System.out.println("Error updating loan return date: " + e.getMessage());
        }
    }

    // Cập nhật trạng thái thanh toán phí
    public void updateLoanFeePaid(String loanId, boolean feePaid) {
        String sql = "UPDATE loans SET fee_paid = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, feePaid);
            pstmt.setString(2, loanId);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                // Update in memory
                for (Loan loan : loans) {
                    if (loan.getId().equals(loanId)) {
                        loan.setFeePaid(feePaid);
                        break;
                    }
                }
                // Reload loans to ensure synchronization
                reloadLoans();
                System.out.println("Successfully updated fee payment status for loan: " + loanId);
            } else {
                System.out.println("No loan found with ID: " + loanId);
            }
        } catch (SQLException e) {
            System.out.println("Error updating loan fee payment status: " + e.getMessage());
        }
    }

    // Cập nhật thông tin mượn trả
    public void updateLoan(Loan loan) {
        String sql = """
            UPDATE loans 
            SET book_id = ?, member_id = ?, borrow_date = ?, due_date = ?, return_date = ?, fee_paid = ?
            WHERE id = ?
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, loan.getBook().getId());
            pstmt.setString(2, loan.getMember().getId());
            pstmt.setString(3, formatDate(loan.getNgayMuon()));
            pstmt.setString(4, formatDate(loan.getNgayDenHan()));
            pstmt.setString(5, loan.getNgayTra() != null ? formatDate(loan.getNgayTra()) : null);
            pstmt.setBoolean(6, loan.isFeePaid());
            pstmt.setString(7, loan.getId());
            pstmt.executeUpdate();
            // Update in memory
            for (int i = 0; i < loans.size(); i++) {
                if (loans.get(i).getId().equals(loan.getId())) {
                    loans.set(i, loan);
                    break;
                }
            }
            reloadLoans();
        } catch (SQLException e) {
            System.out.println("Error updating loan: " + e.getMessage());
        }
    }

    // Lấy tất cả giao dịch mượn (dùng để báo cáo)
    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = """
            SELECT l.id, l.book_id, l.member_id, l.borrow_date, l.due_date, l.return_date, l.fee_paid,
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
                    loan.setNgayTra(parseDate(returnDateStr));
                }
                loan.setFeePaid(rs.getBoolean("fee_paid"));
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

    public void reloadLoans() {
        loadLoans();
    }
}