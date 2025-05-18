package singleton;
//Lớp MemberManager quản lý danh sách thành viên

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberManager {
	private static MemberManager instance;
	private Connection connection;
	
	private MemberManager() {
		 // Lấy kết nối từ DatabaseConnection
        connection = DatabaseConnection.getInstance().getConnection();
	} 
	 // Phương thức Singleton
	public static synchronized MemberManager getInstance() {
        if (instance == null) {
            instance = new MemberManager();
        }
        return instance;
    }
	
	// Thêm thành viên vào cơ sở dữ liệu
    public void addMember(Member member) {
        String sql = "INSERT INTO members (id, name, email) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, member.getId());
            pstmt.setString(2, member.getName());
            pstmt.setString(3, member.getEmail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding member: " + e.getMessage());
        }
    }

    // Xóa thành viên
    public void removeMember(String memberId) {
        String sql = "DELETE FROM members WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error removing member: " + e.getMessage());
        }
    }
    
 // Cập nhật thành viên
    public void updateMember(Member member) {
        String sql = "UPDATE members SET name = ?, email = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getEmail());
            pstmt.setString(3, member.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating member: " + e.getMessage());
        }
    }

    // Lấy thành viên theo ID
    public Member getMember(String memberId) {
        String sql = "SELECT * FROM members WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Member(rs.getString("id"), rs.getString("name"), rs.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting member: " + e.getMessage());
        }
        return null;
    }
    
 // Lấy tất cả thành viên (dùng để báo cáo)
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                members.add(new Member(rs.getString("id"), rs.getString("name"), rs.getString("email")));
            }
        } catch (SQLException e) {
            System.out.println("Error getting all members: " + e.getMessage());
        }
        return members;
    }
    
    
}
