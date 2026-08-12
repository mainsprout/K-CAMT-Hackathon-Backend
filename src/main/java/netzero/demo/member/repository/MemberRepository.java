package netzero.demo.member.repository;

import java.util.Optional;
import netzero.demo.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByGoogleId(String googleId);
}
