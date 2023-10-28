package zerobase.dividend.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import zerobase.dividend.model.Company;
import zerobase.dividend.model.ScrapedResult;
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

    public void addAutocompleteKeyword(String keyword) {
        trie.put(keyword, null);
    }

    public List<String> autocomplete(String keyword) {
        return trie.prefixMap(keyword).keySet()
                .stream()
                .limit(10) // keyword를 입력하지 않은 경우 모든 리스트를 조회하는데, 데이터가 많아지면 limit을 거는게 좋다.
                .toList();
    }

    public void deleteAutocompleteKeyword(String keyword) {
        trie.remove(keyword);
    }
}
