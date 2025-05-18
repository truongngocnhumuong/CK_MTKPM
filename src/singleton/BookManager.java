package singleton;
//Lớp BookManager quản lý danh sách sách

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookManager {
	private static BookManager instanse;
	private Connection connection;
	
	private BookManager() {
		// Lấy kết nối từ DatabaseConnection
        connection = DatabaseConnection.getInstance().getConnection();
	} 
	// Phương thức Singleton
	public static synchronized BookManager getInstanse() {
		if(instanse == null) {
			instanse = new BookManager();
		}
		return instanse;
	}
	// Thêm sách vào cơ sở dữ liệu
    public void addBook(Book book) {
        String sql = "INSERT INTO books (id, title, author, type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, book.getId());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setString(4, book.getType());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }
    
 // Xóa sách
    public void removeBook(String bookId) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error removing book: " + e.getMessage());
        }
    }
    
    
 // Cập nhật sách
    public void updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, type = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getType());
            pstmt.setString(4, book.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
    }
	
    
 // Lấy sách theo ID
    public Book getBook(String bookId) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Book(rs.getString("id"), rs.getString("title"), rs.getString("author"), rs.getString("type"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting book: " + e.getMessage());
        }
        return null;
    }

    // Lấy tất cả sách (dùng để báo cáo)
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(rs.getString("id"), rs.getString("title"), rs.getString("author"), rs.getString("type")));
            }
        } catch (SQLException e) {
            System.out.println("Error getting all books: " + e.getMessage());
        }
        return books;
    }

}
