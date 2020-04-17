package zillowbot.Bot.Analyzer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Objects;

public class CardAnalyzerFactory {
    public static CardAnalyzer CreateCardAnalyzer(String html) {
        CardAnalyzer result = null;
        if(!Objects.equals(html,""))
        {
            Document card = Jsoup.parse(html);
            if (html.contains("itemprop=\"streetAddress\"")) {
                result = new SingleCardAnalyzer(card);
            }else if(html.contains("zsg-photo-card-unit")){
                result = new OptionalCardAnalyzer(card);
            }
        }
        return result;
    }
}
