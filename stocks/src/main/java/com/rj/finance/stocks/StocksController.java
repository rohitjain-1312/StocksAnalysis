package com.rj.finance.stocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Rohit Jain(i860661) on 6/20/18.
 */
@RestController
@RequestMapping("/stocks")
public class StocksController {

  private static final Logger logger = LoggerFactory.getLogger(StocksController.class);
  private static List<String> symbols = Arrays.asList("SPOT", "NFLX", "AMZN", "FB",
    "NVDA", "SQ", "TWTR", "AAPL", "GRPN", "TSLA", "MU", "MSFT", "WMT", "ROKU", "SAP");

  @RequestMapping(value = "/report/zacks",
    method = RequestMethod.GET)
  public String getReport() throws Exception {
    logger.info("Generating zacks report..");
    List<StockPojo> stocks = new ArrayList<>();
    for (String symbol : symbols) {
      try {
        stocks.add(new StockPojo(symbol, getRank(symbol)));
      } catch (IOException e) {
        logger.error(e.toString());
      }
    }
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String result = ow.writeValueAsString(stocks);
    BufferedWriter writer = new BufferedWriter(new FileWriter("stocks.txt"));
    writer.write(result);
    writer.close();
    return result;
  }

  private String getRank(String symbol) throws IOException {
    String url = "https://www.zacks.com/stock/quote/" + symbol;
    logger.info("URL: " + url);
    Document doc = Jsoup.connect(url).get();
    Element rankElement = doc.select(".rank_view").first();
    return rankElement == null ? "" : rankElement.text();
  }
}
