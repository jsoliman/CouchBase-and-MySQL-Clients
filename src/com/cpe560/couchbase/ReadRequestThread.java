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
import java.net.URI;

public class ReadRequestThread implements Runnable {
    private Thread t;
    private Query query;
    private CouchbaseClient client;
    private View view;
    private int id;
    private ConcurrentHashMap<Integer, Long> map;
    private ConcurrentHashMap<String, Object> cb_config_map;
    private CouchBaseConfiguration cb_config;
    private final CountDownLatch countDownLatch;

    public ReadRequestThread(CountDownLatch countDownLatch, ConcurrentHashMap<String, Object> cb_config_map, ConcurrentHashMap<Integer, Long> map, int id) {
        this.countDownLatch = countDownLatch;
        t = new Thread(this, "ReadRequestThread");
        //this.cb_config = cb_config_map.get(1);
        this.cb_config_map = cb_config_map;
        this.map = map;
        this.id = id;
        t.start();
    }

    public CouchbaseClient generateClient() {
        CouchbaseClient client = null;

        // Establish connection with CouchBase Server
        try {
            CouchbaseConnectionFactory cf = new CouchbaseConnectionFactory((List<URI>)cb_config_map.get("uris"), "default", "");
            client = new CouchbaseClient((CouchbaseConnectionFactory) cf);
        } catch (Exception e) {
            System.err.println("Error connecting to Couchbase: "
                + e.getMessage());
            System.exit(0);
        }

        return client;
    }

    public View generateView(CouchbaseClient client) {
        return client.getView((String)cb_config_map.get("documentName"), (String)cb_config_map.get("viewName"));        
    }

    public Query generateQuery() {
        Query query = new Query();
        query.setGroup((Boolean)cb_config_map.get("setGroup"));
        query.setGroupLevel((Integer)cb_config_map.get("groupLevel"));
        query.setReduce((Boolean)cb_config_map.get("setReduce"));   
        return query;
    }

    public void run() {
        long startTime = 0;
        long endTime = 0;
        this.client = generateClient();
        this.query = generateQuery();
        this.view = generateView(client);
        
        try {
            startTime = System.currentTimeMillis();                     
            client.query(view, query);
            endTime = System.currentTimeMillis();            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        client.shutdown(1, TimeUnit.SECONDS);
        map.put(id, endTime - startTime);
        countDownLatch.countDown();
    }

}