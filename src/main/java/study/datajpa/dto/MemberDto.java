package study.datajpa.dto;

import lombok.Data;
import study.datajpa.entity.Member;

@Data // DTO 에서는 사용하지 않는다. getter, setter 다 있는 거라.
public class MemberDto {
    private Long id;
    private String username;
    private String teamname;

    public MemberDto(Long id, String username, String teamname) {
        this.id = id;
        this.username = username;
        this.teamname = teamname;
    }

    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        if (member.getTeam() != null) {
            this.teamname = member.getTeam().getName();
        }
    }
}
