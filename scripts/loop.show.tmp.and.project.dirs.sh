#!/bin/bash
rm /tmp/*response.html
while [ 1 ];
do
	echo "=========================================================================";
	echo "/tmp/tomcat\.*8080 : ";ls -l /tmp/tomcat* 2>/dev/null;
	echo "=========================================================================";
	echo "~/Development...blah..blah ..current project dir ./temp:"
	ls -l ~/Development/Client.Server/eclipse.workspace/spring.boot.large.files.upload.show.progress/temp 2>/dev/null;
	echo;
	echo;
	sleep 0.2;
done
