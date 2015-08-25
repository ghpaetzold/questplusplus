package shef.mt.features.impl.wce;

import java.util.HashMap;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1009 extends WordLevelFeature {

    public WordLevelFeature1009() {
        this.setIndex("WCE1009");
        this.setIdentifier("SRCALIGNLEFT");
        this.setDescription("Left bigram aligned to each target word.");
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
            String leftSource = this.getLeftSource(sourceTokens, alignedIndex);

            //Create value:
            String value = this.getIdentifier() + "=" + targetTokens[i] + "/" + leftSource;

            //Save value:
            result[i] = value;
        }

        //Save values produced:
        this.setValues(result);
    }

    private String getLeftSource(String[] tokens, Integer index) {
        //Determine correct left word:
        if (index == null) {
            return "NULL";
        } else {
            if (index == 0) {
                return "<s>";
            } else {
                return tokens[index - 1];
            }
        }
    }
}
