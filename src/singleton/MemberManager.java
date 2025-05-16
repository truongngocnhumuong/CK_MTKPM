package singleton;
//Lớp MemberManager quản lý danh sách thành viên

import java.util.ArrayList;
import java.util.List;

public class MemberManager {
	private static MemberManager instance;
	private List<Member> members = new ArrayList<>();
	
	private MemberManager() {} // Constructor private để đảm bảo Singleton
	
	public static synchronized MemberManager getInstance() {
        if (instance == null) {
            instance = new MemberManager();
        }
        return instance;
    }
	
	// Thêm, xóa, cập nhật thành viên
    public void addMember(Member member) { 
    	members.add(member); 
    }
    public void removeMember(String memberId) { 
    	members.removeIf(m -> m.getId().equals(memberId));
    }
    public void updateMember(Member member) {
        removeMember(member.getId());
        addMember(member);
    }
    public Member getMember(String memberId) {
    	return members.stream().filter(m -> m.getId().equals(memberId)).findFirst().orElse(null); 
    }

}
