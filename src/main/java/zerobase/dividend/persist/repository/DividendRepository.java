package zerobase.dividend.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.dividend.persist.entity.CompanyEntity;
import zerobase.dividend.persist.entity.DividendEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity, Long> {
    List<DividendEntity> findAllByCompanyEntity(CompanyEntity companyEntity);

    // 해당 컬럼들로 복합 유니크 키를 걸어 놨기 때문에 select where로 조회하는 것 보다 빠르다.
    boolean existsByCompanyEntityAndDate(CompanyEntity companyEntity,
                                         LocalDateTime date);
}
