// Talina Daragmeh 1634835
//Tony Trinh 1606724
//Timur Sultanov 1614939

package org.BDA_three;

/*
 * org.BDA_three.InvertedIndex
 *
 * parameters:
 * args[0] -> input directory
 * args[1] -> output directory
 */

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Task3 extends Configured implements Tool {

    public static class Map extends Mapper<Object, Text, Text, Text> {

        private final Text word = new Text();
        private final Text docId = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            docId.set(key.toString());

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
            Set<String> uniqueWords = new HashSet<>();

            // Use set, so each word per line is saved only once
            while (itr.hasMoreTokens()) {
                uniqueWords.add(itr.nextToken());
            }

            // Output every pair
            for (String w : uniqueWords) {
                word.set(w);
                context.write(word, docId);
            }
        }
    }

    public static class Reduce extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            TreeSet<Long> sortedIds = new TreeSet<>();

            for (Text val : values) {
                sortedIds.add(Long.parseLong(val.toString()));
            }

            // Output only if word is appearing in at least 5 lines
            if (sortedIds.size() >= 5) {
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (Long id : sortedIds) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(id);
                    first = false;
                }
                context.write(key, new Text(sb.toString()));
            }
        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = getConf();

        Job job = Job.getInstance(conf, "inverted index");
        job.setJarByClass(Task3.class);

        // set Mapper and Reducer classes
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        // output data types for Mapper
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        // output data types for Reducer
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

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
        ToolRunner.run(new Configuration(), new Task3(), args);
    }
}