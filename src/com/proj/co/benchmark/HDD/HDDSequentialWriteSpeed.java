package com.proj.co.benchmark.HDD;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;


public class HDDSequentialWriteSpeed implements IBenchmark {
    private String PATH;
    private String fileExtension;
    private long fileSize;
    private int filesToWrite;
    private double result;

    @Override
    public void initialize(Object... params) {
        String partition;
        fileSize = (Long) params[1] * (1024 * 1024);
        filesToWrite = 10;

        // For now, if I am in Linux, I will write to the home folder
        // If I am in windows, I will write in either partition
        if(System.getProperty("os.name").equals("Linux")) {
            partition = FileSystemView.getFileSystemView().getHomeDirectory().toString();
        } else {
            partition = (String) params[0];
        }

        // write the path according to the operating system
        java.nio.file.Path osSpecificPath = java.nio.file.Paths.get(partition, "HDDBenchmark",
                "SEQ", "write-");
        PATH = osSpecificPath.toString();
        fileExtension = ".dat";
    }

    @Override
    public void warmup() {
        FileWriter fr = new FileWriter();
        int myBufferSize = 1024 * 1024 * 6; //4 MB

        try {
            fr.streamWriteFixedSize(PATH, fileExtension, 0, 6, myBufferSize, true);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param options <br>
     *                options[0] can be either "fs" - fixed size or "fb" - fixed buffer <br>
     *                options[1] can be true if the files written should be deleted, or false
     *                if they should be kept
     */
    @Override
    public void run(Object... options) {
        // reset the writeSpeed
        result = 0;

        FileWriter writer = new FileWriter();
        // either "fs" - fixed size, or "fb" - fixed buffer
        String option = (String) options[0];

        // true/false whether the written files should be deleted at the end
        Boolean clean = (Boolean) options[1];

        String prefix = PATH;
        String suffix = ".dat";
        int startIndex = 0;
        int endIndex = filesToWrite;
        int bufferSize = (int)fileSize/ 1024; // 4 KB

        // Check that the option sent is valid, if not just put the "fs" option
        if(!(option.equals("fb") || option.equals("fs")))
            option = "fs";

        try {
            if (option.equals("fs"))
                writer.streamWriteFixedSize(prefix, suffix, startIndex,
                        endIndex, fileSize, clean);
            else if (option.equals("fb"))
                writer.streamWriteFixedBuffer(prefix, suffix, startIndex,
                        endIndex, bufferSize, clean);
            else
                throw new IllegalArgumentException("Argument "
                        + options[0].toString() + " is undefined");


            // get the result of the benchmark
            result = writer.getBenchScore();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void clean() {
        // Here, the created folder has to be deleted
        String temp_directory = PATH.substring(0, PATH.length() - "/SEQ/write-".length());
        File dir = new File(temp_directory);

        // Call a recursive method to delete all the files in the directory
        // I know that the folders should be empty, but it is not so bad to check
        // and delete everything
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
        return nf.format(result) + "MB/S";
    }
}
