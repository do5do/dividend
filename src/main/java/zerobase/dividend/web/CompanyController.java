package zerobase.dividend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zerobase.dividend.model.Company;
import zerobase.dividend.model.constant.CacheKey;
import zerobase.dividend.service.CompanyService;
import zerobase.dividend.web.dto.AddCompanyRequest;

import java.util.List;
import java.util.Objects;


@RequiredArgsConstructor
@RestController
@RequestMapping("/company")
public class CompanyController {
    private final CompanyService companyService;
    private final CacheManager redisCacheManager;

    @PreAuthorize("hasRole('READ')")
    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> autocomplete(@RequestParam String keyword) {
        return ResponseEntity.ok(companyService.getCompanyNamesByKeyword(keyword));
    }

    @PreAuthorize("hasRole('READ')")
    @GetMapping
    public ResponseEntity<Page<Company>> searchCompany(final Pageable pageable) {
        return ResponseEntity.ok(companyService.getAllCompany(pageable));
    }

    @PreAuthorize("hasRole('WRITE')") // spring security 메소드 단위 권한 설정
    @PostMapping
    public ResponseEntity<Company> addCompany(
            @RequestBody @Valid AddCompanyRequest request) {
        String ticker = request.ticker().trim();
        Company company = companyService.save(ticker);
        return ResponseEntity.ok(company);
    }

    @PreAuthorize("hasRole('WRITE')")
    @DeleteMapping("/{ticker}")
    public ResponseEntity<Void> deleteCompany(@PathVariable String ticker) {
        String companyName = companyService.deleteCompanyByTicker(ticker);
        clearFinanceCache(companyName);
        return ResponseEntity.ok().build();
    }

    public void clearFinanceCache(String companyName) {
        Objects.requireNonNull(redisCacheManager.getCache(CacheKey.KEY_FINANCE))
                .evict(companyName);
    }
}
