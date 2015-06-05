package shef.mt.tools;

import java.util.*;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import shef.mt.util.Logger;
import shef.mt.util.StreamGobbler;

import java.io.*;

/**
 *
 * MorphAnalyser encapsulates the MADA mophological analyser for Arabic It
 * requires the MADA install path and the name of the cript to be run and
 * launches the perl script on the input file specified MADA produces its output
 * in the same folder as the input file, therefore care has to be taken that the
 * required writing rights are available in that folder
 *
 * @author Catalina Hallett
 *
 */
public class MorphAnalyser {

    String inputFile;
    String execPath;
    String execName;
    String config;
    String sentIdPath;
    Map<String, String> export;
    boolean forceRun = false;
    private static String TOK_EXT = ".Id.bw.mada.tok";   //MADA extension for the tokenised file
    private static String MADA_EXT = ".Id.bw.mada"; //MADA extension for the file containing the morphological analysis
    private static String POS_EXT = ".Id.bw.mada.pos";  //MADA extension for the file containing the POS tagging

    /*
     * @param inputFile Arabic file (not transliterated)
     * @param execPath MADA installation path
     * @param execName name of the MADA executable
     * @param config full path of the configuration file
     * @param sentIdPath full path of the script that perform sentenece ID insertion
     * @param forceRun indicates whether MorphAnalyser should perform a clean run
     */
    public MorphAnalyser(String inputFile, String execPath, String execName, String config, String sentIdPath, String export, boolean forceRun) {
        this.inputFile = inputFile;
        this.forceRun = forceRun;
        this.execPath = execPath;
        this.execName = execName;
        this.config = config;
        this.sentIdPath = sentIdPath;
        this.export = new HashMap<String, String>();
        String[] envs = export.split(",");
        String[] vars;
        for (String env : envs) {
            vars = env.split("=");
            this.export.put(vars[0], vars[1]);
        }
    }

    public void run() {
        System.out.println("Running mada on " + inputFile + " to create " + inputFile + MADA_EXT);
        File f = new File(inputFile + MADA_EXT);
        if (f.exists() && !forceRun) {
            Logger.log("Output file " + f.getPath() + " already exists. Morphological analyser will not run");
            return;
        }
        long start = System.currentTimeMillis();
        Logger.log("Running morphological analyser on input file:" + inputFile);
        Logger.log("Adding sentence IDs...");
        try {
            String outId = inputFile + ".Id";
            String[] args = new String[]{"perl", sentIdPath};
            ProcessBuilder pb = new ProcessBuilder(args);
            Process process = pb.start();
            //pipe the standard input and output to the lowercase input and output streams so it accepts input from the file
            FileOutputStream fos = new FileOutputStream(outId);
            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "STDERR");

            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "STDOUT", fos, false);
            errorGobbler.start();
            outputGobbler.start();
            if (inputFile != null) {
                BufferedReader br = new BufferedReader(new FileReader(inputFile));
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
            Logger.log("Sentence IDs addedd");
            System.out.println("Sentence IDs addedd");
            Logger.log("Running MADA...");
            args = new String[]{"perl", execName, "config=" + config, "file=" + outId};
            System.out.println("perl " + execName + " " + config + " file=" + outId);
            pb = new ProcessBuilder(args);

            pb.directory(new File(execPath));
            Map<String, String> env = pb.environment();
            env.putAll(export);
            process = pb.start();
//
            errorGobbler = new StreamGobbler(process.getErrorStream(), "STDERR", true);
            errorGobbler.start();
            StreamGobbler outGobbler = new StreamGobbler(process.getInputStream(), "STDOUT", true);
            outGobbler.start();

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(e.getStackTrace().toString());
        }
        long end = System.currentTimeMillis() - start;
        Logger.log("Finished morphological analysis in " + end / 1000f + " sec");
        System.out.println("Finished morphological analysis in " + end / 1000f + " sec");
    }

    public String getMadaOutput() {
        return inputFile + MADA_EXT;
    }

    public String getMadaTok() {
        return inputFile + TOK_EXT;
    }

    public static String getMadaPosExt() {
        return POS_EXT;
    }

    public static String getMadaExt() {
        return MADA_EXT;
    }
}
