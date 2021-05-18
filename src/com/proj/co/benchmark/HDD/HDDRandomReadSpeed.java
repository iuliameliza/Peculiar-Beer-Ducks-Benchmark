package com.proj.co.benchmark.HDD;

import java.io.File;
import java.util.Objects;

public class HDDRandomReadSpeed implements IBenchmark{
    @Override
    public void initialize(Object... params) {

    }

    @Override
    public void warmup() {

    }

    @Override
    public void run(Object... params) {

    }

    @Override
    public void clean() {
//        // Here, the created folder has to be deleted
//        String temp_directory = PATH.substring(0, PATH.length() - "/RND".length());
//
//        File dir = new File(temp_directory);
//
//        // Call a recursive method to delete all the files in the directory
//        clean(dir);
//        dir.delete();
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
        return null;
    }

//    public static void main(String[] args) {
//        HDDRandomReadSpeed rrs = new HDDRandomReadSpeed();
//        rrs.initialize();
//        rrs.warmup();
//        rrs.run();
//    }
}
