package com.cpe560.mysql;

import java.lang.*;
import java.util.*;

import com.cpe560.common.Configuration;
import java.util.concurrent.ConcurrentHashMap;


public class MySQLConfiguration extends Configuration {
	private String userName = null;
	private String password = null;
	private String databaseName = null;
	private String serverName = null;
	private String port = null;
	private List<InsertEntry> insertEntries;
	private String readQuery = null;
    private String createTable = null;
    private String dropTable = null;
    private String workloadType;


    public String getCreateTable() { return this.createTable; }
    public String getDropTable() { return this.dropTable; }
	public String getUsername() { return this.userName;}
	public String getPassword() { return this.password;}
	public String getDatabaseName() { return this.databaseName;}
    public List<InsertEntry> getInsertEntries() { return insertEntries; }
	public String getServerName() { return this.serverName;}
	public String getPort() { return this.port;}
	public String getReadQuery() { return this.readQuery;}
    public String getWorkloadType() { return workloadType; }

    public void setCreateTable(String createTable) { this.createTable = createTable; }
    public void setdropTable(String dropTable) { this.dropTable = dropTable; }
	public void setUsername(String userName) { this.userName = userName;}
	public void setPassword(String password) { this.password = password;}
	public void setDatabaseName(String databaseName) { this.databaseName = databaseName;}
    public void setInsertEntries(List<InsertEntry> insertEntries) { this.insertEntries = insertEntries; }
	public void setServerName(String serverName) { this.serverName = serverName;}
	public void setPort(String port) { this.port = port;}
	public void setReadQuery(String readQuery) { this.readQuery = readQuery;}
    public void setWorkloadType(String workload) { this.workloadType = workload; }

    public ConcurrentHashMap<String, Object> generateConcurrentHashMap() {

        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>(super.generateConcurrentHashMap());
        map.put("userName", userName);
        map.put("password", password);
        map.put("databaseName", databaseName);
        map.put("serverName", serverName);
        map.put("port", port);
        map.put("readQuery", readQuery);
        map.put("createTable", createTable);
        map.put("dropTable", dropTable); 
        return map;
    }

    public static class InsertEntry {
        private String key;
        private String sessionID;
        private String response;
        private String student;

        public String getKey() { return key; }
        public String getSessionID() { return sessionID; }
        public String getResponse() { return response; }
        public String getStudent() { return student; }

        public void setKey(String key) { this.key = key; }
        public void setSessionID(String sessionID) { this.sessionID = sessionID; }
        public void setResponse(String response) { this.response = response; }
        public void setStudent(String student) { this.student = student; }
    }
}
