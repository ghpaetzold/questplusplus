package shef.mt.tools;

import shef.mt.util.Logger;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;
import java.util.Scanner;
import java.util.HashMap;
import java.io.InputStreamReader;

/**
 * @author cbuck@lantis.de
 */
public class GlobalLexicon extends Resource {
    
    private static HashMap<String, HashMap<String, Double>> weights;
    private Integer n_weights;
    
    private void readFile(String filename, Double minvalue) throws 
            java.io.FileNotFoundException, 
            java.io.UnsupportedEncodingException, 
            java.io.IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new GZIPInputStream(new FileInputStream(filename)), "utf-8"));
        Scanner scan = new Scanner(br);
        n_weights = 0;
        while (scan.hasNext()) {
            String targetWord = scan.next();
            String sourceWord = scan.next();
            double weight = scan.nextDouble();
            if (Math.abs(weight) >= minvalue) {
                continue;
            }
            if (!sourceWord.equals("**BIAS**") && !FileModel.containsWord(sourceWord)) {
                continue;
            }
            if (!weights.containsKey(sourceWord)) {
                weights.put(sourceWord, new HashMap<String, Double>());
            }
            weights.get(sourceWord).put(targetWord, weight);
            n_weights++;
        }
    }
    
    public Integer size() {
        return n_weights;
    }
    
    public static Double get(final String sourceWord, final String targetWord) {
        if (weights.containsKey(sourceWord)) {
            if (weights.get(sourceWord).containsKey(targetWord)) {
                return weights.get(sourceWord).get(targetWord);
            }
        }
        return 0.0;
    }
    
    public static Double getBias(final String targetWord) {
        return get("**BIAS**", targetWord);
    }
    
    public GlobalLexicon(final String glfilename, final Double minweight) {
        super(null);
        long start = System.currentTimeMillis();
        Logger.log("Loading GlobalLexicon from file: " + glfilename);
        System.out.println("Loading GlobalLexicon from file: " + glfilename);
        weights = new HashMap<String, HashMap<String, Double>>();
        try {
            readFile(glfilename, 0.5);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        long elapsed = System.currentTimeMillis() - start;
        Logger.log("GlobalLexicon loaded " + weights.size() + 
                " weights in " + elapsed / 1000F + " sec");
        System.out.println("GlobalLexicon loaded " + weights.size() + 
                " weights in " + elapsed / 1000F + " sec");
        ResourceManager.registerResource("GlobalLexicon");
    }

    public static void main(String[] args) {
        Double minweight = 0.0;
        GlobalLexicon gl = new GlobalLexicon(args[0], minweight);
    }
}

