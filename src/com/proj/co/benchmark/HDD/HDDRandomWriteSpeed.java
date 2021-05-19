package com.proj.co.benchmark.HDD;

import com.proj.co.timing.TimeUnit;
import com.proj.co.timing.Timer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.Random;

public class HDDRandomWriteSpeed implements IBenchmark{
    private static String PATH;
    private String fileExtension;
    private long fileSize;
    private final long filesToWrite = 10;
    private final int bufferSize = 1024 * 4; // 4 KB
    private int fileCounter;
    private double writeSpeed;

    @Override
    public void initialize(Object... params) {
        String partition = (String) params[0];
        fileSize = (Long) params[1] * (1024 * 1024);  // MB

        // write the path according to the operating system
        java.nio.file.Path osSpecificPath = java.nio.file.Paths.get(partition, "HDDBenchmark",
                "RND", "write-");
        PATH = osSpecificPath.toString();
        fileExtension = ".dat";


        // If there is not a folder already, create it
        File folderPath = new File(PATH.substring(0, PATH.lastIndexOf(File.separator)));

        if (!folderPath.isDirectory())
            folderPath.mkdirs();
    }

    @Override
    public void warmup() {
        // grab a Snickers
    }

    @Override
    public void run(Object... params) {
        int fileIndex = 0;
        String fileName;

        try {
            while (fileCounter < filesToWrite) {
                fileName = PATH + fileIndex + fileExtension;

                randomWriteFixedSize(fileName);

                fileCounter++;
                fileIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void randomWriteFixedSize(String fileToWriteInto) throws IOException{
        RandomAccessFile file = new RandomAccessFile(fileToWriteInto, "rw"); // file to read from
        Timer timer = new Timer(); // timer
        Random rand = new Random();
        TimeUnit timeUnit = TimeUnit.Sec;
        long counter = 0; // counter for number of reads
        byte[] bytes = new byte[bufferSize]; // buffer for reading
        int noOfBlocks = (int) fileSize / bufferSize; // counter for number of reads
        int rLoc;
        long totalBytes = 0;


        timer.start();
        while (counter++ < noOfBlocks) {
            // get some random data
            rand.nextBytes(bytes);
            // go to a random spot in the file
            rLoc = rand.nextInt(noOfBlocks - 1);
            file.seek((long) rLoc * bufferSize);

            // write the bytes into file
            file.write(bytes, 0, bufferSize);
            totalBytes += bufferSize;
        }

        long stopped = timer.stop();

        file.close();

        double seconds = TimeUnit.toTimeUnit(stopped, timeUnit); // calculated from timer's 'time'
        double megabytes = totalBytes / (1024.0 * 1024);
        double rate = megabytes / seconds; // calculated from the previous two variables

        writeSpeed += rate;
    }

    @Override
    public void clean() {
        // Here, the created folder has to be deleted
        String temp_directory = PATH.substring(0, PATH.length() - "/RND/write-".length());
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
        double result = writeSpeed / filesToWrite;

        return nf.format(result) + "MB/S";
    }
}
