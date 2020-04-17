package zillowbot.Util;

import java.io.*;
import java.util.ArrayList;

public class FileUtil {
    public static ArrayList<String> readFileAsList(String pathname)
    {
        ArrayList<String> result = new ArrayList<String>();
        try
        {
            File file = new File(pathname);
            InputStream instream =new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            bufferedReader.readLine();
            String str;
            while((str=bufferedReader.readLine())!=null)
            {
                result.add(str);
            }
            bufferedReader.close();
            instream.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return result;
    }
}
