package zillowbot.Bot.Analyzer;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import zillowbot.Entities.Estate;

public abstract class CardAnalyzer {

    protected Document document;

    protected String address;
    protected String state;
    protected String locality;
    protected String zpid;
    protected Double latitude;
    protected Double longitude;
    protected String status;
    protected String cardBadge;
    protected String homeDetailsLink;
    protected  String type;

    private String getHomeDetailsLink(Document document)
    {
        if (!document.body().select("a").isEmpty()) {
            return document.body().select("a").first().attr("href");
        }
        return "";
    }

    private String getCardBadge(){
        Element cardBadgeElement = document.body().getElementsByClass("zsg-photo-card-img").first();
        if (cardBadgeElement != null) {
            return cardBadgeElement.text();
        }
        return "";
    }

    private String getType()
    {
        if (document.body().getElementsByClass("zsg-icon-for-sale").first() != null) {
            return "sale";
        }
        if (document.body().getElementsByClass("zsg-icon-for-rent").first() != null) {
            return "rent";
        }
        if (document.body().getElementsByClass("zsg-icon-pre-market").first() != null) {
            return "pre-market";
        }
        return "";
    }

    private String getZPID()
    {
        Element element = document.body().select("article").first();
        if(! (element.attr("data-zpid").isEmpty() )) {
            return element.attr("data-zpid")
                    .replaceAll("'", "")
                    .replaceAll(",","");
        }
        return "";
    }

    CardAnalyzer(Document document) {
        this.document = document;
        homeDetailsLink = getHomeDetailsLink(document);
        cardBadge = getCardBadge();
        type = getType();
        zpid = getZPID();
    }

    public abstract Estate Analyze();
}
