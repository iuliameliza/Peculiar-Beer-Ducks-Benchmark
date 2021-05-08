package com.proj.co.testbench;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;


public class secondaryController {
    @FXML
    private Label seqWrite;
    @FXML
    private Label seqRead;
    @FXML
    private Label rndWrite;
    @FXML
    private Label rndRead;

    public void handleBackButton() throws IOException{
        Main m= new Main();
        m.changeScene("/primary.fxml","Peculiar Beer Ducks");
    }
}
