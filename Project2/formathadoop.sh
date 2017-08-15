cd /root/software/hadoop-1.1.2/bin/
echo "Stopping all hadoop services"
./stop-all.sh
echo "Killing all java processes"
pkill java
echo "Starting all hadoop services"
./start-all.sh
echo "Leaving hadoop safemode"
hadoop dfsadmin -safemode leave
echo "Deleting all files and folders on hdfs"
hadoop dfs -rmr /
echo "Formatting hadoop"
hadoop namenode -format
