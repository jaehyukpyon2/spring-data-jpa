package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;
    @Autowired
    private TeamRepository teamRepository;

    @Test
    public void testMember() throws Exception {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();
        assertThat(member).isEqualTo(savedMember); // true
        assertThat(savedMember).isEqualTo(findMember); // true
    }

    @Test
    public void basicCRUD() throws Exception {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        System.out.println("em.contains(member1) => " + em.contains(member1)); // true

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all).hasSize(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        System.out.println("em.contains(member1) => " + em.contains(member1)); // false
        System.out.println("member1 == findMember1 =>" + (findMember1 == findMember1)); // true
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 10);
    }
    
    @Test
    public void testQuery() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String username : usernameList) {
            System.out.println("username = " + username);
        }
    }

    @Test
    public void findByNames() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println(member);
        }
    }

    @Test
    public void paging() throws Exception {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        PageRequest pageRequest = PageRequest.of(1, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // then
        List<Member> content = page.getContent();
        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println();
    }

    @Test
    public void bulkUpdate() throws Exception {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));

        Member member5 = new Member("member5", 40);
        memberRepository.save(member5);

        System.out.println("----------------------"); // insert query 다섯 개 실행되고 그 이후 update query 실행
        int resultCount = memberRepository.bulkAgePlus(20);

        //em.clear();

        Member findMember = memberRepository.findByUsername("member5").get(0);
        System.out.println("findMember = " + findMember);

        System.out.println("member5 == findMember => " + (member5 == findMember));
        // update bulk 연산 후 em clear하지 않으면 true 반환
        // @Modifying(clearAutomatically = true) 이 설정되어 있거나, em이 clear되었으면 false return
    }


    @Test
    public void findMemberLazy() throws Exception {
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

        List<Member> members = memberRepository.findAll(); // 오직 member만 갖고 온다.
        // fetch eager 설정일 경우 => 그리고 곧바로 team 정보들을 갖고 온다 (두 번의 쿼리로 team1, team2)

        for (Member member : members) {
            System.out.println("member = " + member);
        }

        em.clear();
        System.out.println("==============================================");
        Optional<Member> findMember1 = memberRepository.findById(member1.getId());
        // em.find(Member.class, member1.getId())와 동일하게 동작, 즉 ManyToOne Team team이 Eager로 설정되어 있으면,
        // left join 해서 team정보까지 한꺼번에 갖고 온다. 즉 jpql이 나가는 게 아니다.
    }
    
    @Test
    public void findMemberFetchTeam() throws Exception {
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

        List<Member> members = memberRepository.findMemberFetchJoin();

        for (Member member : members) {
            System.out.println("member = " + member);
        }

        em.clear();
        System.out.println("==============================================");
        Optional<Member> findMember1 = memberRepository.findById(member1.getId());
    }

    @Test
    public void findMemberEntityGraph() throws Exception {
        memberRepository.findMemberEntityGraph();
    }

    @Test
    public void callCustom() throws Exception {
        List<Member> result = memberRepository.findMemberCustom();
    }
    
    @Test
    public void projections() throws Exception {
        Member m1 = new Member("m1", 0, null);
        Member m2 = new Member("m2", 0, null);


    }
}