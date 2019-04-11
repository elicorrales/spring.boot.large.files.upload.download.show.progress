#!/bin/bash

curl -s http://localhost:8080/sessionid| jq;

SESSIONID=$(curl -s http://localhost:8080/sessionid | sed -e 's/^.*uuid":"//g'  -e 's/","message.*$//g');

read -p "SESSIONID is  $SESSIONID . Press <ENTER> to request upload:";

curl  \
	--header "SESSIONID: ${SESSIONID}" \
	-F 'file=@/home/devchu/Downloads/new.file.txt' \
	-F 'file=@/home/devchu/Downloads/new.file2.txt' \
	-F 'file=@/home/devchu/Downloads/new.file3.txt' \
	-F 'file=@/home/devchu/Downloads/nodejs.zip' \
	-F 'file=@/home/devchu/Downloads/google-talkplugin_current_amd64.deb' \
	-F 'file=@/home/devchu/Downloads/axios.min.map' \
	-F 'file=@/home/devchu/Downloads/react.redux.zip' \
	-F 'file=@/home/devchu/Downloads/Postman-linux-x64-7.0.7.tar.gz' \
	-F 'file=@/home/devchu/Downloads/bigbigfile.tar' \
	http://localhost:8080/betterfileupload;
