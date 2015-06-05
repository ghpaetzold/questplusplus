/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.tools;

import java.io.*;

/**
 * This is a wrapper around OpenFst functionality. The operations it exposes are
 * fstcompile, fstnbest, fsttopsort and fstprint
 *
 * @author Catalina Hallett
 *
 *
 */
public class OpenFstWrapper {

    private static String FST_EXT = ".fst";
    private static String NBEST_EXT = ".nbest";
    private static String SORT_EXT = ".sort";
    private static String PRINT_EXT = ".txt";

    public static void compile(String path, String inputFile, String isyms, String osyms, String output, boolean forceRun) {
        try {
            //  	System.out.println("running fstcompile to "+output);
            File f = new File(output);
            if (f.exists() && !forceRun) {
                return;
            }
            String[] args = new String[]{path + File.separator + "fstcompile", "--isymbols=" + isyms, "--osymbols=" + osyms, "--keep_isymbols", "--keep_osymbols", inputFile, output};
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.directory(new File(path));
            Process process = pb.start();
            InputStream stderr = process.getErrorStream();
            String line;
            BufferedReader brCleanUp =
                    new BufferedReader(new InputStreamReader(stderr));
            while ((line = brCleanUp.readLine()) != null) {
                System.out.println("[Stderr] " + line);
            }
            brCleanUp.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shortestpath(String path, String inputFile, String output, int n, boolean forceRun) {
        //	  System.out.println("running fstshortestpath to "+output);
        try {
            File f = new File(output);
            if (f.exists() && !forceRun) {
                return;
            }
            String[] args = new String[]{path + File.separator + "fstshortestpath", "--nshortest=" + n, inputFile, output};
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.directory(new File(path));
            Process process = pb.start();
            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void topsort(String path, String inputFile, String output, boolean forceRun) {
        //  	System.out.println("running fsttopsort to "+output);
        try {
            File f = new File(output);
            if (f.exists() && !forceRun) {
                return;
            }
            String[] args = new String[]{path + File.separator + "fsttopsort", inputFile, output};
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.directory(new File(path));
            Process process = pb.start();
            process.waitFor();
            InputStream stderr = process.getErrorStream();
            String line;
            BufferedReader brCleanUp =
                    new BufferedReader(new InputStreamReader(stderr));
            while ((line = brCleanUp.readLine()) != null) {
                System.out.println("[Stderr] " + line);
            }
            brCleanUp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void print(String path, String inputFile, String output, String isyms, String osyms, boolean forceRun) {
        //System.out.println("running fstprint to "+output);
        try {
            File f = new File(output);
            if (f.exists() && !forceRun) {
                return;
            }
            String[] args = new String[]{path + File.separator + "fstprint", "--isymbols=" + isyms, "--osymbols=" + osyms, inputFile, output};
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.directory(new File(path));
            Process process = pb.start();
            InputStream stderr = process.getErrorStream();
            String line;
            /*			BufferedReader brCleanUp =
             new BufferedReader (new InputStreamReader (stderr));
             while ((line = brCleanUp.readLine ()) != null) {
             System.out.println ("[Stderr] " + line);
             }
             brCleanUp.close();*/
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void run(String path, String inputFile, String isyms, String osyms, String output, int n, boolean forceRun) {
        String compiledFST = inputFile + FST_EXT;
        compile(path, inputFile, isyms, osyms, compiledFST, forceRun);
        String nbestFST = inputFile + NBEST_EXT;
        shortestpath(path, compiledFST, nbestFST, n, forceRun);
        String sortFST = inputFile + SORT_EXT;
        topsort(path, nbestFST, sortFST, forceRun);
        print(path, sortFST, output, isyms, osyms, forceRun);

    }
}
