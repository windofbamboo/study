remotepath="$HOME/bill/common/"

localpath="$HOME/bin/"

cd $localpath
for fil in `ls *.jar`
do
	for host in `cat host.txt`
	do
		expect -c "
		spawn scp -o \"StrictHostKeyChecking=no\" -r $fil jstorm@$host:$remotepath
		expect {
		     \"*assword:\" {set timeout 300;send \"ai\r\";}
		}
		expect eof"
	done
	mv $fil $remotepath
done

