package com.cpe560.mysql;

import com.cpe560.mysql.MySQLConfiguration;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.io.*;
import java.util.*;

/*
 * Class representing test harness for MySQL.
 *
 */
public class MySQLTestHarness {
	private String outputContent;
	private MySQLConfiguration config;
	private MySQLTestHarness() {}

	public static MySQLTestHarness createMySQLTestHarness(String filename) {
		MySQLTestHarness msth = new MySQLTestHarness();
		msth.init(filename);
		return msth;
	}

    public static Connection generateConnection(String userName, String password, 
                                                String serverName, String port, 
                                                String databaseName) 
                                                throws SQLException {

        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", userName);
        connectionProps.put("password", password);
        try {
           Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
           e.printStackTrace();
        }
        conn = DriverManager.getConnection(
                   "jdbc:mysql://" + serverName
                     +
                   ":" + port + "/"
                   + databaseName,
                   connectionProps);
        System.out.println("Connected to database");
        return conn;
    }

    public static void createTable(Connection connection, String createTableStatement) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(createTableStatement); 
    } 

    public static void dropTable(Connection connection, String dropTableStatement) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(dropTableStatement); 
    } 

    /** Reads from configuration file and creates test. */
	private void init(String filename) {
		try {
			FileReader f = new FileReader(filename);
			Gson gson = new Gson();
			config = gson.fromJson(f, MySQLConfiguration.class);
			outputContent = "";
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
        int messagesPerSecond = config.getReadsPerSecond() + config.getWritesPerSecond();

        outputContent += "Messages Per Second: " + messagesPerSecond + "\n";
        outputContent += "Number of Iterations: " + config.getIterations() + "\n";
	} 

    /** Runs the actual tests. */
	public void run() {
        int iterations = config.getIterations();
        int messagesPerSecond = config.getReadsPerSecond() + config.getWritesPerSecond();
        CountDownLatch countDownLatch = new CountDownLatch (iterations * messagesPerSecond);

        Map<Integer, Long> callTimes = dispatchEvents(countDownLatch);

        // Wait for all request threads to finish.
        try {
            countDownLatch.await();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        printResults(callTimes);
	}

    /** Starts the threads. */
    private Map<Integer, Long> dispatchEvents(CountDownLatch countDownLatch) {
        ConcurrentHashMap<Integer, Long> callTimes = new ConcurrentHashMap<Integer, Long>();
        int readsPerSecond = config.getReadsPerSecond();
        int writesPerSecond = config.getWritesPerSecond();
        int iterations = config.getIterations();
        int messagesPerSecond = readsPerSecond + writesPerSecond;
        ConcurrentHashMap<String, Object> config_map = config.generateConcurrentHashMap();
        String workloadType = config.getWorkloadType();

        try {
            for (int rt = 0; rt < iterations; rt++) {
                long start = System.currentTimeMillis();
                if (workloadType.equals("read")) {
                    for (int i = 0; i < readsPerSecond; i++) {
                        new ReadRequestThread(countDownLatch, config_map, callTimes, (rt * messagesPerSecond) + i);
                    }
                }
                else if (workloadType.equals("write")) {
                    for (int i = 0; i < readsPerSecond; i++) {
                        new WriteRequestThread(countDownLatch, config_map, callTimes, (rt * messagesPerSecond) + i, config.getInsertEntries() );
                    }
                }
                else if (workloadType.equals("game_read")) {
                    new ReadThread(countDownLatch, config_map, callTimes, (rt * messagesPerSecond));
                }
                else if (workloadType.equals("game_write")) {
                        new WriteThread(countDownLatch, config_map, callTimes, (rt * messagesPerSecond), config.getInsertEntries());
                }
                else {
                    for (int i = 0; i < readsPerSecond; i++) {
                        new ReadWriteRequestThread(countDownLatch, config_map, callTimes, (rt * messagesPerSecond) + i, config.getInsertEntries());
                    }
                }


                for (int i = readsPerSecond; i < messagesPerSecond; i++) {
                    System.out.println("Implement write request thread");
                }
                long finish = System.currentTimeMillis();
                long difference = finish - start;
                outputContent += "Time to dispatch " + rt + ": " + difference + "ms\n";
                if (difference > 1000) {                    
                    System.out.println("More than 1 second to dispatch request threads");
                } else {
                    Thread.sleep(1000 - difference);
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
            FileWriter fstream = new FileWriter(config.getOutputFilename());
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(outputContent);
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}