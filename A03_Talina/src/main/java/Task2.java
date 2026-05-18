import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Task2 extends Configured implements Tool {

    public static class Map extends Mapper<Object, Text, Text, Text> {

        private static final String PATTERN = "malware";
        private final Text docId = new Text();
        private final Text empty = new Text("");


        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();

            int colIndex = line.indexOf(':');
            if (colIndex == -1) return;

            String id   = line.substring(0, colIndex).trim();
            String text = line.substring(colIndex + 1).trim().toLowerCase();

            if (text.contains(PATTERN)) {
                docId.set(id);
                context.write(docId, empty);
            }
        }
    }


    public int run(String[] args) throws Exception {
        Configuration conf = getConf();

        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(Task2.class);

        // set Mapper and Reducer classes
        job.setMapperClass(Map.class);
        job.setNumReduceTasks(0);

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
        ToolRunner.run(new Configuration(), new Task2(), args);
    }
}
