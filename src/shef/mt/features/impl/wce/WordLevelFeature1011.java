package shef.mt.features.impl.wce;

import java.util.HashMap;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1011 extends WordLevelFeature {

    public WordLevelFeature1011() {
        this.setIndex("WCE1011");
        this.setIdentifier("TRGALIGNLEFT2");
        this.setDescription("Second leftmost target word along with aligned source word.");
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
            String leftTarget = this.getLeftTarget(targetTokens, i);

            //Create value:
            String value = this.getIdentifier() + "=" + alignedWord + "/" + leftTarget;

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

    private String getLeftTarget(String[] tokens, int i) {
        if(i<2){
            return "<s>";
        }else{
            return tokens[i-2];
        }
    }
}
