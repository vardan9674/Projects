package zillowbot.Forms;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.concurrent.CancellationException;

public class ControllerProgress {
    @FXML private ProgressBar progressbar;
    @FXML private Label lbl_data;
    @FXML private Button btn_cancel;

    private Stage stage;
    private SwingWorker<Boolean, String> worker;

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    public void setWorker(SwingWorker<Boolean, String> worker)
    {
        this.worker = worker;
        worker.addPropertyChangeListener(

            new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    switch(evt.getPropertyName()){
                        case "progress":
                            int value=(int)evt.getNewValue();
                            Platform.runLater(() -> progressbar.setProgress((double)value/100));
                            break;
                        case "state":
                            switch ((SwingWorker.StateValue) evt.getNewValue()) {
                                case DONE:
                                    Platform.runLater(() -> stage.close());
                                    break;
                                case STARTED:
                                case PENDING:
                                    break;
                            }
                            break;
                    }
                }
            }
        );
    }

    public void close()
    {
        System.out.println("close progress");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.close();
            }
        });
    }

    public void setData(String data)
    {
        Platform.runLater(
            new Runnable()
            {
                @Override
                public void run()
                {
                    lbl_data.setText(data);
                }
            }
        );
    }

    public void cancel()
    {
        if(worker!=null)
            worker.cancel(true);
    }

    public void btnClose()
    {
        cancel();
        stage.close();
    }

    @FXML
    protected void initialize()
    {
        progressbar.setProgress(0);
        lbl_data.setText("Data");
    }
}
