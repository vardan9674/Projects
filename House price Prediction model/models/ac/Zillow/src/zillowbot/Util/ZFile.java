package zillowbot.Util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class ZFile {
    public static void SaveText(String path,String text,boolean appendDateTime)
    {
        String filePath;
        if(appendDateTime)
            filePath = filePathWithNowTimeAtEnd(path);
        else
            filePath = path;
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))
        {
            try {
                writer.write(text);
                writer.flush();
            }finally {
                writer.close();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void SaveText(String path,String text)
    {
        SaveText(path,text,false);
    }

    public static String getWithoutExtension(String fileFullPath){
        int lastIndexOf = fileFullPath.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return fileFullPath;
        }
        return fileFullPath.substring(0, fileFullPath.lastIndexOf('.'));
    }

    private static String getExtension(String filename) {
        int lastIndexOf = filename.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return filename.substring(lastIndexOf);
    }

    public static String filePathWithNowTimeAtEnd(String path)
    {
        String extension = getExtension(path);
        return getWithoutExtension(path)+"_"+ZDate.now4File()+extension;
    }
}
