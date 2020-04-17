package zillowbot.Bot;

import java.util.stream.Stream;

public class WebData {
    static String zillow="https://www.zillow.com";
    static String zillowHomes = "/homes/";
    static String zillowTail = "_rb/";
    static String[] userAgent = Stream.of(
            "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.149 Safari/537.36",
            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
            "Googlebot/2.1 (+http://www.google.com/bot.html)",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Trident/4.0; Avant Browser; SLCC1; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30618; InfoPath.1)",
            "Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14",
            "NetSurf/2.0 (RISC OS; armv3l)",
            "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.8.1.8pre) Gecko/20070928 Firefox/2.0.0.7 Navigator/9.0RC1",
            "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.1) Gecko/20090722 Firefox/3.5.1 Orca/1.2 build 2"
    ).toArray(String[]::new);
    public static String beginHtml = "<!DOCTYPE html><html itemscope=\"\" itemtype=\"http://schema.org/Organization\" class=\"wf-loading no-js zsg-theme-modernized \" lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:og=\"http://ogp.me/ns#\" xmlns:fb=\"http://www.facebook.com/2008/fbml\" xmlns:product=\"http://ogp.me/product#\" >\n" + "<head></head><body>";
    public static String endHtml = "</body></html>";
}
