package com.proj.co.timing;

public interface ITimer {
    /**
     * Save the relative elapsed time in a long variable <br>
     * Reset the previously stored time
     */
    void start();

    /**
     * The stop method
     * @return Return elapsed time since the start of timer
     */
    long stop();

    /**
     * Save the relative elapsed time in a variable, without any reset
     */
    void resume();

    /**
     * The pause method
     * @return Return elapsed time since the last start/resume
     */
    long pause();
}
