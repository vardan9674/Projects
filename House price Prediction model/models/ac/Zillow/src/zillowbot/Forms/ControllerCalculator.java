package zillowbot.Forms;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import zillowbot.Entities.Estate;
import zillowbot.Global;
import zillowbot.Util.Calculator;
import zillowbot.Util.ZMessage;

import java.util.Objects;

public class ControllerCalculator {
    @FXML private TextField txt_Price;
    @FXML private TextField txt_PriceGrowthPercent;
    @FXML private TextField txt_RentToPrice;
    @FXML private TextField txt_RentGrowthPercent;
    @FXML private TextField txt_DownPayment;
    @FXML private TextField txt_InterestRate;
    @FXML private TextField txt_Tax;
    @FXML private TextField txt_Insurance;
    @FXML private TextField txt_Maintenance;
    @FXML private TextField txt_VacancyRate;
    @FXML private ComboBox<Integer> cmb_Years;
    @FXML private TextField txt_Mortgage;
    @FXML private TextField txt_ROI;
    @FXML private TextField txt_ROI_Profit;
    @FXML private TextField txt_BankInterest;
    @FXML private TextField txt_MonthlyCashFlow;



    public void calculate() {
        try {
            double price = Double.parseDouble(txt_Price.getText());
            double priceGrowthPercent = Double.parseDouble(txt_PriceGrowthPercent.getText());
            double rentToPrice = Double.parseDouble(txt_RentToPrice.getText());
            double rentGrowthPercent = Double.parseDouble(txt_RentGrowthPercent.getText());
            double downPayment = Double.parseDouble(txt_DownPayment.getText());
            double interestRate = Double.parseDouble(txt_InterestRate.getText());
            double tax = Double.parseDouble(txt_Tax.getText());
            double Insurance = Double.parseDouble(txt_Insurance.getText());
            double maintenance = Double.parseDouble(txt_Maintenance.getText());
            double vacancyRate = Double.parseDouble(txt_VacancyRate.getText());
            Integer years = cmb_Years.getValue();
            //-------------------------------------------------------------------

            double Growth = Calculator.growth(price, priceGrowthPercent, years);
            double Mortgage = Calculator.mortgage(price, interestRate / 100, years, downPayment, years);
            double Rent = Calculator.rent(rentToPrice,price,rentGrowthPercent,years);
            double cahsflow_rent1 = Calculator.cahsflow_rent(rentToPrice, price, rentGrowthPercent, vacancyRate, years);
            double Return = Calculator.priceToDown(price, downPayment);


            System.out.println("Growth = " + Growth);
            System.out.println("Mortgage = " + Mortgage);
            System.out.println("Total Rent =" + Rent);
            System.out.println("Total Return = " + Return);
            System.out.println("Total cashflow return = " + cahsflow_rent1);

            int numberOfMonths = years * 12;

            System.out.println("Number of Months = " + numberOfMonths);

            double ROI = ((Return + Growth + Rent - Mortgage - (tax*years) - (Insurance*years) - (maintenance*years)) / downPayment);

            double Cash_flow =  (cahsflow_rent1 - Mortgage + Growth - (tax*years)-(Insurance*years)-(maintenance*years)) / numberOfMonths;

            double intrest = Calculator.calculateInterestForROI(downPayment, ROI, years);

            System.out.println("Bank Interest = " +intrest);

            txt_Mortgage.setText("( "+years+" years ) : $ " + Math.round(Mortgage));
            txt_ROI.setText("( " + years + " years ) : " + Math.round(ROI * 10) / 10);
            txt_ROI_Profit.setText("( " + years + " years ) : $" + Math.round(ROI * downPayment));
            double x = Math.round(intrest * 1000);
            txt_BankInterest.setText("( " + years + " years ) : " + x/1000);
            txt_MonthlyCashFlow.setText("( " + numberOfMonths + " months ): $" + Math.round(Cash_flow));
        } catch (Exception ex) {
            ZMessage.Error("Error has occurred in input.");
        }
    }

    public void setEstate(Estate estate) {
        if (estate != null) {
            int price = estate.getPrice();
            if (price > 0) {
                if (Objects.equals(estate.getType(), "rent")) {
                    double estPrice = (double) price * (1 / Global.getRentToPrice());
                    estPrice = Math.round(Math.round(Math.round(estPrice * 1000) / 1000) / 1000) * 1000;
                    txt_Price.setText(estPrice + "");
                } else {
                    txt_Price.setText(price + "");
                }
            } else {
                if (estate.RentOptions.size() > 0) {
                    int rent = estate.RentOptions.get(0).getPrice();
                    double estPrice = (double) rent * (1 / Global.getRentToPrice());
                    estPrice = Math.round(Math.round(Math.round(estPrice * 1000) / 1000) / 1000) * 1000;
                    txt_Price.setText(estPrice + "");
                }
            }
            if (!Objects.equals(txt_Price.getText(), "")) {
                try{
                    double p = Double.parseDouble(txt_Price.getText());
                    txt_DownPayment.setText((p * Global.getDownPaymentPercent() / 100) + "");
                }catch(Exception ex){}
            }
        }
    }

    private void loadData()
    {
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

    @FXML
    protected void initialize() {
        loadData();
    }
}

