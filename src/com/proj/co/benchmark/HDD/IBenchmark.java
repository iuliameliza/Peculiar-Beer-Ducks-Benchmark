package com.proj.co.benchmark.HDD;

public interface IBenchmark {
    /**
     * Prepare the data for the run method
     * @param params can be file, path, etc
     */
    void initialize(Object ... params);

    /**
     * This method is used before calling the run method to
     * warm-up the task that is to be performed so that the
     * results are more reliable and accurate.
     */
    void warmup();

    /**
     * This method contains the core of the code that will be used to benchmark a hardware component
     * @param params any number of parameters sent
     */
    void run(Object ... params);

    /**
     * Clean up after the run method
     */
    void clean();

    /**
     * Useful when it is needed to cancel the execution of the program
     */
    void cancel();

    /**
     * @return Returns a string with the result of the benchmark
     */
    String getResult();
}
