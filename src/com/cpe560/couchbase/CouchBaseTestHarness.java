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
    private int messagesPerSecond;
    private int iterations;
    private String documentName;
    private String viewName;
    private int groupLevel;
    private boolean group;
    private boolean reduce;
    private List<URI> uris;
    private String outputFilename;
    private String outputContent;

    public static CouchBaseTestHarness createCouchBaseTestHarness(String filename) {
        CouchBaseTestHarness cbth = new CouchBaseTestHarness();
        cbth.init(filename);
        return cbth;
    }

    private void init(String filename) {
        try {
            FileReader f = new FileReader(filename);
            Gson gson = new Gson();
            CouchBaseConfiguration data = gson.fromJson(f, CouchBaseConfiguration.class);
            messagesPerSecond = data.getMessagesPerSecond();
            iterations = data.getIterations();
            viewName = data.getViewName();
            documentName = data.getDocumentName();
            group = data.getSetGroup();
            reduce = data.getSetReduce();
            uris = data.getUris();
            outputFilename = data.getOutputFilename();
            outputContent = "";
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        outputContent += "Messages Per Second: " + messagesPerSecond + "\n";
        outputContent += "Number of Iterations: " + iterations + "\n";

    }

    public void run() {        
        int totalCount = 0;
        CouchbaseClient client = null;

        // Establish connection with CouchBase Server
        try {
            CouchbaseConnectionFactory cf = new CouchbaseConnectionFactory(uris, "default", "");
            client = new CouchbaseClient((CouchbaseConnectionFactory) cf);
        } catch (Exception e) {
            System.err.println("Error connecting to Couchbase: "
                + e.getMessage());
            System.exit(0);
        }

        CountDownLatch countDownLatch = new CountDownLatch (iterations * messagesPerSecond);

        Map<Integer, Long> callTimes = dispatchEvents(countDownLatch, client);

        // Wait for all request threads to finish.
        try {
            countDownLatch.await();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        printResults(callTimes);
        client.shutdown(3, TimeUnit.SECONDS);
    }

    private Map<Integer, Long> dispatchEvents(CountDownLatch countDownLatch, CouchbaseClient client) {
        Map<Integer, Long> callTimes = new ConcurrentHashMap<Integer, Long>();

        try {
            View view = client.getView(documentName, viewName);
            Query query = new Query();
            query.setGroup(group);
            query.setGroupLevel(groupLevel);
            query.setReduce(reduce);            

            for (int rt = 0; rt < iterations; rt++) {
                long start = System.currentTimeMillis();
                for (int i = 0; i < messagesPerSecond; i++) {
                    RequestThread requestThread = new RequestThread(countDownLatch, query, client, view, callTimes, (rt * messagesPerSecond) + i);
                }
                long finish = System.currentTimeMillis();
                long difference = finish - start;
                if (difference > 1000) {
                    System.out.println("More than 1 second to dispatch request threads");
                } else {
                    Thread.sleep(1000 - (finish - start));
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
            FileWriter fstream = new FileWriter(outputFilename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(outputContent);
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // public static void main(String[] args) {

    //     if (args.length != 1) {
    //         System.out.println("Usage");
    //         System.exit(2);
    //     }
    //     CouchBaseTestHarness cbth = CouchBaseTestHarness.createCouchBaseTestHarness(args[0]);
    //     cbth.run();
    //     System.exit(0);
    // }
}