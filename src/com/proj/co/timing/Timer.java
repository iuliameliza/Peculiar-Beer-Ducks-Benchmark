package com.proj.co.timing;

public class Timer implements ITimer{
    private long totalTime;
    private long elapsedTime;

    @Override
    public void start() {
        totalTime = 0;
        elapsedTime = System.nanoTime();
    }

    @Override
    public long stop() {
        // compute it first to ensure a bigger accuracy
        long temp = (System.nanoTime() - elapsedTime) + totalTime;

        // if stop() is called twice or before starting, return zero
        // this is achieved by making sure than elapsed time is zero
        // on a second call of stop or on a first call of stop
        if(elapsedTime == 0) {
            return 0;
        }

        // set elapsed time to zero for safety
        elapsedTime = 0;

        return temp;
    }

    @Override
    public void resume() {
        elapsedTime = System.nanoTime();
    }

    @Override
    public long pause() {

        long relativeTime = System.nanoTime() - elapsedTime;
        // update totalTime
        totalTime += relativeTime;
        return relativeTime;
    }
}
