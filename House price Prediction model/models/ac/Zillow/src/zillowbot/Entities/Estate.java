package zillowbot.Entities;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import zillowbot.Global;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Estate {
    private SimpleStringProperty ZPID;
    private SimpleStringProperty Type;
    private SimpleStringProperty State;
    private SimpleStringProperty Locality;
    private SimpleStringProperty Address;
    private SimpleIntegerProperty ZipCode;
    private  SimpleDoubleProperty Latitude;
    private  SimpleDoubleProperty Longitude;
    private  SimpleIntegerProperty Price;
    private  SimpleStringProperty Options;
    private  SimpleIntegerProperty Bedrooms;
    private  SimpleDoubleProperty Bathrooms;
    private  SimpleIntegerProperty AreaSpace_SQFT;
    private  SimpleStringProperty Status;
    private  SimpleStringProperty CardBadge;
    private  SimpleIntegerProperty EstimatedRent;
    private  SimpleDoubleProperty CashFlow;
    private  SimpleDoubleProperty Price_PerSQFT;
    private  SimpleDoubleProperty ZEstimatePrice;
    private  SimpleStringProperty ZEstimateChange;
    private  SimpleStringProperty ZillowPredict;
    private  SimpleStringProperty Description;
    private  SimpleStringProperty URL_Link;
    private  SimpleBooleanProperty Single_Case;
    public int Id;
    public boolean phase2;
    public ObservableList<Option> RentOptions;
    public ObservableList<Option> SaleOptions;
    public ObservableList<School> Schools;
    public ObservableList<Data> KV1;
    public ObservableList<Data> KV2;
    public ObservableList<Data> KV3;
    public ObservableList<Data> V1;
    public ObservableList<Data> V2;
    public String KV1_Title;
    public String KV2_Title;
    public String KV3_Title;
    public String V1_Title;
    public String V2_Title;
    public Fact FactAndFeatures=null;


    private int lastInserted() {
        int result = 0;
        try {
            Connection conn = Global.getConnection();
            String sql = "select max(Id) from [Estate]";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next())
                result = rs.getInt(1);
            conn.close();
        }catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        return result;
    }

    public void save1(Integer requestZipId,Integer page)
    {
        try {
            Connection conn = Global.getConnection();
            String sql = "insert into [Estate]"+
                    "([RequestZipId],[Page],[ZPID],[Type],[Address],[ZipCode],[Locality],[State],[Latitude],[Longitude],[Price],[Bedrooms],[Bathrooms],[AreaSpace_SQFT],[Status],[URL_Link],[CardBadge],[Options],[Single_Case])"+
                    " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1,requestZipId);
            preparedStatement.setInt(2,page);
            preparedStatement.setString(3,getZPID());
            preparedStatement.setString(4,getType());
            preparedStatement.setString(5,getAddress());
            preparedStatement.setInt(6,getZipCode());
            preparedStatement.setString(7,getLocality());
            preparedStatement.setString(8,getState());
            preparedStatement.setDouble(9,getLatitude());
            preparedStatement.setDouble(10,getLongitude());
            preparedStatement.setInt(11,getPrice());
            preparedStatement.setInt(12,getBedrooms());
            preparedStatement.setDouble(13,getBathrooms());
            preparedStatement.setInt(14,getAreaSpace_SQFT());
            preparedStatement.setString(15,getStatus());
            preparedStatement.setString(16,getURL_Link());
            preparedStatement.setString(17,getCardBadge());
            preparedStatement.setString(18,getOptions());
            preparedStatement.setBoolean(19, isSingle_Case());
            preparedStatement.executeUpdate();
            conn.close();
            Id = lastInserted();
        }catch(SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    public void save2()
    {
        try {
            Connection conn;
            String sql;
            PreparedStatement preparedStatement;
            for (Option option : RentOptions)
                option.save(Id);
            for (Option option : SaleOptions)
                option.save(Id);
            for (School school : Schools)
                school.save(Id);
            String facts="";
            String separator="";
            for (Data data : KV1) {
                facts += separator + data.getKey() + ":" + data.getValue();
                separator=" | ";
                data.save(Id);
            }
            String amenities="";
            for (Data data : KV2) {
                amenities += separator + data.getKey() + ":" + data.getValue();
                separator=" | ";
                data.save(Id);
            }
            String details="";
            for (Data data : KV3) {
                details += separator + data.getKey() + ":" + data.getValue();
                separator=" | ";
                data.save(Id);
            }
            if(FactAndFeatures!=null){
                conn= Global.getConnection();
                sql="insert into [SaleDetails]"+
                        "([EstateId],[EstateType],[YearBuilt],[Heating],[Cooling],[Parking],[PricePerSqft],[Lot],[Saves],[DaysOnZillow])"+
                        " values(?,?,?,?,?,?,?,?,?,?)";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1,Id);
                preparedStatement.setString(2,FactAndFeatures.getEstateType());
                preparedStatement.setInt(3,FactAndFeatures.getYearBuilt());
                preparedStatement.setString(4,FactAndFeatures.getHeating());
                preparedStatement.setString(5,FactAndFeatures.getCooling());
                preparedStatement.setString(6,FactAndFeatures.getParking());
                preparedStatement.setDouble(7,FactAndFeatures.getPricePerSqft());
                preparedStatement.setString(8,FactAndFeatures.getLot());
                preparedStatement.setInt(9,FactAndFeatures.getSaves());
                preparedStatement.setInt(10,FactAndFeatures.getDaysOnZillow());
                preparedStatement.executeUpdate();
            }
            conn = Global.getConnection();
            sql = "update [Estate] "+
                    " set phase2=1 , [Description]=? ,[CardBadge]=? ,[Facts]=? ,[Amenities]=? , [Details]=?"+
                    " where [Id]=?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,getDescription());
            preparedStatement.setString(2,getCardBadge());
            preparedStatement.setString(3,facts);
            preparedStatement.setString(4,amenities);
            preparedStatement.setString(5,details);
            preparedStatement.setInt(6,Id);
            preparedStatement.executeUpdate();
            conn.close();
        }catch(SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    public Estate(String zpid,
                  String type,
                  String state,
                  String locality,
                  String address,
                  Double latitude,
                  Double longitude,
                  Integer price,
                  Integer bedrooms,
                  Double bathrooms,
                  Integer areaSpace_sqft,
                  String status,
                  String cardBadge,
                  String url_link)
    {
        RentOptions = FXCollections.observableArrayList();
        SaleOptions = FXCollections.observableArrayList();
        Schools = FXCollections.observableArrayList();
        KV1 = FXCollections.observableArrayList();
        KV2 = FXCollections.observableArrayList();
        KV3 = FXCollections.observableArrayList();
        V1 = FXCollections.observableArrayList();
        V2 = FXCollections.observableArrayList();
        ZPID = new SimpleStringProperty(zpid);
        Type = new SimpleStringProperty(type);
        State = new SimpleStringProperty(state);
        Locality = new SimpleStringProperty(locality);
        Address = new SimpleStringProperty(address);
        ZipCode = null;
        Latitude = new SimpleDoubleProperty(latitude);
        Longitude = new SimpleDoubleProperty(longitude);
        Price = new SimpleIntegerProperty(price);
        Options = new SimpleStringProperty("");
        Bedrooms = new SimpleIntegerProperty(bedrooms);
        Bathrooms = new SimpleDoubleProperty(bathrooms);
        AreaSpace_SQFT = new SimpleIntegerProperty(areaSpace_sqft);
        Status = new SimpleStringProperty(status);
        CardBadge = new SimpleStringProperty(cardBadge);
        EstimatedRent = new SimpleIntegerProperty(0);
        CashFlow = new SimpleDoubleProperty(0);
        Price_PerSQFT = new SimpleDoubleProperty(0);
        ZEstimatePrice = new SimpleDoubleProperty(0);
        ZEstimateChange = new SimpleStringProperty("");
        ZillowPredict = new SimpleStringProperty("");;
        Description = new SimpleStringProperty("");
        URL_Link = new SimpleStringProperty(url_link);
        Single_Case = new SimpleBooleanProperty(false);
    }

    public String getZPID() {
        return ZPID.get();
    }

    public SimpleStringProperty ZPIDProperty() {
        return ZPID;
    }

    public String getType() {
        return Type.get();
    }

    public SimpleStringProperty typeProperty() {
        return Type;
    }

    public String getState() {
        return State.get();
    }

    public SimpleStringProperty stateProperty() {
        return State;
    }

    public String getLocality() {
        return Locality.get();
    }

    public SimpleStringProperty localityProperty() {
        return Locality;
    }

    public String getAddress() {
        return Address.get();
    }

    public SimpleStringProperty addressProperty() {
        return Address;
    }

    public Integer getZipCode() {
        return ZipCode.get();
    }

    public void setZipCode(Integer zipCode){ ZipCode = new SimpleIntegerProperty(zipCode); }

    public SimpleIntegerProperty zipCodeProperty() {
        return ZipCode;
    }

    public double getLatitude() {
        return Latitude.get();
    }

    public SimpleDoubleProperty latitudeProperty() {
        return Latitude;
    }

    public double getLongitude() {
        return Longitude.get();
    }

    public SimpleDoubleProperty longitudeProperty() {
        return Longitude;
    }

    public int getPrice() {
        return Price.get();
    }

    public SimpleIntegerProperty priceProperty() {
        return Price;
    }

    public String getOptions() {
        return Options.get();
    }

    public SimpleStringProperty optionsProperty() {
        return Options;
    }

    public void setOptions(String value) {
        Options.set(value);
    }

    public int getBedrooms() {
        return Bedrooms.get();
    }

    public SimpleIntegerProperty bedroomsProperty() {
        return Bedrooms;
    }

    public Double getBathrooms() {
        return Bathrooms.get();
    }

    public SimpleDoubleProperty bathroomsProperty() {
        return Bathrooms;
    }

    public Integer getAreaSpace_SQFT() {
        return AreaSpace_SQFT.get();
    }

    public SimpleIntegerProperty areaSpace_SQFTProperty() {
        return AreaSpace_SQFT;
    }

    public String getStatus() {
        return Status.get();
    }

    public SimpleStringProperty statusProperty() {
        return Status;
    }

    public int getEstimatedRent() {
        return EstimatedRent.get();
    }

    public SimpleIntegerProperty estimatedRentProperty() {
        return EstimatedRent;
    }

    public String getCardBadge() {
        return CardBadge.get();
    }

    public SimpleStringProperty CardBadgeProperty() {
        return CardBadge;
    }

    public double getCashFlow() {
        return CashFlow.get();
    }

    public SimpleDoubleProperty cashFlowProperty() {
        return CashFlow;
    }

    public double getPrice_PerSQFT() {
        return Price_PerSQFT.get();
    }

    public SimpleDoubleProperty price_PerSQFTProperty() {
        return Price_PerSQFT;
    }

    public double getZEstimatePrice() {
        return ZEstimatePrice.get();
    }

    public SimpleDoubleProperty ZEstimatePriceProperty() {
        return ZEstimatePrice;
    }

    public String getZEstimateChange() {
        return ZEstimateChange.get();
    }

    public SimpleStringProperty ZEstimateChangeProperty() {
        return ZEstimateChange;
    }

    public String getZillowPredict() {
        return ZillowPredict.get();
    }

    public SimpleStringProperty zillowPredictProperty() {
        return ZillowPredict;
    }

    public String getDescription() {
        return Description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return Description;
    }

    public String getURL_Link() {
        return URL_Link.get();
    }

    public SimpleStringProperty URL_LinkProperty() {
        return URL_Link;
    }

    public boolean isSingle_Case() {
        return Single_Case.get();
    }

    public void setSingle_Case(boolean value) {
        Single_Case.setValue(value);
    }

    public SimpleBooleanProperty single_CaseProperty() {
        return Single_Case;
    }

    public void setDescription(String description) {
        if (Description == null) {
            Description = new SimpleStringProperty(description);
        } else {
            Description.set(description);
        }
    }
}
