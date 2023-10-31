package zerobase.dividend.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import zerobase.dividend.model.Company;
import zerobase.dividend.model.ScrapedResult;
import zerobase.dividend.model.constant.CacheKey;
import zerobase.dividend.persist.CompanyRepository;
import zerobase.dividend.persist.entity.CompanyEntity;
import zerobase.dividend.persist.entity.DividendEntity;
import zerobase.dividend.scraper.Scraper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final Trie<String, String> trie;

    @Transactional
    public Company save(String ticker) {
        if (companyRepository.existsByTicker(ticker)) {
            throw new RuntimeException("already exists ticker -> " + ticker);
        }
        return storeCompanyAndDividend(ticker);
    }

    public Page<Company> getAllCompany(Pageable pageable) {
        return companyRepository.findAll(pageable)
                .map(Company::fromEntity);
    }

    private Company storeCompanyAndDividend(String ticker) {
        Company company = yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        ScrapedResult scrapedResult = yahooFinanceScraper.scrap(company);

        CompanyEntity companyEntity = CompanyEntity.of(company);
        scrapedResult.dividends().forEach(o ->
            companyEntity.addDividends(DividendEntity.of(o)));

        // todo dividend bulk insert
        companyRepository.save(companyEntity);
        return company;
    }

    public List<String> getCompanyNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);
        return companyRepository.findByNameStartsWithIgnoreCase(keyword, limit)
                .stream()
                .map(CompanyEntity::getName)
                .toList();
    }

    // controller, company 추가 시 호출 (addCompany)
    public void addAutocompleteKeyword(String keyword) {
        trie.put(keyword, null);
    }

    // controller, autocomplete 에서 호출
    public List<String> autocomplete(String keyword) {
        return trie.prefixMap(keyword).keySet()
                .stream()
                // keyword를 입력하지 않은 경우 모든 리스트를 조회하는데, 데이터가 많아지면 limit을 거는게 좋다.
                .limit(10)
                .toList();
    }

    // company 삭제 시 호출
    public void deleteAutocompleteKeyword(String keyword) {
        trie.remove(keyword);
    }

    @CacheEvict(value = CacheKey.KEY_FINANCE, key = "#ticker") // todo test 필요
    @Transactional
    public void deleteCompanyByTicker(String ticker) {
        CompanyEntity companyEntity = companyRepository.findByTicker(ticker)
                .orElseThrow(() ->
                        new RuntimeException("No company exists for that ticker. -> " + ticker));

        // todo 연관관계까지 모두 삭제 -> 근데 쿼리가 따로 날아감.. 조회해서 지우는게 나을 듯
        companyRepository.delete(companyEntity);
    }
}
