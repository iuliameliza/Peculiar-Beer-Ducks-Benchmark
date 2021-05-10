package com.proj.co.benchmark.HDD;

import com.proj.co.timing.Timer;

import javax.swing.filechooser.FileSystemView;
import java.io.File;


public class HDDSequentialWrite implements IBenchmark {
    private String PATH;
    private String fileExtension;
    private long fileSize, filesToWrite;
    private double writeSpeed;
    private Timer timer = new Timer();

    @Override
    public void initialize(Object... params) {
        String partition = (String) params[0];
        fileSize = (Long) params[1] * (1024 * 1024);
        filesToWrite = 10;

        // write the path according to the operating system
        String home = System.getProperty("user.home");
        java.nio.file.Path osSpecificPath = java.nio.file.Paths.get(home, "HDDBenchmark",
                "SEQ", "write-");
        PATH = partition + osSpecificPath;
        System.out.println(PATH);
        fileExtension = ".dat";
    }

    @Override
    public void warmup() {

    }

    @Override
    public void run(Object... params) {

    }

    @Override
    public void clean() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public String getResult() {
        return null;
    }

}
