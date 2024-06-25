package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"}) // 연관관계 필드는 출력하지 않는다.
@NamedQuery( // 잘 사용 안 함.
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username")
// 잘 사용 안 함.
@NamedEntityGraph(name = "Member.all", attributeNodes = {@NamedAttributeNode("team")})
public class Member extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    // 연관관계 메소드.
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
