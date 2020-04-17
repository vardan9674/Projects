package zillowbot.Bot;

import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import zillowbot.Bot.Analyzer.DetailAnalyzer;
import zillowbot.Entities.Estate;
import zillowbot.Util.Util;

import java.util.Objects;

public class PhaseTwoWorker extends ZillowWorker{

    private ObservableList<Estate> estates;
    private TableView gridEstate;

    public PhaseTwoWorker(ProgressBar pbMaster, ProgressBar pbDetail, WebEngine webEngine, TextArea txtAreaInfo,
                          ObservableList<Estate> estates, TableView gridEstate){
        super(pbMaster,pbDetail,webEngine,txtAreaInfo);
        this.estates = estates;
        this.gridEstate = gridEstate;
    }

    private String DetailLink(Estate estate) {
        String link = estate.getURL_Link();
        if(Objects.equals(link,""))
            return null;
        return WebData.zillow+estate.getURL_Link();
    }

    private boolean detectedAsBot(Document document) {
        if (document == null || document.body() == null) {
            return false;
        }
        Element element = document.body().getElementsByClass("captcha-container").first();
        return element != null;
    }

    private boolean error404(Document document) {
        if (document == null || document.body() == null) {
            return false;
        }
        Element element = document.body().getElementsByClass("error-text-content").first();
        return element != null;
    }

    private void updateEstate(Estate source,Estate target) {
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        int count = estates.size();
        int i = 0;
        String again = "";
        int pass = 0;
        while (i < count) {
            if(isCancelled())return true;
            checkPause();
            Util.refreshGrid(gridEstate,i);
            Estate estate = estates.get(i);

            String url = DetailLink(estate);
            if (estate.phase2 || url == null) {
                setMasterProgress((double) (i + 1) / count);
                i++;
                continue;
            }

            NewInfo(estate.getZPID()+" started"+again+".");

            Document document = checkAccess(url);
            if(isCancelled())return true;
            checkPause();

            if (document == null || document.body() == null || document.body().childNodeSize() == 0) {
                again = " again";
            } else {
                if (detectedAsBot(document)) {
                    NewInfo("bot detected.");
                    again = " again";
                    pass++;
                    if (pass == 4) {
                        NewInfo("this state is ignored. grab data next time.");
                        pass = 0;
                        setMasterProgress((double) (i + 1) / count);
                        i++;
                    }
                } else if (error404(document)) {
                    NewInfo("page not found!");
                    estate.save2();
                    i++;
                } else {
                    setMasterProgress((double) (i + 1) / count);
                    DetailAnalyzer.Analyze(document, estate);
                    estate.save2();
                    i++;
                    again = "";
                }
                String html = document.html();
                //ZFile.SaveText("Data/z2_.html", html, true);
                loadHtml(html);
                doSleep();
                checkPause();
            }
        }
        return true;
    }
}
