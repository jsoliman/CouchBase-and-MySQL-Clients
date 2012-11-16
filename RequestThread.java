package couchbase;

import java.lang.*;
import java.util.*;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.Query;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ConcurrentHashMap;


class RequestThread implements Runnable {
	private Thread t;
	private Query query;
	private CouchbaseClient client;
	private View view;
	private int id;
	private Map<Integer, Long> map;
   	private final CountDownLatch countDownLatch;

	public RequestThread(CountDownLatch countDownLatch, Query query, CouchbaseClient client, View view, Map<Integer, Long> map, int id) {
		this.countDownLatch = countDownLatch;
		t = new Thread(this, "RequestThread");
		this.query = query;
		this.view = view;
		this.client = client;
		this.map = map;
		this.id = id;
		t.start();
	}

	public void run() {
		long startTime = 0;
		long endTime = 0;
		try {
			startTime = System.currentTimeMillis();        	
           	client.query(view, query);
           	endTime = System.currentTimeMillis();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println(endTime - startTime);

        map.put(id, endTime - startTime);
        countDownLatch.countDown();
	}

}