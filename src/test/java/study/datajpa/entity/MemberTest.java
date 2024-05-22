package study.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity() throws Exception {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
    }

    @Test
    public void jpaEventBaseEntity() throws Exception {
        // Member가 JpaBaseEntity를 상속받았을 때
        Member member = new Member("member1");
        memberRepository.save(member);
        System.out.println("===== 1 ===== > " + member.getCreatedDate() + ", " + member.getUpdatedDate());
        // 여기에서는 created, update 가 same

        Thread.sleep(5_000);

        member.setUsername("member2");
        System.out.println("===== 2 ===== > " + member.getCreatedDate() + ", " + member.getUpdatedDate());
        // member의 이름을 변경했어도, 인스턴스 필드 updatedDate가 바로 변경되지 않는다. 즉 아직까지 created, updated same

        em.flush();
        // flush 해서 db로 insert 돼서 들어갈 때 member의 이름이 변경됐으니 updatedDate가 새로 갱신돼서 updated query가 실행된다.
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        System.out.println("===== 3 ===== > " + findMember.getCreatedDate() + ", " + findMember.getUpdatedDate());
        // 여기에서는 created, updated가 당연시 서로 다름
    }

    @Test
    public void baseEntity() throws Exception {
        // Member가 BaseEntity를 상속받았을 때

        Member member = new Member("member1");
        memberRepository.save(member);
        System.out.println("===== 1 ===== > " + member.getCreatedDate() + ", " + member.getUpdatedDate());
        // 여기에서는 created, update 가 same

        Thread.sleep(5_000);

        member.setUsername("member2");
        System.out.println("===== 2 ===== > " + member.getCreatedDate() + ", " + member.getUpdatedDate());
        // member의 이름을 변경했어도, 인스턴스 필드 updatedDate가 바로 변경되지 않는다. 즉 아직까지 created, updated same

        em.flush();
        // flush 해서 db로 insert 돼서 들어갈 때 member의 이름이 변경됐으니 updatedDate가 새로 갱신돼서 updated query가 실행된다.
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        System.out.println("===== 3 ===== > " + findMember.getCreatedDate() + ", " + findMember.getUpdatedDate());
        // 여기에서는 created, updated가 당연시 서로 다름
    }

    @Test
    public void baseEntity2() throws Exception {
        // Member가 BaseEntity를 상속받았을 때

        Member member = new Member("member1");
        memberRepository.save(member);
        System.out.println("===== 1 ===== > " + member.getCreatedDate() + ", " + member.getUpdatedDate());
        // 여기에서는 created, update 가 same

        Thread.sleep(5_000);

        member.setCreatedDate(LocalDateTime.now());

        System.out.println("===== 1.5 ===== > " + member.getCreatedDate() + ", " + member.getUpdatedDate());

        Thread.sleep(5_000);

        member.setUsername("member2");
        System.out.println("===== 2 ===== > " + member.getCreatedDate() + ", " + member.getUpdatedDate());
        // member의 이름을 변경했어도, 인스턴스 필드 updatedDate가 바로 변경되지 않는다. 즉 아직까지 created, updated same

        em.flush();
        // flush 해서 db로 insert 돼서 들어갈 때 member의 이름이 변경됐으니 updatedDate가 새로 갱신돼서 updated query가 실행된다.
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        System.out.println("===== 3 ===== > " + findMember.getCreatedDate() + ", " + findMember.getUpdatedDate());
        // 여기에서는 created, updated가 당연시 서로 다름
    }
}