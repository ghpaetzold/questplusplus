/**
 *
 */
package shef.mt.tools;

import shef.mt.util.Logger;
import java.io.*;

/**
 * This is a wrapper around the SRILM ngram applications
 *
 * @author cat
 *
 */
public class NGramExec {

    private static String path;
    private static boolean forceRun = false;

    public NGramExec(String path) {
        this.path = path;
    }

    public NGramExec(String path, boolean forceRun) {
        this.path = path;
        this.forceRun = forceRun;
    }

    public void ForceRun(boolean val) {
        forceRun = val;
    }

    public void runNGramCount(String inputFile, String outputFile, int nSize) {
        File f = new File(outputFile);
        if (f.exists() && !forceRun) {
//			System.out.println("Output file "+outputFile+" already exists. Ngram will not run");
            Logger.log("Output file " + outputFile + " already exists. Ngram will not run");
            return;
        }
        long start = System.currentTimeMillis();
        Logger.log("Running ngram on input file:" + inputFile);
        String execProcess = path + "ngram-count -order " + nSize + " -interpolate -text " + inputFile + " -lm " + outputFile;
//		System.out.println(execProcess);
        Logger.log("Executing: " + execProcess);
        try {
            String[] args = new String[]{path + "ngram-count", "-order", nSize + "", "-interpolate", "-text", inputFile, "-lm", outputFile};
            ProcessBuilder pb = new ProcessBuilder(args);

            //			pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(e.getStackTrace().toString());
        }
        long end = System.currentTimeMillis() - start;
        Logger.log("Finished computing perplexities in " + end / 1000f + " sec");
    }

    public void runNGramPerplex(String inputFile, String outputFile, String lmFile, int nSize) {
        //             System.out.println("running ngram perplexities on input file:"+inputFile+" with lm file: "+lmFile);
        File f = new File(outputFile);
        if (f.exists()) {
            Logger.log("Output file " + outputFile + " already exists. Ngram will not run");
//			System.out.println("Output file "+outputFile+" already exists. Ngram will not run");
            return;
        }
        long start = System.currentTimeMillis();

        Logger.log("Running ngram for computing perplexities on input file:" + inputFile + " with lm file: " + lmFile);
        String execProcess = path + "ngram -lm " + lmFile + " -order 3 -debug 1 -ppl " + inputFile + " > " + outputFile;
        Logger.log("Executing: " + execProcess);
        //              	System.out.println("Executing: "+execProcess);
        try {
            String[] args = new String[]{path + "ngram", "-lm", lmFile, "-order", nSize + "", "-debug", "1", "-ppl", inputFile};
            FileWriter fw = new FileWriter(outputFile);
            Process process = new ProcessBuilder(args).start();
            process.waitFor();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            //	       System.out.printf("Output of running %s is:", java.util.Arrays.toString(args));

            while ((line = br.readLine()) != null) {
                fw.write(line);
                fw.write("\r\n");
            }
            fw.close();
            br.close();
            isr.close();
            is.close();
            /*

             Process p = Runtime.getRuntime().exec(execProcess);
             while (!f.exists());
             */
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(e.getStackTrace().toString());
        }

        long end = System.currentTimeMillis() - start;
        Logger.log("Finished computing perplexities in " + end / 1000f + " sec");

    }

    public void runNGramPerplex(String inputFile, String outputFile, String lmFile) {
        File f = new File(outputFile);
        System.out.println("Executing ngramperplex on " + inputFile + " with lm=" + lmFile + " into " + outputFile);
        if (!this.forceRun && f.exists()) {
            Logger.log("Output file " + outputFile + " already exists. Ngram will not run");
            System.out.println("Output file " + outputFile + " already exists. Ngram will not run");
            return;
        }
        long start = System.currentTimeMillis();

        Logger.log("Running ngram for computing perplexities on input file:" + inputFile + " with lm file: " + lmFile);
        String execProcess = path + "ngram -lm " + lmFile + " -order 3 -debug 1 -ppl " + inputFile + " > " + outputFile;
        Logger.log("Executing: " + execProcess);
        try {
            String[] args = new String[]{path + "ngram", "-lm", lmFile, "-order 3", "-debug", "1", "-ppl", inputFile, " > ", outputFile};
            FileWriter fw = new FileWriter(outputFile);
            Process process = new ProcessBuilder(args).start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            //	       System.out.printf("Output of running %s is:", java.util.Arrays.toString(args));

            while ((line = br.readLine()) != null) {
                fw.write(line);
                fw.write("\r\n");
            }
            process.waitFor();
            fw.close();
            br.close();
            isr.close();
            is.close();
            /*

             Process p = Runtime.getRuntime().exec(execProcess);
             while (!f.exists());
             */
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(e.getStackTrace().toString());
        }

        long end = System.currentTimeMillis() - start;
        Logger.log("Finished computing perplexities in " + end / 1000f + " sec");

    }
}
