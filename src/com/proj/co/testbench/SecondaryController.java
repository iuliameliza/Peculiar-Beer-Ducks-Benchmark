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
    @FXML
    private Label type;
    @FXML
    private Label os;
    @FXML
    private Label ram;
    @FXML
    private Label cpu;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //setting the title (selected partition and File Size
        title.setText("Partition: " + PrimaryController.getInstance().getPartition() + "\nFile Size: " + PrimaryController.getInstance().getSize() + " MB");

        //setting the computer information
        type.setText("Laptop HP G6");
        os.setText("Windows 10 Pro");
        cpu.setText("Intel(R) core(TM) i7-8 CPU @ 2.60GHz");
        ram.setText("16 GB");

        //setting the table with the corresponding results
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
