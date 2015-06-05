/**
 *
 */
package shef.mt.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import shef.mt.util.Logger;

/**
 * This class processes a file containing ngrams and produces a LanguageModel
 *
 * @author Catalina Hallett
 *
 */
public class NGramProcessor {

    private String corpus;
    private static int nSize = 3;
    private int[] ngramNos;

    public NGramProcessor(String corpus) {
        this.corpus = corpus;
        ngramNos = new int[nSize];
    }

    public LanguageModel run() {

        System.out.println("Loading language model...");
        Logger.log("Loading language model...");
        long start = System.currentTimeMillis();
        LanguageModel lm = new LanguageModel(nSize);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(corpus), "utf-8"));

            String[] lineSplit;
            int[][] cutOffs = new int[nSize][4];
            String line = br.readLine();
            Logger.log("Cut-off frequencies:");
            for (int i = 0; i < nSize; i++) {
                lineSplit = line.split("\t");
//			System.out.println(line+" "+lineSplit[0]);
                Logger.log(i + "-grams:");
                for (int j = 0; j < 4; j++) {
                    cutOffs[i][j] = Integer.parseInt(lineSplit[j + 1]);
                    Logger.log("quartile " + (j + 1) + ": " + cutOffs[i][j]);
//			System.out.println(i+" "+j +" freq="+cutOffs[i][j]);
                }
                line = br.readLine();
            }
            lm.setCutOffs(cutOffs);
            while (line.trim().isEmpty()) {
                line = br.readLine();
            }

            while (line != null) {
                if (!line.trim().isEmpty()) {
                    lineSplit = line.split("\t");
                    //System.out.println(line);
                    lm.addNGram(lineSplit[0], Integer.parseInt(lineSplit[1]));
//                    System.out.println("T: " + lineSplit[0] + ", F: " + lineSplit[1]);
                    line = br.readLine();
                }
            }
            ResourceManager.registerResource("ngramcount");

            br.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
        long elapsed = System.currentTimeMillis() - start;
        Logger.log("Language model loaded in " + elapsed / 1000F + " sec");
        System.out.println("Language model loaded in " + elapsed / 1000F + " sec");
        return lm;
    }
}
