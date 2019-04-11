#!/bin/bash
SESSIONID=$1;
while [ 1 ];
do
	curl -s --header "SESSIONID: ${SESSIONID}" http://localhost:8080/uploadprogress| jq;
	echo "=======================================";
	echo;
	sleep 0.1;
done
