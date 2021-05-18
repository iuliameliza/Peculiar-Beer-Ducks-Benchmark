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
    private static String tmpPath;
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
        tmpPath = PATH.substring(0, PATH.length() - "write-".length()) + "test.dat";

        System.out.println(tmpPath);

        if (!folderPath.isDirectory())
            folderPath.mkdirs();

        File tempFile = new File(tmpPath);
        RandomAccessFile rafFile;

        // Create a temporary file with random content to be used for
        // writing
        try {
            rafFile = new RandomAccessFile(tempFile, "rw");
            Random rand = new Random();
            long toWrite = fileSize / bufferSize;
            byte[] buffer = new byte[bufferSize];
            long counter = 0;

            while (counter++ < toWrite) {
                rand.nextBytes(buffer);
                rafFile.write(buffer);
            }
            rafFile.close();
            tempFile.deleteOnExit();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void warmup() {

    }

    @Override
    public void run(Object... params) {
        int fileIndex = 0;
        String fileName;
        TimeUnit timeUnit = TimeUnit.Sec;

        try {
            while (fileCounter < filesToWrite) {
                fileName = PATH + fileIndex + fileExtension;

                long writeTime = randomWriteFixedSize(tmpPath, fileName);
                double seconds = TimeUnit.toTimeUnit(writeTime, timeUnit); // calculated from timer's 'time'
                double megabytes = fileSize / (1024.0 * 1024);
                double rate = megabytes / seconds; // calculated from the previous two variables

                fileCounter++;
                fileIndex++;
                writeSpeed += rate;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long randomWriteFixedSize(String filePath, String fileToWriteInto) throws IOException{
        // file to read from
        RandomAccessFile file = new RandomAccessFile(filePath, "rw");
        // counter for number of reads
        long counter = 0;
        // buffer for reading
        byte[] bytes = new byte[bufferSize];
        // timer
        Timer timer = new Timer();

        Random rand = new Random();

        long toWrite = fileSize / bufferSize;

        timer.start();

        while (counter++ < toWrite) {
            rand.nextBytes(bytes);

            // write the bytes into file
            writeToFile(fileToWriteInto, bytes, rand.nextInt((int) fileSize));
        }

        long stopped = timer.stop();

        file.close();
        return stopped;
    }

    /**
     * Write data to a file at a specific position
     *
     * @param filePath
     *            Path to file
     * @param data
     *            Data to be written
     * @param position
     *            Start position in file
     * @throws IOException
     */
    public void writeToFile(String filePath, byte[] data, int position) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filePath, "rw");
        file.seek(position);
        file.write(data);
        file.close();
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
