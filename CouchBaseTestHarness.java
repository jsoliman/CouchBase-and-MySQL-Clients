package couchbase;

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

import java.io.*;
import java.util.*;

class CouchBaseTestHarness {
    // Arguments
    // Bucket name
    // View name
    //  View args
    //  -g <value>: setGroup(true), setGroupLevel(<value>)
    //  -r: setReduce
    //  -i: iterations
    public static void main(String[] args) {
        List<URI> uris = new ArrayList<URI>();
        Map<Integer, Long> callTimes = new ConcurrentHashMap<Integer, Long>();
        int totalCount = 0;
        // Add server 
        uris.add(URI.create("http://10.160.139.71:8091/pools"));
        uris.add(URI.create("http://10.160.29.63:8091/pools"));
        uris.add(URI.create("http://10.160.45.147:8091/pools"));
        uris.add(URI.create("http://10.168.183.229:8091/pools"));

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

        int runTimes = 5;
        int numIterations = 5;

        CountDownLatch countDownLatch = new CountDownLatch (runTimes * numIterations);

        try {
            View view = client.getView("deploy", "trending_over_time");
            Query query = new Query();
            query.setGroup(true);
            query.setGroupLevel(2);
            query.setReduce(true);            

            for (int rt = 0; rt < runTimes; rt++) {
                long start = System.currentTimeMillis();
                for (int i = 0; i < numIterations; i++) {
                    RequestThread requestThread = new RequestThread(countDownLatch, query, client, view, callTimes, (rt * i) + i);
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

        try {
            countDownLatch.await();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        for (Integer i : callTimes.keySet()) {
            System.out.println(callTimes.get(i));
        }
        client.shutdown(3, TimeUnit.SECONDS);
        System.exit(0);
    }
}