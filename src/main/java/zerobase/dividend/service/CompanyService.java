package zerobase.dividend.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import zerobase.dividend.exception.impl.AlreadyExistTickerException;
import zerobase.dividend.exception.impl.FailToScrapTickerException;
import zerobase.dividend.exception.impl.NoCompanyException;
import zerobase.dividend.model.Company;
import zerobase.dividend.model.ScrapedResult;
import zerobase.dividend.persist.repository.CompanyRepository;
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
            throw new AlreadyExistTickerException();
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
            throw new FailToScrapTickerException();
        }

        ScrapedResult scrapedResult = yahooFinanceScraper.scrap(company);

        CompanyEntity companyEntity = CompanyEntity.of(company);
        scrapedResult.dividends().forEach(o ->
            companyEntity.addDividendEntity(DividendEntity.of(o)));

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
                .limit(10)
                .toList();
    }

    // company 삭제 시 호출
    public void deleteAutocompleteKeyword(String keyword) {
        trie.remove(keyword);
    }

    @Transactional
    public String deleteCompanyByTicker(String ticker) {
        CompanyEntity companyEntity = companyRepository.findByTicker(ticker)
                .orElseThrow(NoCompanyException::new);

        companyRepository.delete(companyEntity);
        return companyEntity.getName();
    }
}
