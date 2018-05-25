if [ -d $HOME/geyf/nohup.out ];then
	rm $HOME/geyf/nohup.out
fi

if [ $# -ne 1 ];then
	echo "参数个数错误，不为1 ";
	exit;
else
    check=` echo $1 | awk '{print($0~/^[-]?([0-9])+[.]?([0-9])+$/)?"number":"string"}' `
	if [ ${check} != "number" ];then
	    echo "args[0] is not a number ";
		exit;
	fi
fi

DEAL_ID=$1
echo "deal_id:"${DEAL_ID}

nohup java -cp $HOME/geyf/kafka-data-0.0.1.jar:$HOME/bill/common/*:$HOME/bill/ext/*:$HOME/bill/app/* AppTest reload ${DEAL_ID}&
echo "kafka-data started."
