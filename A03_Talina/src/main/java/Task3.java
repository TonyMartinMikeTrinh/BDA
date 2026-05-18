

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.TreeSet;

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

public class Task3 extends Configured implements Tool {

    public static class Map extends Mapper<Object, Text, Text, IntWritable> {

        private final Text word = new Text();
        private final IntWritable docIdWritable = new IntWritable();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();

            int colonIndex = line.indexOf(':');
            if (colonIndex == -1) return;

            String docIdStr = line.substring(0, colonIndex).trim();
            String text     = line.substring(colonIndex + 1).trim().toLowerCase();

            int docId = Integer.parseInt(docIdStr);
            docIdWritable.set(docId);

            text = text.replace('\"', ' ').replace('\'', ' ')
                    .replace('!', ' ').replace('?', ' ')
                    .replace('(', ' ').replace(')', ' ')
                    .replace('-', ' ').replace('.', ' ')
                    .replace(':', ' ').replace(';', ' ')
                    .replace(',', ' ');


            StringTokenizer itr = new StringTokenizer(text);
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                context.write(word, docIdWritable);
            }
        }
    }

    public static class Reduce extends Reducer<Text, IntWritable, Text, Text> {


        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            TreeSet<Integer> docIds = new TreeSet<>();

            for (IntWritable val : values) {
                docIds.add(val.get());
            }

            if (docIds.size() >= 5) {
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (int id : docIds) {
                    if (!first) sb.append(", ");
                    sb.append(id);
                    first = false;
                }
                context.write(key, new Text(sb.toString()));
            }
        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = getConf();

        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(Task3.class);

        // set Mapper and Reducer classes
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        // output data types for Mapper
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);


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
