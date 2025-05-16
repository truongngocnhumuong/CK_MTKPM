package singleton;
//Lớp DatabaseConnection mô phỏng kết nối cơ sở dữ liệu
public class DatabaseConnection {
	private static DatabaseConnection instance;
	
	private DatabaseConnection() {} // Constructor private để đảm bảo Singleton
	
	public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
            System.out.println("Database kết nối thành công.");
        }
        return instance;
    }
	
	public void connect() {
		System.out.println("Đang kết nối database.....");
		
	}
	public void query(String sql) {
		System.out.println("Đang thực hiện truy vấn : " + sql);
		
	}

}
