package shef.mt.features.impl.wce;

import edu.stanford.nlp.util.ArrayUtils;
import java.util.ArrayList;
import java.util.HashMap;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;
import shef.mt.tools.LanguageModel;

public class WordLevelFeature1040 extends WordLevelFeature {

    public WordLevelFeature1040() {
        this.setIndex("WCE1040");
        this.setIdentifier("LSPNL");
        this.setDescription("Longest source POS n-gram length.");
        this.addResource("source.POSModel");
        this.addResource("source.posngram");
        this.addResource("alignments.file");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];

        //Get language model object:
        LanguageModel lm = (LanguageModel) source.getValue("posngramcount");

        //Get alignments:
        HashMap<Integer, Integer> alignments = (HashMap<Integer, Integer>) target.getValue("alignments.file");

        //Ge tokens from target sentence:
        String[] targetTokens = target.getTokens();

        //Get pos tags or target sentence:
        ArrayList<String> sourcePOSTags = (ArrayList<String>) source.getValue("postags");

        //Output word occurrences:
        for (int i = 0; i < targetTokens.length; i++) {
            //Get index of aligned word in source sentence:
            Integer alignedIndex = alignments.get(i);

            //Get string value:
            String value;
            if (alignedIndex == null) {
                value = this.getIdentifier() + "=0";
            } else {
                //Get ngrams:
                String[] ngrams = this.getNgrams(sourcePOSTags.toArray(new String[sourcePOSTags.size()]), alignedIndex);
                String w3w2w1w = ngrams[0];
                String w2w1w = ngrams[1];
                String w1w = ngrams[2];
                String w = ngrams[3];

                //Get ngram frequencies:
                Integer w3w2w1wi = lm.getFreq(w3w2w1w, 4);
                Integer w2w1wi = lm.getFreq(w2w1w, 3);
                Integer w1wi = lm.getFreq(w1w, 2);
                Integer wi = lm.getFreq(w, 1);

                //Estimate backoff value:
                int backoff;
                if (w3w2w1wi != 0) {
                    backoff = 4;
                } else if (w2w1wi != 0) {
                    backoff = 3;
                } else if (w1wi != 0) {
                    backoff = 2;
                } else if (wi != 0) {
                    backoff = 1;
                } else {
                    backoff = 0;
                }

                //Create value:
                value = this.getIdentifier() + "=" + backoff;
            }

            //Save value:
            result[i] = value;
        }

        //Save values produced:
        this.setValues(result);
    }

    private String[] getNgrams(String[] tokens, int i) {
        //Create fillers:
        String[] fillers = new String[]{"<s>", "<s>", "<s>"};
        String[] allTokens = ArrayUtils.concatenate(fillers, tokens);

        //Ajust word index:
        int index = i + 3;

        //Create array of ngrams:
        String[] result = new String[4];
        result[0] = allTokens[index - 3] + " " + allTokens[index - 2] + " " + allTokens[index - 1] + " " + allTokens[index];
        result[1] = allTokens[index - 2] + " " + allTokens[index - 1] + " " + allTokens[index];
        result[2] = allTokens[index - 1] + " " + allTokens[index];
        result[3] = allTokens[index];
        //Return array of ngrams:
        return result;
    }
}
