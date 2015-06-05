package shef.mt.tools;

import shef.mt.util.Logger;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * This class models the information stored in an alternative giza translation
 * file <br> In order to minimise the amount of giza information we store in
 * memory, we will only hold in the translations for those words found in the
 * source text
 *
 * @author cat
 */
public class Giza2 extends Resource {

    public static final Float[] PROBS_VALUES = new Float[]{0.05f, 0.01f, 0.10f, 0.20f, 0.50f};
    public static final HashSet<Float> PROBS_SET = new HashSet<Float>(Arrays.asList(PROBS_VALUES));
    private static HashMap<String, int[]> translations;
    private static float minProb = 0.01f;

    // in order to minimise the amount of giza information we store in memory, we will only hold in the translations variable those words found in the source text
    public Giza2() {
        super(null);
    }

    public Giza2(String gizaFilePath) {
        super(null);
        Logger.log("Initiating Giza from file..." + gizaFilePath);
        translations = new HashMap<String, int[]>();
        if (loadGiza(gizaFilePath) != -1) {
            ResourceManager.registerResource("Giza2");
        }
    }

    public int loadGiza(String filePath) {
        long start = System.currentTimeMillis();
        System.out.println("Loading Giza...");
        Logger.log("Loading Giza...");
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
        String[] comps = line.split(" ");
        String word = comps[0];
        String trans = comps[1];
        float prob = Float.parseFloat(comps[2]);
        if (prob < minProb) {
            return;
        }
        if (!FileModel.containsWord(word)) {
            return;
        }
        //HashMap<Float,Integer> probs;
        int[] probs;
        Iterator probsIt;
        int probCount;
        int pCount; //for iterating through PROBS
        float f;
        if (translations.containsKey(word)) {
            probs = translations.get(word);
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

        translations.put(word, probs);
//		System.out.println(comps[0]+" "+probs);
    }

    public void print() {
        System.out.println("Loaded Giza");
        Iterator<String> it = translations.keySet().iterator();
        while (it.hasNext()) {
            String word = it.next();
            System.out.print("\r\n" + word + "\t");
            int[] values = translations.get(word);
            for (int i = 0; i < values.length; i++) {
                System.out.print(PROBS_VALUES[i] + ":" + values[i] + "\t");
            }
        }
    }

    public static void main(String[] args) {
        Giza giza = new Giza(args[0]);

    }

    public static int getWordProbabilityCount(String word, float prob) {
        if (!translations.containsKey(word)) {
            return 0;
        }
        float probVal = 0;
        int count = -1;
        while (prob != probVal && count < PROBS_VALUES.length - 1) {
            ++count;
            probVal = PROBS_VALUES[count];
        }
        if (prob == probVal) {
            return translations.get(word)[count];
        }
        return 0;
    }
}
