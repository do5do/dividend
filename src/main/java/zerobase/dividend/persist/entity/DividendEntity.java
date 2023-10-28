package zerobase.dividend.persist.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zerobase.dividend.model.Dividend;

import java.time.LocalDateTime;

@Entity(name = "dividend")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"company_id", "date"} // 복합 유니크 키
                )
        }
)
public class DividendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private CompanyEntity companyEntity;

    private LocalDateTime date;

    private String dividend;

    private DividendEntity(LocalDateTime date, String dividend) {
        this.date = date;
        this.dividend = dividend;
    }

    public static DividendEntity of(Dividend dividend) {
        return new DividendEntity(dividend.date(), dividend.dividend());
    }

    public void setCompanyEntity(CompanyEntity companyEntity) {
        this.companyEntity = companyEntity;
    }
}
