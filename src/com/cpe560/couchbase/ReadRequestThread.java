package com.cpe560.couchbase;

import java.lang.*;
import java.util.*;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.Query;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ConcurrentHashMap;
import com.couchbase.client.CouchbaseConnectionFactory;
import java.util.concurrent.TimeUnit;

public class ReadRequestThread implements Runnable {
    private Thread t;
    private Query query;
    private CouchbaseClient client;
    private View view;
    private int id;
    private Map<Integer, Long> map;
    private CouchBaseConfiguration cb_config;
    private final CountDownLatch countDownLatch;

    public ReadRequestThread(CountDownLatch countDownLatch, CouchBaseConfiguration cb_config, Map<Integer, Long> map, int id) {
        this.countDownLatch = countDownLatch;
        t = new Thread(this, "ReadRequestThread");
        this.cb_config = cb_config;
        this.map = map;
        this.id = id;
        t.start();
    }

    public CouchbaseClient generateClient(CouchBaseConfiguration cb_config) {
        CouchbaseClient client = null;

        // Establish connection with CouchBase Server
        try {
            CouchbaseConnectionFactory cf = new CouchbaseConnectionFactory(cb_config.getUris(), "default", "");
            client = new CouchbaseClient((CouchbaseConnectionFactory) cf);
        } catch (Exception e) {
            System.err.println("Error connecting to Couchbase: "
                + e.getMessage());
            System.exit(0);
        }

        return client;
    }

    public View generateView(CouchbaseClient client, CouchBaseConfiguration cb_config) {
        return client.getView(cb_config.getDocumentName(), cb_config.getViewName());        
    }

    public Query generateQuery(CouchBaseConfiguration cb_config) {
        Query query = new Query();
        query.setGroup(cb_config.getSetGroup());
        query.setGroupLevel(cb_config.getGroupLevel());
        query.setReduce(cb_config.getSetReduce());   
        return query;
    }

    public void run() {
        this.client = generateClient(cb_config);
        this.query = generateQuery(cb_config);
        this.view = generateView(client, cb_config);

        long startTime = 0;
        long endTime = 0;
        try {
            startTime = System.currentTimeMillis();         
            client.query(view, query);
            endTime = System.currentTimeMillis();
            client.shutdown(3, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        map.put(id, endTime - startTime);
        countDownLatch.countDown();
    }

}