#!/bin/bash
rm /tmp/*response.html
while [ 1 ];
do
	echo "project-local-tomcat work dir (temp/work/Tomcat/localhost/ROOT):"
	echo "=========================================================================";
	ls -l ~/Development/Client.Server/eclipse.workspace/spring.boot.large.files.upload.show.progress/temp/work/Tomcat/localhost/ROOT 2>/dev/null | sort;
	echo "=========================================================================";
	echo "project-local final upload dir files:"
	ls -l ~/Development/Client.Server/eclipse.workspace/spring.boot.large.files.upload.show.progress/uploads/* 2>/dev/null | sort;
	echo;
	echo;
	sleep 0.5;
done
