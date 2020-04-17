package zillowbot.Bot;

import zillowbot.Forms.ControllerProgress;

import javax.swing.*;
import java.util.List;

public class TestWorker extends SwingWorker<Boolean, String> {

    private ControllerProgress ctrlProgress;
    public TestWorker(ControllerProgress ctrl)
    {
        ctrlProgress = ctrl;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        for (int i=1;i<=100;i++)
        {
            try
            {
                setProgress(i);
                publish(i+"%");
                Thread.sleep(100);
            }catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return true;
    }

    protected void process(List<String> chunks) {
        // Here we receive the values that we publish().
        // They may come grouped in chunks.
        String mostRecentValue = chunks.get(chunks.size()-1);

        if(ctrlProgress!=null)
            ctrlProgress.setData(mostRecentValue);
    }
}
