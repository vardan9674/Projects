package zillowbot.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ZDate {
    public static String now(String pattern)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    public static String now4File()
    {
        return now("yyyyMMdd_HHmmss");
    }
}
