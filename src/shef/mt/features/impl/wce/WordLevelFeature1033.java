package shef.mt.features.impl.wce;

import java.util.HashMap;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1033 extends WordLevelFeature {

    private double t;
    public final Float[] PROBS_VALUES = new Float[]{0.05f, 0.10f, 0.20f, 0.50f};

    public WordLevelFeature1033() {
        t = 0.05;
        this.setIndex("WCE1033");
        this.setIdentifier("TRCOUNT5");
        this.setDescription("Number of translations of each target word given a probability threshold of 5%.");
        this.addResource("source.translationProbs");
        this.addResource("alignments.file");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];

        //Ge tokens from sentences:
        String[] sourceTokens = source.getTokens();
        String[] targetTokens = target.getTokens();

        //Get alignments object:
        HashMap<String, int[]> probabilities = (HashMap<String, int[]>) target.getValue("translationcounts");
        
        //Get alignments:
        HashMap<Integer, Integer> alignments = (HashMap<Integer, Integer>) target.getValue("alignments.file");

        //For each token, check if it has a dependency link:
        for (int i = 0; i < targetTokens.length; i++) {
            //Get index of aligned word in source sentence:
            Integer alignedIndex = alignments.get(i);
            
            //Get string value:
            String value;
            if(alignedIndex==null){
                value = this.getIdentifier() + "=0";
            }else{
                String alignedWord = sourceTokens[alignedIndex];
                Integer targetCount = this.getProbabilityCount(alignedWord, this.t, probabilities);
                value = this.getIdentifier() + '=' + targetCount;
            }
            
            //Save value:
            result[i] = value;
        }

        //Save values produced:
        this.setValues(result);
    }

    private Integer getProbabilityCount(String word, double prob, HashMap<String, int[]> probabilities) {
        if (!probabilities.containsKey(word)) {
            return 0;
        }
        float probVal = 0;
        int count = -1;
        while (prob != probVal && count < PROBS_VALUES.length - 1) {
            ++count;
            probVal = PROBS_VALUES[count];
        }
        if (prob == probVal) {
            return probabilities.get(word)[count];
        }
        return 0;
    }
}
