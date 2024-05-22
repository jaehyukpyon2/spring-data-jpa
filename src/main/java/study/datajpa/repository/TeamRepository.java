package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;

@Repository // @Repository annotation 없어도 Ok
public interface TeamRepository extends JpaRepository<Team, Long> {
}
