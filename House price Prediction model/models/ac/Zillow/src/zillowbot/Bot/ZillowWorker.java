package zillowbot.Bot;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import zillowbot.Proxy.ProxyProvider;
import zillowbot.Proxy.ZProxy;

import javax.swing.*;
import java.util.Random;

public abstract class ZillowWorker extends SwingWorker<Boolean, String> {
    private ProgressBar pbMaster;
    private ProgressBar pbDetail;
    private TextArea txtAreaInfo;
    private WebEngine webEngine;
    boolean isPaused;

    ZillowWorker(ProgressBar pbMaster, ProgressBar pbDetail, WebEngine webEngine, TextArea txtAreaInfo) {
        this.pbMaster = pbMaster;
        this.pbDetail = pbDetail;
        this.webEngine = webEngine;
        this.txtAreaInfo = txtAreaInfo;
    }

    void loadHtml(String html) {
        if (webEngine == null)
            return;
        Platform.runLater(() -> webEngine.loadContent(html));
    }

    void setDetailProgress(double value) {
        if (pbDetail == null)
            return;
        Platform.runLater(() -> pbDetail.setProgress(value));
    }

    void setMasterProgress(double value) {
        if (pbDetail == null)
            return;
        Platform.runLater(() -> pbMaster.setProgress(value));
    }

    void NewInfo(String info){
        NewInfo(info,true);
    }

    private void NewInfo(String info, boolean printOnOutput) {
        if (printOnOutput) {
            System.out.println(info);
        }
        Platform.runLater(() -> txtAreaInfo.appendText(info + "\n"));
    }

    void doSleep() {
        Random rand = new Random();
        int i = rand.nextInt(4) + 1;
        try {
            NewInfo("Sleep for " + i + " seconds.");
            Thread.sleep(i * 1000);
        } catch (Exception ex) {
            System.out.println("time error.");
        }
    }

    public final void pause() {
        if (!isPaused() && !isDone()) {
            isPaused = true;
            firePropertyChange("paused", false, true);
        }
    }

    public final void resume() {
        if (isPaused() && !isDone()) {
            isPaused = false;
            firePropertyChange("paused", true, false);
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    void checkPause() {
        while (isPaused()) {
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {
                System.out.println("time error.");
            }
        }
    }

    Document checkAccess(String url) {
        Document result;
        Random rand = new Random();
        int r = rand.nextInt(9);
        try {
            ZProxy proxy = ProxyProvider.bindNewHttpsProxy();
            NewInfo("https:"+proxy.Ip+":"+proxy.Port,false);
            NewInfo("try get url : "+url);
            result = Jsoup.connect(url).userAgent(WebData.userAgent[r]).referrer("https://www.google.com").timeout(30 * 1000).get();
            checkPause();
            NewInfo("access authorized.");
        } catch (Exception ex) {
            NewInfo("access failed!");
            doSleep();
            return null;
        }
        return result;
    }
}
