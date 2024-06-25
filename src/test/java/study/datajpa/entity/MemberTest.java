package study.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(value = false)
public class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 12, teamA);
        Member member3 = new Member("member3", 14, teamB);
        Member member4 = new Member("member4", 16, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush(); // db 에 강제로 쿼리 날리기
        em.clear(); // 영속성 컨텍스트 초기화

        // 확인
        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }

    @Test
    public void JpaEventBaseEntityTest() throws Exception {
        // given
        Member member = new Member("member1");
        memberRepository.save(member); // <- @PrePersist 발생.

        Thread.sleep(1000L);
        member.setUsername("member2");

        em.flush(); // @PreUpdate 발생.
        em.clear();

        // when
        Member findMember = memberRepository.findById(member.getId()).get();

        // then
        System.out.println("findMember created = " + findMember.getCreatedDate());
        System.out.println("findMember updated = " + findMember.getLastModifiedDate());
        System.out.println("findMember createdBy = " + findMember.getCreateBy());
        System.out.println("findMember updatedBy = " + findMember.getLastModifiedBy());

    }
}
