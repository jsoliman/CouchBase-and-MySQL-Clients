package com.cpe560.mysql;

import java.lang.*;
import java.util.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;



public class ReadRequestThread implements Runnable {
	private Thread t;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private final CountDownLatch countDownLatch;
    private ConcurrentHashMap<String, Object> cb_config_map;
    private ConcurrentHashMap<Integer, Long> map;
    private int id;


    public ReadRequestThread(CountDownLatch countDownLatch, ConcurrentHashMap<String, Object> cb_config_map, ConcurrentHashMap<Integer, Long> map, int id) {
        this.countDownLatch = countDownLatch;
        t = new Thread(this, "ReadRequestThread");

        this.cb_config_map = cb_config_map;
        this.map = map;
        this.id = id;
        t.start();
    } 

	public Connection generateConnection(String userName, String password) throws SQLException {

	    Connection conn = null;
	    Properties connectionProps = new Properties();
	    connectionProps.put("user", this.cb_config_map.get("userName"));
	    connectionProps.put("password", this.cb_config_map.get("password"));

        conn = DriverManager.getConnection(
                   "jdbc:mysql://" +
                   (String) this.cb_config_map.get("serverName") +
                   ":" + (String) this.cb_config_map.get("port") + "/",
                   connectionProps);
	    System.out.println("Connected to database");
	    return conn;
	}

	public PreparedStatement generatePreparedStatement(Connection con) throws SQLException {
		return con.prepareStatement((String) this.cb_config_map.get("readQuery"));
	}

    public void run() {
        long startTime = 0;
        long endTime = 0;

        try {
            this.connection = generateConnection((String) this.cb_config_map.get("userName"),
                                                (String) this.cb_config_map.get("password"));
            this.preparedStatement = generatePreparedStatement(this.connection);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            startTime = System.currentTimeMillis();                     
            this.preparedStatement.executeUpdate();
            endTime = System.currentTimeMillis();
            this.connection.close();

        }
		catch (Exception ex) {
            ex.printStackTrace();
        }
        map.put(id, endTime - startTime);
        countDownLatch.countDown();
     }

}