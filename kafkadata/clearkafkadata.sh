rm $HOME/geyf/nohup.out
nohup java -cp $HOME/geyf/kafka-data-0.0.1.jar:$HOME/bill/common/*:$HOME/bill/ext/*:$HOME/bill/app/* AppTest clear&
echo "kafka-data started."
