package zerobase.dividend.persist.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zerobase.dividend.model.Company;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "company")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticker;

    private String name;

    @OneToMany(mappedBy = "companyEntity", cascade = CascadeType.ALL)
    private List<DividendEntity> dividends = new ArrayList<>();

    private CompanyEntity(String ticker, String name) {
        this.ticker = ticker;
        this.name = name;
    }

    public static CompanyEntity of(Company company) {
        return new CompanyEntity(company.ticker(), company.name());
    }

    public void addDividendEntity(DividendEntity dividendEntity) {
        this.dividends.add(dividendEntity);
        dividendEntity.setCompanyEntity(this);
    }
}
