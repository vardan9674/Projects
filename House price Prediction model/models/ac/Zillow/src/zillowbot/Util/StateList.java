package zillowbot.Util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import zillowbot.Entities.State;
import zillowbot.Entities.ZipCode;
import zillowbot.Util.FileUtil;

import java.util.ArrayList;
import java.util.Objects;

public class StateList {
    public ObservableList<State> States = FXCollections.observableArrayList();

    public void loadZipAbbreviation()
    {
        ArrayList<String> list= FileUtil.readFileAsList("Data/ZipAbbreviation.csv");
        for (String item : list) {
            String[] s = item.split(",");
            State state = new State(s[0], s[1]);
            States.add(state);
        }
    }

    public State findStateByAbbreviation(String abbreviation)
    {
        for (State state: States) {
            if(Objects.equals(state.getAbbreviation(),abbreviation))
            {
                return state;
            }
        }
        return null;
    }

    private ZipCode findZipCode(int zipCode)
    {
        for (State state: States)
        {
            ZipCode z = state.FindZipCode(zipCode);
            if(z != null)
                return null;
        }
        return null;
    }


    private void updateStatesWithZipCodes()
    {
        ArrayList<String> list = FileUtil.readFileAsList("Data/All_Zip.csv");
        State state = null;
        for (String item : list) {
            String[] s = item.split(",");
            String abbreviation = s[1];
            if (state == null)
                state = findStateByAbbreviation(abbreviation);

            if (state != null) {
                if (!Objects.equals(state.getAbbreviation(), abbreviation)) {
                    state = findStateByAbbreviation(abbreviation);
                }
            }
            if (state != null) {
                int z = Integer.parseInt(s[0]);
                state.ZipCodes.add(new ZipCode(z));
            }
        }
    }

    private void updateStatesWithZipCodesInfo()
    {
        ArrayList<String> list = FileUtil.readFileAsList("Data/ZipCodes.csv");
        for (String item : list) {
            String[] s = item.split(",");
            Integer z = Integer.parseInt(s[0]);
            ZipCode zipCode = findZipCode(z);
            if (zipCode != null) {
                zipCode.setType(s[1]);
                zipCode.setPrimaryCity(s[3]);
            }
        }
    }

    public void loadAll()
    {
        loadZipAbbreviation();
        updateStatesWithZipCodes();
        updateStatesWithZipCodesInfo();
    }

    public int size()
    {
        return States.size();
    }

    public State getState(int i)
    {
        if((i>0)&&(i<size()))
        {
            return States.get(i);
        }
        return null;
    }
}
