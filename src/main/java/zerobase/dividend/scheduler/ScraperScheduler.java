package zerobase.dividend.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zerobase.dividend.model.Company;
import zerobase.dividend.model.ScrapedResult;
import zerobase.dividend.model.constant.CacheKey;
import zerobase.dividend.persist.CompanyRepository;
import zerobase.dividend.persist.DividendRepository;
import zerobase.dividend.persist.entity.CompanyEntity;
import zerobase.dividend.persist.entity.DividendEntity;
import zerobase.dividend.scraper.Scraper;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScraperScheduler {
    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true) // 모든 캐시 삭제
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() { // 이러한 기능의 메소드는 spring batch를 사용하는게 좋다.
        List<CompanyEntity> companies = companyRepository.findAll();

        for (CompanyEntity companyEntity : companies) {
            log.info("scraping scheduler is started -> {}", companyEntity.getName());
            ScrapedResult scrapedResult = yahooFinanceScraper.scrap(
                    Company.fromEntity(companyEntity));

            scrapedResult.dividends().stream()
                    .map(DividendEntity::of)
                    .forEach(o -> {
                        boolean exists =
                                dividendRepository.existsByCompanyEntityAndDate(
                                        o.getCompanyEntity(), o.getDate());
                        if (!exists) {
                            dividendRepository.save(o);
                        }
                    });

            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 한다.
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
