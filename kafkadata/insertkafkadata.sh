rm $HOME/geyf/nohup.out
nohup java -cp $HOME/geyf/kafka-data-0.0.1.jar:$HOME/bill/conf:$HOME/bill/common/*:$HOME/bill/ext/*:$HOME/bill/ext2/* AppTest insert prov 110,120,130&
echo "kafka-data started."