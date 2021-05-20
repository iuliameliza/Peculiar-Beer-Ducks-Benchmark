package com.proj.co.testbench;

import com.proj.co.benchmark.HDD.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrimaryController implements Initializable {
    @FXML
    private ChoiceBox<String> selectPartition, selectSize;
    @FXML
    private Button runButton;
    @FXML
    private Label loading;
    @FXML
    private ProgressIndicator progress;

    private String seqWrite, seqRead, randWrite, randRead;

    private Thread t;

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
        Long size = convertSizeToLong();
        String partition = selectPartition.getValue();

        //SEQUENTIAL WRITING SPEED
        IBenchmark sequentialWrite = new HDDSequentialWriteSpeed();
        sequentialWrite.initialize(partition, size);
        sequentialWrite.warmup();
        sequentialWrite.run("fs", false);
        //sequentialWrite.clean();
        seqWrite = sequentialWrite.getResult();
        System.out.println("sw");

        //SEQUENTIAL READING SPEED
        IBenchmark sequentialRead = new HDDSequentialReadSpeed();
        sequentialRead.initialize(partition, size);
        sequentialRead.warmup();
        sequentialRead.run();
        //sequentialRead.clean();
        seqRead= sequentialRead.getResult();
        System.out.println("sr");

        //RANDOM WRITING SPEED
        IBenchmark randomWrite = new HDDRandomWriteSpeed();
        randomWrite.initialize(partition, size);
        randomWrite.warmup();
        randomWrite.run();
        // randomWrite.clean();
        randWrite= randomWrite.getResult();
        System.out.println("rw");

        //RANDOM READING SPEED
        IBenchmark randomRead = new HDDRandomReadSpeed();
        randomRead.initialize(partition, size);
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

}
