package com.cpe560.mysql;

import com.cpe560.common.Configuration;
import java.util.concurrent.ConcurrentHashMap;


public class MySQLConfiguration extends Configuration {
	private String userName = null;
	private String password = null;
	private String databaseName = null;
	private String serverName = null;
	private String port = null;
	private String readQuery = null;
	private String writeQuery = null;

	public String getUsername() { return this.userName;}
	public String getPassword() { return this.password;}
	public String getDatabaseName() { return this.databaseName;}
	public String getServerName() { return this.serverName;}
	public String getPort() { return this.port;}
	public String getReadQuery() { return this.readQuery;}
	public String getWriteQuery() { return this.writeQuery;}

	public void setUsername(String userName) { this.userName = userName;}
	public void setPassword(String password) { this.password = password;}
	public void setDatabaseName(String databaseName) { this.databaseName = databaseName;}
	public void setServerName(String serverName) { this.serverName = serverName;}
	public void setPort(String port) { this.port = port;}
	public void setReadQuery(String readQuery) { this.readQuery = readQuery;}
	public void setWriteQuery(String writeQuery) { this.writeQuery = writeQuery;}

    public ConcurrentHashMap<String, Object> generateConcurrentHashMap() {

        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>(super.generateConcurrentHashMap());
        map.put("userName", userName);
        map.put("password", password);
        map.put("serverName", serverName);
        map.put("port", port);
        map.put("readQuery", readQuery);
        map.put("writeQuery", writeQuery); 
        return map;
    }
}