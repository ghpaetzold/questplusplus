package shef.mt.features.impl.wce;

import java.util.ArrayList;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1020 extends WordLevelFeature {

    public WordLevelFeature1020() {
        this.setIndex("WCE1020");
        this.setIdentifier("TRGPOSTRIGRAMLEFT");
        this.setDescription("Left trigram of POS tags of target word.");
        this.addResource("target.POSModel");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];

        //Get tokens from target sentence:
        String[] tokens = target.getTokens();

        //Get pos tags or target sentence:
        ArrayList<String> targetPOSTags = (ArrayList<String>) target.getValue("postags");

        //Get POS tags of each target word:
        for (int i = 0; i < tokens.length; i++) {
            String ngram = this.getNgram(targetPOSTags, i, 2, 0);
            String value = this.getIdentifier() + '=' + ngram;
            result[i] = value;
        }

        //Save values produced:
        this.setValues(result);
    }

    private String getNgram(ArrayList<String> postags, int i, int leftSize, int rightSize) {
        //Create POS ngram:
        String ngram = "";
        for (int j = i - leftSize; j <= i + rightSize; j++) {
            try{
                ngram += postags.get(j) + "/";
            }catch(IndexOutOfBoundsException ex){
                ngram += "NULL/";
            }
        }

        //Return POS ngram:
        return ngram;
    }
}
