package com.proj.co.testbench;

import com.proj.co.specs.SystemInformation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    private Label titlePartition;
    @FXML
    private Label titleSize;
    @FXML
    private Label type;
    @FXML
    private Label os;
    @FXML
    private Label ram;
    @FXML
    private Label cpu;
    @FXML
    private ImageView joben;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //setting the title (selected partition and File Size
        titlePartition.setText(PrimaryController.getInstance().getPartition());
        titleSize.setText(PrimaryController.getInstance().getSize() + " MB");

        SystemInformation si = new SystemInformation();

        //setting the computer information
        type.setText(si.getCompModel());
        os.setText(si.getOsType());
        cpu.setText(si.getCpuType());
        ram.setText(si.getRamTotalSize());

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
