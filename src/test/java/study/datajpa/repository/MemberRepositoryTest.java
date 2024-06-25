package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(value = false)
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    public void callCustom() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        List<Member> members = memberRepository.findMemberCustom();
        for (Member m : members) {
            System.out.println("m.toString() = " + m.toString());
        }
    }

    @Test
    public void lockTest() {
        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        List<Member> members = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void queryHintTest() {
        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        findMember.setUsername("member2");

        Member readOnlyMember = memberRepository.findReadOnlyByUsername("member1");
        readOnlyMember.setUsername("member3"); // 변경 감지 체크를 안 한다.

        em.flush(); // 변경 감지. 치명적인 단점: 원본이 있어야 한다.
    }

    @Test
    public void namedEntityGraphByUsername() {
        // given
        // member1 -> teamA 참조
        // member2 -> teamB 참조
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member1", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findNamedEntityGraphByUsername("member1");
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void entityGraphByUsername() {
        // given
        // member1 -> teamA 참조
        // member2 -> teamB 참조
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member1", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findEntityGraphByUsername("member1");
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void entityGraphMix() {
        // given
        // member1 -> teamA 참조
        // member2 -> teamB 참조
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findMemberEntityGraph();
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void entityGraph() {
        // given
        // member1 -> teamA 참조
        // member2 -> teamB 참조
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void findMemberLazyTest() {
        // given
        // member1 -> teamA 참조
        // member2 -> teamB 참조
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
//        List<Member> members = memberRepository.findAll();
//
//        // N + 1 문제.
//        for (Member member : members) {
//            System.out.println("member = " + member.getUsername());
//            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
//            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
//        }

        List<Member> membersFJ = memberRepository.findMemberFetchJoin();
        for (Member member : membersFJ) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void bulkUpdateTest() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        int resultCount = memberRepository.bulkAgePlus(20);
//        em.clear(); // 영속성 컨텍스트 초기화

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        // member5 = Member(id=5, username=member5, age=40) 출력된다.
        // DB 에는 age+1 이 반영되었지만, 영속성 컨텍스트에는 아직 반영되지 않아서 40이 출력된다.
        // -> em.flush(); em.clear(); 호출해서 영속성 컨텍스트 초기화 해야한다.
        // bulk 연산 후 로직이 없으면 안 해도 됨.
        System.out.println("member5 = " + member5.toString());

        assertThat(resultCount).isEqualTo(3);
    }


    @Test
    public void sliceTest() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "username"));

        // when
        // slice 는 0 page 에서 3개 요청 시, +1해서 4개를 요청한다.
        // totalCount 쿼리를 실행하지 않기때문에, totalElements() 메소드 없다.
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);

        // then
        List<Member> members = page.getContent();
//        long totalCount = page.getTotalElements();
//        System.out.println("totalCount = " + totalCount);
        members.forEach(member -> System.out.println("member = " + member.toString()));

        // 다양한 기능을 제공한다!!
        assertThat(members.size()).isEqualTo(3);
//        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
//        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void pagingTest() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        // api 사용시, entity 가 아닌 dto 객체 반환 시 유용한 코드!
        Page<MemberDto> memberDtos = page.map(m -> {
            return new MemberDto(m.getId(), m.getUsername(), m.getTeam().getName());
        });

        // then
        List<Member> members = page.getContent();
        long totalCount = page.getTotalElements();
        System.out.println("totalCount = " + totalCount);
        members.forEach(member -> System.out.println("member = " + member.toString()));

        // 다양한 기능을 제공한다!!
        assertThat(members.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void returnTypeTest() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findListByUsername("AAA");
        Member findMember = memberRepository.findMemberByUsername("abc");
        Optional<Member> findMemberOrNot = memberRepository.findOptionalByUsername("apple");

        // 데이터가 없더라도 empty collection 을 반환 해준다.
        System.out.println("result = " + result);
        System.out.println("findMember = " + findMember); // null 반환
        // data 가 있을 수도 있고 없을 수도 있는 경우에는 Optional 을 사용한다.
        System.out.println("findMemberOrNot = " + findMemberOrNot); // 없으면, Optional.empty 반환, 2개 이상이면, Exception 발생.
    }

    @Test
    public void findByNamesTest() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByNames(List.of("AAA", "BBB"));// Arrarys.asList("AAA", "BBB") 도 가능.
        for (Member member : members) {
            System.out.println("member = " + member.toString());
        }
    }

    @Test
    public void findMemberDtoTest() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10);
        member1.setTeam(team);
        memberRepository.save(member1);

        List<MemberDto> dtos = memberRepository.findMemberDto();
        for (MemberDto dto : dtos) {
            System.out.println("dto = " + dto.toString());
        }
    }

    @Test
    public void findUsernameListTest() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String username : usernameList) {
            System.out.println("name = " + username);
        }
    }

    @Test
    public void queryTest() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(member1);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(10);
    }

    @Test
    public void namedQueryTest() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void findByHelloTest() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        Member member3 = new Member("AAA", 30);
        Member member4 = new Member("AAA", 40);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        List<Member> members = memberRepository.findTop3HelloBy();
        members.forEach(member -> {
            System.out.println(member.toString());
        });
    }

    @Test
    public void findByUsernameAndAgeGreaterThenTest() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        System.out.println("== members ==");
        members.forEach(member -> {
            System.out.println(member.toString());
        });

        assertThat(members.get(0).getUsername()).isEqualTo("AAA");
        assertThat(members.get(0).getAge()).isEqualTo(20);
        assertThat(members.size()).isEqualTo(1);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증.
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1.getId()).isEqualTo(member1.getId());
        assertThat(findMember2.getId()).isEqualTo(member2.getId());
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증.
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증.
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void testMember() {
        System.out.println("memberRepository = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }
}
