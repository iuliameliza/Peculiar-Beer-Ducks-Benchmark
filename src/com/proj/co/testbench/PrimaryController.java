package com.proj.co.testbench;

import com.proj.co.benchmark.HDD.HDDSequentialWriteSpeed;
import com.proj.co.benchmark.HDD.IBenchmark;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable {
    @FXML
    private ChoiceBox<String> selectPartition, selectSize;

    private String seqWrite, seqRead, RandWrite, randRead;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectPartition.getItems().removeAll(selectPartition.getItems());

        String partitionLetter;
        File[] drives = File.listRoots();
        if (drives != null && drives.length > 0) {
            for (File aDrive : drives) {
                partitionLetter= aDrive.toString().replace("\\", "");
                if( aDrive.canWrite() )
                    selectPartition.getItems().add(partitionLetter);
            }
        }
        selectPartition.getSelectionModel().selectFirst();

        selectSize.getItems().removeAll(selectSize.getItems());
        selectSize.getItems().addAll("25 MB", "50 MB", "100 MB", "250 MB");
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
        sequentialWrite.run("fs", true);
        sequentialWrite.clean();
        seqWrite = sequentialWrite.getResult();

        //change scene
        Main m = new Main();
        m.changeScene("/secondary.fxml", "Peculiar Beer Ducks", seqWrite);
    }

    public String getRandRead() {
        return randRead;
    }

    public String getRandWrite() {
        return RandWrite;
    }

    public String getSeqRead() {
        return seqRead;
    }

    public String getSeqWrite() {
        return seqWrite;
    }
}
