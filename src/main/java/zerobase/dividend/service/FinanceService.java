package zerobase.dividend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.dividend.exception.impl.NoCompanyException;
import zerobase.dividend.model.Company;
import zerobase.dividend.model.Dividend;
import zerobase.dividend.model.ScrapedResult;
import zerobase.dividend.model.constant.CacheKey;
import zerobase.dividend.persist.CompanyRepository;
import zerobase.dividend.persist.DividendRepository;
import zerobase.dividend.persist.entity.CompanyEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        CompanyEntity companyEntity = companyRepository.findByName(companyName)
                .orElseThrow(NoCompanyException::new);

        List<Dividend> dividends =
                dividendRepository.findAllByCompanyEntity(companyEntity)
                        .stream().map(Dividend::fromEntity)
                        .toList();
        return new ScrapedResult(Company.fromEntity(companyEntity), dividends);
    }
}
