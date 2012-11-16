package common;

class Configuration {
	private String tableName;
	private int iterations;
	private int messagesPerSecond;
	private String outputFilename;

	public String getTableName() { return tableName; }
	public int getIterations() { return iterations; }
	public int getMessagesPerSecond() { return messagesPerSecond; }
	public String getOutputFilename() { return outputFilename; }

	public void setTableName(String tableName) { this.tableName = tableName; }
	public void setIterations(int iterations) { this.iterations = iterations; }
	public void setMessagesPerSecond(int messagesPerSecond) { this.messagesPerSecond = messagesPerSecond; }
	public void setOutputFilename(String filename) { this.outputFilename = filename; }
}