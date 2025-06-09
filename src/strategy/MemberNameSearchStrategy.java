package strategy;

import singleton.Member;
import singleton.MemberManager;
import java.util.List;
import java.util.stream.Collectors;

public class MemberNameSearchStrategy implements SearchStrategy<Member> {
    private MemberManager memberManager;

    public MemberNameSearchStrategy(MemberManager memberManager) {
        this.memberManager = memberManager;
    }

    @Override
    public List<Member> search(String query) {
        return memberManager.getAllMembers().stream()
                .filter(member -> member.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}
