package com.proj.co.testbench;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class primaryController implements Initializable {
    @FXML
    private ChoiceBox<String> selectPartition, selectSize;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectPartition.getItems().removeAll(selectPartition.getItems());
        selectPartition.getItems().addAll("C:\\", "D:\\");
        selectPartition.getSelectionModel().select("C:\\");

        selectSize.getItems().removeAll(selectSize.getItems());
        selectSize.getItems().addAll("20 MB", "50 MB", "100 MB", "250 MB");
        selectSize.getSelectionModel().select("20 MB");
    }

    public void handleRunButton() throws IOException{
        Main m= new Main();
        m.changeScene("/secondary.fxml","Peculiar Beer Ducks");
    }
}
