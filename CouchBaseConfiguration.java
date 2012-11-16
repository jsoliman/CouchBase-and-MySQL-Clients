package couchbase;

import java.util.List;
import java.net.URI;
    // Arguments
    // Bucket name
    // View name
    //  View args
    //  -g <value>: setGroup(true), setGroupLevel(<value>)
    //  -r: setReduce
    //  -i: iterations
class CouchBaseConfiguration extends Configuration {
	// Table name == view name;
	private boolean setGroup;
	private int groupLevel;
	private boolean setReduce;
	private String documentName;
	private List<URI> uris;

	public boolean getSetGroup() { return setGroup; }
	public int getGroupLevel() { return groupLevel; }
	public boolean getSetReduce() { return setReduce; }
	public String getDocumentName() { return documentName; }
	public List<URI> getUris() { return uris; }

	public void setSetGroup(boolean setGroup) { this.setGroup = setGroup; }
	public void setGroupLevel(int groupLevel) { this.groupLevel = groupLevel; }
	public void setSetReduce(boolean setReduce) { this.setReduce = setReduce; }
	public void setDocumentName(String documentName) { this.documentName = documentName; }
	public void setUris(List<URI> uris) { this.uris = uris; }
}