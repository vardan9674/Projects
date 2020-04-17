package zillowbot.Forms;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import zillowbot.*;
import zillowbot.Bot.Analyzer.CardAnalyzer;
import zillowbot.Bot.Analyzer.CardAnalyzerFactory;
import zillowbot.Bot.Analyzer.DetailAnalyzer;
import zillowbot.Bot.Analyzer.SaleDetailAnalyzer;
import zillowbot.Bot.PhaseOneWorker;
import zillowbot.Bot.PhaseTwoWorker;
import zillowbot.Bot.ZillowWorker;
import zillowbot.DB.ConnectionProvider;
import zillowbot.DB.DbCommand;
import zillowbot.DB.DbWorker;
import zillowbot.DB.SearchData;
import zillowbot.DB.TableUtil;
import zillowbot.Entities.*;
import zillowbot.Proxy.ProxyProvider;
import zillowbot.Proxy.ZProxy;
import zillowbot.Util.ExtraGrids;
import zillowbot.Util.FileUtil;
import zillowbot.Util.StateList;
import zillowbot.Util.ZMessage;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.Semaphore;

public class ControllerMain {

    @FXML private ProgressBar pb_master;
    @FXML private ProgressBar pb_detail;

    @FXML private TableView<ZipCode> grid_ZipCodes;
    @FXML private TableView<Estate> grid_Estates;
    @FXML private TableView<Option> grid_ForSale;
    @FXML private TableView<Option> grid_ForRent;
    @FXML private TableView<School> grid_School;
    @FXML private TableView<Data> grid_KV1;
    @FXML private TableView<Data> grid_KV2;
    @FXML private TableView<Data> grid_KV3;
    @FXML private TableView<Data> grid_V1;
    @FXML private TableView<Data> grid_V2;
    @FXML private TableView<HashMap<String, String>> grid_general;
    @FXML private Label lbl_KV1;
    @FXML private Label lbl_KV2;
    @FXML private Label lbl_KV3;
    @FXML private Label lbl_V1;
    @FXML private Label lbl_V2;

    @FXML private TableColumn<Estate, String> col_EZipCode;
    @FXML private TableColumn<Estate, String> col_EPrimaryCity;
    @FXML private TableColumn<Estate, String> col_EStatus;

    @FXML private TableColumn<Estate, String> col_ZPID;
    @FXML private TableColumn<Estate, String> col_Type;
    @FXML private TableColumn<Estate, String> col_State;
    @FXML private TableColumn<Estate, String> col_Locality;
    @FXML private TableColumn<Estate, String> col_Address;
    @FXML private TableColumn<Estate, String> col_ZipCode;
    @FXML private TableColumn<Estate, String> col_Latitude;
    @FXML private TableColumn<Estate, String> col_Longitude;
    @FXML private TableColumn<Estate, String> col_Price;
    @FXML private TableColumn<Estate, String> col_Options;
    @FXML private TableColumn<Estate, String> col_Bedrooms;
    @FXML private TableColumn<Estate, String> col_Bathrooms;
    @FXML private TableColumn<Estate, String> col_AreaSpace_SQFT;
    @FXML private TableColumn<Estate, String> col_Status;
    @FXML private TableColumn<Estate, String> col_Description;
    @FXML private TableColumn<Estate, Integer> col_CardBadge;

    @FXML private TableColumn<Option, String> col_FR_Type;
    @FXML private TableColumn<Option, Double> col_FR_Bathrooms;
    @FXML private TableColumn<Option, Integer> col_FR_AreaSpace;
    @FXML private TableColumn<Option, Integer> col_FR_Price;

    @FXML private TableColumn<Option, String> col_FS_Type;
    @FXML private TableColumn<Option, Double> col_FS_Bathrooms;
    @FXML private TableColumn<Option, Integer> col_FS_AreaSpace;
    @FXML private TableColumn<Option, Integer> col_FS_Price;

    @FXML private TableColumn<Option, String> col_School_Name;
    @FXML private TableColumn<School, String> col_School_Grades;
    @FXML private TableColumn<School, Double> col_School_Distance;

    @FXML private TableColumn<School, String> col_KV1_Key;
    @FXML private TableColumn<School, String> col_KV1_Value;

    @FXML private TableColumn<School, String> col_KV2_Key;
    @FXML private TableColumn<School, String> col_KV2_Value;

    @FXML private TableColumn<School, String> col_KV3_Key;
    @FXML private TableColumn<School, String> col_KV3_Value;

