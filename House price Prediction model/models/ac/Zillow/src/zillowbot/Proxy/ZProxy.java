package zillowbot.Proxy;

public class ZProxy {
    public String Ip;
    public Integer Port;

    public ZProxy(String ip, Integer port) {
        Ip = ip;
        Port = port;
    }

    public void useOnHttp() {
        System.setProperty("http.proxyHost", Ip);
        System.setProperty("http.proxyPort", Port.toString());
        System.out.println("http:"+Ip+":"+Port);
    }

    public void useOnHttps() {
        System.setProperty("https.proxyHost", Ip);
        System.setProperty("https.proxyPort", Port.toString());
        System.out.println("https:"+Ip+":"+Port);
    }
}
