package zillowbot.Entities;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Fact {
    private SimpleStringProperty estateType;
    private SimpleIntegerProperty yearBuilt;
    private SimpleStringProperty heating;
    private SimpleStringProperty cooling;
    private SimpleStringProperty parking;
    private SimpleDoubleProperty pricePerSqft;
    private SimpleStringProperty lot;
    private SimpleIntegerProperty saves;
    private SimpleIntegerProperty daysOnZillow;

    public Fact(String estateType, Integer yearBuilt, String heating, String cooling, String parking,
                Double pricePerSqft, String lot, Integer saves, Integer daysOnZillow) {
        this.estateType = new SimpleStringProperty(estateType);
        this.yearBuilt = new SimpleIntegerProperty(yearBuilt);
        this.heating = new SimpleStringProperty(heating);
        this.cooling = new SimpleStringProperty(cooling);
        this.parking = new SimpleStringProperty(parking);
        this.pricePerSqft = new SimpleDoubleProperty(pricePerSqft);
        this.lot = new SimpleStringProperty(lot);
        this.saves = new SimpleIntegerProperty(saves);
        this.daysOnZillow = new SimpleIntegerProperty(daysOnZillow);
    }

    public String getEstateType(){return estateType.get();}
    public Integer getYearBuilt(){return yearBuilt.get();}
    public String getHeating(){return heating.get();}
    public String getCooling(){return cooling.get();}
    public String getParking(){return parking.get();}
    public Double getPricePerSqft(){return pricePerSqft.get();}
    public String getLot(){return lot.get();}
    public Integer getSaves(){return saves.get();}
    public Integer getDaysOnZillow(){return daysOnZillow.get();}
}
