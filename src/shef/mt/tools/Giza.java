package shef.mt.tools;

import shef.mt.util.Logger;
import shef.mt.util.Pair;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * This class models the information stored in a giza translation file <br> In
 * order to minimise the amount of giza information we store in memory, we will
 * only hold in the translations for those words found in the source text
 *
 * @author cat
 */
public class Giza extends Resource {

    public static final Float[] PROBS_VALUES = new Float[]{0.05f, 0.01f, 0.10f, 0.20f, 0.50f};
    public static final HashSet<Float> PROBS_SET = new HashSet<Float>(Arrays.asList(PROBS_VALUES));
    /**
     * stores pairs of <word, int[] probability counts>, where int[i] = the
     * number of translations for the word that have the probability >=
     * PROBS_VALUES[i]
     */
    private static HashMap<String, int[]> transProbCount;
    private static HashMap<Pair, Float> translations;
    private static float minProb = 0.01f;
    private static float transMinProb = 0.1f;

    // in order to minimise the amount of giza information we store in memory, we will only hold in the translations for those words found in the source text
    public Giza() {
        super(null);
    }

    /**
     * intialises Giza from a file
     *
     * @param gizaFilePath
     */
    public Giza(String gizaFilePath) {
        super(null);
        Logger.log("initiating Giza from file: " + gizaFilePath);
        transProbCount = new HashMap<String, int[]>();
        translations = new HashMap<Pair, Float>();
        if (loadGiza(gizaFilePath) != -1) {
            ResourceManager.registerResource("Giza");
        }
    }

    public int loadGiza(String filePath) {
        long start = System.currentTimeMillis();
        Logger.log("Loading Giza...");
        System.out.println("Loading Giza...");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
            String line = br.readLine();
            while (line != null) {
                parseLine(line);
                line = br.readLine();
            }
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("Giza loaded in " + elapsed / 1000F + " sec");
            Logger.log("Giza loaded in " + elapsed / 1000F + " sec");
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    private void parseLine(String line) {
        String[] comps = line.split("\\s+");
        if (comps.length != 3) {
            System.err.println("Invalid giza line: " + line);
            return;
        }
        String word = comps[0];
        String trans = comps[1];
        float prob = Float.parseFloat(comps[2]);
        if (prob < minProb) {
            return;
        }
        if (!FileModel.containsWord(word)) {
            return;
        }

        if (prob >= transMinProb) {
            Pair p = new Pair(word, trans);
            translations.put(p, prob);
//			System.out.println("added translation: "+p);
        }
        //HashMap<Float,Integer> probs;
        int[] probs;
        Iterator probsIt;
        int probCount;
        int pCount; //for iterating through PROBS
        float f;
        if (transProbCount.containsKey(word)) {
            probs = transProbCount.get(word);
            for (pCount = 0; pCount < PROBS_VALUES.length; pCount++) {
                f = PROBS_VALUES[pCount];
                if (prob > f) {
                    probs[pCount]++;
                }
            }
            return;
        }


        probs = new int[PROBS_VALUES.length];
        for (pCount = 0; pCount < PROBS_VALUES.length; pCount++) {
            f = PROBS_VALUES[pCount];
            if (prob > f) {
                probs[pCount] = 1;
            } else {
                probs[pCount] = 0;
            }
        }

        transProbCount.put(word, probs);
//		System.out.println(comps[0]+" "+probs);
    }

    public void print() {
        System.out.println("Loaded Giza");
        Iterator<String> it = transProbCount.keySet().iterator();
        while (it.hasNext()) {
            String word = it.next();
            System.out.print("\r\n" + word + "\t");
            int[] values = transProbCount.get(word);
            for (int i = 0; i < values.length; i++) {
                System.out.print(PROBS_VALUES[i] + ":" + values[i] + "\t");
            }
        }
    }

    public static void main(String[] args) {
        Giza giza = new Giza(args[0]);

    }

    public static int getWordProbabilityCount(String word, float prob) {
        if (!transProbCount.containsKey(word)) {
            return 0;
        }
        float probVal = 0;
        int count = -1;
        while (prob != probVal && count < PROBS_VALUES.length - 1) {
            ++count;
            probVal = PROBS_VALUES[count];
        }
        if (prob == probVal) {
            return transProbCount.get(word)[count];
        }
        return 0;
    }

    /**
     * Retrieves the probability of a source word being translated as target
     * word
     *
     * @param sourceWord the word in the source language
     * @param targetWord the word in the target language
     * @return the probability of sourceWord to be translated as targetWord
     */
    public static float getTranslationProbability(String sourceWord, String targetWord) {
        Pair p = new Pair(sourceWord, targetWord);
        if (!translations.containsKey(p)) {
            return 0;
        } else {
            return translations.get(p).floatValue();
        }

    }

    /**
     * Returns all translations for a specified word
     *
     * @param word
     * @return translations for word
     */
    public static HashSet<String> getTranslations(String word, float thresh) {
        word = word.trim();
        HashSet<String> result = new HashSet<String>();
        Iterator<Map.Entry<Pair, Float>> it = translations.entrySet().iterator();
        Pair p;
        Map.Entry<Pair, Float> entry;
        while (it.hasNext()) {
            entry = it.next();
            p = entry.getKey();

            if (p.getKey().trim().equals(word) && entry.getValue() >= thresh) {
                result.add(p.getValue());
            }

        }

        return result;
    }
}
