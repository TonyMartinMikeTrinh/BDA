// Talina Daragmeh 1634835
//Tony Trinh 1606724
//Timur Sultanov 1614939

package org.BDA_three;

/*
 * org.BDA_three.Grep
 *
 * parameters:
 * args[0] -> input directory
 * args[1] -> output directory
 */

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Task2 extends Configured implements Tool {

    public static class Map extends Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();

            // Search term
            String searchTerm = "malware";
            if (line.toLowerCase().contains(searchTerm.toLowerCase())) {
                context.write(new Text(key.toString()), value);
            }
        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = getConf();

        Job job = Job.getInstance(conf, "grep job");
        job.setJarByClass(Task2.class);

        job.setNumReduceTasks(0);

        // // set Mapper classes
        job.setMapperClass(Map.class);

        // output data types for Mapper
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        //delete output directory (may already exist from previous run)
        Path output = new Path(args[1]);
        output.getFileSystem(conf).delete(output, true);

        // set input and output directories
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, output);

        // run the job
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new Task2(), args);
    }
}