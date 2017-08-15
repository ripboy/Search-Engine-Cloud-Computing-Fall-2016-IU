if [ ! -d classes ]; then
        mkdir classes;
fi

# Compile WordCount
javac -classpath $HADOOP_HOME/hadoop-core-1.1.2.jar:$HADOOP_HOME/lib/commons-cli-1.2.jar -d ./classes HadoopPi.java

# Create the Jar
jar -cvf hadooppi.jar -C ./classes/ .
 
# Copy the jar file to the Hadoop distributions
cp hadooppi.jar $HADOOP_HOME/bin/ 

