package com.proj.co.benchmark.HDD;

import com.proj.co.timing.TimeUnit;
import com.proj.co.timing.Timer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class FileWriter {
    private static final int MIN_BUFFER_SIZE = 4*1024;; // KB
    private static final int MAX_BUFFER_SIZE = 1024 * 1024 * 32; // MB
    private static final int MIN_FILE_SIZE = 1024 * 1024; // MB
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 512; // MB
    private Timer timer = new Timer();
    private double benchScore;

    /**
     * Writes files on disk using a variable write buffer and fixed file size.
     *
     * @param filePrefix
     *            - Path and file name
     * @param fileSuffix
     *            - file extension
     * @param minIndex
     *            - start buffer size index
     * @param maxIndex
     *            - end buffer size index
     * @param fileSize
     *            - size of the benchmark file to be written in the disk
     * @throws IOException
     */
    public void streamWriteFixedSize(String filePrefix, String fileSuffix,
                                     int minIndex, int maxIndex, long fileSize, boolean clean)
            throws IOException {
        int currentBufferSize = MIN_BUFFER_SIZE;
        String fileName;
        int fileIndex = 0;
        int counter = 0;
        benchScore = 0;

        while (currentBufferSize <= MAX_BUFFER_SIZE && counter <= maxIndex - minIndex) {
            fileName = filePrefix + fileIndex + fileSuffix;

            writeWithBufferSize(fileName, currentBufferSize, fileSize, clean);

            // update buffer size
			currentBufferSize = currentBufferSize * 2;

			// increment the counter
            counter++;

            // increment the index
            fileIndex++;
        }

        benchScore /= (maxIndex - minIndex + 1);
    }

    /**
     * Writes files on disk using a variable file size and fixed buffer size.
     *
     * @param filePrefix
     *            - Path and file name
     * @param fileSuffix
     *            - file extension
     * @param minIndex
     *            - start file size index
     * @param maxIndex
     *            - end file size index
     * @param bufferSize
     *            - size of the benchmark file to be written on the disk
     */
    public void streamWriteFixedBuffer(String filePrefix, String fileSuffix,
                                       int minIndex, int maxIndex, int bufferSize, boolean clean)
            throws IOException {

        int currentFileSize = MIN_FILE_SIZE;
        int counter = 0;
        int fileIndex = 0;
        String fileName;

        while (currentFileSize <= MAX_FILE_SIZE && counter <= maxIndex - minIndex) {
			fileName = filePrefix + fileIndex + fileSuffix;
			fileIndex++;
			writeWithBufferSize(fileName, bufferSize, currentFileSize, clean);
			counter++;
			currentFileSize *= 2;
        }

        benchScore /= (maxIndex - minIndex + 1);
    }

    /**
     * Writes a file with random binary content on the disk, using a given file
     * path and buffer size.
     */
    private void writeWithBufferSize(String fileName, int myBufferSize, long fileSize, boolean clean)
            throws IOException {
        File folderPath = new File(fileName.substring(0, fileName.lastIndexOf(File.separator)));

        // create folder path to benchmark output
        if (!folderPath.isDirectory())
            folderPath.mkdirs();


        final FileOutputStream file = new FileOutputStream(fileName);
        // create stream writer with given buffer size
        final BufferedOutputStream outputStream = new BufferedOutputStream(file , myBufferSize);

        byte[] buffer = new byte[myBufferSize];
        int i = 0;
        long toWrite = fileSize / myBufferSize;
        Random rand = new Random();

        timer.start();
        while (i < toWrite) {
            // generate random content to write
            rand.nextBytes(buffer);

            outputStream.write(buffer);
            i++;
        }
        updateStats(fileSize);

        outputStream.close();
        if(clean) {
            new File(fileName).delete();
        }
    }

    private void updateStats(long totalBytes) {
        final long time = timer.stop();
        TimeUnit timeUnit = TimeUnit.Sec;
        double seconds = TimeUnit.toTimeUnit(time, timeUnit); // calculated from timer's 'time'
        double megabytes = totalBytes / (1024.0 * 1024);
        double rate = megabytes / seconds; // calculated from the previous two variables

        // actual score is write speed (MB/s)
        benchScore += rate;
    }

    public double getBenchScore() {
        return benchScore;
    }
}
