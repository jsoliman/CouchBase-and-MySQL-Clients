package couchbase;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.couchbase.client.internal.HttpFuture;
import com.couchbase.client.CouchbaseConnectionFactory;
import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.ViewRow;
import com.couchbase.client.protocol.views.ViewResponse;
import net.spy.memcached.internal.GetFuture;
import net.spy.memcached.internal.OperationFuture;

import java.util.concurrent.ConcurrentHashMap;

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
        Map<Integer, HttpFuture> request = new ConcurrentHashMap<Integer, HttpFuture>();
        Map<Integer, Long> startTimes = new ConcurrentHashMap<Integer, Long>();
        Map<Integer, Long> endTimes = new ConcurrentHashMap<Integer, Long>();
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

        CheckFinishThread t = new CheckFinishThread(request, endTimes);

        try {
            View view = client.getView("deploy", "trending_over_time");
            Query query = new Query();
            query.setGroup(true);
            query.setGroupLevel(2);
            query.setReduce(true);
            int numIterations = 1;

            int runTimes = 5;
            for (int rt = 0; rt < runTimes; rt++) {
                long start = System.currentTimeMillis();
                for (int i = 0; i < numIterations; i++) {
                    request.put(totalCount, client.asyncQuery(view, query));
                    startTimes.put(totalCount, System.currentTimeMillis());
                    totalCount += 1;
                }
                long finish = System.currentTimeMillis();
                long difference = finish - start;
                if (difference > 1000) {
                    System.out.println("More than 1 second to send request");
                    break;
                }
                Thread.sleep(1000 - (finish - start));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        while(request.size() > 0);
        t.setExit(true);
        for (int i : endTimes.keySet()) {
            System.out.println(endTimes.get(i) - startTimes.get(i));
        }
        client.shutdown(3, TimeUnit.SECONDS);
        System.exit(0);
    }
}