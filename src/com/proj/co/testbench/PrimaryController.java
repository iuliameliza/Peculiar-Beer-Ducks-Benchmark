package com.proj.co.testbench;

import com.proj.co.benchmark.HDD.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable {
    @FXML
    private ChoiceBox<String> selectPartition, selectSize;

    private String seqWrite, seqRead, randWrite, randRead;

    private static PrimaryController instance;

    public PrimaryController(){
        instance= this;
    }

    public static PrimaryController getInstance(){
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectPartition.getItems().removeAll(selectPartition.getItems());

        String partitionLetter;
        File[] drives = File.listRoots();
        if (drives != null && drives.length > 0) {
            for (File aDrive : drives) {
                partitionLetter= aDrive.toString().replace("\\", "");

                if(System.getProperty("os.name").equals("Linux")) {
                    partitionLetter = FileSystemView.getFileSystemView().getHomeDirectory().toString();
                    aDrive = new File(partitionLetter);
                }

                if( aDrive.canWrite() )
                    selectPartition.getItems().add(partitionLetter);
            }
        }
        selectPartition.getSelectionModel().selectFirst();

        selectSize.getItems().removeAll(selectSize.getItems());
        selectSize.getItems().addAll("1 MB", "2 MB", "4 MB", "8 MB", "16 MB", "32 MB", "64 MB", "128 MB", "256 MB");
        selectSize.getSelectionModel().selectFirst();
    }

    private long convertSizeToLong(){
        String[] arrayOfStrings= selectSize.getValue().split(" ");
        return Long.parseLong(arrayOfStrings[0]);
    }

    public void handleRunButton() throws IOException {
        Long size = convertSizeToLong();
        String partition = selectPartition.getValue();

        //SEQUENTIAL WRITING SPEED
        IBenchmark sequentialWrite = new HDDSequentialWriteSpeed();
        sequentialWrite.initialize(partition, size);
        sequentialWrite.warmup();
        sequentialWrite.run("fs", false);
        //sequentialWrite.clean();
        seqWrite = sequentialWrite.getResult();

        //SEQUENTIAL WRITING SPEED
        IBenchmark sequentialRead = new HDDSequentialReadSpeed();
        sequentialRead.initialize(partition, size);
        sequentialRead.warmup();
        sequentialRead.run();
        //sequentialRead.clean();
        seqRead= sequentialRead.getResult();

        //RANDOM WRITING SPEED
        IBenchmark randomWrite = new HDDRandomWriteSpeed();
        randomWrite.initialize(partition, size);
        randomWrite.warmup();
        randomWrite.run();
        // randomWrite.clean();
        randWrite= randomWrite.getResult();

        //RANDOM READING SPEED
        IBenchmark randomRead = new HDDRandomReadSpeed();
        randomRead.initialize(partition, size);
        randomRead.warmup();
        randomRead.run();
        randomRead.clean();
        randRead= randomRead.getResult();

        //change scene
        Main.getInstance().changeScene("/secondary.fxml", "Peculiar Beer Ducks");
    }

    public String getRandRead() {
        return randRead;
    }

    public String getRandWrite() {
        return randWrite;
    }

    public String getSeqRead() {
        return seqRead;
    }

    public String getSeqWrite() {
        return seqWrite;
    }
}
