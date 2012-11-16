#!/bin/bash -v
couchbase_jars=`echo ~/lib/couchbase/*.jar | tr ' ' ':'`
common_jars=`echo ~/lib/common/*.jar | tr ' ' ':'`
javac -cp $common_jars:$couchbase_jars $@
