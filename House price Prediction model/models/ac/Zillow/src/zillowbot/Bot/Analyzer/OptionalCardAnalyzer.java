package zillowbot.Bot.Analyzer;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import zillowbot.Entities.Estate;
import zillowbot.Entities.EstateOption;
import zillowbot.Util.Util;

import java.util.ArrayList;
import java.util.List;

public class OptionalCardAnalyzer extends CardAnalyzer {
    public OptionalCardAnalyzer(Document document) {
        super(document);
    }

    private String PutPointForString(String data)
    {
        if (!data.contains(".")) {
            data=data.substring(0,2)+"."+data.substring(2);
        }
        return data;
    }

    private Double getLatitude() {
        String result = "0.0";
        Element element = document.select("article").first();
        if (element != null) {
            result = element.attr("data-latitude");
            if (!result.contains(".")) {
                result = result.substring(0,2)+"."+result.substring(2);
            }
        }
        return Double.parseDouble(result);
    }

    private Double getLongitude() {
        String result = "0.0";
        Element element = document.select("article").first();
        if (element != null) {
            result = element.attr("data-longitude");
            if (!result.contains(".")) {
                result = result.substring(0,2)+"."+result.substring(2);
            }
        }
        return Double.parseDouble(result);
    }

    private void getAddressInfo()
    {
        Element element=document.select("span.zsg-photo-card-address").first();
        if (element != null) {
            String[] info = element.text().split(",");
            if (info.length > 0) {
                address = info[0];
            }
            if (info.length > 1) {
                locality = info[1].replaceAll(" ","");
            }
            if (info.length > 2) {
                state = info[2].replaceAll(" ", "");
            }
        }
    }

    private String getStatus()
    {
        if (document.getElementsByClass("zsg-photo-card-status").size() > 0) {
            return document.getElementsByClass("zsg-photo-card-status").first().text();
        }
        return "";
    }

    private List<EstateOption> getEstateOptions()
    {
        List<EstateOption> result = new ArrayList<>();
        Element priceStat = document.select("div.zsg-photo-card-caption").select("span.zsg-photo-card-info").first();
        Elements options = priceStat.getElementsByClass("zsg-photo-card-unit");
        for (Element option : options) {
            EstateOption estateOption = new EstateOption();
            if(option.select("strong").hasText())
            {
                estateOption.Bedrooms = Integer.parseInt(option.select("strong").text());
                if (option.childNodeSize() > 2) {
                    String txt = option.childNodes().get(2).toString();
                    estateOption.Price = Util.getDigits(txt);
                }
                result.add(estateOption);
            }
        }
        return result;
    }

    @Override
    public Estate Analyze() {
        Estate estate = null;
        try {
            Element element = document.body().select("article").first();
            latitude = getLatitude();
            longitude = getLongitude();
            getAddressInfo();
            status = getStatus();
            List<EstateOption> estateOptions = getEstateOptions();
            String options ="";
            String and = "";
            for (EstateOption option : estateOptions) {
                options += and + option.Bedrooms + " bd";
                if(option.Bathrooms>0)
                    options+=" - "+option.Bathrooms+" ba";
                options += " " + option.Price + "$+";
                and = " , ";
            }
            estate = new Estate(zpid, type, state, locality, address, latitude, longitude, -1
                    , -1, -1.0, -1, status,cardBadge, homeDetailsLink);
            estate.setOptions(options);
        }catch(Exception ex){
            System.out.println("Error : OptionalCardAnalyzer : "+ex.getMessage());
        }
        return estate;
    }
}
