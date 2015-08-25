package shef.mt.features.impl.wce;

import java.util.HashMap;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1014 extends WordLevelFeature {

    public WordLevelFeature1014() {
        this.setIndex("WCE1014");
        this.setIdentifier("TRGALIGNRIGHT2");
        this.setDescription("Second rightmost target word along with aligned source word.");
        this.addResource("alignments.file");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];

        //Get alignments object:
        HashMap<Integer, Integer> alignments = (HashMap<Integer, Integer>) target.getValue("alignments.file");

        //Initialized used source word hash:
        HashMap<Integer, Integer> usedAlignments = new HashMap<>();

        //Ge tokens from sentences:
        String[] sourceTokens = source.getTokens();
        String[] targetTokens = target.getTokens();

        //Output word occurrences:
        for (int i = 0; i < targetTokens.length; i++) {
            //Get index of aligned word in source sentence:
            Integer alignedIndex = alignments.get(i);
            
            //Get aligned word:
            String alignedWord = this.getAlignedWord(sourceTokens, alignedIndex);

            //Get left target word:
            String rightTarget = this.getRightTarget(targetTokens, i);

            //Create value:
            String value = this.getIdentifier() + "=" + alignedWord + "/" + rightTarget;

            //Save value:
            result[i] = value;
        }

        //Save values produced:
        this.setValues(result);
    }
    
    private String getAlignedWord(String[] tokens, Integer i) {
        if(i==null){
            return "NULL";
        }else{
            return tokens[i];
        }
    }

    private String getRightTarget(String[] tokens, int i) {
        if(i>tokens.length-3){
            return "<s>";
        }else{
            return tokens[i+2];
        }
    }
}
