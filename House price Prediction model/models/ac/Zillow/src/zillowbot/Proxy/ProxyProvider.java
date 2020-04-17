package zillowbot.Proxy;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import zillowbot.Global;

import java.net.URL;
import java.nio.charset.Charset;

public class ProxyProvider {
    public static ZProxy newProxy(String provider)
    {
        ZProxy result =null;
        try {
            URL url = new URL(provider);
            String jStr = IOUtils.toString(url, Charset.forName("UTF-8"));
            JSONObject json = new JSONObject(jStr);
            String ip = json.get("ip").toString();
            Integer port = Integer.parseInt(json.get("port").toString());
            result = new ZProxy(ip, port);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static void bindNewHttpsProxy(String provider)
    {
        ZProxy proxy = newProxy(provider);
        proxy.useOnHttps();
    }

    public static ZProxy bindNewHttpsProxy()
    {
        ZProxy proxy = newProxy();
        proxy.useOnHttps();
        return proxy;
    }

    public static ZProxy newProxy()
    {
        return newProxy(Global.getProxyProvider());
    }
}
