package zerobase.dividend.model;

import java.util.List;

public record ScrapedResult(
        Company company,
        List<Dividend> dividends) {
}
