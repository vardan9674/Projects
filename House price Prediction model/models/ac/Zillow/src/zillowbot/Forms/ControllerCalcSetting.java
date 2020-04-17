package zillowbot.Forms;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.ini4j.Wini;
import zillowbot.Global;
import zillowbot.Util.ZMessage;

import java.io.File;
import java.util.Objects;

public class ControllerCalcSetting {
    @FXML private Button btn_Cancel;
    @FXML private TextField txt_DownPaymentPercent;
    @FXML private TextField txt_PriceGrowthPercent;
    @FXML private TextField txt_RentToPrice;
    @FXML private TextField txt_RentGrowthPercent;
    @FXML private TextField txt_InterestRate;
    @FXML private TextField txt_Tax;
    @FXML private TextField txt_Insurance;
    @FXML private TextField txt_Maintenance;
    @FXML private TextField txt_VacancyRate;
    @FXML private ComboBox<Integer> cmb_Years;

    public void close() {
        Stage stage = (Stage) btn_Cancel.getScene().getWindow();
        stage.close();
    }

    private void loadData()
    {
        txt_DownPaymentPercent.setText(Global.getDownPaymentPercent().toString());
        txt_PriceGrowthPercent.setText(Global.getPriceGrowthPercent().toString());
        txt_RentToPrice.setText(Global.getRentToPrice().toString());
        txt_RentGrowthPercent.setText(Global.getRentGrowthPercent().toString());
        txt_InterestRate.setText(Global.getInterestRate().toString());
        txt_Tax.setText(Global.getTax().toString());
        txt_Insurance.setText(Global.getInsurance().toString());
        txt_Maintenance.setText(Global.getMaintenance().toString());
        txt_VacancyRate.setText(Global.getVacancyRate().toString());
        if (Global.getYears() == null) {
            cmb_Years.setValue(30);
        } else {
            cmb_Years.setValue(Global.getYears());
        }
    }

    private boolean check() {
        return
                Objects.equals(txt_DownPaymentPercent.getStyle(),"") &&
                        Objects.equals(txt_PriceGrowthPercent.getStyle(),"") &&
                        Objects.equals(txt_RentGrowthPercent.getStyle(),"") &&
                        Objects.equals(txt_RentToPrice.getStyle(),"") &&
                        Objects.equals(txt_Insurance.getStyle(),"") &&
                        Objects.equals(txt_Tax.getStyle(),"") &&
                        Objects.equals(txt_Maintenance.getStyle(),"") &&
                        Objects.equals(txt_VacancyRate.getStyle(),"") &&
                        Objects.equals(txt_InterestRate.getStyle(),"");
    }

    public void save() {
        if (!check()) {
            ZMessage.Error("Invalid data");
            return;
        }
        Global.setDownPaymentPercent(Double.parseDouble(txt_DownPaymentPercent.getText()));
        Global.setPriceGrowthPercent(Double.parseDouble(txt_PriceGrowthPercent.getText()));
        Global.setRentToPrice(Double.parseDouble(txt_RentToPrice.getText()));
        Global.setRentGrowthPercent(Double.parseDouble(txt_RentGrowthPercent.getText()));
        Global.setInterestRate(Double.parseDouble(txt_InterestRate.getText()));
        Global.setTax(Double.parseDouble(txt_Tax.getText()));
        Global.setInsurance(Double.parseDouble(txt_Insurance.getText()));
        Global.setMaintenance(Double.parseDouble(txt_Maintenance.getText()));
        Global.setVacancyRate(Double.parseDouble(txt_VacancyRate.getText()));
        Global.setYears(cmb_Years.getValue());
        Global.saveCalcSetting();
        ZMessage.Information("Saved!");
        close();
    }

    private class DoubleFieldListener implements ChangeListener<String> {
        private final TextField textField ;
        DoubleFieldListener(TextField textField) {
            this.textField = textField ;
        }
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            TextField txt = (TextField) ((StringProperty)observable).getBean();
            try {
                Double.parseDouble(newValue);
                txt.setStyle("");
            } catch (Exception ex) {
                txt.setStyle("-fx-background-color: yellow; -fx-border-color: crimson;");
            }
        }
    }

    @FXML
    protected void initialize() {
        loadData();
        txt_DownPaymentPercent.textProperty().addListener(new DoubleFieldListener(txt_DownPaymentPercent));
        txt_PriceGrowthPercent.textProperty().addListener(new DoubleFieldListener(txt_PriceGrowthPercent));
        txt_RentToPrice.textProperty().addListener(new DoubleFieldListener(txt_RentToPrice));
        txt_RentGrowthPercent.textProperty().addListener(new DoubleFieldListener(txt_RentGrowthPercent));
        txt_InterestRate.textProperty().addListener(new DoubleFieldListener(txt_InterestRate));
        txt_Tax.textProperty().addListener(new DoubleFieldListener(txt_Tax));
        txt_Insurance.textProperty().addListener(new DoubleFieldListener(txt_Insurance));
        txt_Maintenance.textProperty().addListener(new DoubleFieldListener(txt_Maintenance));
        txt_VacancyRate.textProperty().addListener(new DoubleFieldListener(txt_VacancyRate));
    }
}
