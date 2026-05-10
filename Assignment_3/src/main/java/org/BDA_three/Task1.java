// Talina Daragmeh 1634835
//Tony Trinh 1606724
//Timur Sultanov 1614939


package org.BDA_three;

/*
 * org.BDA_three.WordCountPlusPlus example
 *
 * parameters:
 * args[0] -> input directory
 * args[1] -> output directory
 */

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Task1 extends Configured implements Tool {

    public static class Map extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private final Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String input = value.toString()
                    .replace('\"', ' ')
                    .replace('\'', ' ')
                    .replace('!', ' ')
                    .replace('?', ' ')
                    .replace('(', ' ')
                    .replace(')', ' ')
                    .replace('-', ' ')
                    .replace('.', ' ')
                    .replace(':', ' ')
                    .replace(';', ' ')
                    .replace('=', ' ')
                    .replace(',', ' ')
                    .replace('[', ' ')
                    .replace(']', ' ')
                    .toLowerCase();

            StringTokenizer itr = new StringTokenizer(input);

            // Use set, so each word per line is saved only once
            Set<String> uniqueWords = new HashSet<>();

            while (itr.hasMoreTokens()) {
                uniqueWords.add(itr.nextToken());
            }

            // Output Tokens
            for (String w : uniqueWords) {
                word.set(w);
                context.write(word, one);
            }
        }
    }

    public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {

        private final IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }

            // Output only if word is appearing in at least 5 lines
            if (sum >= 5) {
                result.set(sum);
                context.write(key, result);
            }
        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = getConf();

        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(Task1.class);

        // set Mapper and Reducer classes
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        // output data types for Mapper
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        // output data types for Reducer
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // delete output directory (may already exist from previous run)
        Path output = new Path(args[1]);
        output.getFileSystem(conf).delete(output, true);

        // set input and output directories
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, output);

        // run the job
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new Task1(), args);
    }
}