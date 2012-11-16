package com.cpe560.couchbase;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.couchbase.client.CouchbaseConnectionFactory;
import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.ViewRow;
import com.couchbase.client.protocol.views.ViewResponse;
import net.spy.memcached.internal.GetFuture;
import net.spy.memcached.internal.OperationFuture;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import com.google.gson.Gson;
import com.cpe560.common.Configuration;
import java.io.*;
import java.util.*;

public class CouchBaseTestHarness {
    private String outputContent;
    private CouchBaseConfiguration config;
    private CouchBaseTestHarness() {}

    public static CouchBaseTestHarness createCouchBaseTestHarness(String filename) {
        CouchBaseTestHarness cbth = new CouchBaseTestHarness();
        cbth.init(filename);
        return cbth;
    }

    private void init(String filename) {
        try {
            FileReader f = new FileReader(filename);
            Gson gson = new Gson();
            config = gson.fromJson(f, CouchBaseConfiguration.class);
            outputContent = "";

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        int messagesPerSecond = config.getReadsPerSecond() + config.getWritesPerSecond();

        outputContent += "Messages Per Second: " + messagesPerSecond + "\n";
        outputContent += "Number of Iterations: " + config.getIterations() + "\n";

    }

    public void run() {        
        int iterations = config.getIterations();
        int messagesPerSecond = config.getReadsPerSecond() + config.getWritesPerSecond();
        CountDownLatch countDownLatch = new CountDownLatch (iterations * messagesPerSecond);

        Map<Integer, Long> callTimes = dispatchEvents(countDownLatch);

        // Wait for all request threads to finish.
        try {
            countDownLatch.await();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        printResults(callTimes);
    }

    private Map<Integer, Long> dispatchEvents(CountDownLatch countDownLatch) {
        Map<Integer, Long> callTimes = new ConcurrentHashMap<Integer, Long>();
        int readsPerSecond = config.getReadsPerSecond();
        int writesPerSecond = config.getWritesPerSecond();
        int iterations = config.getIterations();
        int messagesPerSecond = readsPerSecond + writesPerSecond;

        try {
            for (int rt = 0; rt < iterations; rt++) {
                long start = System.currentTimeMillis();
                for (int i = 0; i < readsPerSecond; i++) {
                    new ReadRequestThread(countDownLatch, config, callTimes, (rt * messagesPerSecond) + i);
                }
                for (int i = readsPerSecond; i < messagesPerSecond; i++) {
                    System.out.println("Implement write request thread");
                }
                long finish = System.currentTimeMillis();
                long difference = finish - start;
                System.out.println(difference + " ms to dispatch");
                if (difference > 1000) {                    
                    System.out.println("More than 1 second to dispatch request threads");
                } else {
                    Thread.sleep(1000 - difference);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return callTimes;
    }

    private void printResults(Map<Integer, Long> timePerCall) {
        int time = 0;

        for (Integer i : timePerCall.keySet()) {
            time += timePerCall.get(i);
            outputContent += timePerCall.get(i) + "\n";
        }

        outputContent += "Total Time: " + time;
        try{
            FileWriter fstream = new FileWriter(config.getOutputFilename());
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(outputContent);
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}