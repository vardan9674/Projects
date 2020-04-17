package zillowbot.Forms;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import zillowbot.Entities.Data;
import zillowbot.Entities.Estate;
import zillowbot.Entities.Option;
import zillowbot.Entities.School;
import zillowbot.Util.ExtraGrids;

public class ControllerEstate {
    @FXML private SplitPane splitPane_data;
    @FXML private AnchorPane anchor_ForRent;
    @FXML private AnchorPane anchor_ForSale;
    @FXML private AnchorPane anchor_School;
    @FXML private AnchorPane anchor_KV1;
    @FXML private AnchorPane anchor_KV2;
    @FXML private AnchorPane anchor_KV3;
    @FXML private TextField txt_ZPID;
    @FXML private TextField txt_Type;
    @FXML private TextField txt_State;
    @FXML private TextField txt_Locality;
    @FXML private TextField txt_ZipCode;
    @FXML private TextField txt_Address;
    @FXML private TextField txt_Latitude;
    @FXML private TextField txt_Longitude;
    @FXML private TextField txt_Price;
    @FXML private TextField txt_Options;
    @FXML private TextField txt_Bedrooms;
    @FXML private TextField txt_Bathrooms;
    @FXML private TextField txt_AreaSpace_SQFT;
    @FXML private TextField txt_Status;
    @FXML private TextField txt_Description;
    @FXML private TextField txt_CardBadge;

    @FXML private TableView<Option> grid_ForSale;
    @FXML private TableColumn<Option, String> col_FR_Type;
    @FXML private TableColumn<Option, Double> col_FR_Bathrooms;
    @FXML private TableColumn<Option, Integer> col_FR_AreaSpace;
    @FXML private TableColumn<Option, Integer> col_FR_Price;

    @FXML private TableView<Option> grid_ForRent;
    @FXML private TableColumn<Option, String> col_FS_Type;
    @FXML private TableColumn<Option, Double> col_FS_Bathrooms;
    @FXML private TableColumn<Option, Integer> col_FS_AreaSpace;
    @FXML private TableColumn<Option, Integer> col_FS_Price;

    @FXML private TableView<School> grid_School;
    @FXML private TableColumn<Option, String> col_School_Name;
    @FXML private TableColumn<School, String> col_School_Grades;
    @FXML private TableColumn<School, Double> col_School_Distance;

    @FXML private TableView<Data> grid_KV1;
    @FXML private TableColumn<School, String> col_KV1_Key;
    @FXML private TableColumn<School, String> col_KV1_Value;
    @FXML private Label lbl_KV1;

    @FXML private TableView<Data> grid_KV2;
    @FXML private TableColumn<School, String> col_KV2_Key;
    @FXML private TableColumn<School, String> col_KV2_Value;
    @FXML private Label lbl_KV2;

    @FXML private TableView<Data> grid_KV3;
    @FXML private TableColumn<School, String> col_KV3_Key;
    @FXML private TableColumn<School, String> col_KV3_Value;
    @FXML private Label lbl_KV3;

    private void prepareGrids() {
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

        grid_School.setRowFactory(param -> new TableRow<>());

        grid_ForRent.setRowFactory(param -> new TableRow<>());

        grid_ForSale.setRowFactory(param -> new TableRow<>());

        grid_KV1.setRowFactory(param-> new TableRow<>());

        grid_KV2.setRowFactory(param-> new TableRow<>());

        grid_KV3.setRowFactory(param-> new TableRow<>());
    }

    public void setEstate(Estate estate) {
        txt_ZPID.setText(estate.getZPID());
        txt_Type.setText(estate.getType());
        txt_State.setText(estate.getState());
        txt_Locality.setText(estate.getLocality());
        txt_ZipCode.setText(estate.getZipCode()+"");
        txt_Address.setText(estate.getAddress());
        txt_Latitude.setText(estate.getLatitude()+"");
        txt_Longitude.setText(estate.getLongitude()+"");
        txt_Price.setText(estate.getPrice()+"");
        txt_Options.setText(estate.getOptions());
        txt_Bedrooms.setText(estate.getBedrooms()+"");
        txt_Bathrooms.setText(estate.getBathrooms()+"");
        txt_AreaSpace_SQFT.setText(estate.getAreaSpace_SQFT()+"");
        txt_Status.setText(estate.getStatus());
        txt_Description.setText(estate.getDescription());
        txt_CardBadge.setText(estate.getCardBadge());
        while (splitPane_data.getItems().size() > 0) {
            splitPane_data.getItems().remove(0);
        }
        if (estate.RentOptions.size() > 0) {
            grid_ForRent.setItems(estate.RentOptions);
            splitPane_data.getItems().add(anchor_ForRent);
        }
        if (estate.SaleOptions.size() > 0) {
            grid_ForSale.setItems(estate.SaleOptions);
            splitPane_data.getItems().add(anchor_ForSale);
        }
        if (estate.Schools.size() > 0) {
            grid_School.setItems(estate.Schools);
            splitPane_data.getItems().add(anchor_School);
        }
        if (estate.KV1.size() > 0) {
            grid_KV1.setItems(estate.KV1);
            lbl_KV1.setText(estate.KV1_Title);
            splitPane_data.getItems().add(anchor_KV1);
        }
        if (estate.KV2.size() > 0) {
            grid_KV2.setItems(estate.KV2);
            lbl_KV2.setText(estate.KV2_Title);
            splitPane_data.getItems().add(anchor_KV2);
        }
        if (estate.KV3.size() > 0) {
            grid_KV3.setItems(estate.KV3);
            lbl_KV3.setText(estate.KV3_Title);
            splitPane_data.getItems().add(anchor_KV3);
        }
        splitPane_data.setVisible(splitPane_data.getItems().size() > 0);
        if (splitPane_data.isVisible()) {
            ExtraGrids.refreshSplitPaneDividerPosition(splitPane_data);
        }
    }

    @FXML
    protected void initialize()
    {
        prepareGrids();
        splitPane_data.managedProperty().bind(splitPane_data.visibleProperty());
        splitPane_data.setVisible(false);
    }
}
