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
 * A wrapper around the moses caser
 *
 * @author Catalina Hallett & Mariano Felice
 */
public class Caser extends Resource {

    private String input;
    private String output;
    private String lowercasePath;
    private String tokPath;
    
    private boolean forceRun = false;
    private static PropertiesManager resourceManager;

    public Caser(String input, String output, String lowercasePath, boolean run) {
        super(null);
        this.input = input;
        this.output = output;
        this.lowercasePath = lowercasePath;
        this.forceRun = run;
        
    }

    public String getCaser() {
        return output;
    }

    public void run() {
        System.out.println("running "+lowercasePath+" on " + input);
        File f = new File(output);
        


        long start = System.currentTimeMillis();

        try {


            //run lowercase first into a temporary file
            String tempOut = output + ".temp";
            FileOutputStream fos = new FileOutputStream(tempOut);
           // Logger.log("Transforming the input to lower case...");
            //Logger.log("Transforming the input to true case...");


          //  System.out.println("running lowercase");
            //System.out.println("running truecase");
           // String[] args = new String[]{"perl", lowercasePath, "-l", lang};
            //String[] truecaseOptions = lowercasePath.split("\\|");
            //args = new String[]{"perl",truecaseOptions[0], "--model", truecaseOptions[1]};
            //System.err.println(lowercasePath);
            String[] args = ("perl " + lowercasePath).split("\\s+");
            ProcessBuilder pb = new ProcessBuilder(args);
            Process process = pb.start();
            Logger.log("Executing: " + process.toString());

            // Create the final output file
            fos = new FileOutputStream(output);

            // any error message form the process?
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
            process.getOutputStream().close();

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
        Logger.log("Finished casing in " + end / 1000f + " sec");

    }

    public static void main(String[] args) {
        Caser et = new Caser(args[0], args[1], args[2], true);
        et.run();
    }
}
