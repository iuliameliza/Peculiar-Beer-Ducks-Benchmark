package com.proj.co.benchmark.HDD;

import com.proj.co.timing.TimeUnit;
import com.proj.co.timing.Timer;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.Random;

public class HDDRandomReadSpeed implements IBenchmark{
    private static String PATH;
    private static String tmpPath;
    private long fileSize;
    private int bufferSize;
    private int fileCounter;
    private double readSpeed;

    @Override
    public void initialize(Object... params) {
        bufferSize= (int) params[2] * 1024;
        String partition = (String) params[0];
        fileSize = (Long) params[1] * (1024 * 1024);  // MB
        fileCounter = 0;

        // write the path according to the operating system
        java.nio.file.Path osSpecificPath = java.nio.file.Paths.get(partition, "HDDBenchmark",
                "RND");
        PATH = osSpecificPath.toString();

        // If there is not a folder already, create it
        File folderPath = new File(PATH.substring(0, PATH.lastIndexOf(File.separator)));
        tmpPath = PATH + "/test.dat";

        if (!folderPath.isDirectory())
            folderPath.mkdirs();
    }

    @Override
    public void warmup() {
        // grab a Snickers
    }

    @Override
    public void run(Object... params) {
        File dir = new File(PATH);

        try {
            for(File file: Objects.requireNonNull(dir.listFiles())) {

                randomReadFixedSize(file.getAbsolutePath());
                // increment the number of files that were read
                fileCounter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads data from random positions into a fixed size buffer from a
     * given file using RandomAccessFile
     *
     * @param filePath
     *            Full path to file on disk
     * @throws IOException
     */
    public void randomReadFixedSize(String filePath) throws IOException {
        TimeUnit timeUnit = TimeUnit.Sec;
        RandomAccessFile file = new RandomAccessFile(filePath, "r"); // file to read from
        Timer timer = new Timer();
        Random rand = new Random();
        long totalBytes = 0;
        int rLoc;
        long fileSize = (long) (file.getChannel().size() % Long.MAX_VALUE); // size of file
        int noOfBlocks = (int) fileSize / bufferSize; // counter for number of reads
        byte[] bytes = new byte[bufferSize]; // buffer for reading
        int counter = 0;


        timer.start();
        while (counter++ < noOfBlocks) {
            // go to random spot in file
            rLoc = rand.nextInt(noOfBlocks - 1);
            file.seek((long) rLoc * bufferSize);

            // read the bytes into buffer
            file.readFully(bytes, 0, bufferSize);
            totalBytes += bufferSize;
        }
        long stopped = timer.stop();

        file.close();

        double seconds = TimeUnit.toTimeUnit(stopped, timeUnit); // calculated from timer's 'time'
        double megabytes = (double) (totalBytes) / (1024.0 * 1024);

        double res = megabytes / seconds;
        readSpeed += res;
    }

    @Override
    public void clean() {
        // Here, the created folder has to be deleted
        String temp_directory = PATH.substring(0, PATH.length() - "/RND".length());

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
        double result = readSpeed / fileCounter;

        return nf.format(result) + "MB/S";
    }
}
