#!/bin/bash -x
couchbase_jars=`echo ~/lib/couchbase/*.jar | tr ' ' ':'`
common_jars=`echo ~/lib/common/*.jar | tr ' ' ':'`
java -cp .:$common_jars:$couchbase_jars $@
