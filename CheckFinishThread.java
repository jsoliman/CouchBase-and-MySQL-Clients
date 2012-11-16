package couchbase;

import java.lang.*;
import java.util.*;

import com.couchbase.client.internal.HttpFuture;

class CheckFinishThread implements Runnable {
    Thread t;
    Map<Integer, HttpFuture> request;
    Map<Integer, Long> endTimes;
    boolean exit;
    public CheckFinishThread(Map<Integer, HttpFuture> request,  Map<Integer, Long> endTimes ) {
        t = new Thread(this, "CheckFinish");
        this.request = request;
        this.endTimes = endTimes;
        exit = false;
        t.start();
    }

    public void setExit(boolean end) {
        exit = end;
    }

    public void run() {
        while(true) {
            for (Integer i : request.keySet()) {
                if (request.get(i).isDone()) {
                    endTimes.put(i, System.currentTimeMillis());
                    request.remove(i);
                }
            }
            if (exit && request.isEmpty()) {
                break;
            }
        }
    }
}