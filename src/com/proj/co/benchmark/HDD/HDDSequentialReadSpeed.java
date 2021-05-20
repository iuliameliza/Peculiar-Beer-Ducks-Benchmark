package com.proj.co.benchmark.HDD;

import com.proj.co.timing.TimeUnit;
import com.proj.co.timing.Timer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

public class HDDSequentialReadSpeed implements IBenchmark{
    private String PATH;
    private long fileSize;
    private int filesRead;
    private double result;
    private double readSpeed;
    private int bufferSize;
    private Timer timer = new Timer();

    @Override
    public void initialize(Object... params) {
        bufferSize= (int) params[2] * 1024;
        fileSize = (Long) params[1] * (1024 * 1024); // MB
        filesRead = 0;
        String partition = (String) params[0];

        // read the path formatted according to the OS
        java.nio.file.Path osSpecificPath = java.nio.file.Paths.get(partition, "HDDBenchmark", "SEQ");
        PATH = osSpecificPath.toString();
    }

    @Override
    public void warmup() {
        // grab a Snickers
    }

    @Override
    public void run(Object... params) {
        // Reset the result and the readSpeed
        result = 0;
        readSpeed = 0;
        filesRead = 0;

        try {
            readFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFiles() throws IOException {
        File dir = new File(PATH);

        for(File file: Objects.requireNonNull(dir.listFiles())) {
            InputStream fis = null;

            // Try to open the file from the directory
            try {
                fis = Files.newInputStream(Paths.get(file.getAbsolutePath()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            byte[] buff = new byte[Math.toIntExact(bufferSize)];
            long toRead = fileSize / bufferSize;

            int i = 0;

            timer.start();

            if(fis  != null) {
                while(i < toRead) {
                    fis.read(buff);
                    i++;
                }
            }

            // update the score of the read HDD benchmark
            final long time = timer.stop();
            TimeUnit timeUnit = TimeUnit.Sec;
            double seconds = TimeUnit.toTimeUnit(time, timeUnit); // calculated from timer's 'time'
            double megabytes = fileSize / (1024.0 * 1024);
            double rate = megabytes / seconds; // calculated from the previous two variables

            // actual score is write speed (MB/s)
            readSpeed += rate;

            Objects.requireNonNull(fis).close();

            filesRead++; // the numbers of files that were read increased
        }
    }

    @Override
    public void clean() {
        // Here, the created folder has to be deleted
        String temp_directory = PATH.substring(0, PATH.length() - "/SEQ".length());

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

        return nf.format(result) + " MB/S";
    }
}
