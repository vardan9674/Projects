package zillowbot.Entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import zillowbot.Util.FileUtil;

import java.util.ArrayList;
import java.util.Objects;

public class State {
    private final SimpleStringProperty Abbreviation;
    private final SimpleStringProperty Name;
    public ObservableList<ZipCode> ZipCodes = FXCollections.observableArrayList();

    public State(String abbreviation, String name) {
        Abbreviation = new SimpleStringProperty(abbreviation);
        Name = new SimpleStringProperty(name);
    }

    public ZipCode FindZipCode(int zipCode)
    {
        for (ZipCode z:ZipCodes) {
            if(z.getZipCode() == zipCode)
                return z;
        }
        return null;
    }

    public void loadZipCodes()
    {
        ArrayList<String> list = FileUtil.readFileAsList("Data/All_Zip.csv");
        for (String item : list) {
            String[] s = item.split(",");
            String abbreviation = s[1];
            if (Objects.equals(getAbbreviation(), abbreviation)) {
                int z = Integer.parseInt(s[0]);
                ZipCodes.add(new ZipCode(z));
            }
        }
        list = FileUtil.readFileAsList("Data/ZipCodes.csv");
        for (String item : list) {
            String[] s = item.split(",");
            int z = Integer.parseInt(s[0]);
            ZipCode zipCode = FindZipCode(z);
            if(zipCode!=null)
            {
                zipCode.setPrimaryCity(s[3]);
                zipCode.setType(s[1]);
            }
        }

    }

    public String getAbbreviation() {
        return Abbreviation.get();
    }

    public SimpleStringProperty abbreviationProperty() {
        return Abbreviation;
    }

    public String getName() {
        return Name.get();
    }

    public SimpleStringProperty nameProperty() {
        return Name;
    }
}
