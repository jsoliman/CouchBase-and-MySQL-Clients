#!/bin/bash
temp_java_home=/usr/lib/jvm/java-1.6.0-openjdk.x86_64/
if [[ $JAVA_HOME !=  $temp_java_home ]]; then
	if [[ ! -d "$temp_java_home" ]]; then
		export JAVA_HOME=$temp_java_home
		echo "export JAVA_HOME=$temp_java_home" >> ~/.bashrc
	fi
fi

temp_ant_home=`pwd`
temp_ant_home=$temp_ant_home/../lib/apache-ant-1.8.4

if [[ $ANT_HOME != $temp_ant_home ]]; then
	export ANT_HOME=$temp_ant_home
	echo "export ANT_HOME=$temp_ant_home" >> ~/.bashrc
	export PATH=$PATH:$ANT_HOME/bin
	echo "export PATH=$PATH:$temp_ant_home/bin" >> ~/.bashrc
fi

