package zillowbot.Util;

import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ExtraGrids {

    public static double getTableViewColumnsWith(TableView grid) {
        double result = 0;
        for (int i=0;i<grid.getColumns().size();i++) {
            Object column = grid.getColumns().get(i);
            result +=((TableColumn)column).getWidth();
        }
        return result;
    }

    public static void refreshSplitPaneDividerPosition(SplitPane splitPane) {
        int size = splitPane.getDividers().size();
        List<Double> widths = new ArrayList<>();
        Double total = new Double(0);
        for (javafx.scene.Node n0 : splitPane.getItems()) {
            if (n0 instanceof AnchorPane) {
                AnchorPane anchorPane = (AnchorPane) n0;
                for (javafx.scene.Node n1 : anchorPane.getChildren()) {
                    if (n1 instanceof VBox) {
                        VBox vBox = (VBox) n1;
                        for (javafx.scene.Node n2 : vBox.getChildren()) {
                            if (n2 instanceof TableView) {
                                TableView t = (TableView) n2;
                                Double w = getTableViewColumnsWith(t);
                                total += w;
                                widths.add(w);

                            }
                        }
                    }
                }
            }
        }

        double[] pos = new double[size];
        /*for (int i = 1; i <= size; i++) {
            pos[i-1] = i/(double)(size+1);
        }*/
        double prev = 0;
        for (int i = 0; i < size; i++) {
            pos[i] = prev + widths.get(i) / total;
            prev = pos[i];
        }
        splitPane.setDividerPositions(pos);
    }
}
