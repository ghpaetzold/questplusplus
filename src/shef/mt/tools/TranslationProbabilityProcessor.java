package shef.mt.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * Adds translation probability information to the sentences processed.
 * @author GustavoH
 */
public class TranslationProbabilityProcessor extends ResourceProcessor {

    public final Float[] PROBS_VALUES = new Float[]{0.05f, 0.10f, 0.20f, 0.50f};
    private HashMap<String, int[]> transProbCount;

    public TranslationProbabilityProcessor(String probs_path) {
        //Create map:
        this.transProbCount = new HashMap<>();
        try {
            //Open probabilities file:
            BufferedReader br = new BufferedReader(new FileReader(probs_path));

            //Read file:
            while (br.ready()) {
                //Get data from line:
                String[] data = br.readLine().trim().split("\t");
                if (data.length == 3) {
                    String source = data[0].trim();
                    String target = data[1].trim();
                    double prob = Math.exp(Double.parseDouble(data[2].trim()));

                    //Save data:
                    if (prob > 0.01) {
                        if (transProbCount.containsKey(source)) {
                            int[] probs = transProbCount.get(source);
                            for (int i = 0; i < PROBS_VALUES.length; i++) {
                                float aux = PROBS_VALUES[i];
                                if (prob > aux) {
                                    probs[i]++;
                                }
                            }
                            transProbCount.put(source, probs);
                        } else {
                            int[] probs = new int[PROBS_VALUES.length];
                            for (int i = 0; i < PROBS_VALUES.length; i++) {
                                float aux = PROBS_VALUES[i];
                                if (prob > aux) {
                                    probs[i] = 1;
                                } else {
                                    probs[i] = 0;
                                }
                            }
                            transProbCount.put(source, probs);
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("ERROR: Failed to open conditional probabilities file!");
            Logger.getLogger(TranslationProbabilityProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("ERROR: Failed to read conditional probabilities file!");
            Logger.getLogger(TranslationProbabilityProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processNextSentence(Sentence target) {
        //Add resource to sentence:
        target.setValue("translationcounts", this.transProbCount);
    }

    @Override
    public void processNextDocument(Doc source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
