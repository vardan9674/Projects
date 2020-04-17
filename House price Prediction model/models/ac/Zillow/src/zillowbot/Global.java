package zillowbot;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.ini4j.Wini;
import zillowbot.Forms.ControllerProgress;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class Global {
    public static String BasePath = "Data/";
    public static String DbPath = BasePath + "ZillowDB.db";
    public static Stage PrimaryStage;
    public static ControllerProgress getControllerProgress() {
        ControllerProgress ctrlProgress = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Forms/FrmProgress.fxml"));
            Parent form = null;
            form = fxmlLoader.load();
            ctrlProgress = fxmlLoader.getController();
            Stage frmProgress = new Stage();
            frmProgress.initModality(Modality.APPLICATION_MODAL);
            frmProgress.initStyle(StageStyle.UTILITY);
            frmProgress.setTitle("Progress");
            frmProgress.setScene(new Scene(form));
            ctrlProgress.setStage(frmProgress);
            frmProgress.show();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return ctrlProgress;
    }
    public static Connection getConnection()
    {
        String url="jdbc:sqlite:"+DbPath;
        Connection conn=null;
        try
        {
            conn = DriverManager.getConnection(url);
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        return conn;
    }

    private static String proxyProvider = "";

    public static String getNewProxyProvider()
    {
        String result = "";
        String path=Global.BasePath+"proxy.ini";
        File f = new File(path);
        if(f.exists()) {
            try {
                Wini ini = new Wini(f);
                result = ini.get("Proxy", "ProxyProvider", String.class);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        return result;
    }

    public static String getProxyProvider()
    {
        if(!Objects.equals(proxyProvider,""))
            return proxyProvider;
        proxyProvider = getNewProxyProvider();
        return proxyProvider;
    }

    private static Double downPaymentPercent = null;
    private static Double priceGrowthPercent = null;
    private static Double rentToPrice = null;
    private static Double rentGrowthPercent = null;
    private static Double interestRate = null;
    private static Double tax = null;
    private static Double insurance = null;
    private static Double maintenance = null;
    private static Double vacancyRate = null;
    private static Integer years = null;

    public static void setDownPaymentPercent(Double value){ downPaymentPercent = value; }
    public static void setPriceGrowthPercent(Double value){ priceGrowthPercent = value; }
    public static void setRentToPrice(Double value){ rentToPrice = value; }
    public static void setRentGrowthPercent(Double value){ rentGrowthPercent = value; }
    public static void setInterestRate(Double value){ interestRate = value; }
    public static void setTax(Double value){ tax = value; }
    public static void setInsurance(Double value){ insurance = value; }
    public static void setMaintenance(Double value){ maintenance = value; }
    public static void setVacancyRate(Double value){ vacancyRate = value; }
    public static void setYears(Integer value){ years = value; }

    public static Double getDownPaymentPercent() {
        if (downPaymentPercent == null)
            loadCalcSetting();
        return downPaymentPercent;
    }
    public static Double getPriceGrowthPercent() {
        if (priceGrowthPercent == null)
            loadCalcSetting();
        return priceGrowthPercent;
    }
    public static Double getRentToPrice() {
        if (rentToPrice == null)
            loadCalcSetting();
        return rentToPrice;
    }
    public static Double getRentGrowthPercent() {
        if (rentGrowthPercent == null)
            loadCalcSetting();
        return rentGrowthPercent;
    }
    public static Double getInterestRate() {
        if (interestRate == null)
            loadCalcSetting();
        return interestRate;
    }
    public static Double getTax() {
        if (tax == null)
            loadCalcSetting();
        return tax;
    }
    public static Double getInsurance() {
        if (insurance == null)
            loadCalcSetting();
        return insurance;
    }
    public static Double getMaintenance() {
        if (maintenance == null)
            loadCalcSetting();
        return maintenance;
    }
    public static Double getVacancyRate() {
        if (vacancyRate == null)
            loadCalcSetting();
        return vacancyRate;
    }
    public static Integer getYears() {
        if (years == null)
            loadCalcSetting();
        return years;
    }

    private static void loadCalcSetting()
    {
        String path = BasePath + "CalcSetting.ini";
        File f = new File(path);
        if(f.exists()) {
            try {
                Wini ini = new Wini(f);
                downPaymentPercent = ini.get("setting", "downPaymentPercent", Double.class);
                priceGrowthPercent = ini.get("setting", "priceGrowthPercent", Double.class);
                rentToPrice = ini.get("setting", "rentToPrice", Double.class);
                rentGrowthPercent = ini.get("setting", "rentGrowthPercent", Double.class);
                interestRate = ini.get("setting", "interestRate", Double.class);
                tax = ini.get("setting", "tax", Double.class);
                insurance = ini.get("setting", "insurance", Double.class);
                maintenance = ini.get("setting", "maintenance", Double.class);
                vacancyRate = ini.get("setting", "vacancyRate", Double.class);
                years = ini.get("setting", "years", Integer.class);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        else
        {
            downPaymentPercent = 20.0;
            priceGrowthPercent = 1.0;
            rentToPrice = 0.00835;
            rentGrowthPercent = 1.0;
            interestRate = 4.5;
            tax = 0.0;
            insurance = 0.0;
            maintenance = 0.0;
            vacancyRate = 0.0;
            years = 30;
            saveCalcSetting();
        }
    }

    public static void saveCalcSetting() {
        String path = Global.BasePath + "CalcSetting.ini";
        File f = new File(path);
        try{
            boolean ok = f.exists();
            if (!ok) {
                ok = f.createNewFile();
            }
            if (ok) {
                Wini ini = new Wini(f);
                ini.put("setting","count",0);
                ini.put("setting", "downPaymentPercent", downPaymentPercent);
                ini.put("setting", "priceGrowthPercent", priceGrowthPercent);
                ini.put("setting", "rentToPrice", rentToPrice);
                ini.put("setting", "rentGrowthPercent", rentGrowthPercent);
                ini.put("setting", "interestRate", interestRate);
                ini.put("setting", "tax", tax);
                ini.put("setting", "insurance", insurance);
                ini.put("setting", "maintenance", maintenance);
                ini.put("setting", "vacancyRate", vacancyRate);
                ini.put("setting", "years", years);
                ini.store();
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
