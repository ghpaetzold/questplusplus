package shef.mt.features.impl.wce;

import java.util.HashMap;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1010 extends WordLevelFeature {

    public WordLevelFeature1010() {
        this.setIndex("WCE1010");
        this.setIdentifier("SRCALIGNRIGHT");
        this.setDescription("Right bigram aligned to each target word.");
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

            //Get left source word:
            String rightSource = this.getRightSource(sourceTokens, alignedIndex);

            //Create value:
            String value = this.getIdentifier() + "=" + targetTokens[i] + "/" + rightSource;

            //Save value:
            result[i] = value;
        }

        //Save values produced:
        this.setValues(result);
    }

    private String getRightSource(String[] tokens, Integer index) {
        //Determine correct right word:
        if (index == null) {
            return "NULL";
        } else {
            if (index == tokens.length-1) {
                return "<s>";
            } else {
                return tokens[index + 1];
            }
        }
    }
}
