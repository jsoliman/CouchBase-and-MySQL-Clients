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



public class WriteRequestThread implements Runnable {
	private Thread t;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private final CountDownLatch countDownLatch;
    private ConcurrentHashMap<String, Object> cb_config_map;
    private ConcurrentHashMap<Integer, Long> map;
    private List<MySQLConfiguration.InsertEntry> insertEntries;
    private int id;


    public WriteRequestThread(CountDownLatch countDownLatch, ConcurrentHashMap<String, Object> cb_config_map, ConcurrentHashMap<Integer, Long> map, int id, 
                                List<MySQLConfiguration.InsertEntry> insertEntries) {
        this.countDownLatch = countDownLatch;
        t = new Thread(this, "WriteRequestThread");

        this.cb_config_map = cb_config_map;
        this.map = map;
        this.id = id;
        this.insertEntries = insertEntries;
        t.start();
    } 

	public Connection generateConnection(String userName, String password) throws SQLException {

	    Connection conn = null;
	    Properties connectionProps = new Properties();
	    connectionProps.put("user", this.cb_config_map.get("userName"));
	    connectionProps.put("password", this.cb_config_map.get("password"));
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(
                   "jdbc:mysql://" +
                   (String) this.cb_config_map.get("serverName") +
                   ":" + (String) this.cb_config_map.get("port") + "/",
                   connectionProps);
	    System.out.println("Connected to database");
	    return conn;
	}

    public void createTable(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate((String) this.cb_config_map.get("createTable")); 
    } 

    public void dropTable(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate((String) this.cb_config_map.get("dropTable")); 
    } 

    public void run() {
        long startTime = 0;
        long endTime = 0;

        try {
            this.connection = generateConnection((String) this.cb_config_map.get("userName"),
                                                (String) this.cb_config_map.get("password"));
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }


        String insertTableSQL = "INSERT INTO game"
                + "(sessionID, response, student) VALUES"
                + "(?,?,?)";

        PreparedStatement ps = this.connection.prepareStatement(insertTableSQL);

        try {
            // Create table
            createTable(this.connection);
            startTime = System.currentTimeMillis();                     

            for (MySQLConfiguration.InsertEntry entry : insertEntries) {
                Statement stmt = con.createStatement();

                ps.setString(1, entry.getSessionID());
                ps.setString(2, entry.getResponse());
                ps.setInt(3, Integer.parseInt(entry.getStudent()));
                ps.executeUpdate();

                OperationFuture<Boolean> setOp = client.set(key.toString(), EXP_TIME, entry.getValue());
                key++;
                // May need to adjust how timings are done.                
            }

            endTime = System.currentTimeMillis();

            //Drop table
            dropTable(this.connection);

            this.connection.close();

        }
		catch (Exception ex) {
            ex.printStackTrace();
        }
        map.put(id, endTime - startTime);
        countDownLatch.countDown();
     }

}