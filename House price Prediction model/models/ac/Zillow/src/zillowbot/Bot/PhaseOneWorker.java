package zillowbot.Bot;

import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import zillowbot.Bot.Analyzer.CardAnalyzer;
import zillowbot.Bot.Analyzer.CardAnalyzerFactory;
import zillowbot.Entities.Estate;
import zillowbot.Entities.ZipCode;
import zillowbot.Util.Util;
import zillowbot.Util.ZFile;

import java.util.List;
import java.util.Objects;

public class PhaseOneWorker extends ZillowWorker
{
    private ObservableList<ZipCode> zipCodes;
    private ObservableList<Estate> estates;
    private TableView gridRequest;
    private TableView gridEstate;

    public PhaseOneWorker(ObservableList<ZipCode> zipCodes,
                          ProgressBar pbMaster, ProgressBar pbDetail, WebEngine webEngine, TextArea txtAreaInfo,
                          ObservableList<Estate> estates, TableView gridRequest, TableView gridEstate) throws Exception {
        super(pbMaster,pbDetail,webEngine,txtAreaInfo);
        if (zipCodes == null)
            throw new Exception("ZipCode list is null.");
        this.zipCodes = zipCodes;
        this.estates = estates;
        this.gridRequest = gridRequest;
        this.gridEstate = gridEstate;
    }

    private String ZipCodeUrl(ZipCode zipCode) {
        return WebData.zillow +WebData.zillowHomes+ zipCode.getZipCode() + WebData.zillowTail;
    }

