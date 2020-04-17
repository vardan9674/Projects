package zillowbot.DB;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import zillowbot.Entities.*;
import zillowbot.Global;
import zillowbot.Util.Calculator;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class DbWorker extends SwingWorker<Boolean, String> {

    private DbCommand command;
    private Object input;
    private boolean flag;
    private Object output;
    private List<DbDone> listeners = new ArrayList<DbDone>();

    public void addListener(DbDone dbDone)
    {
        listeners.add(dbDone);
    }

    @Override
    protected void done()
    {
        for (DbDone listener:listeners)
        {
            listener.dbDone();
        }
    }

    public Object getOutput()
    {
        return output;
    }

    public DbWorker(DbCommand command, Object input, boolean flag)
    {
        this.command = command;
        this.input = input;
        this.output = output;
        this.flag = flag;
    }

    private boolean CheckDriver()
    {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void log(String logStatement)
    {
        System.out.println(logStatement);
    }

    private Connection connect(String dbPath)
    {
        String url="jdbc:sqlite:"+dbPath;
        Connection conn=null;
        try
        {
            conn= DriverManager.getConnection(url);
        }
        catch(SQLException ex)
        {
            log(ex.getMessage());
        }
        return conn;
    }

    private Connection connect()
    {
        return connect(Global.DbPath);
    }

    private int lastInsertRowId(String tableName,String fieldName)
    {
        try
        {
            Connection conn = connect();
            String sql="select Max(["+fieldName+"]) from ["+tableName+"]";
            Statement statement=conn.createStatement();
            ResultSet rs=statement.executeQuery(sql);
            if(rs.next())
            {
                Integer id = rs.getInt(1);
                conn.close();
                return id;
            }
        }
        catch(SQLException ex)
        {
            log(ex.getMessage());
        }
        return 0;
    }

    private Integer getPaperID(String title)
    {
        try
        {
            Connection conn = connect();
            String sql="select * from [Paper] where [Title] = ?";
            PreparedStatement preparedStatement=conn.prepareStatement(sql);
            preparedStatement.setString(1,title);
            ResultSet rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                Integer id = rs.getInt("ID");
                conn.close();
                return id;
            }
        }
        catch(SQLException ex)
        {
            log(ex.getMessage());
        }
        return 0;
    }

    private ObservableList<Request> getAllRequests()
    {
        ObservableList<Request> result = FXCollections.observableArrayList();
        try
        {
            Connection conn;
            String sql;

            conn = connect();
            sql="select count(*) from [Request]";
            PreparedStatement preparedStatement=conn.prepareStatement(sql);
            ResultSet rs=preparedStatement.executeQuery();
            int count = rs.getInt(1);
            conn.close();

            conn = connect();
            sql="select * from [Request]";
            preparedStatement=conn.prepareStatement(sql);
            rs=preparedStatement.executeQuery();
            while(rs.next())
            {
                Request request = new Request();
                request.Id = rs.getInt("Id");
                request.RequestTime = rs.getString("RequestTime");
                request.UpdateTime = rs.getString("UpdateTime");
                request.Type = rs.getString("RequestType");
                request.ZipCodes = null;
                result.add(request);
            }
            conn.close();
        }
        catch(SQLException ex)
        {
            log(ex.getMessage());
        }
        return result;
    }

    private void getRequestZipCodes(Request request)
    {
        if(request.ZipCodes!=null)
        {
            request.ZipCodes.clear();
            request.ZipCodes = null;
        }
        request.ZipCodes = FXCollections.observableArrayList();
        try
        {
            String sql;
            Connection conn;

            conn = connect();
            sql="select * from [RequestZip] where [RequestId] = ?";
            PreparedStatement preparedStatement=conn.prepareStatement(sql);
            preparedStatement.setInt(1,request.Id);
            ResultSet rs=preparedStatement.executeQuery();
            int count = rs.getInt(1);
            conn.close();

            conn = connect(Global.DbPath);
            sql="select * from [RequestZip] where [RequestId] = ?";
            preparedStatement=conn.prepareStatement(sql);
            preparedStatement.setInt(1,request.Id);

            rs=preparedStatement.executeQuery();
            setProgress(0);
            int prevProgress = 0;
            int i = 0;
            while(rs.next())
            {
                int requestZipId = rs.getInt("Id");
                int z = rs.getInt("ZipCode");
                String primaryCity = rs.getString("PrimaryCity");
                String type = rs.getString("ZipType");
                String status = rs.getString("Status");
                int sys = rs.getInt("Sys");
                ZipCode zipCode = new ZipCode(z,primaryCity,type,status);
                zipCode.Sys = sys;
                zipCode.PrevSys = sys;
                zipCode.RequestZipId = requestZipId;
                request.ZipCodes.add(zipCode);
                i++;
                int newProgress = (int)Math.round(((double)i/count)*100);
                if(prevProgress!=newProgress)
                {
                    prevProgress = newProgress;
                    setProgress(newProgress);
                }
                publish("ZipCode: "+zipCode.getZipCode());
            }
            conn.close();
        }
        catch(SQLException ex)
        {
            log(ex.getMessage());
        }
    }

    private void SaveRequest(Request request)
    {
        SaveRequest(request,true);
    }

    private void SaveRequest(Request request, boolean update)
    {
        if(request == null)
            return;
        try
        {
            setProgress(0);
            int prevProgress = 0;
            Connection conn;
            String sql;
            if(request.Id == 0)
            {
                conn=connect();
                sql = "insert into [Request]([RequestTime],[UpdateTime],[RequestType]) values(?,?,?)";
                PreparedStatement preparedStatement=conn.prepareStatement(sql);
                preparedStatement.setString(1,request.RequestTime);
                preparedStatement.setString(2,request.UpdateTime);
                preparedStatement.setString(3,request.Type);
                preparedStatement.executeUpdate();
                conn.close();
                request.Id = lastInsertRowId("Request","Id");
                publish("request info saved.");
            }
            if(request.ZipCodes!=null)
            {
                int count = request.ZipCodes.size();
                int i = 0;
                int newProgress;
                for (ZipCode zipCode:request.ZipCodes)
                {
                    conn = connect();
                    if(zipCode.RequestZipId == 0)
                    {
                        sql = "insert into [RequestZip]([RequestId],[ZipCode],[PrimaryCity],[ZipType],[Status],[Sys]) values(?,?,?,?,?,?)";
                        PreparedStatement preparedStatement=conn.prepareStatement(sql);
                        preparedStatement.clearParameters();
                        preparedStatement.setInt(1,request.Id);
                        preparedStatement.setInt(2,zipCode.getZipCode());
                        preparedStatement.setString(3,zipCode.getPrimaryCity());
                        preparedStatement.setString(4,zipCode.getType());
                        preparedStatement.setString(5,zipCode.getStatus());
                        preparedStatement.setInt(6,zipCode.Sys);
                        preparedStatement.executeUpdate();
                        conn.close();
                        zipCode.RequestZipId = lastInsertRowId("RequestZip","Id");
                        publish("ZipCode "+zipCode.getZipCode()+" inserted.");
                    }
                    else if(update)
                    {
                        sql = "update [RequestZip] set [Status]=? , [Sys]=? where Id=?";
                        PreparedStatement preparedStatement=conn.prepareStatement(sql);
                        preparedStatement.clearParameters();
                        preparedStatement.setString(1,zipCode.getStatus());
                        preparedStatement.setInt(2,zipCode.Sys);
                        preparedStatement.setInt(3,zipCode.RequestZipId);
                        preparedStatement.executeUpdate();
                        conn.close();
                        publish("ZipCode "+zipCode.getZipCode()+" updated.");
                    }
                    i++;
                    newProgress = (int)Math.round(((double)i/count)*100);
                    if(prevProgress != newProgress)
                    {
                        prevProgress = newProgress;
                        setProgress(newProgress);
                    }
                }
            }
        }
        catch (SQLException ex)
        {
            log(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private Request getRequest(Object object) throws Exception
    {
        if(object == null)
            throw new Exception("request is null.");
        if(! (object instanceof Request))
            throw new Exception("Request is needed for updating ZipCodes.");
        Request request = (Request)object;
        return request;
    }

    private SearchData getSearchData(Object object) throws Exception {
        if (object == null)
            throw new Exception("Search data is null.");
        if(!(object instanceof SearchData))
            throw new Exception("SearchData needed for search.");
        return (SearchData) object;
    }

    private Estate getEstateFromResultSet(ResultSet rs) throws Exception{
        String zpid = rs.getString("ZPID");
        String type = rs.getString("Type");
        String state = rs.getString("State");
        String locality = rs.getString("Locality");
        String address = rs.getString("Address");
        Double latitude = rs.getDouble("Latitude");
        Double longitude = rs.getDouble("Longitude");
        Integer price = rs.getInt("Price");
        Integer bedrooms = rs.getInt("Bedrooms");
        Double bathrooms = rs.getDouble("Bathrooms");
        Integer areaSpace_sqft = rs.getInt("AreaSpace_SQFT");
        String status = rs.getString("Status");
        String url_link = rs.getString("URL_Link");
        int RequestZipId = rs.getInt("RequestZipId");
        int page = rs.getInt("Page");
        int zipCode = rs.getInt("ZipCode");
        int id = rs.getInt("Id");
        String cardBadge = rs.getString("CardBadge");
        boolean phase2 = rs.getBoolean("phase2");
        String options = rs.getString("Options");
        String description = rs.getString("Description");
        boolean singleCase = rs.getBoolean("Single_Case");
        Estate estate = new Estate(zpid,
                type,
                state,
                locality,
                address,
                latitude,
                longitude,
                price,
                bedrooms,
                bathrooms,
                areaSpace_sqft,
                status,
                cardBadge,
                url_link);
        estate.setZipCode(zipCode);
        estate.Id = id;
        estate.phase2 = phase2;
        estate.setOptions(options);
        estate.setDescription(description);
        estate.setSingle_Case(singleCase);
        return estate;
    }

    private List<Option> getEstateOptions(Estate estate, String dealType) {
        List<Option> result = new ArrayList<>();
        try {
            Connection conn = connect();
            String sql = "select * from Option where [DealType]=? and [EstateId]=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,dealType);
            preparedStatement.setInt(2,estate.Id);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String estateType = rs.getString("EstateType");
                int bedrooms = rs.getInt("Bedrooms");
                double bathrooms = rs.getDouble("Bathrooms");
                int areaSpace = rs.getInt("AreaSpace");
                int price = rs.getInt("Price");
                String unitNumber = rs.getString("UnitNumber");
                Option option = new Option(dealType,estateType,bedrooms,bathrooms,areaSpace,price,unitNumber);
                option.Id = rs.getInt("OptionId");
                result.add(option);
            }
        } catch (Exception ex) {
            log(ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    private List<School> getEstateSchools(Estate estate) {
        List<School> result = new ArrayList<>();
        try {
            Connection conn = connect();
            String sql = "select * from [School] join [EstateSchool] on [School].[SchoolId] = [EstateSchool].[SchoolId] "+
                    "where [EstateId]=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1,estate.Id);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String name = rs.getString("Name");
                String grades = rs.getString("Grades");
                double distance = rs.getDouble("Distance");
                School school = new School(name, grades, distance);
                result.add(school);
            }
        } catch (Exception ex) {
            log(ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    private List<Data> getEstateData(Estate estate,String category) {
        List<Data> result = new ArrayList<>();
        try {
            Connection conn = connect();
            String sql = "select * from [Data] where [EstateId]=? and [Category]=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1,estate.Id);
            preparedStatement.setString(2,category);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String title = rs.getString("Title");
                String key = rs.getString("Key");
                String value = rs.getString("Value");
                String valueType = rs.getString("ValueType");
                Data data = new Data(category,title,key,value,valueType);
                result.add(data);
            }
        } catch (Exception ex) {
            log(ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    private void getEstatesFromResultSet(int count,ResultSet rs,ObservableList<Estate> estates) throws Exception{
        estates.clear();
        int i = 0;
        double prevProgress = 0;
        while (rs.next()){
            Estate estate = getEstateFromResultSet(rs);
            estates.add(estate);
            for (Option option : getEstateOptions(estate, "rent")) {
                estate.RentOptions.add(option);
            }
            for (Option option : getEstateOptions(estate, "sale")) {
                estate.SaleOptions.add(option);
            }
            for (School school : getEstateSchools(estate)) {
                estate.Schools.add(school);
            }
            estate.KV1_Title="Facts and Features";
            estate.KV2_Title="Details";
            estate.KV3_Title="Amenities";
            for (Data data : getEstateData(estate, "Facts")) {
                estate.KV1.add(data);
            }
            for (Data data : getEstateData(estate, "Details")) {
                estate.KV2.add(data);
            }
            for (Data data : getEstateData(estate, "Amenities")) {
                estate.KV3.add(data);
            }
            int newProgress = (int)Math.round(((double)i++/count)*100);
            if(prevProgress != newProgress)
            {
                prevProgress = newProgress;
                setProgress(newProgress);
            }
        }
    }

    private void selectEstatesOfARequest(SearchData searchData) {
        searchData.Estates.clear();
        try {
            Connection conn;
            conn = connect();
            int requestId = Integer.parseInt(searchData.Data[0]);
            String sql = "select [Id] from [RequestZip] where RequestId=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1,requestId);
            ResultSet rs = preparedStatement.executeQuery();
            String Ids = "";
            String and ="";
            while(rs.next()){
                Ids+=and+rs.getInt(1);
                and=",";
            }
            conn.close();

            int count = 0;
            conn =connect();
            Ids = "("+Ids+")";
            sql = "select count(*) from [Estate] where RequestZipId in "+Ids;
            preparedStatement = conn.prepareStatement(sql);
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            conn.close();
            if (count > 0) {
                conn =connect();
                sql = "select * from [Estate] where RequestZipId in "+Ids;
                preparedStatement = conn.prepareStatement(sql);
                rs = preparedStatement.executeQuery();
                getEstatesFromResultSet(count, rs, searchData.Estates);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void Search(SearchData searchData) {
        searchData.Estates.clear();
        String sql="";
        try {
            Connection conn;

            conn = connect();
            sql="select * from [Estate]";
            String[] fields = Stream.of("State","Locality","Address","ZipCode").toArray(String[]::new);
            if (searchData.Data != null && searchData.Data.length > 0) {
                if (searchData.Data != null && searchData.Data.length != 0 && (!Objects.equals(searchData.Data[0], ""))) {
                    sql += " where ";
                }
                String orc = "";
                for (String data : searchData.Data) {
                    if (!Objects.equals(data, "")) {
                        for (String field : fields) {
                            sql += orc+" ["+field+"] like ?";
                            orc = " or ";
                        }
                    }
                }
            }
            PreparedStatement preparedStatement;
            String sql2 =sql.replaceAll("select \\*","select count(*) ");
            preparedStatement = conn.prepareStatement(sql2);
            preparedStatement.clearParameters();
            if (searchData.Data != null && searchData.Data.length > 0) {
                int i = 1;
                for (String data : searchData.Data) {
                    if (!Objects.equals(data, "")) {
                        for (String ignored : fields) {
                            String d = data.replace("!", "!!")
                                    .replace("%", "!%")
                                    .replace("_", "!_")
                                    .replace("[", "![");
                            preparedStatement.setString(i++, "%"+d+"%");
                        }
                    }
                }
            }
            ResultSet rs = preparedStatement.executeQuery();
            int count = rs.getInt(1);
            conn.close();

            conn = connect();
            preparedStatement=conn.prepareStatement(sql);
            preparedStatement.clearParameters();
            if (searchData.Data != null && searchData.Data.length > 0) {
                int i = 1;
                for (String data : searchData.Data) {
                    if (!Objects.equals(data, "")) {
                        for (String ignored : fields) {
                            String d = data.replace("!", "!!")
                                    .replace("%", "!%")
                                    .replace("_", "!_")
                                    .replace("[", "![");
                            preparedStatement.setString(i++, "%"+d+"%");
                        }
                    }
                }
            }
            rs=preparedStatement.executeQuery();
            getEstatesFromResultSet(count, rs, searchData.Estates);
        }catch(Exception ex){
            log(ex.getMessage());
            System.out.println(sql);
        }
    }

    @Override
    protected Boolean doInBackground() throws Exception
    {
        switch(command)
        {
            case GetAllRequests:
                {
                    output = getAllRequests();
                }break;
            case GetRequestZipCodes:
                {
                    Request request = getRequest(input);
                    getRequestZipCodes(request);
                }break;
            case SaveRequest:
                {
                    Request request = getRequest(input);
                    SaveRequest(request,flag);
                }break;
            case Search:
                {
                    SearchData searchData = getSearchData(input);
                    Search(searchData);
                }break;
            case GetRequestEstates:
                {
                    SearchData searchData = getSearchData(input);
                    selectEstatesOfARequest(searchData);
                }break;
            case RunCalculation:
                {
                    SearchData searchData = getSearchData(input);
                    runCalculation(searchData);
                }break;
        }
        return true;
    }

    private void updateOption(Option option, CalcData data) throws Exception{
        Connection conn = connect();
        String sql = "update [Option] " +
                "set OptionMortgage=? , OptionROI = ? , OptionBankInterest=? , OptionCashFlow=? " +
                "where [OptionId]=?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setDouble(1, data.Mortgage);
        preparedStatement.setDouble(2, data.ROI);
        preparedStatement.setDouble(3, data.BankInterest);
        preparedStatement.setDouble(4, data.CashFlow);
        preparedStatement.setInt(5, option.Id);
        preparedStatement.executeUpdate();
        conn.close();
    }

    private void runCalculation(SearchData searchData) {
        try {
            Connection conn;
            String sql;
            int index = 0;
            int count = searchData.Estates.size();
            int prevProgress = 0;
            setProgress(prevProgress);
            for (Estate estate : searchData.Estates) {
                CalcData data = null;
                double price;
                String estateType = estate.getType().toLowerCase();
                if (Objects.equals(estateType, "rent") || Objects.equals(estateType, "sale")) {
                    price = estate.getPrice();
                    if (price > 0) {
                        if(Objects.equals(estateType,"rent")) {
                            double estPrice = price * (1 / Global.getRentToPrice());
                            price = Math.round(Math.round(Math.round(estPrice * 1000) / 1000) / 1000) * 1000;
                        }
                        data = Calculator.calculate(price);
                        conn = connect();
                        sql = "update [Estate] " +
                                "set Mortgage=? , ROI = ? , BankInterest=? , CashFlow=? " +
                                "where [Id]=?";
                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                        preparedStatement.setDouble(1, data.Mortgage);
                        preparedStatement.setDouble(2, data.ROI);
                        preparedStatement.setDouble(3, data.BankInterest);
                        preparedStatement.setDouble(4, data.CashFlow);
                        preparedStatement.setInt(5, estate.Id);
                        preparedStatement.executeUpdate();
                        conn.close();
                    } else {
                        for (Option option : estate.RentOptions) {
                            if(option.Id == null)
                                continue;
                            price = option.getPrice();
                            double estPrice = price * (1 / Global.getRentToPrice());
                            price = Math.round(Math.round(Math.round(estPrice * 1000) / 1000) / 1000) * 1000;
                            data = Calculator.calculate(price);
                            updateOption(option,data);
                        }
                        for (Option option : estate.SaleOptions) {
                            if(option.Id == null)
                                continue;
                            price = option.getPrice();
                            data = Calculator.calculate(price);
                            updateOption(option,data);
                        }
                    }
                }
                int newProgress = (int)Math.round(((double)index/count)*100);
                index++;
                if(prevProgress != newProgress)
                {
                    prevProgress = newProgress;
                    setProgress(newProgress);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
