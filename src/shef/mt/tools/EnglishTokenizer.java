/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.tools;

import shef.mt.util.Logger;
import shef.mt.util.StreamGobbler;
import java.io.*;


/**
 * A wrapper around the moses tokenizer
 *
 * @author cat
 */
public class EnglishTokenizer extends Resource {

    private String input;
    private String output;
    private String lowercasePath;
    private String tokPath;
    private String lang;
    private boolean forceRun = false;

    public EnglishTokenizer(String input, String output, String lowercasePath, String tokPath, String lang, boolean run) {
        super(null);
        this.input = input;
        this.output = output;
        this.lowercasePath = lowercasePath;
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
        if (f.exists() && !forceRun) {
            Logger.log("Output file " + output + " already exists. Tokenizer will not run");
            System.out.println("Output file " + output + " already exists. Tokenizer will not run");
            return;
        }


        long start = System.currentTimeMillis();

        try {
            System.out.println(lowercasePath);
            System.out.println(tokPath);
            Logger.log("Transforming the input to lower case...");

            //run lowercase first into a temporary file
            String tempOut = output + ".temp";
            System.out.println("running lowercase");
            String[] args = new String[]{"perl", lowercasePath, "-l", lang};

            ProcessBuilder pb = new ProcessBuilder(args);
            Process process = pb.start();
            Logger.log("Executing: " + process.toString());


            //pipe the standard input and output to the lowercase input and output streams so it accepts input from the file
            FileOutputStream fos = new FileOutputStream(tempOut);
            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "STDERR");

            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "STDOUT", fos);
            errorGobbler.start();
            outputGobbler.start();
            if (input != null) {
                BufferedReader br = new BufferedReader(new FileReader(input));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(process.getOutputStream())), true);
                int ch;
                //    	String line;
                while ((ch = br.read()) != -1) {
                    writer.print((char) ch);
                    //    		System.out.print((char)ch);
                }
                writer.flush();
                writer.close();
                br.close();
            }
            process.getOutputStream().close();

            // inputGobbler.start();

            process.waitFor();

            fos.close();

            System.out.println("done");

            //finished running lowercase
            //now run the tokenizer
            System.out.println("tokenizing...");
            System.out.println("perl " + tokPath + " -l " + lang + " " + tempOut);
            Logger.log("Tokenizing the input...");
            args = new String[]{"perl", tokPath, "-l", lang};
            pb = new ProcessBuilder(args);
            process = pb.start();

            Logger.log("Executing: " + process.toString());

            if (input != null) {
                BufferedReader br = new BufferedReader(new FileReader(tempOut));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(process.getOutputStream())), true);
                String line;
                while ((line = br.readLine()) != null) {
                    writer.println(line);
                    //                      		System.out.println(line);
                }
                writer.flush();
                writer.close();
                br.close();
            }
            process.getOutputStream().close();

//                    fis = new FileInputStream(tempOut);
//                    inputGobbler = new StreamGobbler(fis,"INPUT",process.getOutputStream());
            fos = new FileOutputStream(output);
            // any error message?
            errorGobbler = new StreamGobbler(process.getErrorStream(), "STDERR");

            // any output?
            outputGobbler = new StreamGobbler(process.getInputStream(), "STDOUT", fos);

//                    inputGobbler.start();
            errorGobbler.start();
            outputGobbler.start();
            process.waitFor();
//                    fis.close();
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
        EnglishTokenizer et = new EnglishTokenizer(args[0], args[1], args[2], args[3], args[4], true);
        et.run();
    }
}
