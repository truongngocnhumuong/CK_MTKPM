package singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

//Lớp DatabaseConnection mô phỏng kết nối cơ sở dữ liệu
public class DatabaseConnection {
	private static DatabaseConnection instance;
	private Connection connection;
	
	private DatabaseConnection() {
		try {
			// Đăng ký driver (tùy chọn, thường không cần với phiên bản mới)
            Class.forName("org.sqlite.JDBC");
			// Kết nối tới SQLite
            String url = "jdbc:sqlite:library.db";
            connection = DriverManager.getConnection(url);
            System.out.println("Database connection established.");
            
         // Tạo bảng nếu chưa tồn tại
            createTables();
		} catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver not found: " + e.getMessage());
        }
	} 
	
	// Phương thức Singleton
	public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
            //System.out.println("Database kết nối thành công.");
        }
        return instance;
    }
	
	// Tạo các bảng books, members, loans
	private void createTables() throws SQLException{
		String createBooksTable = """
	            CREATE TABLE IF NOT EXISTS books (
	                id TEXT PRIMARY KEY,
	                title TEXT NOT NULL,
	                author TEXT NOT NULL,
	                type TEXT NOT NULL
	            )
	        """;
		String createMembersTable = """
	            CREATE TABLE IF NOT EXISTS members (
	                id TEXT PRIMARY KEY,
	                name TEXT NOT NULL,
	                email TEXT NOT NULL
	            )
	        """;
	        String createLoansTable = """
	            CREATE TABLE IF NOT EXISTS loans (
	                id TEXT PRIMARY KEY,
	                book_id TEXT,
	                member_id TEXT,
	                borrow_date TEXT,
	                due_date TEXT,
	                return_date TEXT,
	                FOREIGN KEY(book_id) REFERENCES books(id),
	                FOREIGN KEY(member_id) REFERENCES members(id)
	            )
	        """;
	        try (Statement stmt = connection.createStatement()) {
	            stmt.execute(createBooksTable);
	            stmt.execute(createMembersTable);
	            stmt.execute(createLoansTable);
	            System.out.println("Tables created successfully.");
	        }

		
	}
	// Getter cho connection
	public Connection getConnection() { return connection; }
	// Đóng kết nối
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

}