    @FXML private TableColumn<School, String> col_V1_Value;

    @FXML private TableColumn<School, String> col_V2_Value;

    @FXML private ComboBox<State> cmb_Estate;
    @FXML private ComboBox<Request> cmb_Request;
    @FXML private RadioButton rb_State;
    @FXML private RadioButton rb_Custom;
    @FXML private RadioButton rb_PrevRequest;
    @FXML private TextField txt_customZipCodes;

    @FXML private WebView webview;
    @FXML private TextArea txtArea_Info;

    @FXML private Button btn_phaseOneStart;
    @FXML private Button btn_phaseOnePauseAndResume;
    @FXML private Button btn_phaseOneCancel;
    @FXML private Button btn_phaseTwoStart;
    @FXML private Button btn_phaseTwoPauseAndResume;
    @FXML private Button btn_phaseTwoCancel;
    @FXML private ImageView img_pauseResume;
    @FXML private ImageView img_pauseResume2;
    @FXML private ImageView img_pause;
    @FXML private ImageView img_resume;

    @FXML private HBox hbox_progressbar;
    @FXML private HBox hbox_search;

    @FXML private TextField txt_search;

    @FXML private SplitPane splitPane_data;
    @FXML private AnchorPane anchor_ForRent;
    @FXML private AnchorPane anchor_ForSale;
    @FXML private AnchorPane anchor_School;
    @FXML private AnchorPane anchor_KV1;
    @FXML private AnchorPane anchor_KV2;
    @FXML private AnchorPane anchor_KV3;
    @FXML private AnchorPane anchor_V1;
    @FXML private AnchorPane anchor_V2;

    @FXML private TextArea txt_Query;

    private StateList stateList;
    private ObservableList<Estate> estates = FXCollections.observableArrayList();
    private PhaseOneWorker phaseOneWorker = null;
    private PhaseTwoWorker phaseTwoWorker = null;

