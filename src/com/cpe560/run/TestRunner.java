package com.cpe560.run;

import com.cpe560.couchbase.CouchBaseTestHarness;

public class TestRunner {
    public static void main(String[] args) {
        if (args.length != 2) {
            printUsage();
            System.exit(2);
        }

        if (args[0].equals("MySQL")) {
            CouchBaseTestHarness cbth = CouchBaseTestHarness.createCouchBaseTestHarness(args[0]);
            cbth.run();
        } else if (args[0].equals("CouchBase")) {
            System.out.println("Need to write MySQL stuff");
        } else {
            printUsage();
        }
        System.exit(0);
    }

    public static void printUsage() {
        System.out.println("Usage: java -cp <classpath> JavaRunner type configFile");
        System.out.println("    type: Test type to run. Must be MySQL or CouchBase");
        System.out.println("    configFile: Configuration file for the test");      
    }
}