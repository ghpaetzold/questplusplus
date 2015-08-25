package shef.mt.features.impl.wce;

import java.util.HashMap;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1008 extends WordLevelFeature {

    public WordLevelFeature1008() {
        this.setIndex("WCE1008");
        this.setIdentifier("SRCALIGN");
        this.setDescription("Source word aligned to each target word.");
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
            
            //Determine feature value:
            String value = this.getIdentifier() + "=";
            
            //Determine if aligned word exists:
            if(alignedIndex!=null){
                //Get aligned word:
                String alignedWord = sourceTokens[alignedIndex];
                
                //Determine appropriate prefix:
                if(usedAlignments.get(alignedIndex)==null){
                    value += "B-" + alignedWord;
                    usedAlignments.put(alignedIndex, 1);
                }else{
                    value += "I-" + alignedWord;
                }
            }else{
                //Add empty alignment
                value += "O";
            }
            
            //Save value:
            result[i] = value;
        }

        //Save values produced:
        this.setValues(result);
    }
}
