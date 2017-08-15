import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.StringTokenizer;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import java.util.List;
import java.util.ArrayList;

public class BasicStatistics {

 public static class Map
 extends Mapper < LongWritable, Text, Text, CompositeWritable > {

  public void map(LongWritable key, Text value, Context context) throws IOException,
  InterruptedException {
   String a[] = value.toString().split(",");
   Double d[] = new Double[10];


   double partialSum = 0.0, partialSquareSum = 0.0, partialMin = 10.0, partialMax = 0.0, partialCount = 0.0;
   for (int i = 0; i < a.length; i++) {
    d[i] = Double.parseDouble(a[i]);
   }

   for (int j = 0; j < d.length; j++) {
    if (partialMin >= d[j])
     partialMin = d[j];
    if (partialMax <= d[j])
     partialMax = d[j];
    partialSum += d[j];
    partialCount++;
    partialSquareSum += d[j] * d[j];
   }

   CompositeWritable c = new CompositeWritable(partialSum, partialSquareSum, partialMin, partialMax, partialCount);
   context.write(new Text("partialvalues"), c); // create a pair <keyword, 1>
  }
 }

 public static class Reduce
 extends Reducer < Text, CompositeWritable, Text, DoubleWritable > {

  private CompositeWritable result = new CompositeWritable();

  public void reduce(Text key, Iterable < CompositeWritable > values,
   Context context
  ) throws IOException,
  InterruptedException {


   double globalSum = 0.0, globalSquareSum = 0.0, globalMin = 100.0, globalMax = 0.0, globalCount = 0.0, globalAverage, globalSD;

   for (CompositeWritable val: values) {
    globalSum += val.val1;
    globalSquareSum += val.val2;
    if (globalMin >= val.val3)
     globalMin = val.val3;
    if (globalMax <= val.val4)
     globalMax = val.val4;
    globalCount += val.val5;
   }

   globalAverage = globalSum / globalCount;
   globalSD = Math.sqrt((globalSquareSum / globalCount) - (2 * (globalAverage / globalCount) * globalSum) + (globalAverage * globalAverage));

   context.write(new Text("Minimum Number: "), new DoubleWritable(globalMin));
   context.write(new Text("Maximum Number: "), new DoubleWritable(globalMax));
   context.write(new Text("Average: "), new DoubleWritable(globalAverage));
   context.write(new Text("Standard Deviation: "), new DoubleWritable(globalSD));
  }
 }

 public static class CompositeWritable implements Writable {
  double val1 = 0.0;
  double val2 = 0.0;
  double val3 = 0.0;
  double val4 = 0.0;
  double val5 = 0.0;

  public CompositeWritable() {}

  public CompositeWritable(double val1, double val2, double val3, double val4, double val5) {
   this.val1 = val1;
   this.val2 = val2;
   this.val3 = val3;
   this.val4 = val4;
   this.val5 = val5;
  }

  @Override
  public void readFields(DataInput in ) throws IOException {
   val1 = in .readDouble();
   val2 = in .readDouble();
   val3 = in .readDouble();
   val4 = in .readDouble();
   val5 = in .readDouble();
  }

  @Override
  public void write(DataOutput out) throws IOException {
   out.writeDouble(val1);
   out.writeDouble(val2);
   out.writeDouble(val3);
   out.writeDouble(val4);
   out.writeDouble(val5);
  }

  public void merge(CompositeWritable other) {
   this.val1 += other.val1;
   this.val2 += other.val2;
   this.val3 += other.val3;
   this.val4 += other.val4;
   this.val5 += other.val5;
  }

  @Override
  public String toString() {
   return this.val1 + "\t" + this.val2 + "\t" + this.val3 + "\t" + this.val4 + "\t" + this.val5;
  }
 }

 // Driver program
 public static void main(String[] args) throws Exception {
  Configuration conf = new Configuration();
  String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs(); // get all args
  if (otherArgs.length != 2) {
   System.err.println("Usage: WordCount <in> <out>");
   System.exit(2);
  }

  // create a job with name "wordcount"
  Job job = new Job(conf, "wordcount");
  job.setJarByClass(BasicStatistics.class);
  job.setMapperClass(Map.class);
  job.setReducerClass(Reduce.class);

  // Add a combiner here, not required to successfully run the wordcount program  

  // set output key type   
  job.setOutputKeyClass(Text.class);
  // set output value type
  job.setOutputValueClass(CompositeWritable.class);
  //set the HDFS path of the input data
  FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
  // set the HDFS path for the output
  FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

  //Wait till job completion
  System.exit(job.waitForCompletion(true) ? 0 : 1);
 }
}
