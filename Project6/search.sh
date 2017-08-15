# start hadoop
cd /root/software/hadoop-1.1.2/
./MultiNodesOneClickStartUp.sh /root/software/jdk1.6.0_33/ nodes

# start hbase
cd /root/software/hbase-0.94.7/ 
./bin/start-hbase.sh

cp /root/software/hbase-0.94.7/conf/hbase-site.xml /root/software/hadoop-1.1.2/conf/

cd /root/software/hadoop-1.1.2/

export HADOOP_CLASSPATH=`/root/software/hbase-0.94.7/bin/hbase classpath`

./bin/hadoop jar lib/cglHBaseMooc.jar iu.pti.hbaseapp.clueweb09.TableCreatorClueWeb09

# create one directory for mapreduce data input 
mkdir -p /root/MoocHomeworks/HBaseWordCount/data/clueweb09/mrInput

# create input’s metadata for HBbase data loader 
./bin/hadoop jar lib/cglHBaseMooc.jar iu.pti.hbaseapp.clueweb09.Helpers create-mr-input /root/MoocHomeworks/HBaseWordCount/data/clueweb09/files/ /root/MoocHomeworks/HBaseWordCount/data/clueweb09/mrInput/ 1

# copy metadata to Hadoop HDFS
./bin/hadoop dfs -copyFromLocal /root/MoocHomeworks/HBaseWordCount/data/clueweb09/mrInput/ /cw09LoadInput
./bin/hadoop dfs -ls /cw09LoadInput

# load data into HBase (takes 10-20 minutes to finish)
./bin/hadoop jar lib/cglHBaseMooc.jar iu.pti.hbaseapp.clueweb09.DataLoaderClueWeb09 /cw09LoadInput

cd /root/MoocHomeworks/Project5/
compileAndExecFreqIndexBuilderClueWeb.sh

cd /root/software/hadoop-1.1.2/

./bin/hadoop jar /root/software/hadoop-1.1.2/lib/cglHBaseMooc.jar iu.pti.hbaseapp.clueweb09.PageRankTableLoader /root/MoocHomeworks/HBaseInvertedIndexing/resources/en0000-01and02.docToNodeIdx.txt /root/MoocHomeworks/HBaseInvertedIndexing/resources/en0000-01and02_reset_idx_and_square_pagerank.out

cd /root/MoocHomeworks/Project6/

ant

cp /root/MoocHomeworks/Project6/dist/lib/cglHBaseMooc.jar /root/software/hadoop-1.1.2/lib/

cd /root/software/hadoop-1.1.2/
./bin/hadoop jar lib/cglHBaseMooc.jar  iu.pti.hbaseapp.clueweb09.SearchEngineTester search-keyword snapshot
./bin/hadoop jar lib/cglHBaseMooc.jar  iu.pti.hbaseapp.clueweb09.SearchEngineTester get-page-snapshot 00000113548 |  grep snapshot
