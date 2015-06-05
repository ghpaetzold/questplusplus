
package shef.mt.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import shef.mt.util.Logger;

/**
 *
 * @author gustavo
 */
public class NGramExecIRSTLM {
 
    private static String path;
    private static boolean forceRun = false;

    public NGramExecIRSTLM(String path) {
        this.path = path;
    }

    public NGramExecIRSTLM(String path, boolean forceRun) {
        this.path = path;
        this.forceRun = forceRun;
    }

    public void ForceRun(boolean val) {
        forceRun = val;
    }

    public void runNGramPerplex(String inputFile, String outputFile, String lmFile, int nSize){
        File f = new File(outputFile);
        if (f.exists()) {
            Logger.log("Output file " + outputFile + " already exists. Ngram will not run");
            return;
        }
        long start = System.currentTimeMillis();

        Logger.log("Running ngram for computing perplexities on input file:" + inputFile + " with lm file: " + lmFile);
        String execProcess = path + "score-lm -lm " + lmFile;
        Logger.log("Executing: " + execProcess);
        
        try {
            String[] args = new String[]{path + "score-lm", "-lm"};
            
            BufferedWriter outf = new BufferedWriter(new FileWriter(new File(outputFile)));
            BufferedReader inf = new BufferedReader(new FileReader(new File(inputFile)));
            
            Process process = new ProcessBuilder(args).start();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            while(inf.ready()){
                String line = inf.readLine().trim();
                bw.write(line + "\n");
                bw.flush();
                String data = br.readLine().trim();
                outf.write("SENTENCE_PLACE_HOLDER\n");
                outf.write("OOVs\n");
                outf.write("0 zeroprobs, logprob= "+ data +" ppl= "+ data +" ppl1= " + data + "\n\n");
            }
            
            outf.close();
            inf.close();
            br.close();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(e.getStackTrace().toString());
        }

        long end = System.currentTimeMillis() - start;
        Logger.log("Finished computing perplexities in " + end / 1000f + " sec");

    }

    public void runNGramPerplex(String inputFile, String outputFile, String lmFile) {
        File f = new File(outputFile);
        if (f.exists()) {
            Logger.log("Output file " + outputFile + " already exists. Ngram will not run");
            return;
        }
        long start = System.currentTimeMillis();

        Logger.log("Running ngram for computing perplexities on input file:" + inputFile + " with lm file: " + lmFile);
        String execProcess = path + "score-lm -lm " + lmFile;
        Logger.log("Executing: " + execProcess);
        
        try {
            String[] args = new String[]{path + "score-lm", "-lm", lmFile};
            
            BufferedWriter outf = new BufferedWriter(new FileWriter(new File(outputFile)));
            BufferedReader inf = new BufferedReader(new FileReader(new File(inputFile)));
            
            Process process = new ProcessBuilder(args).start();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            while(inf.ready()){
                String line = inf.readLine().trim();
                bw.write(line + "\n");
                bw.flush();
                String data = br.readLine().trim();
                outf.write("SENTENCE_PLACE_HOLDER\n");
                outf.write("OOVs\n");
                outf.write("0 zeroprobs, logprob= "+ data +" ppl= "+ data +" ppl1= " + data + "\n\n");
            }
            
            outf.close();
            inf.close();
            br.close();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(e.getStackTrace().toString());
        }

        long end = System.currentTimeMillis() - start;
        Logger.log("Finished computing perplexities in " + end / 1000f + " sec");

    }
    
}
