package zillowbot.Entities;

import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Request {
    public int Id;
    public String RequestTime;
    public String UpdateTime;
    public String Type;
    public ObservableList<ZipCode> ZipCodes;

    public Request()
    {
        Id = 0;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        RequestTime = dtf.format(now);
        UpdateTime = RequestTime;
        ZipCodes = null;
    }
}
