package com.proj.co.testbench;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class SecondaryController implements Initializable {
    @FXML
    private Label seqWrite;
    @FXML
    private Label seqRead;
    @FXML
    private Label rndWrite;
    @FXML
    private Label rndRead;
    @FXML
    private Label title;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        title.setText("Partition: " + PrimaryController.getInstance().getPartition() + "\nSize: " + PrimaryController.getInstance().getSize());
        seqWrite.setText(PrimaryController.getInstance().getSeqWrite());
        seqRead.setText(PrimaryController.getInstance().getSeqRead());
        rndWrite.setText(PrimaryController.getInstance().getRandWrite());
        rndRead.setText(PrimaryController.getInstance().getRandRead());
    }

    public void handleBackButton() throws IOException{
        Main m= new Main();
        m.changeScene("/primary.fxml","Peculiar Beer Ducks");
    }
}
