import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

class Point{
	double x = (double) 0.0;
	double y = (double) 0.0;
}

public class HadoopPi {

	public static class Map extends Mapper<Point, Text, Text, IntWritable> {

		private final static IntWritable one = new IntWritable(1); // type of output value
		private Text type = new Text(); // type of output key - "inside"

		public void map(Point key, Text value, Context context) throws IOException, InterruptedException {

			int count = 100;

			while (count > 0) {

				Point p = new Point();
				// points are generated using Random function in the range of 0 - 1
				p.x = Math.random();
				p.y = Math.random();

				// calculate distance from (0,0)
				double sqSum = p.x * p.x + p.y * p.y;
				double dist = (double) Math.sqrt(sqSum);

				if (dist <= 1) {
					Text k = new Text("in");
					context.write(k, one); // create a pair <keyword, 1>
				}
				
				count--;
			}
		}
	}

	public static class Reduce extends Reducer<Text, IntWritable, Text, DoubleWritable> {

		private DoubleWritable result;// = new DoubleWritable();

		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			int sum = 0; // initialize the sum for each keyword
			for (IntWritable val : values) {
				sum += val.get();
			}
			double pi=(sum/100)*4;
			result = new DoubleWritable(pi);
			context.write(key, result); // create a pair <keyword, val of pi>
		}
	}

	// Driver program
	public static void main(String[] args) throws Exception{
  Configuration conf = new Configuration();
/*  String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs(); // get all args
  if (otherArgs.length != 2) {
   System.err.println("Usage: WordCount <in> <out>");
   System.exit(2);
  }
*/
  // create a job with name "hadooppi"
  Job job = new Job(conf, "hadooppi");
  job.setJarByClass(HadoopPi.class);
  job.setMapperClass(Map.class);
  job.setReducerClass(Reduce.class);

  // Add a combiner here, not required to successfully run the wordcount program  

  // set output key type   
  job.setOutputKeyClass(Text.class);
  // set output value type
  job.setOutputValueClass(DoubleWritable.class);
  //set the HDFS path of the input data
  FileInputFormat.addInputPath(job, new Path("input"));
  // set the HDFS path for the output
  FileOutputFormat.setOutputPath(job, new Path("output"));

  //Wait till job completion
  System.exit(job.waitForCompletion(true) ? 0 : 1);
 }
}
