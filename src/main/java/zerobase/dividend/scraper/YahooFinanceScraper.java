package zerobase.dividend.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import zerobase.dividend.model.Company;
import zerobase.dividend.model.Dividend;
import zerobase.dividend.model.ScrapedResult;
import zerobase.dividend.model.constant.Month;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {
    private static final String URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400; // 60 * 60 * 24 (1일을 초로 환산)

    @Override
    public ScrapedResult scrap(Company company) {
        long now = System.currentTimeMillis() / 1000; // 초로 변환
        String url = String.format(URL, company.ticker(), START_TIME, now);

        try {
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableEle = parsingDivs.get(0);
            Element tbody = tableEle.children().get(1);

            List<Dividend> dividends = new ArrayList<>();
            for (Element e : tbody.children()) {
                String text = e.text();
                if (!text.endsWith("Dividend")) {
                    continue;
                }

                String[] split = text.split(" ");
                int month = Month.strToNumber(split[0]);
                int day = Integer.parseInt(split[1].replace(",", ""));
                int year = Integer.parseInt(split[2]);
                String dividend = split[3];

                if (month < 0) {
                    throw new RuntimeException("Unexpected Month enum value ->" + split[0]);
                }

                dividends.add(new Dividend(
                        LocalDateTime.of(year, month, day, 0, 0),
                        dividend));
            }

            return new ScrapedResult(company, dividends);
        } catch (IOException e) {
            // todo 예외처리
            throw new RuntimeException(e);
        }
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Elements h2 = document.getElementsByTag("h1");
            if (h2.isEmpty()) {
                throw new RuntimeException("올바르지 않은 ticker");
            }

            String name = h2.get(0).text()
                    .replaceAll("\\(.*", "").trim();

            return new Company(ticker, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // todo 예외처리
        return null;
    }
}
