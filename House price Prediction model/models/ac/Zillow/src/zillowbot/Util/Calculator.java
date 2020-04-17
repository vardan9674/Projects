package zillowbot.Util;

import zillowbot.Entities.CalcData;
import zillowbot.Global;

public class Calculator {
    public static double calculateInterestForROI(double downPayment, double ROI, int years) {
        double A = ROI *downPayment;
        double P = downPayment;
        int n = 12;
        int t = years;
        double interest = n * ( Math.pow((A/P), (1/((double)n*(double)t))) - 1);
        return interest * 100;
    }

    public static double priceToDown(double price,double downpayment){
        double result = price - downpayment;
        return result;
    }

    public static double growth(double price,double growth,int years) {

        double pr = price;
        for (int i = 0; i < years; i++) {
            price = price + (price * growth/100);
        }
        return (price - pr);
    }

    public static double mortgage(double price,double interest,double numberOfPayments,double downPayment,int years){
        double loan = price - downPayment;
        interest = interest/12;
        numberOfPayments = numberOfPayments*12;
        double monthlyPayments = loan * (interest * Math.pow((1 + interest) , numberOfPayments)) / (Math.pow((1 + interest) , numberOfPayments) - 1);
        return (monthlyPayments * years * 12);
    }

    public static double rent(double rentToPrice,double price,double rentGrowth,int years){
        double pr;
        double A = 0;

        for (int i=0;i<years;i++){
            pr = (price * rentToPrice * 12);
            rentToPrice = rentToPrice + (rentToPrice * rentGrowth / 100);
            A = A + pr;
        }
        return A;
    }

    public static double cahsflow_rent(double rentToPrice,double price,double rentGrowth,double vacancy,int years){
        double pr;
        double A = 0;
        for (int i=0;i<years;i++){
            pr = (price * rentToPrice * (12 - vacancy));
            rentToPrice = rentToPrice + (rentToPrice*rentGrowth/100);
            A = A + pr;
        }
        return A;
    }

    public static double decrease(double price,double dec,int years){
        for (int i = 0; i < years; i++) {
            price = price - (price * dec);
        }
        return price;
    }

    public static CalcData calculate(double price, double priceGrowthPercent, double rentToPrice,
                                     double rentGrowthPercent, double downPayment, double interestRate, double tax,
                                     double Insurance, double maintenance, double vacancyRate, int years) {
        CalcData data = new CalcData();
        double Growth = Calculator.growth(price, priceGrowthPercent, years);
        data.Mortgage = Calculator.mortgage(price, interestRate / 100, years, downPayment, years);
        double Rent = Calculator.rent(rentToPrice,price,rentGrowthPercent,years);
        double cashFlowRent = Calculator.cahsflow_rent(rentToPrice, price, rentGrowthPercent, vacancyRate, years);
        double Return = Calculator.priceToDown(price, downPayment);

        int numberOfMonths = years * 12;

        data.ROI = ((Return + Growth + Rent - data.Mortgage - (tax*years) - (Insurance*years) - (maintenance*years)) / downPayment);
        data.ROI_Profit = Math.round(data.ROI * downPayment);

        data.CashFlow = (cashFlowRent - data.Mortgage + Growth - (tax * years) - (Insurance * years) - (maintenance * years)) / numberOfMonths;

        data.BankInterest = Calculator.calculateInterestForROI(downPayment, data.ROI, years);

        data.ROI = Math.round(data.ROI*100)/100;
        data.Mortgage = Math.round(data.Mortgage*100)/100;
        data.BankInterest = Math.round(data.BankInterest*100)/100;
        data.CashFlow = Math.round(data.CashFlow*100)/100;

        return data;
    }

    public static CalcData calculate(double price) {
        double downPayment = price * Global.getDownPaymentPercent() / 100;
        return calculate(price, Global.getPriceGrowthPercent(), Global.getRentToPrice(), Global.getRentGrowthPercent(),
                downPayment, Global.getInterestRate(), Global.getTax(), Global.getInsurance(), Global.getMaintenance(),
                Global.getVacancyRate(), Global.getYears());
    }
}
