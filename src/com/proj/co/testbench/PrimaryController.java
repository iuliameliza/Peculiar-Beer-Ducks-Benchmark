package com.proj.co.testbench;

import com.proj.co.benchmark.HDD.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable {
    @FXML
    private ChoiceBox<String> selectPartition, selectSize;
    @FXML
    private ChoiceBox<String> selectSeqW, selectSeqR;
    @FXML
    private ChoiceBox<String> selectRndW, selectRndR;
    @FXML
    private Button runButton;
    @FXML
    private Label loading;
    @FXML
    private ProgressIndicator progress;

    private String seqWrite;
    private String seqRead;
    private String randWrite;
    private String randRead;
    private String partition;
    private Long size;

    private static PrimaryController instance;

    public PrimaryController(){
        instance= this;
    }

    public static PrimaryController getInstance(){
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        progress.setVisible(false);
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

        selectSeqW.getItems().removeAll();
        selectSeqW.getItems().addAll("1 kib", "2 kib", "4 kib", "8 kib", "16 kib", "32 kib", "64 kib", "128 kib", "256 kib", "512 kib");
        selectSeqW.getSelectionModel().selectFirst();

        selectSeqR.getItems().removeAll();
        selectSeqR.getItems().addAll("1 kib", "2 kib", "4 kib", "8 kib", "16 kib", "32 kib", "64 kib", "128 kib", "256 kib", "512 kib");
        selectSeqR.getSelectionModel().selectFirst();

        selectRndW.getItems().removeAll();
        selectRndW.getItems().addAll("4 kib", "8 kib", "16 kib", "32 kib", "64 kib", "128 kib", "256 kib", "512 kib");
        selectRndW.getSelectionModel().selectFirst();

        selectRndR.getItems().removeAll();
        selectRndR.getItems().addAll("4 kib", "8 kib", "16 kib", "32 kib", "64 kib", "128 kib", "256 kib", "512 kib");
        selectRndR.getSelectionModel().selectFirst();

        runButton.setOnMouseClicked(event -> {
            loading.setText("Loading... Please wait!");
            progress.setVisible(true);

            Task runBenchTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    handleRunButton();
                    return null;
                }
            };

            runBenchTask.setOnSucceeded(evt -> {
                changeTheScene();
            });

            new Thread(runBenchTask).start();
        });
    }

    private void changeTheScene() {
        try {
            Main.getInstance().changeScene("/secondary.fxml", "Peculiar Beer Ducks");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long convertSizeToLong(){
        String[] arrayOfStrings= selectSize.getValue().split(" ");
        return Long.parseLong(arrayOfStrings[0]);
    }

    public void handleRunButton() {
        size = convertSizeToLong();
        partition = selectPartition.getValue();
        System.out.println("aaaaaaaaaaa");

        //SEQUENTIAL WRITING SPEED
        IBenchmark sequentialWrite = new HDDSequentialWriteSpeed();
        sequentialWrite.initialize(partition, size, selectSeqW.getValue());
        sequentialWrite.warmup();
        sequentialWrite.run("fs", false);
        //sequentialWrite.clean();
        seqWrite = sequentialWrite.getResult();
        System.out.println("sw");

        //SEQUENTIAL READING SPEED
        IBenchmark sequentialRead = new HDDSequentialReadSpeed();
        sequentialRead.initialize(partition, size, selectSeqR.getValue());
        sequentialRead.warmup();
        sequentialRead.run();
        //sequentialRead.clean();
        seqRead= sequentialRead.getResult();
        System.out.println("sr");

        //RANDOM WRITING SPEED
        IBenchmark randomWrite = new HDDRandomWriteSpeed();
        randomWrite.initialize(partition, size, selectRndW.getValue());
        randomWrite.warmup();
        randomWrite.run();
        // randomWrite.clean();
        randWrite= randomWrite.getResult();
        System.out.println("rw");

        //RANDOM READING SPEED
        IBenchmark randomRead = new HDDRandomReadSpeed();
        randomRead.initialize(partition, size, selectRndR.getValue());
        randomRead.warmup();
        randomRead.run();
        randomRead.clean();
        randRead= randomRead.getResult();
        System.out.println("rr");
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

    public String getPartition() {
        //the Linux does not have a partition ending with ':' so we check to see what String we send
        if( partition.charAt(partition.length()-1) == ':' )
            return partition.substring(0, partition.length()-1);
        return partition;
    }

    public Long getSize() {
        return size;
    }

}
