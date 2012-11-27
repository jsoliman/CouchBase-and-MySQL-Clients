package com.cpe560.mysql;

import java.lang.*;
import java.util.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement; 
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * Thread for writing to a database. 
 * Used in the mixed test. 
 *
 */
public class WriteThread implements Runnable {
	private Thread t;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private final CountDownLatch countDownLatch;
    private ConcurrentHashMap<String, Object> cb_config_map;
    private ConcurrentHashMap<Integer, Long> map;
    private List<MySQLConfiguration.InsertEntry> insertEntries;
    private int id;


    public WriteThread(CountDownLatch countDownLatch, ConcurrentHashMap<String, Object> cb_config_map, ConcurrentHashMap<Integer, Long> map, int id, 
                                List<MySQLConfiguration.InsertEntry> insertEntries) {
        this.countDownLatch = countDownLatch;
        t = new Thread(this, "WriteThread");

        this.cb_config_map = cb_config_map;
        this.map = map;
        this.id = id;
        this.insertEntries = insertEntries;
        t.start();
    } 

    public void run() {
        long startTime = 0;
        long endTime = 0;

        try {
            this.connection = MySQLTestHarness.generateConnection((String) this.cb_config_map.get("userName"),
                                                (String) this.cb_config_map.get("password"),
                                                (String) this.cb_config_map.get("serverName"),
                                                (String) this.cb_config_map.get("port"),
                                                (String) this.cb_config_map.get("databaseName"));
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }


        String insertTableSQL = "INSERT INTO game"
                + "(sessionID, response, student) VALUES"
                + "(?,?,?)";

        try {
            PreparedStatement ps = this.connection.prepareStatement(insertTableSQL);
            // Create table
            //createTable(this.connection);
            startTime = System.currentTimeMillis();                     


            for (MySQLConfiguration.InsertEntry entry : insertEntries) {
                Statement stmt = this.connection.createStatement();

                ps.setString(1, entry.getSessionID());
                ps.setString(2, entry.getResponse());
                ps.setInt(3, Integer.parseInt(entry.getStudent()));
                ps.executeUpdate();


                // May need to adjust how timings are done.                
            }

            endTime = System.currentTimeMillis();

            this.connection.close();

        }
		catch (Exception ex) {
            ex.printStackTrace();
        }
        map.put(id, endTime);
        countDownLatch.countDown();
     }

}