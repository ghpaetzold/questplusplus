/**
 *
 */
package shef.mt.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * Processes a language model file The result of this processing will be a new
 * file containing at the top information about the cut-off frequencies in
 * various portions of the language model, followed by those entries in the
 * original file that have the frequency larger than a given threshold
 *
 * @author cat
 *
 *
 *
 */
public class NGramSorter {

    private static HashMap<String, Integer> ngrams;
    private static ArrayList[] freqs;
    private static int[][] cutOffs;	//for storing cut-off frequencies

    public NGramSorter() {
    }

    public static void run(String corpus, int sliceNo, int nSize, int minFreq, String output) {
        ngrams = new HashMap<String, Integer>();
        freqs = new ArrayList[nSize];
        for (int i = 0; i < nSize; i++) {
            freqs[i] = new ArrayList();
        }
        cutOffs = new int[nSize][sliceNo];
//        System.out.println("Sorting ngrams ...");
        long start = System.currentTimeMillis();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(corpus), "utf-8"));
            String line = br.readLine();
            String[] lineSplit;
            while (line.trim().isEmpty()) {
                line = br.readLine();
            }

            while (line != null) {
                if (!line.trim().isEmpty()) {
                    lineSplit = line.split("\t");
                    //System.out.println(line);
                    int freq = Integer.parseInt(lineSplit[1]);
                    int ngSize = lineSplit[0].split(" ").length - 1;
                    if (freq > minFreq) {
                        ngrams.put(lineSplit[0], freq);
                        //System.out.println(ngSize+"\t"+freq);
                        freqs[ngSize].add(freq);
                        //			System.out.println(lineSplit[0]+"  "+ngSize+" "+freq);
                    }
                    line = br.readLine();
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        computeCutoffs(sliceNo, nSize);
        writeToFile(output, sliceNo, nSize);
    }

    public static void computeCutoffs(int sliceNo, int nSize) {
        for (int i = 0; i < nSize; i++) {
            Collections.sort(freqs[i]);
            int size = freqs[i].size() - 1;

            for (int j = 0; j < sliceNo; j++) {
//                System.out.println("i=" + i + "j=" + j + " size=" + size + " " + (j + 1) * size / sliceNo + " " + freqs[i].get((j + 1) * size / sliceNo));
                cutOffs[i][j] = ((Integer) freqs[i].get((int) Math.floor((j + 1) * size / sliceNo))).intValue();
            }
        }
    }

    private static void writeToFile(String output, int sliceNo, int nSize) {
        try {
            BufferedWriter bwOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output + ".clean"), "utf-8"));
            for (int i = 0; i < nSize; i++) {
                bwOut.write(i + "-gram\t");
                for (int val = 0; val < sliceNo; val++) {
//                    System.out.println("i=" + (i + 1) + " val=" + val + " " + cutOffs[i][val] + "\t");
                    bwOut.write(cutOffs[i][val] + "\t");
                }
                bwOut.write("\r\n");
            }


            Iterator<String> it = ngrams.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                bwOut.write(key + "\t" + ngrams.get(key) + "\r\n");
            }
            bwOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length == 5) {
            NGramSorter.run(args[0], new Integer(args[1]).intValue(), new Integer(args[2]).intValue(), new Integer(args[3]).intValue(), args[4]);
        } else {
            NGramSorter.run(args[0], 4, 3, 2, args[1]);
        }
    }
}
