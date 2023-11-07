package zerobase.dividend.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import zerobase.dividend.persist.entity.MemberEntity;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    @Query("select m from member m left join fetch m.roles")
    Optional<MemberEntity> findByUsernameFetchJoin(String username);

    boolean existsByUsername(String username);
}
