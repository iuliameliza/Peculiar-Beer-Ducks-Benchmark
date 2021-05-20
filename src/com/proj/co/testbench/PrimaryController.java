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

    //using an instance of the PrimaryController to get information in Secondary Controller
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

        //adding the partitions to Partition ChoiceBox
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

        //adding sizes to the File Size ChoiceBox
        selectSize.getItems().removeAll(selectSize.getItems());
        selectSize.getItems().addAll("1 MB", "2 MB", "4 MB", "8 MB", "16 MB", "32 MB", "64 MB", "128 MB", "256 MB");
        selectSize.getSelectionModel().selectFirst();

        //adding sizes to the BufferSize ChoiceBox
        //Sequential Write
        selectSeqW.getItems().removeAll();
        selectSeqW.getItems().addAll("1 KB", "2 KB", "4 KB", "8 KB", "16 KB", "32 KB", "64 KB", "128 KB", "256 KB", "512 KB");
        selectSeqW.getSelectionModel().selectFirst();

        //Sequential Read
        selectSeqR.getItems().removeAll();
        selectSeqR.getItems().addAll("1 KB", "2 KB", "4 KB", "8 KB", "16 KB", "32 KB", "64 KB", "128 KB", "256 KB", "512 KB");
        selectSeqR.getSelectionModel().selectFirst();

        //Random Write
        selectRndW.getItems().removeAll();
        selectRndW.getItems().addAll("4 KB", "8 KB", "16 KB", "32 KB", "64 KB", "128 KB", "256 KB", "512 KB");
        selectRndW.getSelectionModel().selectFirst();

        //Random Read
        selectRndR.getItems().removeAll();
        selectRndR.getItems().addAll("4 KB", "8 KB", "16 KB", "32 KB", "64 KB", "128 KB", "256 KB", "512 KB");
        selectRndR.getSelectionModel().selectFirst();

        //handling the Run Button
        runButton.setOnMouseClicked(event -> {
            loading.setText("Loading... Please wait!");
            progress.setVisible(true);

            Task<Void> runBenchTask = new Task<Void>() {
                @Override
                protected Void call() {
                    runBenchmark();
                    return null;
                }
            };

            runBenchTask.setOnSucceeded(evt -> changeTheScene());

            new Thread(runBenchTask).start();
        });
    }

    private int stringToInt(String text){
        String[] arrayOfStrings= text.split(" ");
        return Integer.parseInt(arrayOfStrings[0]);
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

    public void runBenchmark() {
        size = convertSizeToLong();
        partition = selectPartition.getValue();

        //SEQUENTIAL WRITING SPEED
        IBenchmark sequentialWrite = new HDDSequentialWriteSpeed();
        sequentialWrite.initialize(partition, size, stringToInt(selectSeqW.getValue()));
        sequentialWrite.warmup();
        sequentialWrite.run("fs", false);
        seqWrite = sequentialWrite.getResult();

        //SEQUENTIAL READING SPEED
        IBenchmark sequentialRead = new HDDSequentialReadSpeed();
        sequentialRead.initialize(partition, size, stringToInt(selectSeqR.getValue()));
        sequentialRead.warmup();
        sequentialRead.run();
        seqRead= sequentialRead.getResult();

        //RANDOM WRITING SPEED
        IBenchmark randomWrite = new HDDRandomWriteSpeed();
        randomWrite.initialize(partition, size, stringToInt(selectRndW.getValue()));
        randomWrite.warmup();
        randomWrite.run();
        randWrite= randomWrite.getResult();

        //RANDOM READING SPEED
        IBenchmark randomRead = new HDDRandomReadSpeed();
        randomRead.initialize(partition, size, stringToInt(selectRndR.getValue()));
        randomRead.warmup();
        randomRead.run();
        randomRead.clean();
        randRead= randomRead.getResult();
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
