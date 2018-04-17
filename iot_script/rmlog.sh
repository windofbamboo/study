#!/bin/sh
remotepath="$HOME/jstorm-2.2.1/logs/TopoOutAccount/"

for host in `cat host.txt`
do
	expect -c "
	spawn rm -f *log* jstorm@$host:$remotepath
	expect {
	     \"*assword:\" {set timeout 300;send \"ai\r\";}
	}
	expect eof"
done