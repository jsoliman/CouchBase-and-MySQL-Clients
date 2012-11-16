package com.cpe560.couchbase;

import com.cpe560.common.Configuration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.net.URI;
    // Arguments
    // Bucket name
    // View name
    //  View args
    //  -g <value>: setGroup(true), setGroupLevel(<value>)
    //  -r: setReduce
    //  -i: iterations
public class CouchBaseConfiguration extends Configuration {
    // Table name == view name;
    private String viewName;
    private boolean setGroup;
    private int groupLevel;
    private boolean setReduce;
    private String documentName;
    private List<URI> uris;

    public String getViewName() { return viewName; }
    public boolean getSetGroup() { return setGroup; }
    public int getGroupLevel() { return groupLevel; }
    public boolean getSetReduce() { return setReduce; }
    public String getDocumentName() { return documentName; }
    public List<URI> getUris() { return uris; }

    public void setViewName(String viewName) { this.viewName = viewName; }
    public void setSetGroup(boolean setGroup) { this.setGroup = setGroup; }
    public void setGroupLevel(int groupLevel) { this.groupLevel = groupLevel; }
    public void setSetReduce(boolean setReduce) { this.setReduce = setReduce; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }
    public void setUris(List<URI> uris) { this.uris = uris; }

    public ConcurrentHashMap<String, Object> generateConcurrentHashMap() {

        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>(super.generateConcurrentHashMap());
        map.put("viewName", viewName);
        map.put("setGroup", setGroup);
        map.put("groupLevel", groupLevel);
        map.put("setReduce", setReduce);
        map.put("documentName", documentName);
        map.put("uris", uris);
        return map;
    }
}