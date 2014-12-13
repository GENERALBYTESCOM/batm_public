package com.generalbytes.batm.server.extensions;

/**
 * Created by b00lean on 12/12/14.
 */
public interface ITask {
    public boolean onCreate();
    public boolean onDoStep();
    public void onFinish();
    public boolean isFinished();
    public String getResult();
    public boolean isFailed();
    public long getShortestTimeForNexStepInvocation();
}
