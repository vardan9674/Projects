package zillowbot.Bot.Analyzer;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import zillowbot.Entities.Estate;
import zillowbot.Entities.EstateOption;
import zillowbot.Util.Util;

public class SingleCardAnalyzer extends CardAnalyzer {

    public SingleCardAnalyzer(Document document) {
        super(document);
    }

    private Double getLatitude(Element element) {
        if (element != null) {
            Element e = element.getElementsByTag("meta").first();
            if ((e != null) && (!e.attr("content").isEmpty())) {
                String latitude = e.attr("content");
                latitude = latitude.replaceAll(" ", "");
                return Double.parseDouble(latitude);
            }
        }
        return 0.0;
    }

    private Double getLongitude(Element element)
    {

        if(element != null) {
            Element e = element.getElementsByTag("meta").last();
            if ((e != null) && (!e.attr("content").isEmpty())) {
                String longitude = e.attr("content");
                longitude = longitude.replaceAll(" ", "");
                return Double.parseDouble(longitude);
            }
        }
        return 0.0;
    }

    private Integer getPrice() {
        Element priceElement=document.getElementsByClass("zsg-photo-card-price").first();
        Integer result = 0;
        if (priceElement != null) {
            String priceText = priceElement.text();
            result = (int) Math.round(Util.getDouble(priceText));
            if(priceText.contains("k") || priceText.contains("K"))
                result = result*1000;
        }
        return result;
    }

    private String getAddress(Element element)
    {
        String result = "";
        if ((element != null) && (element.childNodeSize() > 0) && (element.child(0).hasText())) {
            result = element.child(0).text();
            result = result.replaceAll(",","-");
        }
        return result;
    }

    private String getLocality(Element element)
    {
        String result = "";
        if ((element != null) && (element.childNodeSize() > 1) && (element.child(1).hasText())) {
            result = element.child(1).text();
            result = result.substring(0, 1).toUpperCase() + result.substring(1).toLowerCase();
            result = result.replaceAll(",","-");
        }
        return result;
    }

    private String getState(Element element)
    {
        String result = "";
        if ((element != null) && (element.childNodeSize() > 2) && (element.child(2).hasText())) {
            result = element.child(2).text();
        }
        return result;
    }

    private Integer getPostalCode(Document document)
    {
        String result = "0";
        Element e = document.select("span[itemprop=postalCode]").first();
        if (e != null) {
            result = e.text();
        }
        return Integer.parseInt(result);
    }

    private String getStatus()
    {
        String result = "";
        if (document.getElementsByClass("zsg-photo-card-status").size()>0)
        {
            Element StatusElement = document.getElementsByClass("zsg-photo-card-status").first();
            result = StatusElement.text();
        }
        return result;
    }

    private EstateOption getEstateOption()
    {
        EstateOption estateOption = new EstateOption();
        estateOption.Price = getPrice();
        if (document.getElementsByClass("zsg-photo-card-info").hasText() )
        {
            Element element= document.getElementsByClass("zsg-photo-card-info").first();
            String txt = element.text().toLowerCase();

            if (txt.contains("acre")||txt.contains("acres")||txt.contains("ac")) {
                estateOption.AreaSpace = (int)Math.round(Util.getDouble(txt)*43560);
            } else if(txt.contains("lot")){
                estateOption.AreaSpace = (int)Math.round(Util.getDouble(txt));
            } else {
                estateOption.Bedrooms = Util.getDigits(element.childNodes().get(0).toString());
                estateOption.Bathrooms = Util.getDouble(element.childNodes().get(2).toString());
                String area = element.childNodes().get(4).toString();
                estateOption.AreaSpace = Util.getDigits(area);
            }
        }
        return estateOption;
    }

    @Override
    public Estate Analyze() {
        Estate estate = null;
        try {
            Element element = document.body().select("article").first();
            Integer zipCode = getPostalCode(document);
            latitude = getLatitude(element);
            longitude = getLongitude(element);
            element = document.getElementsByClass("hide").first();
            address = getAddress(element);
            state = getState(element);
            locality = getLocality(element);
            status = getStatus();
            EstateOption estateOption = getEstateOption();
            estate = new Estate(zpid, type, state, locality, address, latitude, longitude, estateOption.Price
                    , estateOption.Bedrooms, estateOption.Bathrooms, estateOption.AreaSpace, status, cardBadge
                    , homeDetailsLink);
        }catch(Exception ex) {
            System.out.println("Error : SingleCardAnalyzer : "+ex.getMessage());
        }
        return estate;
    }
}