    private void PrepareGrids()
    {
        col_EZipCode.setCellValueFactory(new PropertyValueFactory<>("ZipCode"));
        col_EPrimaryCity.setCellValueFactory(new PropertyValueFactory<>("PrimaryCity"));
        col_EStatus.setCellValueFactory(new PropertyValueFactory<>("Status"));

        col_ZPID.setCellValueFactory(new PropertyValueFactory<>("ZPID"));
        col_Type.setCellValueFactory(new PropertyValueFactory<>("Type"));
        col_State.setCellValueFactory(new PropertyValueFactory<>("State"));
        col_Locality.setCellValueFactory(new PropertyValueFactory<>("Locality"));
        col_Address.setCellValueFactory(new PropertyValueFactory<>("Address"));
        col_ZipCode.setCellValueFactory(new PropertyValueFactory<>("ZipCode"));
        col_Latitude.setCellValueFactory(new PropertyValueFactory<>("Latitude"));
        col_Longitude.setCellValueFactory(new PropertyValueFactory<>("Longitude"));
        col_Price.setCellValueFactory(new PropertyValueFactory<>("Price"));
        col_Options.setCellValueFactory(new PropertyValueFactory<>("Options"));
        col_Bedrooms.setCellValueFactory(new PropertyValueFactory<>("Bedrooms"));
        col_Bathrooms.setCellValueFactory(new PropertyValueFactory<>("Bathrooms"));
        col_AreaSpace_SQFT.setCellValueFactory(new PropertyValueFactory<>("AreaSpace_SQFT"));
        col_Status.setCellValueFactory(new PropertyValueFactory<>("Status"));
        col_Description.setCellValueFactory(new PropertyValueFactory<>("Description"));
        col_CardBadge.setCellValueFactory(new PropertyValueFactory<>("CardBadge"));

        col_FR_Type.setCellValueFactory(new PropertyValueFactory<>("Type"));
        col_FR_Bathrooms.setCellValueFactory(new PropertyValueFactory<>("Bathrooms"));
        col_FR_AreaSpace.setCellValueFactory(new PropertyValueFactory<>("AreaSpace"));
        col_FR_Price.setCellValueFactory(new PropertyValueFactory<>("Price"));

        col_FS_Type.setCellValueFactory(new PropertyValueFactory<>("Type"));
        col_FS_Bathrooms.setCellValueFactory(new PropertyValueFactory<>("Bathrooms"));
        col_FS_AreaSpace.setCellValueFactory(new PropertyValueFactory<>("AreaSpace"));
        col_FS_Price.setCellValueFactory(new PropertyValueFactory<>("Price"));

        col_School_Name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        col_School_Grades.setCellValueFactory(new PropertyValueFactory<>("Grades"));
        col_School_Distance.setCellValueFactory(new PropertyValueFactory<>("Distance"));

        col_KV1_Key.setCellValueFactory(new PropertyValueFactory<>("Key"));
        col_KV1_Value.setCellValueFactory(new PropertyValueFactory<>("Value"));

        col_KV2_Key.setCellValueFactory(new PropertyValueFactory<>("Key"));
        col_KV2_Value.setCellValueFactory(new PropertyValueFactory<>("Value"));

        col_KV3_Key.setCellValueFactory(new PropertyValueFactory<>("Key"));
        col_KV3_Value.setCellValueFactory(new PropertyValueFactory<>("Value"));

        col_V1_Value.setCellValueFactory(new PropertyValueFactory<>("Key"));

        col_V2_Value.setCellValueFactory(new PropertyValueFactory<>("Key"));

        grid_ZipCodes.setRowFactory(param -> new TableRow<>());

        grid_Estates.setRowFactory(param -> {
            TableRow<Estate> estate= new TableRow<>();
            estate.setOnMouseClicked((javafx.scene.input.MouseEvent event) -> {
                if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY) && (!estate.isEmpty())) {
                    Estate est;
                    est = grid_Estates.getSelectionModel().getSelectedItem();
                    if (est != null) {
                        ControllerEstate controllerEstate = showEstate();
                        controllerEstate.setEstate(est);
                    }
                }
            });//*/
            return estate;
        });

        grid_Estates.getSelectionModel().selectedItemProperty().addListener((obs,oldSelection,newSelection)->{
            if(newSelection!=null){
                assignDataToDetailGrids(newSelection);
            }
        });//*/

        grid_School.setRowFactory(param -> new TableRow<>());

        grid_ForRent.setRowFactory(param -> new TableRow<>());

        grid_ForSale.setRowFactory(param -> new TableRow<>());

        grid_KV1.setRowFactory(param -> new TableRow<>());

        grid_KV2.setRowFactory(param -> new TableRow<>());

        grid_KV3.setRowFactory(param -> new TableRow<>());

        grid_V1.setRowFactory(param -> new TableRow<>());

        grid_V2.setRowFactory(param -> new TableRow<>());
    }

    private void assignDataToDetailGrids(Estate estate) {
        ObservableList<javafx.scene.Node> nodes = splitPane_data.getItems();
        while(nodes.size()>0)
            nodes.remove(0);
        if (estate.RentOptions.size() > 0) {
            grid_ForRent.setItems(estate.RentOptions);
            nodes.add(anchor_ForRent);
        }
        if(estate.SaleOptions.size()>0){
            grid_ForSale.setItems(estate.SaleOptions);
            nodes.add(anchor_ForSale);
        }
        if (estate.Schools.size() > 0) {
            grid_School.setItems(estate.Schools);
            nodes.add(anchor_School);
        }
        if (estate.KV1.size() > 0) {
            grid_KV1.setItems(estate.KV1);
            nodes.add(anchor_KV1);
            lbl_KV1.setText(estate.KV1_Title);
        }
        if (estate.KV2.size() > 0) {
            grid_KV2.setItems(estate.KV2);
            nodes.add(anchor_KV2);
            lbl_KV2.setText(estate.KV2_Title);
        }
        if (estate.KV3.size() > 0) {
            grid_KV3.setItems(estate.KV3);
            nodes.add(anchor_KV3);
            lbl_KV3.setText(estate.KV3_Title);
        }
        if (estate.V1.size() > 0) {
            grid_V1.setItems(estate.V1);
            nodes.add(anchor_V1);
            lbl_V1.setText(estate.V1_Title);
        }
        if (estate.V2.size() > 0) {
            grid_V2.setItems(estate.V1);
            nodes.add(anchor_V2);
            lbl_V2.setText(estate.V2_Title);
        }
        splitPane_data.setVisible(nodes.size()>0);
        if (splitPane_data.isVisible()) {
            ExtraGrids.refreshSplitPaneDividerPosition(splitPane_data);
        }
    }

    private void FillEstatesInComboBox()
    {
        cmb_Estate.setItems(stateList.States);
        cmb_Estate.setConverter(new StringConverter<State>() {
            @Override
            public String toString(State object) {
                return object.getName()+" - "+object.getAbbreviation() ;
            }

            @Override
            public State fromString(String string) {
                if((string==null) || Objects.equals(string,""))
                    return null;
                String[] s = string.split("-");
                String abbreviation = s[1].substring(1, s[1].length());
                return stateList.findStateByAbbreviation(abbreviation);
            }//*/
        });
    }

    private void test1() throws Exception{
        File input = new File("Data/z__20181212_141109.html");
        Document document= Jsoup.parse(input,"UTF-8");
        Element photoCards = document.body().getElementsByClass("photo-cards").first();
        webview.getEngine().loadContent(document.html());
        if (photoCards != null) {
            List<Node> cards = photoCards.childNodes();
            int i = 0;
            for (Node node : cards) {
                i++;
                try
                {
                    String outerHtml = node.outerHtml();
                    if(!Objects.equals(outerHtml,""))
                    {
                        CardAnalyzer cardAnalyzer = CardAnalyzerFactory.CreateCardAnalyzer(outerHtml);
                        if(cardAnalyzer != null)
                        {
                            Estate estate = cardAnalyzer.Analyze();
                            if (estate != null) {
                                estate.setZipCode(22201);
                                this.estates.add(estate);
                                grid_Estates.setItems(this.estates);
                            }
                        }
                    }
                }catch(Exception ex){
                    System.out.println(ex.getMessage());
                    System.out.println(i);
                }
            }
        }
    }
    private void test0() throws Exception {
        //ZMessage.Error("error");
        //ZFile.SaveText("Data/ztest.txt","hello",true);
        //String url = "https://www.oracle.com/index.html";
        //Platform.runLater(() -> webview.getEngine().load(url));
        try {
            ZProxy proxy = ProxyProvider.newProxy(Global.getProxyProvider());
            if (proxy != null) {
                System.out.println(proxy.Ip + ":" + proxy.Port);
                System.setProperty("http.proxyHost", proxy.Ip);
                System.setProperty("http.proxyPort", proxy.Port.toString());
                webview.getEngine().load("http://www.ip-adress.eu");
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    private void test2() throws Exception {
        Estate estate = (Estate)grid_Estates.getSelectionModel().getSelectedItem();
        Data d = new Data("xxx","aa","bbb","c","d");
        if (estate != null) {
            estate.SaleOptions.add(new Option("sale","1 bedrooms",1,1.0,800,365000,""));
            estate.RentOptions.add(new Option("sale","1 bedrooms",1,1.0,800,365000,""));

            estate.KV1.add(d);
            estate.KV2.add(d);
            estate.KV3.add(d);
            estate.V1.add(d);
            estate.V2.add(d);
            estate.Schools.add(new School("aaaaa","45-45",10.5));
        }
    }
    private void test3() throws Exception {
        Estate estate = new Estate("123","",",","","",0.0,0.0,0,0,0.0,0,"","","");
        estate.setZipCode(12345);

        File input = new File("Data/Z8.html");
        Document document= Jsoup.parse(input,"UTF-8");
        DetailAnalyzer.Analyze(document,estate);
        estates.clear();
        estates.add(estate);
        grid_Estates.setItems(estates);
    }
    private void test4(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FrmEstate.fxml"));
            Parent form = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Estate");
            stage.setScene(new Scene(form));
            stage.show();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    private void test5(){
        try
        {
            Connection conn = ConnectionProvider.connect();
            String sql="select [id] as 'test id',* from [Request]";
            Statement statement=conn.createStatement();
            ResultSet rs=statement.executeQuery(sql);
            TableUtil.populate(grid_general,rs);
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void test6() throws Exception{
        File input = new File("Data/Z1.html");
        Document document= Jsoup.parse(input,"UTF-8");
        Element facts = document.body().getElementsByClass("home-facts-at-a-glance-section").first();
        webview.getEngine().loadContent(document.html());
        if (facts != null) {
            List<Node> cards = facts.childNodes();
            int i = 0;
            for (Node node : cards) {
                Element e= (Element)node;
                Element e2=e.getElementsByClass("fact-value").first();
                System.out.println(e2.text());
            }
        }
    }
    private void test7() throws Exception{
        File input = new File("Data/Z1.html");
        Document document= Jsoup.parse(input,"UTF-8");
        Fact fact = SaleDetailAnalyzer.getFacts(document);
        System.out.println(fact);
    }

    private ControllerEstate showEstate() {
        ControllerEstate result = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FrmEstate.fxml"));
            Parent form = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Estate");
            stage.setScene(new Scene(form,1000,500));
            result = fxmlLoader.getController();
            stage.show();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return result;
    }

    public void test() throws Exception {
        test7();
    }

    public void selectQuery() {
        try
        {
            Connection conn = ConnectionProvider.connect();
            String sql=txt_Query.getText();
            Statement statement=conn.createStatement();
            ResultSet rs=statement.executeQuery(sql);
            TableUtil.populate(grid_general,rs);
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
            ZMessage.Error(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            ZMessage.Error(ex.getMessage());
        }
    }

    public void saveTableViewInCsv() {
        if (grid_general.getColumns().size() == 0) {
            ZMessage.Error("No data to save!");
            return;
        }
        List<String> columnNames = new ArrayList<>();
        for (TableColumn column : grid_general.getColumns()) {
            columnNames.add(column.getText());
        }

        FileChooser fileChooser=new FileChooser();
        FileChooser.ExtensionFilter csvExtensionFilter =
                new FileChooser.ExtensionFilter(
                        "CSV Format (.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(csvExtensionFilter);
        File file=fileChooser.showSaveDialog(null);
        if(file!=null)
        {
            try
            {
                FileWriter writer=new FileWriter(file.getPath(),false);
                BufferedWriter bw=new BufferedWriter(writer);
                PrintWriter pw=new PrintWriter(bw);
                char csvSeparator =';';
                int i = 0;
                for(String columnName:columnNames) {
                    if (i != 0)
                        pw.print(csvSeparator);
                    pw.print(columnName);
                    i++;
                }
                pw.println();
                ObservableList<HashMap<String,String>> items = grid_general.getItems();
                for (HashMap<String,String> item:items) {
                    i = 0;
                    for(String columnName:columnNames) {
                        if(i!=0)
                            pw.print(csvSeparator);
                        String str = item.get(columnName).replaceAll(";"," ");
                        pw.print(str);
                        i++;
                    }
                    pw.println();
                }
                pw.close();
                ZMessage.ShowMessage(Alert.AlertType.INFORMATION,"Info","Saved!");
            }catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void loadRequests()
    {
        DbWorker dbWorker = new DbWorker(DbCommand.GetAllRequests,null,false);
        dbWorker.addListener(() -> {
            ObservableList<Request>requests = (ObservableList<Request>)dbWorker.getOutput();
            Platform.runLater(
                    new Runnable() {
                        @Override
                        public void run() {
                            cmb_Request.setItems(requests);
                        }
                    }
            );
        });
        dbWorker.execute();
    }

    private void manageRequestComboBox()
    {
        cmb_Request.setConverter(new StringConverter<Request>() {
            @Override
            public String toString(Request object) {
                return object.Id+"-"+object.Type+" - "+object.RequestTime+" - "+object.UpdateTime ;
            }

            @Override
            public Request fromString(String string) {
                if((string==null) || Objects.equals(string,""))
                    return null;
                String s[]=string.split("-");
                int id = Integer.parseInt(s[0]);
                Request result = null;
                for (Object obj:cmb_Request.getItems())
                {
                    Request request = (Request)obj;
                    if(id == request.Id)
                    {
                        result = request;
                        break;
                    }
                }
                return result;
            }//*/
        });
    }

    private ObservableList<ZipCode> getCustomZipCodes()
    {
        ObservableList<ZipCode> customZipCodes = FXCollections.observableArrayList();
        customZipCodes.clear();
        boolean okZipCode = false;
        String[] zipCodes = txt_customZipCodes.getText().split(",");
        try
        {
            for (String z:zipCodes)
            {
                Integer.parseInt(z);
            }
            okZipCode = true;
        }
        catch(Exception ex)
        {
            String errorInfo = "Error : ZipCodes are not valid";
            System.out.println(errorInfo);
            ZMessage.Error(errorInfo);
        }
        if(okZipCode)
        {
            ZipCode zipCode;
            for (String z:zipCodes)
            {
                zipCode = new ZipCode(Integer.parseInt(z));
                customZipCodes.add(zipCode);

            }
            ArrayList<String> list = FileUtil.readFileAsList("Data/ZipCodes.csv");
            for (String item : list) {
                String[] s = item.split(",");
                Integer z = Integer.parseInt(s[0]);
                zipCode = null;
                for (ZipCode zc:customZipCodes)
                {
                    if(zc.getZipCode() == z)
                    {
                        zipCode = zc;
                        break;
                    }
                }
                if (zipCode != null) {
                    zipCode.setType(s[1]);
                    zipCode.setPrimaryCity(s[3]);
                }
            }
        }
        return customZipCodes;
    }

    private void getRequestZipCodes(Request request,Semaphore semaphore)
    {
        DbWorker dbWorker = new DbWorker(DbCommand.GetRequestZipCodes,request,false);
        dbWorker.addListener(() -> semaphore.release());
        dbWorker.execute();
    }

    private void saveRequest(Request request)
    {
        DbWorker dbWorker = new DbWorker(DbCommand.SaveRequest,request,false);
        ControllerProgress ctrlProgress = Global.getControllerProgress();
        ctrlProgress.setWorker(dbWorker);
        dbWorker.addListener(() -> {
            if(!rb_PrevRequest.isSelected())
                loadRequests();
        });
        dbWorker.execute();
    }

    public void phaseOneStart() {
        ObservableList<ZipCode> zipCodes = null;
        Request request = null;
        if (rb_State.isSelected()) {
            State state = cmb_Estate.getValue();
            if (state == null) {
                String errorInfo = "No State selected. Please select one!";
                System.out.println(errorInfo);
                ZMessage.Error(errorInfo);
                return;
            }
            if (state.ZipCodes.size() == 0)
                state.loadZipCodes();
            zipCodes = state.ZipCodes;
            if (zipCodes.size() != 0) {
                request = new Request();
                request.ZipCodes = zipCodes;
                request.Type = "State-" + state.getAbbreviation();
            }
        } else if (rb_Custom.isSelected()) {
            zipCodes = getCustomZipCodes();
            if (zipCodes.size() != 0) {
                request = new Request();
                request.ZipCodes = zipCodes;
                request.Type = "Custom-" + zipCodes.get(0).getZipCode();
            }
        } else if (rb_PrevRequest.isSelected()) {
            request = cmb_Request.getValue();
            if (request == null) {
                String errorInfo = "Please select a request.";
                System.out.println(errorInfo);
                ZMessage.Error(errorInfo);
                return;
            }
            if ((request.ZipCodes == null) || (request.ZipCodes.size() == 0)) {
                Semaphore semaphore = new Semaphore(0);
                getRequestZipCodes(request, semaphore);
                try {
                    semaphore.acquire();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            zipCodes = request.ZipCodes;
        }

        if ((zipCodes == null) || (zipCodes.size() == 0))
            return;

        grid_ZipCodes.setItems(zipCodes);

        saveRequest(request);

        estates.clear();
        grid_Estates.setItems(estates);

        if (phaseOneWorker != null) {
            if (phaseOneWorker.isPaused()) {
                phaseOneWorker.resume();
                phaseOneWorker.cancel(false);
                phaseOneWorker = null;
            }
        }

        try {
            pb_master.setProgress(0);
            pb_detail.setProgress(0);
            WebEngine webEngine = webview.getEngine();
            phaseOneWorker = new PhaseOneWorker(zipCodes, pb_master, pb_detail, webEngine,
                    txtArea_Info, estates, grid_ZipCodes, grid_Estates);
            phaseOneWorker.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    switch (evt.getPropertyName()) {
                        case "progress": {

                        }
                        break;
                        case "state": {
                            switch ((SwingWorker.StateValue) evt.getNewValue()) {
                                case DONE: {
                                    btn_phaseOneCancel.setVisible(false);
                                    btn_phaseOnePauseAndResume.setVisible(false);
                                    btn_phaseOnePauseAndResume.setText("Pause");
                                    img_pauseResume.setImage(img_pause.getImage());
                                    btn_phaseOneStart.setVisible(true);
                                    btn_phaseTwoStart.setVisible(true);
                                    hbox_progressbar.setVisible(false);
                                    hbox_search.setVisible(true);
                                }
                                break;
                                case STARTED: {
                                    btn_phaseOneStart.setVisible(false);
                                    btn_phaseTwoStart.setVisible(false);
                                    btn_phaseOneCancel.setVisible(true);
                                    btn_phaseOnePauseAndResume.setVisible(true);
                                    btn_phaseOnePauseAndResume.setText("Pause");
                                    img_pauseResume.setImage(img_pause.getImage());
                                    hbox_progressbar.setVisible(true);
                                    hbox_search.setVisible(false);
                                }
                                break;
                                case PENDING: {
                                    btn_phaseOnePauseAndResume.setText("Resume");
                                    img_pauseResume.setImage(img_resume.getImage());
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            });
            phaseOneWorker.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }//*/
    }

    private void pauseAndResume(ZillowWorker worker, Button btn_PauseAndResume, ImageView img_pauseResume) {
        if (worker == null) return;
        if (worker.isPaused()) {
            worker.resume();
            img_pauseResume.setImage(img_pause.getImage());
            btn_PauseAndResume.setText("Pause");
        } else {
            worker.pause();
            img_pauseResume.setImage(img_resume.getImage());
            btn_PauseAndResume.setText("Resume");
        }
    }

    public void phaseOnePauseAndResume() {
        pauseAndResume(phaseOneWorker,btn_phaseOnePauseAndResume,img_pauseResume);
    }

    public void phaseTwoPauseAndResume(){
        pauseAndResume(phaseTwoWorker,btn_phaseTwoPauseAndResume,img_pauseResume2);
    }

    private boolean cancelPhase(ZillowWorker worker, Button btn_Start, Button btn_PauseAndResume, Button btn_Cancel,
                             ImageView img_pauseResume,Button btn_otherStart) {
        if (worker == null) return false;
        String type = "";
        if (worker instanceof PhaseOneWorker) {

            type = " one";
        } else {
            type = " two";
        }
        if (ZMessage.Confirm("Cancel phase" + type + "?")) {
            worker.cancel(true);
            phaseOneWorker = null;
            phaseTwoWorker = null;
            btn_PauseAndResume.setVisible(false);
            btn_Cancel.setVisible(false);
            img_pauseResume.setImage(img_pause.getImage());
            btn_PauseAndResume.setText("Pause");
            btn_Start.setVisible(true);
            btn_otherStart.setVisible(true);
            hbox_search.setVisible(true);
            return true;
        }
        return false;
    }

    public void phaseOneCancel(){
        if(cancelPhase(phaseOneWorker,btn_phaseOneStart,btn_phaseOnePauseAndResume,btn_phaseOneCancel,img_pauseResume,
                btn_phaseTwoStart)) phaseOneWorker = null;
    }

    public void phaseTwoCancel() {
        if(cancelPhase(phaseTwoWorker,btn_phaseTwoStart,btn_phaseTwoPauseAndResume,btn_phaseTwoCancel,img_pauseResume2,
                btn_phaseOneStart)) phaseTwoWorker = null;
    }

    private void getRequestEstates(int requestId,Semaphore semaphore) {
        estates.clear();
        SearchData searchData = new SearchData();
        String id = requestId + "";
        searchData.Data = id.split(" ");
        searchData.Estates = estates;
        DbWorker dbWorker = new DbWorker(DbCommand.GetRequestEstates, searchData, false);
        ControllerProgress ctrlProgress = Global.getControllerProgress();
        ctrlProgress.setWorker(dbWorker);
        dbWorker.addListener(() -> {
            semaphore.release();
            grid_Estates.setItems(estates);
        });
        dbWorker.execute();
    }

    public void phaseTwoStart(){
        Request request = cmb_Request.getValue();
        if (rb_PrevRequest.isSelected() && request != null) {
            Semaphore semaphore = new Semaphore(0);
            getRequestEstates(request.Id, semaphore);
            try {
                semaphore.acquire();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (estates.size() == 0) {
            ZMessage.Error("No data selected.");
            return;
        }
        try
        {
            pb_master.setProgress(0);
            pb_detail.setProgress(0);
            WebEngine webEngine = webview.getEngine();
            phaseTwoWorker = new PhaseTwoWorker(pb_master, pb_detail, webEngine,
                    txtArea_Info, estates, grid_Estates);
            phaseTwoWorker.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    switch(evt.getPropertyName()){
                        case "progress":{

                        }break;
                        case "state": {
                            switch ((SwingWorker.StateValue) evt.getNewValue()) {
                                case DONE: {
                                    btn_phaseTwoCancel.setVisible(false);
                                    btn_phaseTwoPauseAndResume.setVisible(false);
                                    btn_phaseTwoPauseAndResume.setText("Pause");
                                    img_pauseResume2.setImage(img_pause.getImage());
                                    btn_phaseOneStart.setVisible(true);
                                    btn_phaseTwoStart.setVisible(true);
                                    hbox_progressbar.setVisible(false);
                                    hbox_search.setVisible(true);
                                }break;
                                case STARTED:
                                {
                                    btn_phaseOneStart.setVisible(false);
                                    btn_phaseTwoStart.setVisible(false);
                                    btn_phaseTwoCancel.setVisible(true);
                                    btn_phaseTwoPauseAndResume.setVisible(true);
                                    btn_phaseTwoPauseAndResume.setText("Pause");
                                    img_pauseResume2.setImage(img_pause.getImage());
                                    hbox_progressbar.setVisible(true);
                                    hbox_search.setVisible(false);
                                }break;
                                case PENDING:
                                {
                                    btn_phaseTwoPauseAndResume.setText("Resume");
                                    img_pauseResume2.setImage(img_resume.getImage());
                                }break;
                            }
                        }break;
                    }
                }
            });
            phaseTwoWorker.execute();
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }//*/
    }

    public void search() {
        estates.clear();
        SearchData searchData = new SearchData();
        searchData.Data = txt_search.getText().split(" ");
        searchData.Estates = estates;
        DbWorker dbWorker = new DbWorker(DbCommand.Search,searchData,false);
        ControllerProgress ctrlProgress = Global.getControllerProgress();
        ctrlProgress.setWorker(dbWorker);
        dbWorker.addListener(() -> {
            grid_Estates.setItems(estates);
        });
        dbWorker.execute();
    }

    public void showCalcSetting() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FrmCalcSetting.fxml"));
            Parent form = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Calculator Setting");
            stage.resizableProperty().setValue(Boolean.FALSE);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("calculatorSetting.png")));
            stage.setScene(new Scene(form));
            stage.show();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showCalculator() {
        Estate estate = null;
        if (grid_Estates.getItems().size() > 0) {
            estate = grid_Estates.getSelectionModel().getSelectedItem();
        } else {
            /*ZMessage.Error("No data available in the table.");
            return;*/
        }
        if (estate == null) {
            /*ZMessage.Error("No row selected. Select one row please.");
            return;*/
        }
        ControllerCalculator controller = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FrmCalculator.fxml"));
            Parent form = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("ROI & CashFlow Calculator");
            stage.resizableProperty().setValue(Boolean.FALSE);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("calculator.png")));
            stage.setScene(new Scene(form));
            controller = fxmlLoader.getController();
            stage.show();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        if (controller != null) {
            controller.setEstate(estate);
        }
    }

    private void loadSql() {
        try {
            File file = new File(Global.BasePath + "sql.txt");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            String str = new String(data, "UTF-8");
            txt_Query.setText(str);
        }catch(Exception ex){}
    }

    public void runCalculation() {
        SearchData searchData = new SearchData();
        searchData.Estates = estates;
        DbWorker dbWorker = new DbWorker(DbCommand.RunCalculation,searchData,false);
        ControllerProgress ctrlProgress = Global.getControllerProgress();
        ctrlProgress.setWorker(dbWorker);
        dbWorker.addListener(() -> {
            grid_Estates.setItems(estates);
        });
        dbWorker.execute();
    }

    @FXML
    protected void initialize()
    {
        loadSql();
        stateList = new StateList();
        stateList.loadZipAbbreviation();
        FillEstatesInComboBox();
        manageRequestComboBox();
        loadRequests();
        PrepareGrids();
        grid_Estates.setItems(estates);
        btn_phaseOneStart.managedProperty().bind(btn_phaseOneStart.visibleProperty());
        btn_phaseOnePauseAndResume.managedProperty().bind(btn_phaseOnePauseAndResume.visibleProperty());
        btn_phaseOneCancel.managedProperty().bind(btn_phaseOneCancel.visibleProperty());
        btn_phaseTwoStart.managedProperty().bind(btn_phaseTwoStart.visibleProperty());
        btn_phaseTwoPauseAndResume.managedProperty().bind(btn_phaseTwoPauseAndResume.visibleProperty());
        btn_phaseTwoCancel.managedProperty().bind(btn_phaseTwoCancel.visibleProperty());
        hbox_progressbar.managedProperty().bind(hbox_progressbar.visibleProperty());
        hbox_search.managedProperty().bind(hbox_search.visibleProperty());
        splitPane_data.managedProperty().bind(splitPane_data.visibleProperty());
        splitPane_data.setVisible(false);
    }
}
