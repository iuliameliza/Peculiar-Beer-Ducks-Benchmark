package com.proj.co.benchmark.HDD;

import com.proj.co.timing.TimeUnit;
import com.proj.co.timing.Timer;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

public class HDDSequentialReadSpeed implements IBenchmark{
    private String PATH;
    private long fileSize;
    private int filesRead;
    private final int filesToRead = 10;
    private double result;
    private double readSpeed;
    private Timer timer = new Timer();

    @Override
    public void initialize(Object... params) {
        fileSize = (Long) params[1] * (1024 * 1024);
        filesRead = 0;
        String partition;

        // For now, if I am in Linux, I will write to the home folder
        // If I am in windows, I will write in either partition
        if(System.getProperty("os.name").equals("Linux")) {
            partition = FileSystemView.getFileSystemView().getHomeDirectory().toString();
        } else {
            partition = (String) params[0];
        }

        // read the path formatted according to the OS
        java.nio.file.Path osSpecificPath = java.nio.file.Paths.get(partition, "HDDBenchmark", "SEQ");
        PATH = osSpecificPath.toString();

        System.out.println(PATH);
    }

    @Override
    public void warmup() {
        // In the warmup I read a maximum of 3 files
        try {
            readFiles(10);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(Object... params) {
        // Reset the result and the readSpeed
        result = 0;
        readSpeed = 0;
        filesRead = 0;

        try {
            readFiles(filesToRead);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFiles(int maxNbFiles) throws IOException {
        int myBufferSize = 1024; // 1 KB
        File dir = new File(PATH);

        for(File file: Objects.requireNonNull(dir.listFiles())) {
            if(maxNbFiles == 0) {
                break;
            }

            FileInputStream fis = null;

            // Try to open the file from the directory
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            byte[] buff = new byte[myBufferSize];
            long toRead = fileSize / myBufferSize;

            int i = 0;

            timer.start();

            if(fis  != null) {
                while(i < toRead) {
                    fis.read(buff);
                    i++;
                }
            }

            // update the score of the read HDD benchmark
            updateStats(fileSize);

            Objects.requireNonNull(fis).close();

            myBufferSize *= 2;
            filesRead++; // the numbers of files that were read increased
            maxNbFiles--;
        }

    }

    private void updateStats(long totalBytes) {
        final long time = timer.stop();
        TimeUnit timeUnit = TimeUnit.Sec;
        double seconds = TimeUnit.toTimeUnit(time, timeUnit); // calculated from timer's 'time'
        double megabytes = totalBytes / (1024.0 * 1024);
        double rate = megabytes / seconds; // calculated from the previous two variables

        // actual score is write speed (MB/s)
        readSpeed += rate;
    }

    @Override
    public void clean() {
        // Here, the created folder has to be deleted
        String temp_directory = PATH.substring(0, PATH.length() - "/SEQ/write-".length());
        File dir = new File(temp_directory);

        // Call a recursive method to delete all the files in the directory
        clean(dir);
        dir.delete();
    }

    private void clean(File file) {
        for (File subFile : Objects.requireNonNull(file.listFiles())) {

            if (subFile.isDirectory()) {
                clean(subFile);
            }

            subFile.delete();
        }
    }

    @Override
    public String getResult() {
        NumberFormat nf = new DecimalFormat("#.00");
        result = readSpeed / filesRead;
        return nf.format(result) + "MB/S";
    }

}
