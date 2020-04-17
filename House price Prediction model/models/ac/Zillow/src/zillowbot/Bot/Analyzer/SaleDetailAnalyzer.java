package zillowbot.Bot.Analyzer;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import zillowbot.Entities.Fact;
import zillowbot.Util.Util;

import java.util.Objects;

public class SaleDetailAnalyzer {
    public static Fact getFacts(Document document){
        Elements elements = document.getElementsByClass("fact-group");
        String estateType = "";
        Integer yearBuilt = 0;
        String heating = "";
        String cooling = "";
        String parking = "";
        Double pricePerSqft = 0.0;
        String lot = "";
        Integer daysOnZillow = 0;
        Integer saves = 0;
        for (Element element : elements) {
            try {
                Element labelElement = element.getElementsByClass("fact-label").first();
                Element valueElement = element.getElementsByClass("fact-value").first();
                String label = labelElement.text().toLowerCase();
                String value = valueElement.text();
                if (label.contains("type")) {
                    estateType = value;
                } else if (label.contains("year built")) {
                    yearBuilt = Util.getDigits(value);
                } else if (label.contains("heating")) {
                    heating = value;
                } else if (label.contains("cooling")) {
                    cooling = value;
                } else if (label.contains("parking")) {
                    parking = value;
                } else if (label.contains("lot")) {
                    lot = value;
                } else if (label.contains("days on zillow")) {
                    daysOnZillow = Util.getDigits(value);
                } else if (label.contains("price/sqft")) {
                    pricePerSqft = Util.getDouble(value);
                } else if (label.contains("saves")) {
                    saves = Util.getDigits(value);
                }
            }catch(Exception ex){
                //Do nothing
            }
        }
        return new Fact(estateType, yearBuilt, heating, cooling, parking, pricePerSqft, lot, saves, daysOnZillow);
    }
}
