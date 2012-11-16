package com.cpe560.common;

public class Configuration {
    public Configuration() {};
    private int iterations;
    private int messagesPerSecond;
    private String outputFilename;

    public int getIterations() { return iterations; }
    public int getMessagesPerSecond() { return messagesPerSecond; }
    public String getOutputFilename() { return outputFilename; }

    public void setIterations(int iterations) { this.iterations = iterations; }
    public void setMessagesPerSecond(int messagesPerSecond) { this.messagesPerSecond = messagesPerSecond; }
    public void setOutputFilename(String filename) { this.outputFilename = filename; }
}