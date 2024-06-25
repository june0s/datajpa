package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"}) // 연관관계 필드는 출력하지 않는다.
public class Team extends JpaBaseEntity {

    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team") // foreignkey 가 없는 쪽에 mappedBy 사용
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
