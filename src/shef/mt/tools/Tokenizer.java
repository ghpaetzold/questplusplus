/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.tools;

import shef.mt.util.PropertiesManager;
import shef.mt.util.Logger;
import shef.mt.util.StreamGobbler;
import java.io.*;


/**
 * A wrapper around the moses tokenizer
 *
 * @author Catalina Hallett & Mariano Felice
 */
public class Tokenizer extends Resource {

    private String input;
    private String output;
    private String tokPath;
    private String lang;
    private boolean forceRun = false;
    private static PropertiesManager resourceManager;

    public Tokenizer(String input, String output, String tokPath, String lang, boolean run) {
        super(null);
        this.input = input;
        this.output = output;
        this.tokPath = tokPath;
        this.forceRun = run;
        this.lang = lang;
    }

    public String getTok() {
        return output;
    }

    public void run() {
        System.out.println("running tokenizer on " + input);
        File f = new File(output);
        


        long start = System.currentTimeMillis();

        try {
            System.out.println(tokPath);


            //run lowercase first into a temporary file
            String tempOut = output + ".temp";
            //pipe the standard input and output to the lowercase input and output streams so it accepts input from the file
            FileOutputStream fos = new FileOutputStream(tempOut);

            //now run the tokenizer
            System.out.println("tokenizing...");
            Logger.log("Tokenizing the input...");
            String tokCmd = "perl " + tokPath + " -q -l " + lang;
            System.err.println(tokCmd);
            String[] args = tokCmd.split("\\s+");
            ProcessBuilder pb = new ProcessBuilder(args);
            Process process = pb.start();
            Logger.log("Executing: " + process.toString());

            // Create the final output file
            fos = new FileOutputStream(output);
            

            // any error message from the process?
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "STDERR");
            // any output from the process?
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "STDOUT", fos);

            // Start listeners for the process's errors and output
            errorGobbler.start();
            outputGobbler.start();

            // Process any input to the process
            if (input != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(input), "utf-8"));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(process.getOutputStream())), true);

                // Send input to the process
                int ch;
                while ((ch = br.read()) != -1) {
                    writer.print((char) ch);
                    //System.out.print((char)ch);
                }
                writer.flush();
                writer.close();
                br.close();
            }

            //process.getOutputStream().close();

            // Let the process finish
            process.waitFor();

            // Wait until we're done with remaining error and output
            errorGobbler.join();
            outputGobbler.join();

            fos.close();

            System.out.println("done");

            //we don't need the lowercase temporary output, we can delete it
            f = new File(tempOut);
            f.delete();

        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(e.getStackTrace().toString());
        }

        long end = System.currentTimeMillis() - start;
        Logger.log("Finished tokenising in " + end / 1000f + " sec");

    }

    public static void main(String[] args) {
        Tokenizer et = new Tokenizer(args[0], args[1], args[2], args[3], true);
        et.run();
    }
}
