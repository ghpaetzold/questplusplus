package shef.mt.features.impl.wce;

import java.util.HashMap;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1031 extends WordLevelFeature {

    public WordLevelFeature1031() {
        this.setIndex("WCE1031");
        this.setIdentifier("SRCSENSECOUNT");
        this.setDescription("Number of senses of the source word aligned to each target word.");
        this.addResource("tools.universalwordnet.path");
        this.addResource("alignments.file");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];

        //Ge tokens from sentences:
        String[] targetTokens = target.getTokens();
        
        //Get alignments object:
        HashMap<Integer, Integer> alignments = (HashMap<Integer, Integer>) target.getValue("alignments.file");
        
        //Get sense counts for target tokens:
        int[] senseCounts = (int[]) source.getValue("tools.universalwordnet.path");

        //For each token, check if it has a dependency link:
        for (int i = 0; i < targetTokens.length; i++) {
            //Get index of aligned word in source sentence:
            Integer alignedIndex = alignments.get(i);
            
            //Get aligned word's sense count:
            int sourceSenseCount = 0;
            if(alignedIndex!=null){
                sourceSenseCount = senseCounts[alignedIndex];
            }
            
            //Save value:
            String value = this.getIdentifier() + '=' + sourceSenseCount;
            result[i] = value;
        }

        //Save values produced:
        this.setValues(result);
    }
}