    private int zillowPageCount(Document document) {
        int pageCount = 0;
        try {
            Element pages = document.body().getElementsByClass("zsg-pagination").first();
            if ((pages != null) && (pages.childNodeSize() > 1)) {
                int index = pages.childNodeSize() - 2;
                Node pageNode = pages.childNodes().get(index);

                String str = pageNode.toString();
                Document doc = Jsoup.parse(str);
                pageCount = Integer.parseInt(doc.select("a").first().text());
            }
            if (pageCount == 0) {
                Element element = document.body().getElementsByClass("photo-cards").first();
                if (element != null) {
                    pageCount = 1;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return pageCount;
    }

    private boolean detectedAsBot(Document document) {
        if (document == null || document.body() == null) {
            return false;
        }
        Element element = document.body().getElementsByClass("g-recaptcha").first();
        return element != null;
    }


    private void AnalyzePage(Document document, ZipCode zipCode, int pageIndex, int pageCount) {
        if (document == null || document.body() == null || document.body().childNodeSize() == 0) {

            return;
        }
        Element photoCards = document.body().getElementsByClass("photo-cards").first();
        String html = document.html();//WebData.beginHtml+photoCards.html()+WebData.endHtml;
        loadHtml(html);
        //ZFile.SaveText("Data/z_.html", html, true);

        if (photoCards != null) {
            List<Node> cards = photoCards.childNodes();
            int estateCount = 0;
            for (Node node : cards) {
                if(isCancelled()) return;
                checkPause();
                String outerHtml = node.outerHtml();
                if(!Objects.equals(outerHtml,""))
                {
                    CardAnalyzer cardAnalyzer = CardAnalyzerFactory.CreateCardAnalyzer(outerHtml);
                    if(cardAnalyzer != null)
                    {
                        Estate estate = cardAnalyzer.Analyze();
                        if (estate != null) {
                            estateCount ++;

                            if(isCancelled()) return;
                            checkPause();
                            estate.setZipCode(zipCode.getZipCode());
                            estate.save1(zipCode.RequestZipId,pageIndex);
                            estate.setZipCode(zipCode.getZipCode());
                            this.estates.add(estate);
                        }
                    }
                }
            }
            NewInfo(estateCount+" estates grabbed.");
            Util.refreshGrid(gridEstate,-1);
        }

        if (pageIndex != pageCount) {
            zipCode.Sys = (pageCount * 1000) + (pageIndex + 1);
            zipCode.setStatus("on progress ("+(pageIndex+1)+"/"+pageCount+")");
            zipCode.update();
        } else {
            zipCode.Sys = 4;
            zipCode.setStatus("grabbed successfully");
            zipCode.update();
        }
    }

    private void doProgressOnPage(String url, int pageIndex, int pageCount, ZipCode zipCode, int zipIndex) {
        while (pageIndex <= pageCount) {
            if(isCancelled()) return;
            checkPause();
            Document doc = checkAccess(url + pageIndex + "_p/");
            if (doc == null || doc.body() == null || doc.body().childNodeSize() == 0) {
                NewInfo("access failed on page " + pageIndex + ".");
                doSleep();
            } else {
                if (detectedAsBot(doc)) {
                    NewInfo("bot detected! page : " + pageIndex + "/" + pageCount + " zip : " + zipCode.getZipCode());
                    doSleep();
                } else {
                    AnalyzePage(doc, zipCode, pageIndex, pageCount);
                    Util.refreshGrid(gridRequest, zipIndex);
                    setDetailProgress((double) pageIndex / pageCount);
                    pageIndex++;
                }
            }
        }
        zipCode.Sys = 4;//grabbed
        zipCode.setStatus("grabbed successfully");
        zipCode.update();
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        isPaused = false;
        int zipCodeCount = zipCodes.size();
        int i = 0;
        String again="";
        while(i < zipCodeCount) {
            if(isCancelled()) return true;
            checkPause();
            setDetailProgress(0);
            ZipCode zipCode = zipCodes.get(i);
            String url = ZipCodeUrl(zipCode);
            if (zipCode.RequestZipId == 0) {
                doSleep();
            } else if (zipCode.Sys == 2 || zipCode.Sys == 4) {
                i++;
            } else if (zipCode.Sys >10) {
                int pageCount = zipCode.Sys / 1000;
                int pageIndex = zipCode.Sys - (pageCount * 1000);
                doProgressOnPage(url, pageIndex, pageCount, zipCode, i);
                if(isCancelled()) return true;
            } else if (zipCode.Sys == 0 || zipCode.Sys == 1 || zipCode.Sys == 3) {
                NewInfo("ZipCode " + zipCode.getZipCode() + " is started" + again + ".");
                Document document = checkAccess(url);
                if(isCancelled()) return true;
                checkPause();
                if (document == null || document.body() == null || document.body().childNodeSize() == 0) {
                    zipCode.Sys = 1;//access to page failed
                    zipCode.setStatus("access failed");
                    zipCode.update();
                    again = " again";
                } else {
                    int pageCount;
                    pageCount = zillowPageCount(document);
                    if (pageCount == 0) {
                        if (detectedAsBot(document)) {
                            zipCode.Sys = 3;
                            zipCode.setStatus("bot detected");
                            NewInfo("bot detected!");
                            again = " again";
                            doSleep();
                        } else {
                            again = "";
                            setMasterProgress((double) (i + 1) / zipCodeCount);
                            zipCode.Sys = 2;// = 3; zipCode not
                            zipCode.setStatus("no result");
                            NewInfo(zipCode.getZipCode()+" has no result.");
                            zipCode.update();
                            Util.refreshGrid(gridRequest,i);
                            String html = document.html();//WebData.beginHtml+photoCards.html()+WebData.endHtml;
                            loadHtml(html);
                            //ZFile.SaveText("Data/z_.html", html, true);
                            i++;
                        }
                    } else {
                        again = "";
                        setMasterProgress((double) (i + 1) / zipCodeCount);
                        i++;
                        AnalyzePage(document, zipCode, 1, pageCount);
                        Util.refreshGrid(gridRequest,0);
                        setDetailProgress(1.0 / pageCount);
                        if (pageCount > 1) {
                            doProgressOnPage(url, 2, pageCount, zipCode, i);
                        }
                        if(isCancelled()) return true;
                    }
                }
            }
        }
        NewInfo("grabbing data is finished.");
        return true;
    }
}
